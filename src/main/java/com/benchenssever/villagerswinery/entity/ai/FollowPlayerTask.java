package com.benchenssever.villagerswinery.entity.ai;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

public class FollowPlayerTask extends Task<LivingEntity> {
    private final double moveSpeed;
    private static final MemoryModuleType<PlayerEntity> TARGET_PLAYER = RegistryEvents.villagesFollowPlayerMemory.get();

    public FollowPlayerTask(double moveSpeed) {
        super(ImmutableMap.of(TARGET_PLAYER, MemoryModuleStatus.VALUE_PRESENT));
        this.moveSpeed = moveSpeed;
    }

    @Override
    protected void tick(@NotNull ServerWorld world, @NotNull LivingEntity owner, long gameTime) {
        PlayerEntity targetPlayer = owner.getBrain().getMemory(TARGET_PLAYER).orElse(null);
        if (targetPlayer != null && owner instanceof AbstractVillagerEntity) {
            AbstractVillagerEntity theVillager = (AbstractVillagerEntity) owner;
            theVillager.getNavigation().moveTo(targetPlayer, moveSpeed);
        }
    }

    @Override
    protected boolean canStillUse(@NotNull ServerWorld world, LivingEntity entity, long gameTime) {
        return entity.getBrain().getMemory(TARGET_PLAYER).isPresent();
    }

    @Override
    protected boolean timedOut(long pGameTime) {
        return false;
    }
}
