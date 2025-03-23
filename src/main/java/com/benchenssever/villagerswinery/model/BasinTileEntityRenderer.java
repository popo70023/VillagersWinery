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
        Random rand = new Random(tileEntityIn.getBlockPos().hashCode());

        for (int i = 0; i < itemCount; i++) {
            matrixStackIn.pushPose();

            if (itemStack.getItem() instanceof BlockItem) {
                int layer = i / 4;
                int posInLayer = i % 4;
                float scale = 0.6f;

                float xOffset = 0.3f + (posInLayer % 2) * 0.4f;
                float zOffset = 0.3f + (posInLayer / 2) * 0.4f;
                float yOffset = 0.25f + (scale * (layer + 0.5f) / 2.0f);
                matrixStackIn.translate(xOffset, yOffset, zOffset);
                matrixStackIn.scale(scale, scale, scale);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rand.nextFloat() * 360.0f));
            } else {
                float scale = 0.7f;

                float xOffset = 0.5f + ((rand.nextFloat() - 0.5f) * 0.1f);
                float zOffset = 0.5f + ((rand.nextFloat() - 0.5f) * 0.1f);
                float yOffset = 0.25f + (scale * (i + 0.5f) / 16.0f);
                matrixStackIn.translate(xOffset, yOffset, zOffset);
                matrixStackIn.scale(scale, scale, scale);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0f));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rand.nextFloat() * 360.0f));
            }

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            IBakedModel ibakedmodel = itemRenderer.getModel(itemStack, tileEntityIn.getLevel(), null);
            itemRenderer.render(itemStack, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.popPose();
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

        matrixStackIn.pushPose();

        RenderMaterial fluidMaterial = ModelLoaderRegistry.blockMaterial(attributes.getStillTexture(fluidStack));
        TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(fluidMaterial);
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucentCull(sprite.atlas().location()));

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        Matrix4f matrix = matrixStackIn.last().pose();

        vertexBuilder.vertex(matrix, 0.0625f, yOffset, 0.9375f) // 顶点 1
                .color(red, green, blue, alpha)
                .uv(minU, minV)
                .overlayCoords(combinedOverlayIn)
                .uv2(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        vertexBuilder.vertex(matrix, 0.9375f, yOffset, 0.9375f) // 顶点 2
                .color(red, green, blue, alpha)
                .uv(maxU, minV)
                .overlayCoords(combinedOverlayIn)
                .uv2(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        vertexBuilder.vertex(matrix, 0.9375f, yOffset, 0.0625f) // 顶点 3
                .color(red, green, blue, alpha)
                .uv(maxU, maxV)
                .overlayCoords(combinedOverlayIn)
                .uv2(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        vertexBuilder.vertex(matrix, 0.0625f, yOffset, 0.0625f) // 顶点 4
                .color(red, green, blue, alpha)
                .uv(minU, maxV)
                .overlayCoords(combinedOverlayIn)
                .uv2(combinedLightIn)
                .normal(0, 1, 0)
                .endVertex();

        matrixStackIn.popPose();
    }
}
