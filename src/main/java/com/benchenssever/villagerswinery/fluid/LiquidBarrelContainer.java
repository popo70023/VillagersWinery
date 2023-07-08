package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class LiquidBarrelContainer extends Container {

    private final IIntArray liquidBarrelData;
    public final List<FluidSlot> inventoryFluidSlot = Lists.newArrayList();
    private final PlayerInventory playerinventory;

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, PacketBuffer data) {
        this(id, playerinventory, FluidStack.readFromPacket(data), new IntArray(3));
    }

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, FluidStack fluidStack, IIntArray liquidBarrelData) {
        super(RegistryEvents.liquidBarrelContainer.get(), id);

        this.addFluidSlot(new FluidSlot(fluidStack, 0, 20, 20, 16, 40));
        this.liquidBarrelData = liquidBarrelData;
        trackIntArray(liquidBarrelData);
        this.playerinventory = playerinventory;
        layoutPlayerInventorySlots(playerinventory, 8, 84);
    }

    protected FluidSlot addFluidSlot(FluidSlot slotIn) {
        slotIn.slotNumber = this.inventoryFluidSlot.size();
        this.inventoryFluidSlot.add(slotIn);
        return slotIn;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) { return true; }

    private int addSlotRange(IInventory inventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(inventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IInventory inventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(inventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(IInventory inventory, int leftCol, int topRow) {
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }
    public IIntArray getLiquidBarrelData() { return this.liquidBarrelData; }

    public static class FluidSlot {
        public final FluidStack fluidStack;
        public int slotNumber;
        public final int xPos;
        public final int yPos;
        public final int xSize;
        public final int ySize;

        public FluidSlot(FluidStack fluidStack, int index, int xPosition, int yPosition, int xSize, int ySize) {
            this.fluidStack = fluidStack;
            this.slotNumber = index;
            this.xPos = xPosition;
            this.yPos = yPosition;
            this.xSize = xSize;
            this.ySize = ySize;
        }
    }
}
