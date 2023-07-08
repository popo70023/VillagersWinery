package com.benchenssever.villagerswinery.fluid.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipeWithFluid<C extends IInventory> extends IRecipe<C> {

    boolean matches(FluidStack[] fs, IInventory inv, World worldIn);
    @Override
    default boolean matches(IInventory inv, World worldIn) { return false; }

    FluidStack getRecipeFluidOutput(FluidStack[] fs);

    FluidStack getRecipeFluidOutput();

    @Override
    default boolean canFit(int width, int height) { return true; }

    @Override
    default ItemStack getCraftingResult(IInventory inv) { return null; }

    @Override
    default ItemStack getRecipeOutput() { return null; }

    default NonNullList<FluidIngredient> getFluidIngredients() { return NonNullList.create(); }
}