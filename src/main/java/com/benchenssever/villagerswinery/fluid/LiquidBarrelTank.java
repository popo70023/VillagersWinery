package com.benchenssever.villagerswinery.fluid;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class LiquidBarrelTank extends FluidTank {

    private boolean changed = false;

    public LiquidBarrelTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    @Override
    public FluidTank readFromNBT(CompoundNBT nbt) {
        if (nbt != null && nbt.contains(FLUID_NBT_KEY)) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound(FLUID_NBT_KEY));
            setFluid(fluid);
        }
        return this;
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        CompoundNBT fluidTag = new CompoundNBT();
        fluid.writeToNBT(fluidTag);
        return (CompoundNBT) nbt.put(FLUID_NBT_KEY, fluidTag);
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    public void resetChanged() {
        this.changed = false;
    }

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        onContentsChanged();
    }
}
