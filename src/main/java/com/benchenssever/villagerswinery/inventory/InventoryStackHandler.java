package com.benchenssever.villagerswinery.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class InventoryStackHandler extends ItemStackHandler implements IInventory {
    public InventoryStackHandler(int size) {
        super(size);
    }

    @Override
    public int getSizeInventory() {
        return getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : stacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        markDirty();
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(@NotNull PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
    }
}
