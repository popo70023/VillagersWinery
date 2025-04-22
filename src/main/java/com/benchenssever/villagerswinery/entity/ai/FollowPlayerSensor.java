package com.benchenssever.villagerswinery.entity.ai;

import com.benchenssever.villagerswinery.content.capability.ItemStackFluidHandler;
import com.benchenssever.villagerswinery.content.drinkable.IDrinkable;
import com.benchenssever.villagerswinery.content.equipment.DrinkwareItem;
import com.benchenssever.villagerswinery.content.equipment.LiquidBarrelItem;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FollowPlayerSensor extends Sensor<LivingEntity> {
    private static final int SEARCH_RADIUS = 10;

    public static boolean isEntityUnavailable(Brain<?> brain) {
        return brain.isActive(Activity.REST) || brain.isActive(Activity.PANIC) || brain.isActive(Activity.HIDE);
    }

    public static PlayerEntity findNearestPlayer(LivingEntity entity) {
        Vector3d entitylocal = entity.position();
        return entity.level.getNearestPlayer(
                entitylocal.x(), entitylocal.y(), entitylocal.z(),
                SEARCH_RADIUS,
                predicateEntity -> {
                    if (!(predicateEntity instanceof PlayerEntity)) return false;
                    PlayerEntity player = (PlayerEntity) predicateEntity;
                    IDrinkable heldDrink = hasHoldingDrinkableItem(player);
                    if (heldDrink != null && getPlayerReputation(entity, player) > -5) {
                        return preferenceByAge(heldDrink, entity);
                    }
                    return false;
                }
        );
    }

    public static int getPlayerReputation(LivingEntity villager, PlayerEntity player) {
        if (!(villager instanceof VillagerEntity)) return 0;
        return ((VillagerEntity) villager).getGossips().getReputation(player.getUUID(), gossipType -> true);
    }

    public static IDrinkable hasHoldingDrinkableItem(PlayerEntity player) {
        ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
        if (heldItem.getItem() instanceof LiquidBarrelItem || heldItem.getItem() instanceof DrinkwareItem) {
            FluidStack fluidInside = ItemStackFluidHandler.getFluidStackFromNBT(heldItem);
            if (fluidInside.getFluid() instanceof IDrinkable) {
                return (IDrinkable) fluidInside.getFluid();
            }
        }
        return null;
    }

    public static boolean preferenceByAge(IDrinkable drinkable, LivingEntity entity) {
        return drinkable.isForDrink() && entity.isBaby() != drinkable.isAlcohol();
    }

    @Override
    protected void doTick(@NotNull ServerWorld world, @NotNull LivingEntity entity) {
        PlayerEntity nearestPlayer = findNearestPlayer(entity);
        Brain<?> brain = entity.getBrain();
        PlayerEntity memberPlayer = brain.getMemory(RegistryEvents.followPlayerMemory.get()).orElse(null);

        if (nearestPlayer == null || isEntityUnavailable(brain)) {
            if (memberPlayer != null) {
                brain.eraseMemory(RegistryEvents.followPlayerMemory.get());
            }
            return;
        }

        if (memberPlayer == null || memberPlayer.getUUID() != nearestPlayer.getUUID()) {
            brain.setMemory(RegistryEvents.followPlayerMemory.get(), nearestPlayer);
        }
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(RegistryEvents.followPlayerMemory.get());
    }
}
