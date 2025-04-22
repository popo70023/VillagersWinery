package com.benchenssever.villagerswinery.content.equipment;

import com.benchenssever.villagerswinery.content.capability.FluidUtils;
import com.benchenssever.villagerswinery.content.capability.ItemStackFluidHandler;
import com.benchenssever.villagerswinery.content.drinkable.Drinkable;
import com.benchenssever.villagerswinery.content.drinkable.IDrinkable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DrinkwareItem extends Item {
    protected final Supplier<? extends Item> swap;
    protected final int capacity;
    protected final Predicate<FluidStack> validator;

    public DrinkwareItem(Properties properties, int capacity, Predicate<FluidStack> validator, Supplier<? extends Item> swap) {
        super(properties);
        this.swap = swap;
        this.capacity = capacity;
        this.validator = validator;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull World worldIn, @NotNull LivingEntity entityLiving) {
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) playerentity, stack);
        }
        FluidStack stackFluid = ItemStackFluidHandler.getFluidStackFromNBT(stack);

        if (playerentity != null && stackFluid.getFluid() instanceof IDrinkable && stackFluid.getAmount() >= capacity) {
            if (!worldIn.isClientSide) {
                Drinkable.onDrinkConsumed(playerentity, (IDrinkable) stackFluid.getFluid());
            }
            playerentity.awardStat(Stats.ITEM_USED.get(this));
            if (!playerentity.abilities.instabuild) {
                stack = new ItemStack(swap.get());
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        Fluid stackFluid = ItemStackFluidHandler.getFluidStackFromNBT(stack).getFluid();
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
        FluidStack stackFluid = ItemStackFluidHandler.getFluidStackFromNBT(stack);

        if (stackFluid.getFluid() instanceof IDrinkable && stackFluid.getAmount() >= capacity && Drinkable.isCanConsumed(playerIn, (IDrinkable) stackFluid.getFluid())) {
            return DrinkHelper.useDrink(worldIn, playerIn, handIn);
        }
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Override
    public @NotNull ITextComponent getName(@NotNull ItemStack stack) {
        FluidStack fluidStack = ItemStackFluidHandler.getFluidStackFromNBT(stack);
        return new TranslationTextComponent(this.getDescriptionId(stack), new TranslationTextComponent(fluidStack.getTranslationKey()));
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        FluidStack fluidStack = ItemStackFluidHandler.getFluidStackFromNBT(stack);
        if (!fluidStack.isEmpty()) {
            tooltip.add(FluidUtils.addFluidAmountTooltip(fluidStack.getAmount()));

            if (fluidStack.getFluid() instanceof IDrinkable) {
                tooltip.add(((IDrinkable) fluidStack.getFluid()).getTooltip());
            }
        }
    }


    @Override
    public void fillItemCategory(@NotNull ItemGroup group, @NotNull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            for (Drinkable drink : Drinkable.getDrinkableCollection()) {
                ItemStack stack = new ItemStack(this);
                ItemStackFluidHandler.setFluidStackToNBT(stack, new FluidStack(drink.getFluid(), capacity));
                items.add(stack);
            }
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ItemStackFluidHandler.SwapEmpty(stack, new ItemStack(swap.get()), capacity, validator);
    }

    public static class Empty extends DrinkwareItem {
        public Empty(Properties properties, int capacity, Predicate<FluidStack> validator, Supplier<Item> swap) {
            super(properties, capacity, validator, swap);
        }

        @Override
        public @NotNull ITextComponent getName(@NotNull ItemStack stack) {
            return new TranslationTextComponent(this.getDescriptionId(stack));
        }

        @Override
        public void fillItemCategory(@NotNull ItemGroup group, @NotNull NonNullList<ItemStack> items) {
            if (this.allowdedIn(group)) {
                items.add(new ItemStack(this));
            }
        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
            return new ItemStackFluidHandler(new ItemStack(swap.get()), capacity, validator);
        }
    }
}
