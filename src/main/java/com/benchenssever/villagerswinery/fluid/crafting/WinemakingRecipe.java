package com.benchenssever.villagerswinery.fluid.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class WinemakingRecipe implements IRecipeWithFluid<IFluidInventory, IInventory> {

    protected final IRecipeType<?> type = IRecipeType.register("winemaking");
    protected final ResourceLocation id;
    protected final FluidIngredient ingredient;
    protected final FluidStack result;
    protected final int winemakingTime;

    public WinemakingRecipe(ResourceLocation id, FluidIngredient ingredient, FluidStack result, int winemakingTime) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.winemakingTime = winemakingTime;
    }

    @Override
    public boolean matches(IInventory inv, IFluidInventory invF, World worldIn) {
        return ingredient.test(invF.getStackInSlot(0));
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        NonNullList<FluidIngredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override
    public FluidStack getCraftingResult(IFluidInventory invF) {
        return this.result.copy();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public FluidStack getRecipeFluidOutput() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public IRecipeType<?> getType() {
        return this.type;
    }

    public static class Serializer<T extends WinemakingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<WinemakingRecipe> {

        private final Serializer.IFactory<T> factory;
        protected final int winemakingTime;

        public Serializer(IFactory<T> factory, int winemakingTime) {
            this.factory = factory;
            this.winemakingTime = winemakingTime;
        }

        @Override
        public WinemakingRecipe read(ResourceLocation recipeId, JsonObject json) {
            FluidIngredient fluidingredient = FluidIngredient.deserialize(JSONUtils.getJsonObject(json, "material"));
            FluidStack result = FluidCraftingHelper.deserializeFluidStack(JSONUtils.getJsonObject(json, "product"));
            int i = JSONUtils.getInt(json, "winemakingtime", this.winemakingTime);

            return this.factory.create(recipeId, fluidingredient, result, i);
        }

        @Nullable
        @Override
        public WinemakingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            FluidIngredient fluidingredient = FluidIngredient.read(buffer);
            FluidStack result = buffer.readFluidStack();
            int i = buffer.readInt();

            return this.factory.create(recipeId, fluidingredient, result, i);
        }

        @Override
        public void write(PacketBuffer buffer, WinemakingRecipe recipe) {
            recipe.ingredient.write(buffer);
            buffer.writeFluidStack(recipe.result);
            buffer.writeVarInt(recipe.winemakingTime);
        }

        public interface IFactory<T extends WinemakingRecipe> {
            T create(ResourceLocation id, FluidIngredient ingredient, FluidStack result, int winemakingTime);
        }
    }
}
