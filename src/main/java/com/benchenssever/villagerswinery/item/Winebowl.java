package com.benchenssever.villagerswinery.item;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class Winebowl extends Item {

    public Winebowl(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), Potions.WATER);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, stack);
        }

        if (!worldIn.isRemote) {
            for(EffectInstance effectinstance : PotionUtils.getEffectsFromStack(stack)) {
                if (effectinstance.getPotion().isInstant()) {
                    effectinstance.getPotion().affectEntity(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addPotionEffect(new EffectInstance(effectinstance));
                }
            }
            EffectInstance drunk = entityLiving.getActivePotionEffect(RegistryEvents.drunk.get());
            int time = 0;
            if(drunk != null) {
                time = drunk.getDuration();
            }
            entityLiving.addPotionEffect(new EffectInstance(RegistryEvents.drunk.get(), time + 3600));
        }

        if (playerentity != null) {
            playerentity.addStat(Stats.ITEM_USED.get(this));
            if (!playerentity.abilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        if (playerentity == null || !playerentity.abilities.isCreativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (playerentity != null) {
                playerentity.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }


    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        EffectInstance drunk = playerIn.getActivePotionEffect(RegistryEvents.drunk.get());

        if(drunk ==null || drunk.getDuration() < 3600) {
            return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
        }
        return ActionResult.resultFail(playerIn.getHeldItem(handIn));
    }


    @Override
    public String getTranslationKey(ItemStack stack) {
        return PotionUtils.getPotionFromItem(stack).getNamePrefixed(this.getTranslationKey() + ".effect.");
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
    }


    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for(RegistryObject<Potion> potion: RegistryEvents.POTION.getEntries()) {
                items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potion.get()));
            }
        }
    }

    @Override
    public net.minecraft.util.ActionResultType itemInteractionForEntity(ItemStack stack, net.minecraft.entity.player.PlayerEntity playerIn, LivingEntity entity, net.minecraft.util.Hand hand) {
        if (entity.world.isRemote) return net.minecraft.util.ActionResultType.PASS;
        if (entity instanceof net.minecraft.entity.merchant.IMerchant) {

            EffectInstance drunk = entity.getActivePotionEffect(RegistryEvents.drunk.get());
            if(drunk == null || drunk.getDuration() < 3600) {
                int time = 0;
                if(drunk != null) {
                    time = drunk.getDuration();
                }
                entity.addPotionEffect(new EffectInstance(RegistryEvents.drunk.get(), time + 3600));

                for(EffectInstance effectinstance : PotionUtils.getEffectsFromStack(stack)) {
                    if (effectinstance.getPotion().isInstant()) {
                        effectinstance.getPotion().affectEntity(playerIn, playerIn, entity, effectinstance.getAmplifier(), 1.0D);
                    } else {
                        entity.addPotionEffect(new EffectInstance(effectinstance));
                    }
                }

                if (playerIn != null) {
                    playerIn.addStat(Stats.ITEM_USED.get(this));
                    if (!playerIn.abilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }

                if (playerIn == null || !playerIn.abilities.isCreativeMode) {
                    if (stack.isEmpty()) {
                        stack = new ItemStack(RegistryEvents.emptyWinebowl.get());
                    }

                    if (playerIn != null) {
                        playerIn.inventory.addItemStackToInventory(new ItemStack(RegistryEvents.emptyWinebowl.get()));
                    }
                }

                return net.minecraft.util.ActionResultType.SUCCESS;
            }
        }
        return net.minecraft.util.ActionResultType.PASS;
    }
}
