package com.benchenssever.villagerswinery.fluid.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipeWithFluid<F extends IFluidInventory,C extends IInventory> extends IRecipe<C> {

    boolean matches(C inv, F invF, World worldIn);

    FluidStack getCraftingResult(F invF);
    FluidStack getRecipeFluidOutput();

    default NonNullList<FluidStack> getRemainingFluids(F invF) {
        NonNullList<FluidStack> nonnulllist = NonNullList.withSize(invF.getSizeInventory(), FluidStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            FluidStack fluid = invF.getStackInSlot(i);
            if (!fluid.isEmpty()) {
                nonnulllist.set(i, fluid);
            }
        }

        return nonnulllist;
    }

    default NonNullList<FluidIngredient> getFluidIngredients() {
        return NonNullList.create();
    }
}