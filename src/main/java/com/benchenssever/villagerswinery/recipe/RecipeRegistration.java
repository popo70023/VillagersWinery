package com.benchenssever.villagerswinery.recipe;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class RecipeRegistration<T extends IRecipe<?>> {
    public final RegistryObject<IRecipeSerializer<T>> serializer;
    public final IRecipeType<T> recipe;

    public RecipeRegistration(String id, Supplier<IRecipeSerializer<T>> serializerSupplier) {
        this.serializer = RegistryEvents.RECIPE_SERIALIZERS.register(id, serializerSupplier);
        this.recipe = IRecipeType.register(id);
    }
}
