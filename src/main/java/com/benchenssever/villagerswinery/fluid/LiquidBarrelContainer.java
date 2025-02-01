package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.fluids.FluidStack;

public class LiquidBarrelContainer extends Container {
    public FluidStack fluidStack;
    public FluidSlot fluidSlot;
    public String worldAndPos;
    private final IIntArray liquidBarrelData;

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, PacketBuffer data) {
        this(id, playerinventory, FluidStack.readFromPacket(data), data.readString(32767), new IntArray(3));
    }

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, FluidStack fluidStack, String worldAndPos, IIntArray liquidBarrelData) {
        super(RegistryEvents.liquidBarrelContainer.get(), id);

        this.worldAndPos = worldAndPos;
        this.fluidStack = fluidStack;
        this.liquidBarrelData = liquidBarrelData;
        trackIntArray(liquidBarrelData);
        this.fluidSlot = new FluidSlot(20, 20, 16, 40);
        layoutPlayerInventorySlots(playerinventory, 8, 84);
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

    public static class FluidSlot {
        public final int xPos;
        public final int yPos;
        public final int xSize;
        public final int ySize;

        public FluidSlot(int xPos, int yPos, int xSize, int ySize) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.xSize = xSize;
            this.ySize = ySize;
        }
    }
}
