package com.benchenssever.villagerswinery.model;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.fluid.WinebowlFluidHandler;
import com.benchenssever.villagerswinery.item.Winebowl;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
            cachedQuads.add(FluidTransferUtil.getLiquidQuad(fluidStack, Winebowl.DEFAULT_CAPACITY, 6, 10, 6, 10, 1, 6));
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
                WinebowlFluidHandler winebowl = new WinebowlFluidHandler(stack, Winebowl.DEFAULT_CAPACITY);
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
}

