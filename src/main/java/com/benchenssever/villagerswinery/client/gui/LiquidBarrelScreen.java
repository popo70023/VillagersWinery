package com.benchenssever.villagerswinery.client.gui;

import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class LiquidBarrelScreen extends ContainerScreen<LiquidBarrelContainer> {
    private final ResourceLocation LIQUID_BARREL_RESOURCE = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

    @Nullable
    protected LiquidBarrelContainer.FluidSlot hoveredFluidSlot;

    public LiquidBarrelScreen(LiquidBarrelContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        TextureManager tm = this.getMinecraft().getTextureManager();
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.hoveredFluidSlot = null;
        for(LiquidBarrelContainer.FluidSlot slot : this.container.inventoryFluidSlot) {
            FluidStack fluidStack = slot.fluidStack;
            if(!fluidStack.isEmpty()) {
                ResourceLocation fluidResource = fluidStack.getFluid().getAttributes().getStillTexture();
                tm.bindTexture(new ResourceLocation(fluidResource.getNamespace(), "textures/" + fluidResource.getPath() + ".png"));
                int fluidAmount = this.getProgressionScaled(fluidStack.getAmount(), 8000, slot.ySize);
                int color = fluidStack.getFluid().getAttributes().getColor();
                RenderSystem.color4f((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, (color >> 24 & 0xFF) / 255f);
                blit(matrixStack, this.guiLeft + slot.xPos, this.guiTop + slot.yPos + slot.ySize - fluidAmount, 0, 512 - fluidAmount, slot.xSize, fluidAmount, 16, 512);
            }

            if(this.isFluidSlotSelected(slot, mouseX, mouseY)) {
                this.hoveredFluidSlot = slot;
            }
        }
        RenderSystem.color4f(1, 1, 1, 1);
        tm.bindTexture(LIQUID_BARREL_RESOURCE);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    private boolean isFluidSlotSelected(LiquidBarrelContainer.FluidSlot slotIn, double mouseX, double mouseY) {
        return this.isPointInRegion(slotIn.xPos, slotIn.yPos, slotIn.xSize, slotIn.ySize, mouseX, mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y) {
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredFluidSlot != null) {
            ITextComponent itextcomponent = new TranslationTextComponent(this.hoveredFluidSlot.fluidStack.getTranslationKey());
            this.renderTooltip(matrixStack, itextcomponent, x, y);
        }
        super.renderHoveredTooltip(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        TextureManager tm = this.getMinecraft().getTextureManager();

        tm.bindTexture(LIQUID_BARREL_RESOURCE);
        blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        int winemakingTime = this.getProgressionScaled(this.container.getLiquidBarrelData().get(0), 1600, 24);
        blit(matrixStack, this.guiLeft + 79, this.guiTop + 34, 176, 14, winemakingTime, 16);
    }

    public int getProgressionScaled(int existing, int max, int scale) {
        return max != 0 && existing != 0 ? existing * scale / max : 0;
    }
}