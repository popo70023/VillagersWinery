package com.benchenssever.villagerswinery.content.drinkable;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.item.Items.BUCKET;

public class Drinkable {
    public static final ResourceLocation STILL_WATER_TEXTURE = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING_WATER_TEXTURE = new ResourceLocation("block/water_flow");

    private static final List<Drinkable> drinkableCollection = new ArrayList<>();
    public final String id;
    public final int color;
    private final RegistryObject<Item> bucket;
    private final RegistryObject<FlowingFluidBlock> fluidBlock;
    private RegistryObject<FlowingFluid> fluid;
    private RegistryObject<FlowingFluid> flowingFluid;
    private final TranslationTextComponent tooltip;
    private final Food food;
    private final boolean isAlcohol;
    private final boolean isForDrink;

    //TODO: 添加factories模式更換支持，來更換可用的初始化class類別，參考FluidAttributes.Builder
    public Drinkable(Builder builder) {
        this.id = builder.id;
        this.color = builder.color;
        this.food = builder.food;
        this.isAlcohol = builder.isAlcohol;
        this.isForDrink = builder.isForDrink;

        bucket = DrinksRegistry.ITEMS.register(
                builder.id + "_fluid_bucket",
                () -> new DrinkableFluidBucket(
                        fluid,
                        new Item.Properties()
                                .tab(builder.group)
                                .craftRemainder(BUCKET)
                                .stacksTo(1),
                        this
                )
        );

        fluidBlock = DrinksRegistry.BLOCKS.register(
                "fluid_" + builder.id + "_block",
                () -> new DrinkableFluidBlock(
                        fluid,
                        Block.Properties
                                .of(Material.WATER)
                                .noCollission()
                                .strength(100.0F)
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

        tooltip = new TranslationTextComponent("item." + VillagersWineryMod.MODID + "." + id + ".information");

        drinkableCollection.add(this);
    }

    private static ForgeFlowingFluid.Properties drinkProperties(Supplier<FlowingFluid> still, Supplier<FlowingFluid> flowing, int color, Supplier<Item> bucket, Supplier<FlowingFluidBlock> block) {
        return new ForgeFlowingFluid.Properties(
                still,
                flowing,
                FluidAttributes
                        .builder(STILL_WATER_TEXTURE, FLOWING_WATER_TEXTURE)
                        .color(color)
                        .density(4000)
                        .viscosity(4000)
                        .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY))
                .bucket(bucket)
                .block(block)
                .slopeFindDistance(3)
                .explosionResistance(100F);
    }

    public FlowingFluid getFluid() {
        return fluid.get();
    }

    public FlowingFluid getFlowingFluid() {
        return flowingFluid.get();
    }

    public Item getBucket() {
        return bucket.get();
    }

    public FlowingFluidBlock getFluidBlock() {
        return fluidBlock.get();
    }

    public Food getFood() {
        return food;
    }

    public TranslationTextComponent getTooltip() {
        return tooltip;
    }

    public boolean isAlcohol() {
        return isAlcohol;
    }

    public boolean isForDrink() {
        return isForDrink;
    }

    public static List<Drinkable> getDrinkableCollection() {
        return Collections.unmodifiableList(drinkableCollection);
    }

    public static class Builder {
        private final String id;
        private ItemGroup group;
        private int color = 0xFFFFFFFF;
        private Food food;
        private boolean isAlcohol = false;
        private boolean isForDrink = true;

        public Builder(String id) {
            this.id = id;
        }

        public final Builder color(int color) {
            this.color = color;
            return this;
        }

        public final Builder food(Food food) {
            this.food = food;
            return this;
        }

        public final Builder group(ItemGroup group) {
            this.group = group;
            return this;
        }

        public final Builder isAlcohol() {
            isAlcohol = true;
            return this;
        }

        public final Builder notForDrink() {
            isForDrink = false;
            return this;
        }

        public final Drinkable build() {
            return new Drinkable(this);
        }

    }

    public static boolean isCanConsumed(LivingEntity entityLivingBaseIn, IDrinkable drinkable) {
        EffectInstance drunkEffect = entityLivingBaseIn.getEffect(DrinksRegistry.drunk.get());
        Food drinkFood = drinkable.getFood();
        //TODO: 時間限制要記得改回來
        if (drinkFood != null && (drunkEffect == null || drunkEffect.getDuration() > 0)) {
            if (entityLivingBaseIn instanceof PlayerEntity) {
                return ((PlayerEntity) entityLivingBaseIn).getFoodData().needsFood() || drinkFood.canAlwaysEat();
            }
            return !(drinkable.isAlcohol() && entityLivingBaseIn.isBaby());
        }
        return false;
    }

    public static void onDrinkConsumed(LivingEntity entityLivingBaseIn, IDrinkable drinkable) {
        EffectInstance drunkEffect = entityLivingBaseIn.getEffect(DrinksRegistry.drunk.get());
        int time = 0;
        if (drunkEffect != null) {
            time += drunkEffect.getDuration();
        }
        Food drinkFood = drinkable.getFood();
        if (drinkFood == null) return;
        if (entityLivingBaseIn instanceof PlayerEntity) {
            ((PlayerEntity) entityLivingBaseIn).getFoodData().eat(drinkFood.getNutrition(), drinkFood.getSaturationModifier());
        }
        applyDrinkFoodEffects(entityLivingBaseIn, drinkFood, time);
    }

    public static void applyDrinkFoodEffects(LivingEntity entityIn, Food food, int time) {
        World worldIn = entityIn.getCommandSenderWorld();
        if (worldIn.isClientSide) return;
        for (Pair<EffectInstance, Float> pair : food.getEffects()) {
            if (pair.getFirst() != null && worldIn.random.nextFloat() < pair.getSecond()) {
                EffectInstance effectinstance = pair.getFirst();
                if (effectinstance.getEffect() == DrinksRegistry.drunk.get()) {
                    entityIn.addEffect(new EffectInstance(effectinstance.getEffect(), time + effectinstance.getDuration()));
                } else {
                    if (effectinstance.getEffect().isInstantenous()) {
                        effectinstance.getEffect().applyInstantenousEffect(entityIn, null, entityIn, effectinstance.getAmplifier(), 1.0D);
                    } else {
                        entityIn.addEffect(new EffectInstance(effectinstance));
                    }
                }
            }
        }
    }
}



