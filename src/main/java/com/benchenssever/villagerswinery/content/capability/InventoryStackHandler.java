package com.benchenssever.villagerswinery.content.capability;

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
    public int getContainerSize() {
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
    public @NotNull ItemStack getItem(int pIndex) {
        return getStackInSlot(pIndex);
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        setChanged();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return true;
    }

    @Override
    public void clearContent() {
    }
}
