package com.benchenssever.villagerswinery.model;

import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BasinTileEntityRenderer extends TileEntityRenderer<BasinTileEntity> {
    public BasinTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(@NotNull BasinTileEntity tileEntityIn, float partialTicks, @NotNull MatrixStack matrixStackIn, @NotNull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack tileItemStack = tileEntityIn.getItemStack(0);
        FluidStack tileFluidStack = tileEntityIn.getFluidStack();

        if (!tileFluidStack.isEmpty()) {
            renderFluid(tileFluidStack, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (!tileItemStack.isEmpty()) {
            renderItem(tileItemStack, tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
    }

    private static void renderItem(ItemStack itemStack, BasinTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int itemCount = (int) Math.ceil((itemStack.getCount()) / 8.0);
        Random rand = new Random(tileEntityIn.getPos().hashCode());

        for (int i = 0; i < itemCount; i++) {
            matrixStackIn.push();

            if (itemStack.getItem() instanceof BlockItem) {
                int layer = i / 4;
                int posInLayer = i % 4;
                float scale = 0.6f;

                float xOffset = 0.3f + (posInLayer % 2) * 0.4f;
                float zOffset = 0.3f + (posInLayer / 2) * 0.4f;
                float yOffset = 0.25f + (scale * (layer + 0.5f) / 2.0f);
                matrixStackIn.translate(xOffset, yOffset, zOffset);
                matrixStackIn.scale(scale, scale, scale);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rand.nextFloat() * 360.0f));
            } else {
                float scale = 0.7f;

                float xOffset = 0.5f + ((rand.nextFloat() - 0.5f) * 0.1f);
                float zOffset = 0.5f + ((rand.nextFloat() - 0.5f) * 0.1f);
                float yOffset = 0.25f + (scale * (i + 0.5f) / 16.0f);
                matrixStackIn.translate(xOffset, yOffset, zOffset);
                matrixStackIn.scale(scale, scale, scale);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0f));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(rand.nextFloat() * 360.0f));
            }

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(itemStack, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }
    }

    private static void renderFluid(FluidStack fluidStack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        FluidAttributes attributes = fluidStack.getFluid().getAttributes();
        float yOffset = 0.25f + (fluidStack.getAmount() * 0.5f / BasinTileEntity.DEFAULT_CAPACITY);
        int color = attributes.getColor(fluidStack);
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int alpha = (color >> 24) & 0xFF;

        matrixStackIn.push();

        RenderMaterial fluidMaterial = ModelLoaderRegistry.blockMaterial(attributes.getStillTexture(fluidStack));
        TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(fluidMaterial);
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityTranslucentCull(sprite.getAtlasTexture().getTextureLocation()));

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        Matrix4f matrix = matrixStackIn.getLast().getMatrix();

        vertexBuilder.pos(matrix, 0.0625f, yOffset, 0.9375f) // 顶点 1
                .color(red, green, blue, alpha)
                .tex(minU, minV)
                .overlay(combinedOverlayIn)
                .lightmap(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        vertexBuilder.pos(matrix, 0.9375f, yOffset, 0.9375f) // 顶点 2
                .color(red, green, blue, alpha)
                .tex(maxU, minV)
                .overlay(combinedOverlayIn)
                .lightmap(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        vertexBuilder.pos(matrix, 0.9375f, yOffset, 0.0625f) // 顶点 3
                .color(red, green, blue, alpha)
                .tex(maxU, maxV)
                .overlay(combinedOverlayIn)
                .lightmap(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        vertexBuilder.pos(matrix, 0.0625f, yOffset, 0.0625f) // 顶点 4
                .color(red, green, blue, alpha)
                .tex(minU, maxV)
                .overlay(combinedOverlayIn)
                .lightmap(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        matrixStackIn.pop();
    }
}
