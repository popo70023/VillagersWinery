package com.benchenssever.villagerswinery.mixin;

import com.benchenssever.villagerswinery.api.IVillagerEntityMixin;
import com.benchenssever.villagerswinery.drinkable.Drinks;
import com.benchenssever.villagerswinery.drinkable.IDrinkable;
import com.benchenssever.villagerswinery.entity.ai.VillagerFollowPlayerGoal;
import com.benchenssever.villagerswinery.fluid.ItemStackFluidHandler;
import com.benchenssever.villagerswinery.item.Winebowl;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.VillagerType;
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

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;Lnet/minecraft/entity/villager/VillagerType;)V", at = @At("TAIL"))
    private void villagerFollowPlayer(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type, CallbackInfo ci) {
        this.goalSelector.addGoal(2, new VillagerFollowPlayerGoal(this, .4D, false));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    protected void addDrinkConsumed(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == DrinksRegistry.winebowl.get()) {
            FluidStack fluidInside = ItemStackFluidHandler.getFluid(stack);
            if (fluidInside.getFluid() instanceof IDrinkable && fluidInside.getAmount() >= Winebowl.DEFAULT_CAPACITY && Drinks.isCanConsumed(this, (IDrinkable) fluidInside.getFluid())) {
                Drinks.onDrinkConsumed(this, (IDrinkable) fluidInside.getFluid());
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                if (!player.abilities.instabuild) {
                    player.setItemInHand(hand, new ItemStack(DrinksRegistry.emptyWinebowl.get()));
                }
                level.playSound(player, this, SoundEvents.GENERIC_DRINK, this.getSoundSource(), 1.0F, 1.0F);
                cir.setReturnValue(ActionResultType.sidedSuccess(this.level.isClientSide));
            }
        }
    }
}
