package com.benchenssever.villagerswinery.client.gui;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.fluid.FluidUtils;
import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class LiquidBarrelScreen extends ContainerScreen<LiquidBarrelContainer> {
    private final ResourceLocation LIQUID_BARREL_RESOURCE = new ResourceLocation(VillagersWineryMod.MODID, "textures/gui/container/barrel.png");

    protected LiquidBarrelContainer.FluidSlot hoveredFluidSlot;
    private int winemakingTimer = 0;

    public LiquidBarrelScreen(LiquidBarrelContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    public static int getProgressionScaled(int existing, int max, int scale) {
        return max != 0 && existing != 0 ? existing * scale / max : 0;
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        TextureManager tm = this.getMinecraft().getTextureManager();
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.hoveredFluidSlot = null;

        FluidStack fluidStack = this.menu.fluidStack;
        LiquidBarrelContainer.FluidSlot fluidslot = this.menu.fluidSlot;
        int fluidslotx1 = this.leftPos + fluidslot.xPos;
        int fluidslotx2 = this.leftPos + fluidslot.xPos + fluidslot.xSize;
        int fluidsloty1 = this.topPos + fluidslot.yPos;
        int fluidsloty2 = this.topPos + fluidslot.yPos + fluidslot.ySize;
        if (!fluidStack.isEmpty()) {
            ResourceLocation fluidResource = fluidStack.getFluid().getAttributes().getStillTexture();
            tm.bind(new ResourceLocation(fluidResource.getNamespace(), "textures/" + fluidResource.getPath() + ".png"));
            int fluidAmount = getProgressionScaled(fluidStack.getAmount(), 8000, fluidslot.ySize);
            int color = fluidStack.getFluid().getAttributes().getColor();
            RenderSystem.color4f((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, (color >> 24 & 0xFF) / 255f);
            blit(matrixStack, fluidslotx1, fluidsloty2 - fluidAmount, 0, 512 - fluidAmount, fluidslot.xSize, fluidAmount, 16, 512);
        }

        tm.bind(LIQUID_BARREL_RESOURCE);
        RenderSystem.color4f(1, 1, 1, 1);
        blit(matrixStack, fluidslotx1, fluidsloty1, 180, 1, fluidslot.xSize, fluidslot.ySize);

        if (this.isFluidSlotSelected(fluidslot, mouseX, mouseY)) {
            this.hoveredFluidSlot = fluidslot;
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            int slotColor = this.getSlotColor(0);
            this.fillGradient(matrixStack, fluidslotx1, fluidsloty1, fluidslotx2, fluidsloty2, slotColor, slotColor);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }

        renderTooltip(matrixStack, mouseX, mouseY);
    }

    private boolean isFluidSlotSelected(LiquidBarrelContainer.FluidSlot slotIn, double mouseX, double mouseY) {
        return this.isHovering(slotIn.xPos, slotIn.yPos, slotIn.xSize, slotIn.ySize, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(@NotNull MatrixStack matrixStack, int x, int y) {
        if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredFluidSlot != null) {
            ITextComponent itextcomponent = FluidUtils.addFluidStackTooltip(this.menu.fluidStack);
            this.renderTooltip(matrixStack, itextcomponent, x, y);
        }
        super.renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrixStack, int x, int y) {
        super.renderLabels(matrixStack, x, y);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrixStack, float partialTicks, int x, int y) {
        TextureManager tm = this.getMinecraft().getTextureManager();

        tm.bind(LIQUID_BARREL_RESOURCE);
        blit(matrixStack, this.leftPos, this.topPos, 1, 1, imageWidth, imageHeight);

        if (this.menu.getLiquidBarrelData().get(2) != 0) {
            int winemakingProgression = getProgressionScaled(this.menu.getLiquidBarrelData().get(0), this.menu.getLiquidBarrelData().get(1), 30);
            int renderWinemakingProgression = Math.min(winemakingProgression, winemakingTimer / 2);
            RenderSystem.enableBlend();
            blit(matrixStack, this.leftPos + 63, this.topPos + 54 - renderWinemakingProgression, 179, 63 - renderWinemakingProgression, 13, renderWinemakingProgression);
            winemakingTimer++;
            if (winemakingTimer > 90) {
                winemakingTimer = 0;
            }
        }
    }

    public void updateFluid(FluidStack fluidStack, String worldAndPos) {
        if (this.menu.worldAndPos.equals(worldAndPos)) {
            this.menu.fluidStack = fluidStack;
        }
    }
}