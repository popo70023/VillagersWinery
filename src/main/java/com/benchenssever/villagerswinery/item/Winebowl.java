package com.benchenssever.villagerswinery.item;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.drinkable.Drinks;
import com.benchenssever.villagerswinery.fluid.WinebowlFluidHandler;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Winebowl extends Item {
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME / 4;
    public final RegistryObject<Item> emptyBowl;

    public Winebowl(Properties properties, RegistryObject<Item> emptyBowl) {
        super(properties);
        this.emptyBowl = emptyBowl;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), Potions.WATER);
    }

    @Override
    public @NotNull ItemStack onItemUseFinish(@NotNull ItemStack stack, @NotNull World worldIn, @NotNull LivingEntity entityLiving) {
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
        WinebowlFluidHandler winebowl = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) playerentity, stack);
        }

        if (!worldIn.isRemote) {
            Drinks.addDrunkEffectsToEntity(entityLiving, entityLiving, PotionUtils.getEffectsFromStack(stack), true);
        }

        if (playerentity != null) {
            playerentity.addStat(Stats.ITEM_USED.get(this));
            if (!playerentity.abilities.isCreativeMode) {
                winebowl.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            }
        }

        return stack;
    }


    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @Override
    public @NotNull UseAction getUseAction(@NotNull ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, PlayerEntity playerIn, @NotNull Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        WinebowlFluidHandler winebowl = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);

        if (!PotionUtils.getEffectsFromStack(stack).isEmpty() && winebowl.getFluid().getAmount() >= DEFAULT_CAPACITY) {
            if (Drinks.addDrunkEffectsToEntity(playerIn, playerIn, PotionUtils.getEffectsFromStack(stack), false)) {
                return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
            }
        }
        return ActionResult.resultFail(playerIn.getHeldItem(handIn));
    }

    @Override
    public @NotNull ITextComponent getDisplayName(@NotNull ItemStack stack) {
        FluidStack fluidStack = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY).getFluid();
        if (!fluidStack.isEmpty()) {
            return new TranslationTextComponent("item." + VillagersWineryMod.MODID + ".winebowl", new TranslationTextComponent(fluidStack.getTranslationKey()));
        } else {
            return new TranslationTextComponent("item." + VillagersWineryMod.MODID + ".empty_winebowl");
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@NotNull ItemStack stack, World worldIn, List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        FluidStack fluidStack = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY).getFluid();
        tooltip.add(new TranslationTextComponent("item." + VillagersWineryMod.MODID + ".winebowl.information", new StringTextComponent(Integer.toString(fluidStack.getAmount()))));
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
    }


    @Override
    public void fillItemGroup(@NotNull ItemGroup group, @NotNull NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (Drinks drinks : DrinksRegistry.drinksCollection) {
                if (drinks.potion == null) continue;
                ItemStack stack = new ItemStack(this);
                WinebowlFluidHandler fluidStack = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
                if (fluidStack.fill(new FluidStack(drinks.getFluid(), DEFAULT_CAPACITY), IFluidHandler.FluidAction.EXECUTE) == DEFAULT_CAPACITY) {
                    items.add(stack);
                }
            }
            ItemStack stack = new ItemStack(this);
            items.add(stack);
        }
    }

    @Override
    public @NotNull ActionResultType itemInteractionForEntity(@NotNull ItemStack stack, @NotNull PlayerEntity playerIn, LivingEntity entity, @NotNull Hand hand) {
        if (entity.world.isRemote) return ActionResultType.PASS;
        if (entity instanceof VillagerEntity) {
            WinebowlFluidHandler winebowl = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
            if (!PotionUtils.getEffectsFromStack(stack).isEmpty() && winebowl.getFluid().getAmount() >= DEFAULT_CAPACITY) {
                if (Drinks.addDrunkEffectsToEntity(playerIn, entity, PotionUtils.getEffectsFromStack(stack), true)) {
                    playerIn.addStat(Stats.ITEM_USED.get(this));
                    if (!playerIn.abilities.isCreativeMode) {
                        winebowl.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
    }
}
