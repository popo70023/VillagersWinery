package com.benchenssever.villagerswinery.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FluidInventory extends Inventory {
    private final NonNullList<FluidTank> inventoryFluidContents;

    public FluidInventory(FluidTank... stacksIn) {
        super(stacksIn.length);
        this.inventoryFluidContents = NonNullList.from(new FluidTank(0), stacksIn);
    }

    public FluidStack getFluidStackInSlot(int index) {
        return index >= 0 && index < this.inventoryFluidContents.size() ? this.inventoryFluidContents.get(index).getFluid() : FluidStack.EMPTY;
    }

    public void setInventoryFluidSlotContents(int index, FluidStack stack) {
        this.inventoryFluidContents.get(index).setFluid(stack);

        this.markDirty();
    }

    public FluidStack removeFluidStackFromSlot(int index) {
        FluidStack fluidstack = this.inventoryFluidContents.get(index).getFluid();
        if (fluidstack.isEmpty()) {
            return FluidStack.EMPTY;
        } else {
            this.inventoryFluidContents.get(index).setFluid(FluidStack.EMPTY);
            return fluidstack;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) {
        return FluidTransferUtil.isInteractableWithFluidStack(stack);
    }

    public boolean hasAnyFluid(Set<Fluid> set) {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            FluidStack fluidstack = this.getFluidStackInSlot(i);
            if (set.contains(fluidstack.getFluid()) && fluidstack.getAmount() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        super.clear();
        this.inventoryFluidContents.clear();
    }
}
