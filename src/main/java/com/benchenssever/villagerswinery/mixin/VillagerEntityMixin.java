package com.benchenssever.villagerswinery.mixin;

import com.benchenssever.villagerswinery.api.IBrainMixin;
import com.benchenssever.villagerswinery.api.IVillagerEntityMixin;
import com.benchenssever.villagerswinery.content.capability.FluidUtils;
import com.benchenssever.villagerswinery.content.capability.ItemStackFluidHandler;
import com.benchenssever.villagerswinery.content.drinkable.Drinkable;
import com.benchenssever.villagerswinery.content.drinkable.IDrinkable;
import com.benchenssever.villagerswinery.entity.ai.FollowPlayerTask;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends AbstractVillagerEntity implements IVillagerEntityMixin {
    @Shadow
    private byte foodLevel;

    protected VillagerEntityMixin(EntityType<? extends AbstractVillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract int getVillagerXp();

    @Shadow
    public abstract void setVillagerXp(int xp);

    @Shadow
    protected abstract void updateTrades();

    @Unique
    public void villagersWinery$addFoodLevel(int foodLevel) {
        this.foodLevel += (byte) foodLevel;
    }

    @Unique
    public void villagersWinery$refreshMerchantOffers() {
        offers = new MerchantOffers();
        updateTrades();
    }

    @Inject(method = "makeBrain", at = @At("RETURN"), cancellable = true)
    protected void modifyBrain(Dynamic<?> pDynamic, CallbackInfoReturnable<Brain<?>> cir) {
        Brain<?> newBrain = cir.getReturnValue();
        if (newBrain instanceof IBrainMixin) {
            IBrainMixin brainMixin = (IBrainMixin) newBrain;
            brainMixin.villagersWinery$addMemoryModuleType(ImmutableList.of(RegistryEvents.followPlayerMemory.get()));
            brainMixin.villagersWinery$addSensorType(ImmutableList.of(RegistryEvents.followPlayerSensor.get()));
        }
        cir.setReturnValue(newBrain);
    }

    @Inject(method = "registerBrainGoals", at = @At("TAIL"))
    protected void addBrainGoals(Brain<VillagerEntity> villagerBrain, CallbackInfo ci) {
        villagerBrain.addActivity(Activity.CORE, 4, ImmutableList.of(new FollowPlayerTask(.4d)));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    protected void addDrinkConsumed(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == DrinksRegistry.winebowl.filled.get()) {
            FluidStack fluidInside = ItemStackFluidHandler.getFluidStackFromNBT(stack);
            if (fluidInside.getFluid() instanceof IDrinkable && fluidInside.getAmount() >= FluidUtils.WINEBOWL_DEFAULT_CAPACITY && Drinkable.isCanConsumed(this, (IDrinkable) fluidInside.getFluid())) {
                Drinkable.onDrinkConsumed(this, (IDrinkable) fluidInside.getFluid());
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                if (!player.abilities.instabuild) {
                    player.setItemInHand(hand, new ItemStack(DrinksRegistry.winebowl.empty.get()));
                }
                level.playSound(player, this, SoundEvents.GENERIC_DRINK, this.getSoundSource(), 1.0F, 1.0F);
                cir.setReturnValue(ActionResultType.sidedSuccess(this.level.isClientSide));
            }
        }
    }
}
