package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class LiquidBarrelContainer extends Container {

    private final FluidTank tank;
    private final IIntArray liquidBarrelData;

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, PacketBuffer data) {
        this(id, playerinventory, new FluidTank(8000), new IntArray(3));
    }

    public LiquidBarrelContainer(int id, PlayerInventory playerinventory, FluidTank tank, IIntArray liquidBarrelData) {
        super(RegistryEvents.liquidBarrelContainer.get(), id);

        this.tank = tank;
        this.liquidBarrelData = liquidBarrelData;
        trackIntArray(liquidBarrelData);
        layoutPlayerInventorySlots(playerinventory, 8, 84);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return ItemStack.EMPTY;
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

    public IIntArray getLiquidBarrelData() { return liquidBarrelData; }

    public FluidTank getTank() { return tank; }
}
