package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.Wine.WineEffect;
import com.benchenssever.villagerswinery.block.*;
import com.benchenssever.villagerswinery.client.gui.LiquidBarrelScreen;
import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.benchenssever.villagerswinery.item.EmptyWinebowl;
import com.benchenssever.villagerswinery.item.Winebowl;
import com.benchenssever.villagerswinery.fluid.crafting.WinemakingRecipe;
import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import com.benchenssever.villagerswinery.tileentity.LiquidBarrelTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IntArray;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryEvents {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(ForgeRegistries.POTIONS, VillagersWineryMod.MODID);
    public static final DeferredRegister<Potion> POTION = DeferredRegister.create(ForgeRegistries.POTION_TYPES, VillagersWineryMod.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES  = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, VillagersWineryMod.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS  = DeferredRegister.create(ForgeRegistries.CONTAINERS, VillagersWineryMod.MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS  = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VillagersWineryMod.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS  = DeferredRegister.create(ForgeRegistries.PROFESSIONS, VillagersWineryMod.MODID);

    public static final RegistryObject<Item> winebowl = ITEMS.register("wine_bowl", () -> new Winebowl(new Item.Properties().group(RegistryEvents.wineryItemGroup).maxStackSize(1)));
    public static final RegistryObject<Item> emptyWinebowl = ITEMS.register("empty_wine_bowl", () -> new EmptyWinebowl(new Item.Properties().group(RegistryEvents.wineryItemGroup).maxStackSize(16)));

    public static final RegistryObject<Item> liquidBarrelItem = ITEMS.register("liquid_barrel", () -> new BlockItem(RegistryEvents.liquidBarrelBlock.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> basinItem = ITEMS.register("basin", () -> new BlockItem(RegistryEvents.basinBlock.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> standItem = ITEMS.register("stand", () -> new BlockNamedItem(RegistryEvents.stand.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> grapeVineItem = ITEMS.register("grape_vine", () -> new BlockItem(RegistryEvents.grapeVine.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> grape = ITEMS.register("grape",() -> new Item(new Item.Properties().group(RegistryEvents.wineryItemGroup).food(new Food.Builder().fastToEat().saturation(1).hunger(3).build())));


    public static final RegistryObject<Block> liquidBarrelBlock = BLOCKS.register("liquid_barrel", () -> new LiquidBarrel(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> basinBlock = BLOCKS.register("basin", () -> new Basin(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> stand = BLOCKS.register("stand", () -> new Stand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> vineStand = BLOCKS.register("vine_stand", () -> new VineStand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.VINE).notSolid()));
    public static final RegistryObject<Block> grapeVineStand = BLOCKS.register("grape_vine_stand", () -> new GrapeVineStand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.VINE).notSolid()));
    public static final RegistryObject<Block> grapeVine = BLOCKS.register("grape_vine", () -> new GrapeVine(AbstractBlock.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.2F).sound(SoundType.VINE).notSolid()));

    public static final RegistryObject<Effect> drunk = EFFECT.register("drunk", () -> new WineEffect(EffectType.NEUTRAL, 0xFF796400));
    public static final RegistryObject<Effect> hapiness = EFFECT.register("hapiness", () -> new WineEffect(EffectType.BENEFICIAL, 0xFF796400));

//    public static final RegistryObject<Potion> beer = POTION.register("beer", () -> new Wine("beer", fluidBeer.get(), new EffectInstance(RegistryEvents.hapiness.get(), 3600)));

    public static final RegistryObject<TileEntityType<LiquidBarrelTileEntity>> liquidBarrelTileEntity = TILE_ENTITIES.register("liquid_barrel_tileentity", () -> TileEntityType.Builder.create(LiquidBarrelTileEntity::new, RegistryEvents.liquidBarrelBlock.get()).build(null));
    public static final RegistryObject<TileEntityType<BasinTileEntity>> basinTileEntity = TILE_ENTITIES.register("basin_tileentity", () -> TileEntityType.Builder.create(BasinTileEntity::new, RegistryEvents.basinBlock.get()).build(null));
    public static final RegistryObject<ContainerType<LiquidBarrelContainer>> liquidBarrelContainer = CONTAINERS.register("liquid_barrel_container", () -> IForgeContainerType.create(LiquidBarrelContainer::new));

    public static final RegistryObject<IRecipeSerializer<WinemakingRecipe>> winemakingRecipe = RECIPE_SERIALIZERS.register("winemaking_recipe", () -> new WinemakingRecipe.Serializer(WinemakingRecipe::new, 1600));

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
        CONTAINERS.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        PROFESSIONS.register(eventBus);
    }

    public static void setRender(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(RegistryEvents.stand.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(RegistryEvents.vineStand.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(RegistryEvents.grapeVineStand.get(), RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(RegistryEvents.grapeVine.get(), RenderType.getCutout());


            BlockColors blockColors = Minecraft.getInstance().getBlockColors();
            blockColors.register((state, reader, pos, color) -> reader != null && pos != null ? BiomeColors.getFoliageColor(reader, pos) : FoliageColors.getDefault(), RegistryEvents.vineStand.get());

            ScreenManager.registerFactory(RegistryEvents.liquidBarrelContainer.get(), LiquidBarrelScreen::new);
        });
    }
}