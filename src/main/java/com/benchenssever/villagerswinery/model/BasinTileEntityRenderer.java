package com.benchenssever.villagerswinery.model;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class BasinTileEntityRenderer extends TileEntityRenderer<BasinTileEntity> {
    private static final int FILL_SPEED = BasinTileEntity.DEFAULT_CAPACITY / 20;
    private Fluid lastFluid = Fluids.EMPTY;
    private int fillLevel = 0;
    private boolean isFirstRender = true;

    public BasinTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(@NotNull BasinTileEntity tileEntityIn, float partialTicks, @NotNull MatrixStack matrixStackIn, @NotNull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack tileItemStack = tileEntityIn.getItemStack(0);
        FluidStack tileFluidStack = tileEntityIn.getFluidStack();

        if(!tileFluidStack.isEmpty()) {
            lastFluid = tileFluidStack.getFluid();
        }
        if(isFirstRender) {
            fillLevel = tileFluidStack.getAmount();
            isFirstRender = false;
        }
        fillLevel = approach(fillLevel, tileFluidStack.getAmount(), FILL_SPEED);

        if(fillLevel > 0) {
            renderFluid(lastFluid, fillLevel, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if(tileItemStack.getCount() > 0) {
            renderItem(tileItemStack, tileEntityIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
    }

    private static void renderItem(ItemStack itemStack, BasinTileEntity tileEntityIn,MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();

        matrixStackIn.translate(8.0f / 16.0f, 2.0f / 16.0f, 8.0f / 16.0f);
        float thickness = (itemStack.getCount() / 2.0f) * 0.7f;
        matrixStackIn.scale(0.7f, thickness, 0.7f);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(itemStack, tileEntityIn.getWorld(), null);
        itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
        matrixStackIn.pop();
    }

    private static void renderFluid(Fluid fluid, int fillLevel, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        FluidStack fluidStack = new FluidStack(fluid, fillLevel);
        if(!fluidStack.isEmpty()) {
            IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getTranslucent());
            matrixStackIn.push();
            BakedQuad quad = FluidTransferUtil.getLiquidQuad(fluidStack, BasinTileEntity.DEFAULT_CAPACITY, 0, 14, 2, 14, 2, 10);
            int color = fluid.getAttributes().getColor(fluidStack);
            int red = color >> 16 & 0xFF;
            int green = color >> 8 & 0xFF;
            int blue = color & 0xFF;
            vertexBuilder.addQuad(matrixStackIn.getLast(), quad, red / 256.0f, green / 256.0f, blue / 256.0f, combinedLightIn, combinedOverlayIn);
            matrixStackIn.pop();
        }
    }

    public static int approach(int current, int target, int speed) {
        int difference = target - current;
        if(Math.abs(difference) < speed) {
            return target;
        }
        return current + Integer.signum(difference) * speed;
    }
}
