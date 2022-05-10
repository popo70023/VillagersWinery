package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.Supplier;

//TODO: 要重構
public class FluidTransferUtil{

    public static final ResourceLocation STILL_WATER_TEXTURE = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING_WATER_TEXTURE = new ResourceLocation("block/water_flow");

    public static ForgeFlowingFluid.Properties waterProperties(Supplier<FlowingFluid> still, Supplier<FlowingFluid> flowing, int color, Supplier<Item> bucket, Supplier<FlowingFluidBlock> block) {
        return new ForgeFlowingFluid.Properties(still, flowing, FluidAttributes.builder(STILL_WATER_TEXTURE, FLOWING_WATER_TEXTURE).color(color).density(4000).viscosity(4000).sound(SoundEvents.ITEM_BUCKET_FILL,SoundEvents.ITEM_BUCKET_EMPTY)).bucket(bucket).block(block).slopeFindDistance(3).explosionResistance(100F);
    }

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
}
