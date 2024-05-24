package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public interface IFluidStackRecipe extends IRecipe<IInventory> {
    boolean matches(FluidStack stack, World world);
    FluidStack getFluidCraftingResult(FluidStack fluidStack);
    FluidStack getFluidRecipeOutput();
    FluidStack getFluidRecipeInput();
    int getSpendTime();

    @Override
    default boolean matches(@Nonnull IInventory inv, @Nonnull World worldIn) { return true; }

    @Nonnull
    @Override
    default ItemStack getCraftingResult(@Nonnull IInventory inv) { return new ItemStack(RegistryEvents.liquidBarrelItem.get()); }

    @Override
    default boolean canFit(int width, int height) { return true; }

    @Nonnull
    @Override
    default ItemStack getRecipeOutput() { return new ItemStack(RegistryEvents.liquidBarrelItem.get()); }
}
