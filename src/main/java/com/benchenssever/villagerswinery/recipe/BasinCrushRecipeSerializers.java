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
import org.jetbrains.annotations.NotNull;

public class BasinCrushRecipeSerializers<T extends BasinCrushRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
    private final BasinCrushRecipeSerializers.IFactory<T> factory;

    public BasinCrushRecipeSerializers(BasinCrushRecipeSerializers.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public @NotNull T read(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
        Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
        FluidStack output = FluidTransferUtil.getFluidStackFromJson(json.getAsJsonObject("output"));
        int crushTime = json.get("crushtime").getAsInt();
        return factory.create(recipeId, input, output, crushTime);
    }

    @Override
    public T read(@NotNull ResourceLocation recipeId, @NotNull PacketBuffer buffer) {
        Ingredient input = Ingredient.read(buffer);
        FluidStack output = buffer.readFluidStack();
        int crushTime = buffer.readInt();
        return factory.create(recipeId, input, output, crushTime);
    }

    @Override
    public void write(@NotNull PacketBuffer buffer, T recipe) {
        recipe.input.write(buffer);
        buffer.writeFluidStack(recipe.getFluidRecipeOutput());
        buffer.writeInt(recipe.getCrushTime());
    }

    public interface IFactory<T extends IFluidStackRecipe> {
        T create(ResourceLocation recipeId, Ingredient input, FluidStack output, int spendTime);
    }
}
