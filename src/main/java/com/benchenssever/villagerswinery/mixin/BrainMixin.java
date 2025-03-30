package com.benchenssever.villagerswinery.mixin;

import com.benchenssever.villagerswinery.api.IBrainMixin;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(Brain.class)
public abstract class BrainMixin implements IBrainMixin {
    @Final
    @Shadow
    private Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories;

    @Final
    @Shadow
    private Map<SensorType<? extends Sensor<?>>, Sensor<?>> sensors;

    @Unique
    public void villagersWinery$addMemoryModuleType(Collection<? extends MemoryModuleType<?>> memoryTypes) {
        for (MemoryModuleType<?> memorymoduletype : memoryTypes) {
            this.memories.put(memorymoduletype, Optional.empty());
        }
    }

    @Unique
    public void villagersWinery$addSensorType(Collection<? extends SensorType<? extends Sensor<?>>> sensorTypes) {
        List<Sensor<?>> tmpSensors = new ArrayList<>();

        for (SensorType<? extends Sensor<?>> sensortype : sensorTypes) {
            Sensor<?> tmpSensor = sensortype.create();
            this.sensors.put(sensortype, tmpSensor);
            tmpSensors.add(tmpSensor);
        }

        for (Sensor<?> sensor : tmpSensors) {
            for (MemoryModuleType<?> memorymoduletype1 : sensor.requires()) {
                this.memories.put(memorymoduletype1, Optional.empty());
            }
        }
    }
}
