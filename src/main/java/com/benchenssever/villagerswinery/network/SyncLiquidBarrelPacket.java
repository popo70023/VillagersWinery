package com.benchenssever.villagerswinery.network;

import com.benchenssever.villagerswinery.client.gui.LiquidBarrelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncLiquidBarrelPacket {
    private final FluidStack fluidStack;
    private final String worldAndPos;

    public SyncLiquidBarrelPacket(FluidStack fluid, String worldAndPos) {
        this.fluidStack = fluid;
        this.worldAndPos = worldAndPos;
    }

    public static void encode(SyncLiquidBarrelPacket packet, PacketBuffer buffer) {
        packet.fluidStack.writeToPacket(buffer);
        buffer.writeString(packet.worldAndPos);
    }

    public static SyncLiquidBarrelPacket decode(PacketBuffer buffer) {
        return new SyncLiquidBarrelPacket(FluidStack.readFromPacket(buffer), buffer.readString(32767));
    }

    public static void handle(SyncLiquidBarrelPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            Screen currentScreen = minecraft.currentScreen;
            if (currentScreen instanceof LiquidBarrelScreen) {
                ((LiquidBarrelScreen) currentScreen).updateFluid(packet.fluidStack, packet.worldAndPos);
            }
        });
        context.get().setPacketHandled(true);
    }
}
