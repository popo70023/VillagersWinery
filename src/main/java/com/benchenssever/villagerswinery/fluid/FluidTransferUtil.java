package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

//TODO: 要重構
public class FluidTransferUtil {

    public static FluidStack tryTransfer(IFluidHandler input, IFluidHandler output, int maxFill) {
        // first, figure out how much we can drain
        FluidStack simulated = input.drain(maxFill, IFluidHandler.FluidAction.SIMULATE);
        if (!simulated.isEmpty()) {
            // next, find out how much we can fill
            int simulatedFill = output.fill(simulated, IFluidHandler.FluidAction.SIMULATE);
            if (simulatedFill > 0) {
                // actually drain
                FluidStack drainedFluid = input.drain(simulatedFill, IFluidHandler.FluidAction.EXECUTE);
                if (!drainedFluid.isEmpty()) {
                    // acutally fill
                    int actualFill = output.fill(drainedFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                    if (actualFill != drainedFluid.getAmount()) {
                        VillagersWineryMod.LOGGER.error("Lost {} fluid during transfer", drainedFluid.getAmount() - actualFill);
                    }
                }
                return drainedFluid;
            }
        }
        return FluidStack.EMPTY;
    }

    public static boolean interactWithBucket(World world, BlockPos pos, PlayerEntity player, Hand hand, Direction hit, Direction offset) {
        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() instanceof BucketItem) {
            BucketItem bucket = (BucketItem) held.getItem();
            Fluid fluid = bucket.getFluid();
            if (fluid != Fluids.EMPTY) {
                if (!world.isRemote) {
                    TileEntity te = world.getTileEntity(pos);
                    if (te != null) {
                        te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit)
                                .ifPresent(handler -> {
                                    FluidStack fluidStack = new FluidStack(bucket.getFluid(), FluidAttributes.BUCKET_VOLUME);
                                    // must empty the whole bucket
                                    if (handler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) == FluidAttributes.BUCKET_VOLUME) {
                                        handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                                        bucket.onLiquidPlaced(world, held, pos.offset(offset));
                                        world.playSound(null, pos, fluid.getAttributes().getEmptySound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                                        if (!player.isCreative()) {
                                            player.setHeldItem(hand, held.getContainerItem());
                                        }
                                    }
                                });
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean interactWithFluidItem(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        // success if the item is a fluid handler, regardless of if fluid moved
        ItemStack stack = player.getHeldItem(hand);
        Direction face = hit.getFace();
        // fetch capability before copying, bit more work when its a fluid handler, but saves copying time when its not
        if (!stack.isEmpty() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
            // only server needs to transfer stuff
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);
                if (te != null) {
                    LazyOptional<IFluidHandler> teCapability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face);
                    if (teCapability.isPresent()) {
                        IFluidHandler teHandler = teCapability.orElse(EmptyFluidHandler.INSTANCE);
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
                        copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(itemHandler -> {
                            // first, try filling the TE from the item
                            FluidStack transferred = tryTransfer(itemHandler, teHandler, Integer.MAX_VALUE);
                            if (!transferred.isEmpty()) {
                                world.playSound(null, pos, transferred.getFluid().getAttributes().getEmptySound(transferred), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            } else {
                                // if that failed, try filling the item handler from the TE
                                transferred = tryTransfer(teHandler, itemHandler, Integer.MAX_VALUE);
                                if (!transferred.isEmpty()) {
                                    world.playSound(null, pos, transferred.getFluid().getAttributes().getFillSound(transferred), SoundCategory.BLOCKS, 1.0F, 1.0F);
                                }
                            }
                            // if either worked, update the player's inventory
                            if (!transferred.isEmpty()) {
                                player.setHeldItem(hand, DrinkHelper.fill(stack, player, itemHandler.getContainer()));
                            }
                        });
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean interactWithTank(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return interactWithFluidItem(world, pos, player, hand, hit)
                || interactWithBucket(world, pos, player, hand, hit.getFace(), hit.getFace());
    }

    public static boolean isInteractableWithFluidStack(ItemStack held) {
        if (held.getItem() instanceof BucketItem) {
            BucketItem bucket = (BucketItem) held.getItem();
            Fluid fluid = bucket.getFluid();
            return fluid != Fluids.EMPTY;
        } else
            return !held.isEmpty() && held.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
    }

    public static TranslationTextComponent addFluidTooltip(FluidStack fluidStack) {
        return new TranslationTextComponent("item." + VillagersWineryMod.MODID + ".fluid.information",
                new TranslationTextComponent(fluidStack.getTranslationKey()),
                new StringTextComponent(Integer.toString(fluidStack.getAmount())));
    }

    public static FluidStack getFluidStackFromJson(JsonObject json) {
        String fluidName = JSONUtils.getString(json, "fluid");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
        if (fluid == null) throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");
        return new FluidStack(fluid, JSONUtils.getInt(json, "amount", 1000));
    }

    public static BakedQuad getLiquidQuad(FluidStack fluidStack, int capacity, float x1, float x2, float z1, float z2, float bottom, float hight) {
        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        int color = attributes.getColor(fluidStack);
        int luminosity = attributes.getLuminosity(fluidStack); //TODO: luminosity?
        float liquidLevel = bottom + ((float) fluidStack.getAmount() / capacity) * hight;
        RenderMaterial fluidMaterial = ModelLoaderRegistry.blockMaterial(attributes.getStillTexture(fluidStack));


        Function<RenderMaterial, TextureAtlasSprite> spriteGetter = ModelLoader.defaultTextureGetter();
        TextureAtlasSprite sprite = spriteGetter.apply(fluidMaterial);
        FaceBakery faceBakery = new FaceBakery();

        final int ROTATION_NONE = 0;
        BlockFaceUV blockFaceUV = new BlockFaceUV(new float[]{0, 0, 4, 4}, ROTATION_NONE);

        final Direction NO_FACE_CULLING = null;
        final int TINT_INDEX_NONE = -1;  // used for tintable blocks such as grass, which make a call to BlockColors to change their rendering colour.  -1 for not tintable.
        final String DUMMY_TEXTURE_NAME = "";  // texture name is only needed for loading from json files; not needed here

        BlockPartFace blockPartFace = new BlockPartFace(NO_FACE_CULLING, TINT_INDEX_NONE, DUMMY_TEXTURE_NAME, blockFaceUV);

        final IModelTransform NO_TRANSFORMATION = SimpleModelTransform.IDENTITY;
        final BlockPartRotation DEFAULT_ROTATION = null;   // rotate based on the face direction
        final boolean APPLY_SHADING = true;
        final ResourceLocation DUMMY_RL = new ResourceLocation("dummy_name");  // used for error message only
        BakedQuad bakedQuad = faceBakery.bakeQuad(new Vector3f(x1, liquidLevel, z1), new Vector3f(x2, liquidLevel, z2), blockPartFace, sprite, Direction.UP, NO_TRANSFORMATION, DEFAULT_ROTATION,
                APPLY_SHADING, DUMMY_RL);

        int alpha = color >> 24 & 0xFF;
//        int alpha = 127;
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        for (int i = 0; i < 4; ++i) {
            bakedQuad.getVertexData()[i * 8 + 3] = alpha << 24 | blue << 16 | green << 8 | red;
        }
        return bakedQuad;
    }
}
