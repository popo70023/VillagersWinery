package com.benchenssever.villagerswinery.client.gui;

import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class LiquidBarrelScreen extends ContainerScreen<LiquidBarrelContainer> {
    private final ResourceLocation LIQUID_BARREL_RESOURCE = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
    public LiquidBarrelScreen(LiquidBarrelContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) { super(screenContainer, inv, titleIn); }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderBackground(matrixStack);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
        this.drawCenteredString(matrixStack, this.font, Integer.toString(this.getContainer().getLiquidBarrelData().get(0)), 82, 20, 0xeb0505);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        this.minecraft.getTextureManager().bindTexture(LIQUID_BARREL_RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(matrixStack, i, j, 0, 0, xSize, ySize, this.xSize, ySize);
    }
}
