package com.benchenssever.villagerswinery.content.capability;

import com.benchenssever.villagerswinery.content.drinkable.Drinkable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemStackFluidHandler extends FluidHandlerItemStack {
    protected Predicate<FluidStack> validator;

    public ItemStackFluidHandler(ItemStack container, int capacity) {
        super(container, capacity);
        this.validator = fluidStack -> true;
    }

    public ItemStackFluidHandler(ItemStack container, int capacity, Predicate<FluidStack> validator) {
        super(container, capacity);
        this.validator = validator;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return validator.test(stack);
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

    public static void setFluid(ItemStack fluidContainer, Drinkable drinkable, int capacity) {
        FluidStack fluidStack = new FluidStack(drinkable.getFluid(), capacity);
        CompoundNBT tagCompound = fluidContainer.getOrCreateTag();
        tagCompound.put(FLUID_NBT_KEY, fluidStack.writeToNBT(new CompoundNBT()));
    }
}
