package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.drinkable.Drinks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DrinksRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(ForgeRegistries.POTIONS, VillagersWineryMod.MODID);

    public static final Drinks grapeWine = new Drinks.Builder("grape_wine")
            .effects(() -> new EffectInstance[]{
                    new EffectInstance(RegistryEvents.hapiness.get(), 3600)
            })
            .color(0xff9d2ebf)
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks beer = new Drinks.Builder("beer")
            .effects(() -> new EffectInstance[]{
                    new EffectInstance(RegistryEvents.hapiness.get(), 3600)
            })
            .color(0xFF796400)
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks wort = new Drinks.Builder("wort")
            .color(0xFFf5b642)
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks appleJuice = new Drinks.Builder("apple_juice")
            .color(0xFFebd834)
            .group(RegistryEvents.wineryItemGroup)
            .build();


    public static final Drinks cider = new Drinks.Builder("cider")
            .color(0xFFfcf89a)
            .group(RegistryEvents.wineryItemGroup)
            .build();

    public static final Drinks[] drinksCollection = {grapeWine, beer, wort, appleJuice, cider};

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
