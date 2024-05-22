package com.benchenssever.villagerswinery.drinkable;

import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

import static net.minecraft.item.Items.BUCKET;

public class Drinks {


    public static final ResourceLocation STILL_WATER_TEXTURE = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING_WATER_TEXTURE = new ResourceLocation("block/water_flow");

    private static ForgeFlowingFluid.Properties drinkProperties(Supplier<FlowingFluid> still, Supplier<FlowingFluid> flowing, int color, Supplier<Item> bucket, Supplier<FlowingFluidBlock> block) {
        return new ForgeFlowingFluid.Properties(
                still,
                flowing,
                FluidAttributes
                        .builder(STILL_WATER_TEXTURE, FLOWING_WATER_TEXTURE)
                        .color(color)
                        .density(4000)
                        .viscosity(4000)
                        .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY))
                .bucket(bucket)
                .block(block)
                .slopeFindDistance(3)
                .explosionResistance(100F);
    }

    private RegistryObject<Item> bucket;
    private RegistryObject<FlowingFluidBlock> fluidBlock;
    private RegistryObject<FlowingFluid> fluid;
    private RegistryObject<FlowingFluid> flowingFluid;

    public final RegistryObject<Potion> potion;
    public final String id;
    public final int color;

    //TODO: 添加factories模式更換支持，來更換可用的初始化class類別，參考FluidAttributes.Builder
    public Drinks(Builder builder) {
        this.id = builder.id;
        this.color = builder.color;
        this.potion = builder.potion;


        bucket = DrinksRegistry.ITEMS.register(
                builder.id + "_fluid_bucket",
                () -> new DrinkableFluidBucket(
                        fluid,
                        new Item
                                .Properties()
                                .group(builder.group)
                                .containerItem(BUCKET),
                        this
                )
        );

        fluidBlock = DrinksRegistry.BLOCKS.register(
                "fluid_" + builder.id + "_block",
                () -> new DrinkableFluidBlock(
                        fluid,
                        Block.Properties
                                .create(Material.WATER)
                                .doesNotBlockMovement()
                                .hardnessAndResistance(100.0F)
                                .noDrops(),
                        this
                )
        );

        fluid = DrinksRegistry.FLUIDS.register(
                "fluid_" + builder.id,
                () -> new DrinkableFluid.Source(drinkProperties(fluid, flowingFluid, builder.color, bucket, fluidBlock), this)
        );

        flowingFluid = DrinksRegistry.FLUIDS.register(
                "fluid_" + builder.id + "_flowing",
                () -> new DrinkableFluid.Flowing(drinkProperties(fluid, flowingFluid, builder.color, bucket, fluidBlock), this)
        );

    }

    public FlowingFluid getFluid(){
        return fluid.get();
    }

    public FlowingFluid getFlowingFluid(){
        return flowingFluid.get();
    }

    public Item getBucket(){
        return bucket.get();
    }

    public FlowingFluidBlock getFluidBlock(){
        return fluidBlock.get();
    }


    public static class Builder {
        private final String id;
        private int color = 0xFFFFFFFF;
        private RegistryObject<Potion> potion;

        ItemGroup group;

        public Builder(String id) {
            this.id = id;
        }

        public final Builder color(int color) {
            this.color = color;
            return this;
        }

        public final Builder effects(RegistryObject<Potion> potion) {
            this.potion = potion;
            return this;
        }

        public final Builder group(ItemGroup group) {
            this.group = group;
            return this;
        }

        public final Drinks build() {
            return new Drinks(this);
        }

    }
}



