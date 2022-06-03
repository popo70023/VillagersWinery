package com.benchenssever.villagerswinery.fluid.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IClearable;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public interface IFluidInventory extends IClearable {
    int getSizeInventory();

    boolean isEmpty();

    FluidStack getStackInSlot(int index);

    FluidStack decrStackSize(int index, int count);

    FluidStack removeStackFromSlot(int index);

    void setInventorySlotContents(int index, FluidStack stack);

    int getInventoryStackLimit();

    void markDirty();

    boolean isUsableByPlayer(PlayerEntity player);

    default void openInventory(PlayerEntity player) {}

    default void closeInventory(PlayerEntity player) {}

    default boolean isFluidValidForSlot(int index, FluidStack stack) { return true; }

    default int count(Fluid fluidIn) {
        int i = 0;

        for(int j = 0; j < this.getSizeInventory(); ++j) {
            FluidStack fluidstack = this.getStackInSlot(j);
            if (fluidstack.getFluid().equals(fluidIn)) {
                i += fluidstack.getAmount();
            }
        }

        return i;
    }

    default boolean hasAny(Set<Fluid> set) {
        for(int i = 0; i < this.getSizeInventory(); ++i) {
            FluidStack fluidstack = this.getStackInSlot(i);
            if (set.contains(fluidstack.getFluid()) && fluidstack.getAmount() > 0) {
                return true;
            }
        }
        return false;
    }
}