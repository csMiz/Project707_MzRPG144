package com.mizfrank.mzrpg144.block;

import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class MzMedalBoxNetwork {

    public static boolean handle(int[] msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            PlayerEntity player = ctx.get().getSender();
            if (player == null){
                player = Minecraft.getInstance().player;
            }
            // FIXME: player is null on client!
            player.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(null)
                    .setSpec(msg);

             //MzMedalBoxScreen::open
        });
        ctx.get().setPacketHandled(true);
        return true;
    }

}
