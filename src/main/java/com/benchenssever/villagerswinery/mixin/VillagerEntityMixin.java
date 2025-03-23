package com.benchenssever.villagerswinery.mixin;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {

//TODO: mixin的mapping仍然有問題 暫時註解掉

//    @Inject(method = "tick", at = @At("HEAD"))
//    private void onVillagerTick(CallbackInfo ci) {
//        System.out.println("This is a test message from VillagerEntityMixin!");
//    }
}
