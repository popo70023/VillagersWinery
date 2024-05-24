package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

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
    public FluidStack getFluidCraftingResult(FluidStack fluidStack) {
        return output.copy();
    }

    @Override
    public FluidStack getFluidRecipeInput() {
        return input;
    }

    @Override
    public FluidStack getFluidRecipeOutput() {
        return output;
    }

    @Override
    public int getSpendTime() {
        return time;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RegistryEvents.wineRecipeSerializer.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return RegistryEvents.wineRecipe;
    }
}
