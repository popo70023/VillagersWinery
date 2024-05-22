package com.benchenssever.villagerswinery.item;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.drinkable.Drinks;
import com.benchenssever.villagerswinery.drinkable.WinebowlFluidHandler;
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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Winebowl extends Item {
    public final RegistryObject<Item> emptyBowl;
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME / 4;

    public Winebowl(Properties properties, RegistryObject<Item> emptyBowl) {
        super(properties);
        this.emptyBowl = emptyBowl;
    }

    @Nonnull
    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), Potions.WATER);
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull LivingEntity entityLiving) {
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
        WinebowlFluidHandler winebowl = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, stack);
        }

        if (!worldIn.isRemote) {
            for(EffectInstance effectinstance : PotionUtils.getEffectsFromStack(stack)) {
                if(effectinstance.getPotion() == DrinksRegistry.drunk.get()) continue;
                if (effectinstance.getPotion().isInstant()) {
                    effectinstance.getPotion().affectEntity(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addPotionEffect(new EffectInstance(effectinstance));
                }
            }
            EffectInstance drunk = entityLiving.getActivePotionEffect(DrinksRegistry.drunk.get());
            int time = 0;
            if(drunk != null) {
                time = drunk.getDuration();
            }
            entityLiving.addPotionEffect(new EffectInstance(DrinksRegistry.drunk.get(), time + 3600));
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
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 32;
    }

    @Nonnull
    @Override
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.DRINK;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        WinebowlFluidHandler winebowl = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
        EffectInstance drunk = playerIn.getActivePotionEffect(DrinksRegistry.drunk.get());

        if(!PotionUtils.getEffectsFromStack(stack).isEmpty() && winebowl.getFluid().getAmount() >= DEFAULT_CAPACITY && (drunk == null || drunk.getDuration() < 3600)) {
            return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
        }
        return ActionResult.resultFail(playerIn.getHeldItem(handIn));
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        FluidStack fluidStack = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY).getFluid();
        if(!fluidStack.isEmpty()) {
            return new TranslationTextComponent("item."+ VillagersWineryMod.MODID+".winebowl", new TranslationTextComponent(fluidStack.getTranslationKey()));

        } else {
            return  new TranslationTextComponent("item."+ VillagersWineryMod.MODID+".empty_winebowl");
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
    }


    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for(Drinks drinks : DrinksRegistry.drinksCollection) {
                if(drinks.potion == null) continue;
                ItemStack stack = new ItemStack(this);
                WinebowlFluidHandler fluidStack = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
                if(fluidStack.fill(new FluidStack(drinks.getFluid(), DEFAULT_CAPACITY), IFluidHandler.FluidAction.EXECUTE) == DEFAULT_CAPACITY) {
                    items.add(stack);
                }
            }
            ItemStack stack = new ItemStack(this);
            items.add(stack);
        }
    }

    @Nonnull
    @Override
    public net.minecraft.util.ActionResultType itemInteractionForEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity playerIn, LivingEntity entity, @Nonnull Hand hand) {
        if (entity.world.isRemote) return ActionResultType.PASS;
        if (entity instanceof VillagerEntity) {
            EffectInstance drunkEffect = entity.getActivePotionEffect(DrinksRegistry.drunk.get());
            WinebowlFluidHandler winebowl = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);

            if(!PotionUtils.getEffectsFromStack(stack).isEmpty() && winebowl.getFluid().getAmount() >= DEFAULT_CAPACITY && (drunkEffect == null || drunkEffect.getDuration() < 3600)) {
                int time = 0;
                if(drunkEffect != null) {
                    time = drunkEffect.getDuration();
                }
                entity.addPotionEffect(new EffectInstance(DrinksRegistry.drunk.get(), time + 3600));

                for(EffectInstance effectinstance : PotionUtils.getEffectsFromStack(stack)) {
                    if(effectinstance.getPotion() == DrinksRegistry.drunk.get()) continue;
                    if (effectinstance.getPotion().isInstant()) {
                        effectinstance.getPotion().affectEntity(playerIn, playerIn, entity, effectinstance.getAmplifier(), 1.0D);
                    } else {
                        entity.addPotionEffect(new EffectInstance(effectinstance));
                    }
                }

                playerIn.addStat(Stats.ITEM_USED.get(this));
                if (!playerIn.abilities.isCreativeMode) {
                    winebowl.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                }

                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
    }
}
