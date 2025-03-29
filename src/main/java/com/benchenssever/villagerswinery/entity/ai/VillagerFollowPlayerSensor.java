package com.benchenssever.villagerswinery.entity.ai;

import com.benchenssever.villagerswinery.drinkable.IDrinkable;
import com.benchenssever.villagerswinery.fluid.ItemStackFluidHandler;
import com.benchenssever.villagerswinery.item.LiquidBarrelItem;
import com.benchenssever.villagerswinery.item.Winebowl;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
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

public class VillagerFollowPlayerSensor extends Sensor<VillagerEntity> {
    private static final int SEARCH_RADIUS = 10;

    @Override
    protected void doTick(@NotNull ServerWorld world, @NotNull VillagerEntity entity) {
        PlayerEntity nearestPlayer = findNearestPlayer(entity);
        boolean hasFollowPlayerMemory = entity.getBrain().getMemory(RegistryEvents.villagesFollowPlayerMemory.get()).isPresent();
        if (nearestPlayer != null && !hasFollowPlayerMemory) {
            entity.getBrain().setMemory(RegistryEvents.villagesFollowPlayerMemory.get(), nearestPlayer);
        } else if (nearestPlayer == null && hasFollowPlayerMemory) {
            entity.getBrain().eraseMemory(RegistryEvents.villagesFollowPlayerMemory.get());
        }
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(RegistryEvents.villagesFollowPlayerMemory.get());
    }

    public static PlayerEntity findNearestPlayer(LivingEntity entity) {
        Vector3d entitylocal = entity.position();
        return entity.level.getNearestPlayer(
                entitylocal.x(), entitylocal.y(), entitylocal.z(),
                SEARCH_RADIUS,
                predicateEntity -> {
                    if (!(predicateEntity instanceof PlayerEntity)) return false;
                    IDrinkable heldDrink = hasHoldingDrinkableItem((PlayerEntity) predicateEntity);
                    if (heldDrink != null) {
                        return preferenceByAge(heldDrink, entity);
                    }
                    return false;
                }
        );
    }

    public static IDrinkable hasHoldingDrinkableItem(PlayerEntity player) {
        ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
        if (heldItem.getItem() instanceof LiquidBarrelItem || heldItem.getItem() instanceof Winebowl) {
            FluidStack fluidInside = ItemStackFluidHandler.getFluid(heldItem);
            if (fluidInside.getFluid() instanceof IDrinkable) {
                return (IDrinkable) fluidInside.getFluid();
            }
        }
        return null;
    }

    public static boolean preferenceByAge(IDrinkable drinkable, LivingEntity entity) {
        return drinkable.isForDrink() && entity.isBaby() != drinkable.isAlcohol();
    }
}
