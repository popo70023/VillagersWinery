package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.drinkable.Drinks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class WoodenContainerFluidHandler extends FluidHandlerItemStack {
    public WoodenContainerFluidHandler(ItemStack container, int capacity) {
        super(container, capacity);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return stack.getFluid().getAttributes().getTemperature() < 500;
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluid) {
        return this.isFluidValid(0, fluid);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return this.isFluidValid(0, fluid);
    }

    public static FluidStack getFluid(ItemStack fluidContainer) {
        CompoundNBT tagCompound = fluidContainer.getTag();
        if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY, Constants.NBT.TAG_COMPOUND)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
    }

    public static void setFluid(ItemStack fluidContainer, Drinks drinks, int capacity) {
        FluidStack fluidStack = new FluidStack(drinks.getFluid(), capacity);
        CompoundNBT tagCompound = fluidContainer.getOrCreateTag();
        tagCompound.put(FLUID_NBT_KEY, fluidStack.writeToNBT(new CompoundNBT()));
    }
}
