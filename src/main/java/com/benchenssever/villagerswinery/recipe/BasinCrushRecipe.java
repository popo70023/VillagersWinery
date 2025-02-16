package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class BasinCrushRecipe implements IFluidStackRecipe {
    protected final ResourceLocation id;
    protected final Ingredient input;
    protected final FluidStack output;
    protected final int crushtime;

    public BasinCrushRecipe(ResourceLocation id, Ingredient input, FluidStack output, int crushtime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.crushtime = crushtime;
    }

    @Override
    public boolean matches(FluidStack stack, World world) {
        return true;
    }

    @Override
    public FluidStack getFluidRecipeInput() {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack getFluidRecipeOutput() {
        return output;
    }

    public int getCrushTime() {
        return crushtime;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return RegistryEvents.basinCrushRecipeSerializer.get();
    }

    @Override
    public @NotNull IRecipeType<?> getType() {
        return RegistryEvents.basinCrushRecipe;
    }

    @Override
    public boolean matches(IInventory inv, @NotNull World worldIn) {
        return input.test(inv.getStackInSlot(0));
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getIcon() {
        return new ItemStack(RegistryEvents.basinItem.get());
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(input);
        return nonnulllist;
    }
}
