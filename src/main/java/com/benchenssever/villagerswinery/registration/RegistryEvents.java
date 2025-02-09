package com.benchenssever.villagerswinery.registration;

import com.benchenssever.villagerswinery.block.*;
import com.benchenssever.villagerswinery.client.gui.LiquidBarrelScreen;
import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.benchenssever.villagerswinery.item.LiquidBarrelItem;
import com.benchenssever.villagerswinery.recipe.BasinRecipe;
import com.benchenssever.villagerswinery.recipe.BasinRecipeSerializers;
import com.benchenssever.villagerswinery.recipe.WineRecipeSerializers;
import com.benchenssever.villagerswinery.recipe.WineRecipe;
import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import com.benchenssever.villagerswinery.tileentity.LiquidBarrelTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import static com.benchenssever.villagerswinery.VillagersWineryMod.MODID;

public class RegistryEvents {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES  = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS  = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS  = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<Item> liquidBarrelItem = ITEMS.register("liquid_barrel", () -> new LiquidBarrelItem(RegistryEvents.liquidBarrelBlock.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup).maxStackSize(1)));
    public static final RegistryObject<Item> basinItem = ITEMS.register("basin", () -> new BlockItem(RegistryEvents.basinBlock.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> standItem = ITEMS.register("stand", () -> new BlockNamedItem(RegistryEvents.stand.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> grapeVineItem = ITEMS.register("grape_vine", () -> new BlockItem(RegistryEvents.grapeVine.get(), new Item.Properties().group(RegistryEvents.wineryItemGroup)));
    public static final RegistryObject<Item> grape = ITEMS.register("grape",() -> new Item(new Item.Properties().group(RegistryEvents.wineryItemGroup).food(new Food.Builder().fastToEat().saturation(1).hunger(3).build())));


    public static final RegistryObject<Block> liquidBarrelBlock = BLOCKS.register("liquid_barrel", () -> new LiquidBarrel(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> basinBlock = BLOCKS.register("basin", () -> new Basin(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> stand = BLOCKS.register("stand", () -> new Stand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.WOOD).notSolid()));
    public static final RegistryObject<Block> vineStand = BLOCKS.register("vine_stand", () -> new VineStand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.VINE).notSolid(), Items.VINE));
    public static final RegistryObject<Block> grapeVineStand = BLOCKS.register("grape_vine_stand", () -> new CropVineStand(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(2.5F).sound(SoundType.VINE).notSolid(), RegistryEvents.grapeVine, RegistryEvents.grape));
    public static final RegistryObject<Block> grapeVine = BLOCKS.register("grape_vine", () -> new CropVine(AbstractBlock.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.2F).sound(SoundType.VINE).notSolid(), grape));

    public static final RegistryObject<TileEntityType<LiquidBarrelTileEntity>> liquidBarrelTileEntity = TILE_ENTITIES.register("liquid_barrel_tileentity", () -> TileEntityType.Builder.create(LiquidBarrelTileEntity::new, RegistryEvents.liquidBarrelBlock.get()).build(null));
    public static final RegistryObject<TileEntityType<BasinTileEntity>> basinTileEntity = TILE_ENTITIES.register("basin_tileentity", () -> TileEntityType.Builder.create(BasinTileEntity::new, RegistryEvents.basinBlock.get()).build(null));
    public static final RegistryObject<ContainerType<LiquidBarrelContainer>> liquidBarrelContainer = CONTAINERS.register("liquid_barrel_container", () -> IForgeContainerType.create(LiquidBarrelContainer::new));

    public static final RegistryObject<IRecipeSerializer<WineRecipe>> wineRecipeSerializer = RECIPE_SERIALIZERS.register("winerecipe", () -> new WineRecipeSerializers<>(WineRecipe::new));
    public static final RegistryObject<IRecipeSerializer<BasinRecipe>> basinRecipeSerializer = RECIPE_SERIALIZERS.register("basinrecipe", () -> new BasinRecipeSerializers<>(BasinRecipe::new));

    public static final IRecipeType<WineRecipe> wineRecipe = IRecipeType.register("winerecipe");
    public static final IRecipeType<BasinRecipe> basinRecipe = IRecipeType.register("basinrecipe");

    public static final ItemGroup wineryItemGroup = new ItemGroup("villagerswinery") {
        @Override
        public @NotNull ItemStack createIcon() {
            return new ItemStack(DrinksRegistry.emptyWinebowl.get());
        }
    };

    public static void setRegister(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        TILE_ENTITIES.register(eventBus);
        CONTAINERS.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
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