package com.benchenssever.villagerswinery.fluid.crafting;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
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

public class WinemakingRecipe implements IRecipeWithFluid<IInventory> {

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
    public boolean matches(FluidStack[] fs, IInventory inv, World worldIn) {
        return this.ingredient.test(fs[0]);
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        NonNullList<FluidIngredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public FluidStack getRecipeFluidOutput(FluidStack[] fs) {
        FluidStack output = new FluidStack(this.result, 0);
        double amount = fs[0].getAmount() / ingredient.amountRequired;
        output.setAmount((int) (this.result.getAmount() * amount));
        return output;
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
        return RegistryEvents.winemakingRecipe.get();
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
