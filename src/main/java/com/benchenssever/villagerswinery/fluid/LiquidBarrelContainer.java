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
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class LiquidBarrelContainer extends Container {

    public final List<FluidSlot> inventoryFluidSlots = Lists.newArrayList();
    private final IIntArray liquidBarrelData;

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, PacketBuffer data) {
        this(id, playerinventory, new FluidInventory(new FluidTank(8000)), new IntArray(3));
    }

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, FluidInventory fluidinventory, IIntArray liquidBarrelData) {
        super(RegistryEvents.liquidBarrelContainer.get(), id);

        this.liquidBarrelData = liquidBarrelData;
        trackIntArray(liquidBarrelData);
        layoutPlayerInventorySlots(playerinventory, 8, 84);
        this.addFluidSlot(new FluidSlot(fluidinventory, 0, 20, 20, 16, 40));
    }

    protected FluidSlot addFluidSlot(FluidSlot slotIn) {
        slotIn.slotNumber = this.inventoryFluidSlots.size();
        this.inventoryFluidSlots.add(slotIn);
        return slotIn;
    }

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

    public IIntArray getLiquidBarrelData() {
        return this.liquidBarrelData;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public static class FluidSlot extends Slot {
        public final int xSize;
        public final int ySize;

        public FluidSlot(FluidInventory fluidInventoryIn, int index, int xPosition, int yPosition, int xSize, int ySize) {
            super(fluidInventoryIn, index, xPosition, yPosition);
            this.xSize = xSize;
            this.ySize = ySize;
        }

        public FluidStack getFluidStack() {
            if (inventory instanceof FluidInventory) {
                return ((FluidInventory) inventory).getFluidStackInSlot(getSlotIndex());
            }
            return FluidStack.EMPTY;
        }

        public void putFluidStack(FluidStack stack) {
            if (inventory instanceof FluidInventory) {
                ((FluidInventory) inventory).setInventoryFluidSlotContents(getSlotIndex(), stack);
                this.onSlotChanged();
            }
        }
    }
}
