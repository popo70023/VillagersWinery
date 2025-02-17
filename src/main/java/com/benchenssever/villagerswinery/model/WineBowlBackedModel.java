package com.benchenssever.villagerswinery.model;

import com.benchenssever.villagerswinery.fluid.WoodenContainerFluidHandler;
import com.benchenssever.villagerswinery.item.Winebowl;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;


public class WineBowlBackedModel implements IBakedModel {
    protected final Map<Direction, List<BakedQuad>> faceQuads = new EnumMap<Direction, List<BakedQuad>>(Direction.class);
    private final IBakedModel existingModel;
    private final FluidStack fluidStack;
    private final List<BakedQuad> cachedQuads;

    public WineBowlBackedModel(IBakedModel existingModel, FluidStack fluid) {
        this.existingModel = existingModel;
        this.fluidStack = fluid;
        Random rand = new Random();
        for (Direction side : Direction.values()) {
            faceQuads.put(side, new ArrayList<>(existingModel.getQuads(null, side, rand)));
        }
        cachedQuads = new ArrayList<>(existingModel.getQuads(null, null, rand));
        if (!fluidStack.isEmpty()) {
            cachedQuads.add(getLiquidQuad(fluidStack));
        }
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull Random rand) {
        return side == null ? this.cachedQuads : this.faceQuads.get(side);
    }


    @Override
    public boolean isAmbientOcclusion() {
        return existingModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return existingModel.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return existingModel.isSideLit();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return existingModel.isBuiltInRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleTexture() {
        return existingModel.getParticleTexture();
    }

    @Override
    public @NotNull ItemCameraTransforms getItemCameraTransforms() {
        return existingModel.getItemCameraTransforms();
    }

    @Override
    public @NotNull ItemOverrideList getOverrides() {

        return new ItemOverrideList() {
            private WineBowlBackedModel cachedModel;

            @Override
            public IBakedModel getOverrideModel(@NotNull IBakedModel model, @NotNull ItemStack stack, ClientWorld world, LivingEntity livingEntity) {
                WoodenContainerFluidHandler winebowl = new WoodenContainerFluidHandler(stack, Winebowl.DEFAULT_CAPACITY);
                FluidStack fluidStack = winebowl.getFluid();
                if (cachedModel == null || !cachedModel.fluidStack.equals(fluidStack)) {
                    cachedModel = new WineBowlBackedModel(model, fluidStack);
                }
                return cachedModel;
            }
        };
    }

    @Override
    public IBakedModel getBakedModel() {
        return this;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        return getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return existingModel.isAmbientOcclusion(state);
    }

    @Override
    public @NotNull IModelData getModelData(@NotNull IBlockDisplayReader world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData tileData) {
        return existingModel.getModelData(world, pos, state, tileData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@NotNull IModelData data) {
        return existingModel.getParticleTexture(data);
    }

    @Override
    public boolean isLayered() {
        return existingModel.isLayered();
    }

    @Override
    public List<Pair<IBakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous) {
        return existingModel.getLayerModels(itemStack, fabulous);
    }

    private static BakedQuad getLiquidQuad(FluidStack fluidStack) {
        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        int color = attributes.getColor(fluidStack);
        int luminosity = attributes.getLuminosity(fluidStack); //TODO: luminosity?
        float liquidLevel = 1.0f + (fluidStack.getAmount() * 6.0f / Winebowl.DEFAULT_CAPACITY);
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
        BakedQuad bakedQuad = faceBakery.bakeQuad(new Vector3f(6.0f, liquidLevel, 6.0f), new Vector3f(10.0f, liquidLevel, 10.0f), blockPartFace, sprite, Direction.UP, NO_TRANSFORMATION, DEFAULT_ROTATION,
                APPLY_SHADING, DUMMY_RL);

        int alpha = color >> 24 & 0xFF;
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        for (int i = 0; i < 4; ++i) {
            bakedQuad.getVertexData()[i * 8 + 3] = alpha << 24 | blue << 16 | green << 8 | red;
        }
        return bakedQuad;
    }
}

