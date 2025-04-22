package com.benchenssever.villagerswinery.content.capability;

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

    public static FluidStack getFluidStackFromNBT(ItemStack fluidContainer) {
        CompoundNBT tagCompound = fluidContainer.getTag();
        if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY, Constants.NBT.TAG_COMPOUND)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
    }

    public static void setFluidStackToNBT(ItemStack fluidContainer, FluidStack fluidStack) {
        CompoundNBT tagCompound = fluidContainer.getOrCreateTag();
        tagCompound.put(FLUID_NBT_KEY, fluidStack.writeToNBT(new CompoundNBT()));
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

    public static class SwapEmpty extends ItemStackFluidHandler {
        private final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity, Predicate<FluidStack> validator) {
            super(container, capacity, validator);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            container = emptyContainer;
        }
    }
}
