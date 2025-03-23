package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class WineRecipe implements IFluidStackRecipe {
    private final ResourceLocation id;
    private final FluidStack input;
    private final FluidStack output;
    private final int time;

    public WineRecipe(ResourceLocation id, FluidStack input, FluidStack output, int time) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.time = time;
    }

    @Override
    public boolean matches(FluidStack stack, World world) {
        return stack.isFluidEqual(input);
    }

    @Override
    public FluidStack getFluidRecipeInput() {
        return input;
    }

    @Override
    public FluidStack getFluidRecipeOutput() {
        return output;
    }

    public int getSpendTime() {
        return time;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return RegistryEvents.wineRecipeSerializer.get();
    }

    @Override
    public @NotNull IRecipeType<?> getType() {
        return RegistryEvents.wineRecipe;
    }

    @Override
    public boolean matches(@NotNull IInventory inv, @NotNull World worldIn) {
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(RegistryEvents.liquidBarrelItem.get());
    }
}
