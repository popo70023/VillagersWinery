package com.benchenssever.villagerswinery.item;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.drinkable.Drinks;
import com.benchenssever.villagerswinery.drinkable.IDrinkable;
import com.benchenssever.villagerswinery.fluid.WoodenContainerFluidHandler;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Winebowl extends Item {
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME / 4;

    public Winebowl(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull World worldIn, @NotNull LivingEntity entityLiving) {
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) playerentity, stack);
        }
        FluidStack stackFluid = WoodenContainerFluidHandler.getFluid(stack);

        if (playerentity != null && stackFluid.getFluid() instanceof IDrinkable && stackFluid.getAmount() >= DEFAULT_CAPACITY) {
            if (!worldIn.isClientSide) {
                Drinks.onDrinkConsumed(playerentity, (IDrinkable) stackFluid.getFluid());
            }
            playerentity.awardStat(Stats.ITEM_USED.get(this));
            if (!playerentity.abilities.instabuild) {
                stack = new ItemStack(DrinksRegistry.emptyWinebowl.get());
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        Fluid stackFluid = WoodenContainerFluidHandler.getFluid(stack).getFluid();
        if (stackFluid instanceof IDrinkable) {
            IDrinkable drink = (IDrinkable) stackFluid;
            if (drink.getFood() != null) {
                return drink.getFood().isFastFood() ? 16 : 32;
            }
        }
        return 0;
    }

    @Override
    public @NotNull UseAction getUseAnimation(@NotNull ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(@NotNull World worldIn, PlayerEntity playerIn, @NotNull Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        FluidStack stackFluid = WoodenContainerFluidHandler.getFluid(stack);

        if (stackFluid.getFluid() instanceof IDrinkable && stackFluid.getAmount() >= DEFAULT_CAPACITY && Drinks.isCanConsumed(playerIn, (IDrinkable) stackFluid.getFluid())) {
            return DrinkHelper.useDrink(worldIn, playerIn, handIn);
        }
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Override
    public @NotNull ITextComponent getName(@NotNull ItemStack stack) {
        FluidStack fluidStack = WoodenContainerFluidHandler.getFluid(stack);
        if (!fluidStack.isEmpty()) {
            return new TranslationTextComponent("item." + VillagersWineryMod.MODID + ".winebowl", new TranslationTextComponent(fluidStack.getTranslationKey()));
        } else {
            return new TranslationTextComponent("item." + VillagersWineryMod.MODID + ".empty_winebowl");
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        FluidStack fluidStack = WoodenContainerFluidHandler.getFluid(stack);
        if (!fluidStack.isEmpty()) {
            tooltip.add(new TranslationTextComponent("item." + VillagersWineryMod.MODID + ".winebowl.information", new StringTextComponent(Integer.toString(fluidStack.getAmount()))));

            if (fluidStack.getFluid() instanceof IDrinkable) {
                tooltip.add(((IDrinkable) fluidStack.getFluid()).getTooltip());
            }
        }
    }


    @Override
    public void fillItemCategory(@NotNull ItemGroup group, @NotNull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            if (this == DrinksRegistry.winebowl.get()) {
                for (Drinks drink : DrinksRegistry.drinksCollection) {
                    ItemStack stack = new ItemStack(this);
                    WoodenContainerFluidHandler.setFluid(stack, drink, DEFAULT_CAPACITY);
                    items.add(stack);
                }
            } else {
                ItemStack stack = new ItemStack(this);
                items.add(stack);
            }
        }
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, @NotNull PlayerEntity playerIn, LivingEntity entity, @NotNull Hand hand) {
        if (entity.level.isClientSide) return ActionResultType.PASS;
        if (entity instanceof VillagerEntity) {
            FluidStack fluidInside = WoodenContainerFluidHandler.getFluid(stack);
            if (fluidInside.getFluid() instanceof IDrinkable && fluidInside.getAmount() >= DEFAULT_CAPACITY && Drinks.isCanConsumed(entity, (IDrinkable) fluidInside.getFluid())) {
                Drinks.onDrinkConsumed(entity, (IDrinkable) fluidInside.getFluid());
                playerIn.awardStat(Stats.ITEM_USED.get(this));
                if (!playerIn.abilities.instabuild) {
                    playerIn.setItemInHand(hand, new ItemStack(DrinksRegistry.emptyWinebowl.get()));
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        if (stack.getItem() == DrinksRegistry.winebowl.get()) {
            return new WoodenContainerFluidHandler(stack, DEFAULT_CAPACITY) {
                @Override
                protected void setContainerToEmpty() {
                    super.setContainerToEmpty();
                    container = new ItemStack(DrinksRegistry.emptyWinebowl.get());
                }
            };
        } else {
            return new WoodenContainerFluidHandler(new ItemStack(DrinksRegistry.winebowl.get()), DEFAULT_CAPACITY);
        }
    }
}
