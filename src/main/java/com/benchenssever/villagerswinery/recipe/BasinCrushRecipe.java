package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.content.capability.FluidUtils;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasinCrushRecipe implements IFluidStackRecipe {
    protected final ResourceLocation id;
    protected final Ingredient input;
    protected final FluidStack output;
    protected final int crushTime;

    public BasinCrushRecipe(ResourceLocation id, Ingredient input, FluidStack output, int crushTime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.crushTime = crushTime;
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
        return crushTime;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return RegistryEvents.basinCrushRecipe.serializer.get();
    }

    @Override
    public @NotNull IRecipeType<?> getType() {
        return RegistryEvents.basinCrushRecipe.recipe;
    }

    @Override
    public boolean matches(IInventory inv, @NotNull World worldIn) {
        return input.test(inv.getItem(0));
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
        return new ItemStack(RegistryEvents.basinItem.get());
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(input);
        return nonnulllist;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BasinCrushRecipe> {

        @Override
        public @NotNull BasinCrushRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            Ingredient input = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "ingredient"));
            FluidStack output = FluidUtils.getFluidStackFromJson(json.getAsJsonObject("output"));
            int crushTime = json.get("crushtime").getAsInt();
            return new BasinCrushRecipe(recipeId, input, output, crushTime);
        }

        @Override
        public @Nullable BasinCrushRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull PacketBuffer buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            FluidStack output = buffer.readFluidStack();
            int crushTime = buffer.readInt();
            return new BasinCrushRecipe(recipeId, input, output, crushTime);
        }

        @Override
        public void toNetwork(@NotNull PacketBuffer buffer, BasinCrushRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeFluidStack(recipe.getFluidRecipeOutput());
            buffer.writeInt(recipe.getCrushTime());

        }
    }
}
