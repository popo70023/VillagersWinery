package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.drinkable.DrinkableFluid;
import com.benchenssever.villagerswinery.drinkable.Drinks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class WoodenContainerFluidHandler extends FluidHandlerItemStack {
    public WoodenContainerFluidHandler(ItemStack container, int capacity) {
        super(container, capacity);
    }

    @Override
    protected void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
        if (fluid.getFluid() instanceof DrinkableFluid) {
            DrinkableFluid drinkableFluid = (DrinkableFluid) fluid.getFluid();
            Potion potion = drinkableFluid.drinks.getPotion();
            if (potion != null) {
                this.container.getOrCreateTag().putString("Potion", potion.getRegistryName().toString());
                return;
            }
        }
        this.container.removeChildTag("Potion");
    }

    @Override
    protected void setContainerToEmpty() {
        super.setContainerToEmpty();
        this.container.removeChildTag("Potion");
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
        Potion potion = drinks.getPotion();
        CompoundNBT tagCompound = fluidContainer.getOrCreateTag();
        tagCompound.put(FLUID_NBT_KEY, fluidStack.writeToNBT(new CompoundNBT()));
        if (potion != null) {
            tagCompound.putString("Potion", potion.getRegistryName().toString());
        }
    }
}
