package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.Wine.Wine;
import com.benchenssever.villagerswinery.item.EmptyWineBowl;
import com.benchenssever.villagerswinery.item.WineBowl;
import com.benchenssever.villagerswinery.Wine.WineEffect;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    public static Item wineBowl;
    public static Item emptyWineBowl;

    public static Effect drunk;

    public static ItemGroup wineryItemGroup = new ItemGroup("villagers.winery") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(emptyWineBowl);
        }
    };

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
        // register a new item here
        wineBowl = new WineBowl(new Item.Properties().tab(wineryItemGroup).stacksTo(1)).setRegistryName(VillagersWineryMod.MODID, "wine_bowl");
        emptyWineBowl = new EmptyWineBowl(new Item.Properties().tab(wineryItemGroup).stacksTo(16)).setRegistryName(VillagersWineryMod.MODID, "empty_wine_bowl");

        IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
        registry.registerAll(wineBowl, emptyWineBowl);
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

        IForgeRegistry<Effect> registry = effectRegistryEvent.getRegistry();
    }

    @SubscribeEvent
    public static void onPotionRegistry(final RegistryEvent.Register<Potion> potionRegistryEvent) {
        // register a new potion here

        IForgeRegistry<Potion> registry = potionRegistryEvent.getRegistry();
    }
}