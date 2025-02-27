package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.drinkable.Drinks;
import com.benchenssever.villagerswinery.drinkable.WineEffect;
import com.benchenssever.villagerswinery.item.Winebowl;
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

    public static final RegistryObject<Effect> drunk = EFFECT.register("drunk", () -> new WineEffect(EffectType.NEUTRAL, 0xFF796400));
    public static final RegistryObject<Effect> getIMerchantXp = EFFECT.register("get_merchant_xp", () -> new WineEffect(EffectType.BENEFICIAL, 0xFF796400, true));

    public static final RegistryObject<Item> emptyWinebowl = ITEMS.register("empty_winebowl", () -> new Winebowl(new Item.Properties().group(RegistryEvents.wineryItemGroup).maxStackSize(16)));
    public static final RegistryObject<Item> winebowl = ITEMS.register("winebowl", () -> new Winebowl(new Item.Properties().group(RegistryEvents.wineryItemGroup).maxStackSize(1)));

    public static final Drinks wort = new Drinks.Builder("wort")
            .color(0xFFf5b642)
            .food(new Food.Builder()
                    .hunger(3)
                    .saturation(3.6f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks beer = new Drinks.Builder("beer")
            .color(0xFF796400)
            .food(new Food.Builder()
                    .hunger(1)
                    .saturation(2.0f)
                    .effect(() -> new EffectInstance(drunk.get(), 3600), 1.0f)
                    .effect(() -> new EffectInstance(getIMerchantXp.get()), 1.0f)
                    .setAlwaysEdible()
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks grapeJuice = new Drinks.Builder("grape_juice")
            .color(0xffc34ac0)
            .food(new Food.Builder()
                    .hunger(2)
                    .saturation(0.6f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks grapeWine = new Drinks.Builder("grape_wine")
            .color(0xff9d2ebf)
            .food(new Food.Builder()
                    .saturation(0.3f)
                    .effect(() -> new EffectInstance(drunk.get(), 3600), 1.0f)
                    .effect(() -> new EffectInstance(getIMerchantXp.get()), 1.0f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks appleJuice = new Drinks.Builder("apple_juice")
            .color(0xFFebd834)
            .food(new Food.Builder().hunger(3)
                    .saturation(1.44f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();


    public static final Drinks cider = new Drinks.Builder("cider")
            .color(0xFFfcf89a)
            .food(new Food.Builder()
                    .saturation(0.3f)
                    .effect(() -> new EffectInstance(drunk.get(), 3600), 1.0f)
                    .build())
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks[] drinksCollection = {wort, beer, grapeJuice, grapeWine, appleJuice, cider};

    public static void setRegister(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        FLUIDS.register(eventBus);
        EFFECT.register(eventBus);
    }

    public static void setRender(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Drinks drinks : drinksCollection) {
                RenderTypeLookup.setRenderLayer(drinks.getFluid(), RenderType.getTranslucent());
                RenderTypeLookup.setRenderLayer(drinks.getFlowingFluid(), RenderType.getTranslucent());
            }
        });
    }
}
