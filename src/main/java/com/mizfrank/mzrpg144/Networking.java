package com.mizfrank.mzrpg144;

import com.mizfrank.mzrpg144.block.MzMedalBoxNetwork;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;

public class Networking {

    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(MzRPG.MOD_ID, "mzrpg144_main"),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.registerMessage(nextID(), int[].class,
                (msg, buffer) -> {buffer.writeVarIntArray(msg);},
                (buffer) -> {return buffer.readVarIntArray();}, MzMedalBoxNetwork::handle);
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

}
