package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BasinRecipeSerializers<T extends BasinRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
    private final BasinRecipeSerializers.IFactory<T> factory;

    public BasinRecipeSerializers(BasinRecipeSerializers.IFactory<T> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public T read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
        FluidStack output = FluidTransferUtil.getFluidStackFromJson(json.getAsJsonObject("output"));
        int time = json.get("time").getAsInt();
        return this.factory.create(recipeId, input, output, time);
    }

    @Nullable
    @Override
    public T read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
        Ingredient input = Ingredient.read(buffer);
        FluidStack output = buffer.readFluidStack();
        int time = buffer.readInt();
        return this.factory.create(recipeId, input, output, time);
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer, T recipe) {
        recipe.input.write(buffer);
        buffer.writeFluidStack(recipe.getFluidRecipeOutput());
        buffer.writeInt(recipe.getSpendTime());
    }

    public interface IFactory<T extends IFluidStackRecipe> {
        T create(ResourceLocation recipeId, Ingredient input, FluidStack output, int spendTime);
    }
}
