package com.benchenssever.villagerswinery.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static com.benchenssever.villagerswinery.VillagersWineryMod.MODID;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++,
                SyncLiquidBarrelPacket.class,
                SyncLiquidBarrelPacket::encode,
                SyncLiquidBarrelPacket::decode,
                SyncLiquidBarrelPacket::handle
        );
    }
}
