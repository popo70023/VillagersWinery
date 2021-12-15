package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.Wine.Wine;
import com.benchenssever.villagerswinery.item.EmptyWinebowl;
import com.benchenssever.villagerswinery.item.Winebowl;
import com.benchenssever.villagerswinery.Wine.WineEffect;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    public static Item winebowl;
    public static Item emptyWinebowl;

    public static Effect drunk;
    public static Effect hapiness;

    public static Map<String, Potion> Wines = new HashMap<>();
    public static Potion beer;

    public static ItemGroup wineryItemGroup = new ItemGroup("villagers.winery") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(emptyWinebowl);
        }
    };

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
        // register a new item here
        winebowl = new Winebowl(new Item.Properties().group(wineryItemGroup).maxStackSize(1)).setRegistryName(VillagersWineryMod.MODID, "wine_bowl");
        emptyWinebowl = new EmptyWinebowl(new Item.Properties().group(wineryItemGroup).maxStackSize(16)).setRegistryName(VillagersWineryMod.MODID, "empty_wine_bowl");

        IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
        registry.registerAll(winebowl, emptyWinebowl);
    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
        // register a new block here

        IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();
    }

    @SubscribeEvent
    public static void onFluidRegistry(final RegistryEvent.Register<Fluid> fluidRegistryEvent) {
        // register a new fluid here

        IForgeRegistry<Fluid> registry = fluidRegistryEvent.getRegistry();
    }

    @SubscribeEvent
    public static void onEffectsRegistry(final RegistryEvent.Register<Effect> effectRegistryEvent) {
        // register a new effect here

        drunk = new WineEffect(EffectType.NEUTRAL, 0x796400).setRegistryName(VillagersWineryMod.MODID, "drunk");
        hapiness = new WineEffect(EffectType.BENEFICIAL, 0x796400).setRegistryName(VillagersWineryMod.MODID, "hapiness");

        IForgeRegistry<Effect> registry = effectRegistryEvent.getRegistry();
        registry.registerAll(drunk, hapiness);
    }

    @SubscribeEvent
    public static void onPotionRegistry(final RegistryEvent.Register<Potion> potionRegistryEvent) {
        // register a new potion here

        beer = new Wine("beer", new EffectInstance(hapiness.getEffect(), 3600)).setRegistryName("beer");

        Wines.put("beer", beer);

        IForgeRegistry<Potion> registry = potionRegistryEvent.getRegistry();
        registry.registerAll(beer);
    }
}