package com.benchenssever.villagerswinery.fluid.crafting;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidCraftingHelper {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static FluidStack deserializeFluidStack(JsonObject json) {
        ResourceLocation id = new ResourceLocation(JSONUtils.getString(json, "fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        if (fluid == null)
            throw new JsonSyntaxException("Unknown fluid '" + id + "'");
        int amount = JSONUtils.getInt(json, "amount");
        FluidStack stack = new FluidStack(fluid, amount);

        if (!json.has("nbt"))
            return stack;

        try {
            JsonElement element = json.get("nbt");
            stack.setTag(JsonToNBT.getTagFromJson(
                    element.isJsonObject() ? GSON.toJson(element) : JSONUtils.getString(element, "nbt")));

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return stack;
    }

}
