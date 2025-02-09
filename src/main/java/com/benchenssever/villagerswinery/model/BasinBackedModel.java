package com.benchenssever.villagerswinery.model;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BasinBackedModel implements IBakedModel {
    private final IBakedModel existingModel;

    public BasinBackedModel(IBakedModel existingModel) {
        this.existingModel = existingModel;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand) {
        return existingModel.getQuads(state, side, rand);
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
        return existingModel.getOverrides();
    }

    @Override
    public boolean doesHandlePerspectives() {
        return existingModel.doesHandlePerspectives();
    }

    @Override
    public IBakedModel getBakedModel() {
        return this;
    }

    @Override
    public List<Pair<IBakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous) {
        return existingModel.getLayerModels(itemStack, fabulous);
    }

    @Override
    public @NotNull IModelData getModelData(@NotNull IBlockDisplayReader world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData tileData) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof BasinTileEntity) {
            BasinTileEntity basin = (BasinTileEntity) tile;
            return new ModelDataMap.Builder()
                    .withInitial(BasinTileEntity.FLUID_STACK_MODEL_PROPERTY, basin.getFluid())
                    .build();
        }
        return existingModel.getModelData(world, pos, state, tileData);
    }


    @Override
    public TextureAtlasSprite getParticleTexture(@NotNull IModelData data) {
        return existingModel.getParticleTexture(data);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        if (side != Direction.UP) {
            return existingModel.getQuads(state, side, rand, extraData);
        }
        FluidStack fluidStack = extraData.getData(BasinTileEntity.FLUID_STACK_MODEL_PROPERTY);
        List<BakedQuad> cachedQuads = new ArrayList<>(existingModel.getQuads(state, Direction.UP, rand, extraData));
        if (!fluidStack.isEmpty()) {
            cachedQuads.add(FluidTransferUtil.getLiquidQuad(fluidStack, BasinTileEntity.DEFAULT_CAPACITY, 2, 14, 2, 14, 2, 10));
        }

        return cachedQuads;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        return existingModel.handlePerspective(cameraTransformType, mat);
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return existingModel.isAmbientOcclusion(state);
    }

    @Override
    public boolean isLayered() {
        return existingModel.isLayered();
    }
}
