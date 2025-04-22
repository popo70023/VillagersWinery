package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.content.capability.FluidUtils;
import com.benchenssever.villagerswinery.content.drinkable.Drinkable;
import com.benchenssever.villagerswinery.content.drinkable.WineEffect;
import com.benchenssever.villagerswinery.content.equipment.Equipments;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DrinksRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(ForgeRegistries.POTIONS, VillagersWineryMod.MODID);

    public static final RegistryObject<Effect> drunk = EFFECT.register("drunk", () -> new WineEffect(EffectType.NEUTRAL, 0xFF796400, false));
    public static final RegistryObject<Effect> addFoodLevel = EFFECT.register("add_food_level", () -> new WineEffect.Villager(EffectType.BENEFICIAL, 0xFF796400, true));
    public static final Drinkable beer = new Drinkable.Builder("beer")
            .color(0xFF796400)
            .food(new Food.Builder()
                    .nutrition(1)
                    .saturationMod(2.0f)
                    .effect(() -> new EffectInstance(drunk.get(), 3600), 1.0f)
                    .effect(() -> new EffectInstance(addFoodLevel.get()), 1.0f)
                    .alwaysEat()
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .isAlcohol()
            .build();
    public static final RegistryObject<Effect> getIMerchantXp = EFFECT.register("get_merchant_xp", () -> new WineEffect.Villager(EffectType.BENEFICIAL, 0xFF796400, true));
    public static final Drinkable grapeWine = new Drinkable.Builder("grape_wine")
            .color(0xff9d2ebf)
            .food(new Food.Builder()
                    .saturationMod(0.3f)
                    .effect(() -> new EffectInstance(drunk.get(), 3600), 1.0f)
                    .effect(() -> new EffectInstance(getIMerchantXp.get()), 1.0f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .isAlcohol()
            .build();
    public static final RegistryObject<Effect> refreshOffers = EFFECT.register("refresh_offers", () -> new WineEffect.Villager(EffectType.BENEFICIAL, 0xFF796400, true));
    public static final Drinkable cider = new Drinkable.Builder("cider")
            .color(0xFFfcf89a)
            .food(new Food.Builder()
                    .saturationMod(0.3f)
                    .effect(() -> new EffectInstance(drunk.get(), 3600), 1.0f)
                    .effect(() -> new EffectInstance(refreshOffers.get()), 1.0f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .isAlcohol()
            .build();
    public static final Equipments.Drinkware winebowl = new Equipments.Drinkware("winebowl", FluidUtils.WINEBOWL_DEFAULT_CAPACITY, FluidUtils.WOODEN_CONTAINER_VALIDATOR, 16);
    public static final Drinkable wort = new Drinkable.Builder("wort")
            .color(0xFFf5b642)
            .food(new Food.Builder()
                    .nutrition(3)
                    .saturationMod(3.6f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .notForDrink()
            .build();
    public static final Drinkable grapeJuice = new Drinkable.Builder("grape_juice")
            .color(0xffc34ac0)
            .food(new Food.Builder()
                    .nutrition(2)
                    .saturationMod(0.6f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();
    public static final Drinkable appleJuice = new Drinkable.Builder("apple_juice")
            .color(0xFFebd834)
            .food(new Food.Builder().nutrition(3)
                    .saturationMod(1.44f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static void setRegister(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        FLUIDS.register(eventBus);
        EFFECT.register(eventBus);
    }

    public static void setRender(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Drinkable drinkable : Drinkable.getDrinkableCollection()) {
                RenderTypeLookup.setRenderLayer(drinkable.getFluid(), RenderType.translucent());
                RenderTypeLookup.setRenderLayer(drinkable.getFlowingFluid(), RenderType.translucent());
            }
        });
    }
}
