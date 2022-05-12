package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.Wine.Wine;
import com.benchenssever.villagerswinery.Wine.WineEffect;
import com.benchenssever.villagerswinery.block.*;
import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.item.EmptyWinebowl;
import com.benchenssever.villagerswinery.item.Winebowl;
import com.benchenssever.villagerswinery.tileentity.LiquidBarrelTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.item.Items.BUCKET;

public class RegistryEvents {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(ForgeRegistries.POTIONS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Potion> POTION = DeferredRegister.create(ForgeRegistries.POTION_TYPES, VillagersWineryMod.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES  = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, VillagersWineryMod.MODID);

    public static final RegistryObject<Item> winebowl = ITEMS.register("wine_bowl", () -> new Winebowl(new Item.Properties().group(RegistryEvents.wineryItemGroup).maxStackSize(1)));
    public static final RegistryObject<Item> emptyWinebowl = ITEMS.register("empty_wine_bowl", () -> new EmptyWinebowl(new Item.Properties().group(RegistryEvents.wineryItemGroup).maxStackSize(16)));

    public static final RegistryObject<Item> liquidBarrelItem = ITEMS.register("liquid_barrel", () -> new BlockItem(RegistryEvents.liquidBarrelBlock.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> mixingBowlItem = ITEMS.register("mixing_bowl", () -> new BlockItem(RegistryEvents.mixingBowlBlock.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> standItem = ITEMS.register("stand", () -> new BlockNamedItem(RegistryEvents.stand.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> grapeVineItem = ITEMS.register("grapevine", () -> new BlockItem(RegistryEvents.grapeVine.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> grape = ITEMS.register("grape",() -> new Item(new Item.Properties().group(RegistryEvents.wineryItemGroup).food(new Food.Builder().fastToEat().saturation(1).hunger(3).build())));


    public static final RegistryObject<Block> liquidBarrelBlock = BLOCKS.register("liquid_barrel", () -> new LiquidBarrel(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> mixingBowlBlock = BLOCKS.register("mixing_bowl", () -> new MixingBowl(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> stand = BLOCKS.register("stand", () -> new Stand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> vineStand = BLOCKS.register("vinestand", () -> new VineStand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.VINE).notSolid()));
    public static final RegistryObject<Block> grapeVineStand = BLOCKS.register("grapevinestand", () -> new GrapeVineStand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.VINE).notSolid()));
    public static final RegistryObject<Block> grapeVine = BLOCKS.register("grapevine", () -> new GrapeVine(AbstractBlock.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.2F).sound(SoundType.VINE).notSolid()));

    public static final RegistryObject<Effect> drunk = EFFECT.register("drunk", () -> new WineEffect(EffectType.NEUTRAL, 0xFF796400));
    public static final RegistryObject<Effect> hapiness = EFFECT.register("hapiness", () -> new WineEffect(EffectType.BENEFICIAL, 0xFF796400));

//    public static final RegistryObject<Potion> beer = POTION.register("beer", () -> new Wine("beer", fluidBeer.get(), new EffectInstance(RegistryEvents.hapiness.get(), 3600)));

    public static final RegistryObject<TileEntityType<LiquidBarrelTileEntity>> liquidBarrelTileEntity = TILE_ENTITIES.register("liquid_barrel_tileentity", () -> TileEntityType.Builder.create(LiquidBarrelTileEntity::new, RegistryEvents.liquidBarrelBlock.get()).build(null));

    public static ItemGroup wineryItemGroup = new ItemGroup("villagerswinery") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryEvents.emptyWinebowl.get());
        }
    };

    public static void setRegister(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        FLUIDS.register(eventBus);
        EFFECT.register(eventBus);
        POTION.register(eventBus);
        TILE_ENTITIES.register(eventBus);
    }

    public static void setRender(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
//            RenderTypeLookup.setRenderLayer(RegistryEvents.fluidBeer.get(), RenderType.getTranslucent());
//            RenderTypeLookup.setRenderLayer(RegistryEvents.fluidBeerFlowing.get(), RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(RegistryEvents.stand.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(RegistryEvents.vineStand.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(RegistryEvents.grapeVineStand.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(RegistryEvents.grapeVine.get(), RenderType.getCutout());
        });
    }
}