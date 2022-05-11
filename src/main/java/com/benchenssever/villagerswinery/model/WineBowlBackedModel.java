package com.benchenssever.villagerswinery.model;

import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class WineBowlBackedModel implements IBakedModel {
    private IBakedModel existingModel;
    private FluidStack fluidStack;
    private final BakedQuad fluidQuad;
    private List<BakedQuad> cachedQuads = null;
    protected final Map<Direction, List<BakedQuad>> faceQuads = new EnumMap<Direction, List<BakedQuad>>(Direction.class);

    public WineBowlBackedModel(IBakedModel existingModel) {
        this.existingModel = existingModel;
        this.fluidStack = new FluidStack(DrinksRegistry.beer.getFluid(), 1000);
        this.fluidQuad = getLiquidQuad(fluidStack);
        Random rand = new Random();
        for (Direction side : Direction.values()) {
            faceQuads.put(side,new ArrayList<>(existingModel.getQuads(null,side,rand)));
        }
        cachedQuads = new ArrayList<>(existingModel.getQuads(null,null,rand));
        cachedQuads.add(fluidQuad);

    }

    BakedQuad getLiquidQuad(FluidStack fluidStack){
        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        int color = attributes.getColor(fluidStack);
        int luminosity = attributes.getLuminosity(fluidStack); //TODO: luminosity?
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
        BakedQuad bakedQuad = faceBakery.bakeQuad(new Vector3f(6, 7, 6), new Vector3f(10, 7, 10), blockPartFace, sprite, Direction.UP, NO_TRANSFORMATION, DEFAULT_ROTATION,
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

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
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
    public TextureAtlasSprite getParticleTexture() {
        return existingModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return existingModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return existingModel.getOverrides();
    }

    @Override
    public IBakedModel getBakedModel() {
        return this;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return existingModel.isAmbientOcclusion(state);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        return existingModel.getModelData(world, pos, state, tileData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
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

//    class WineBowlItemOverrideList extends ItemOverrideList{
//
//        @Nullable
//        @Override
//        public IBakedModel getOverrideModel(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
//
//
//            return super.getOverrideModel(model, stack, world, livingEntity);
//        }
//    }
}

