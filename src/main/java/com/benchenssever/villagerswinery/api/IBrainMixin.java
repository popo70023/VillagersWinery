package com.benchenssever.villagerswinery.api;

import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;

import java.util.Collection;

public interface IBrainMixin {
    void villagersWinery$addMemoryModuleType(Collection<? extends MemoryModuleType<?>> memoryTypes);

    void villagersWinery$addSensorType(Collection<? extends SensorType<? extends Sensor<?>>> sensorTypes);
}
