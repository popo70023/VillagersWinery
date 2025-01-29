package com.benchenssever.villagerswinery.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidStackRecipe extends IRecipe<IInventory> {
    boolean matches(FluidStack stack, World world);

    FluidStack getFluidRecipeOutput();

    FluidStack getFluidRecipeInput();
}
