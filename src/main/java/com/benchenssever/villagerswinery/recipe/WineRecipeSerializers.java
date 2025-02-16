package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

public class WineRecipeSerializers<T extends WineRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
    private final WineRecipeSerializers.IFactory<T> factory;

    public WineRecipeSerializers(IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public @NotNull T read(@NotNull ResourceLocation recipeId, JsonObject json) {
        FluidStack input = FluidTransferUtil.getFluidStackFromJson(json.getAsJsonObject("input"));
        FluidStack output = FluidTransferUtil.getFluidStackFromJson(json.getAsJsonObject("output"));
        int time = json.get("time").getAsInt();
        return factory.create(recipeId, input, output, time);
    }

    @Override
    public T read(@NotNull ResourceLocation recipeId, PacketBuffer buffer) {
        FluidStack input = buffer.readFluidStack();
        FluidStack output = buffer.readFluidStack();
        int time = buffer.readInt();
        return factory.create(recipeId, input, output, time);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeFluidStack(recipe.getFluidRecipeInput());
        buffer.writeFluidStack(recipe.getFluidRecipeOutput());
        buffer.writeInt(recipe.getSpendTime());
    }

    public interface IFactory<T extends IFluidStackRecipe> {
        T create(ResourceLocation recipeId, FluidStack input, FluidStack output, int spendTime);
    }
}
