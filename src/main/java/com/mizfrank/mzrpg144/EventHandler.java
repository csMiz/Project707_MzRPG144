package com.mizfrank.mzrpg144;

import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class EventHandler {
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();
        IMzSpecialty playerSpec = player.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(null);

        if (playerSpec == null){
            String message = String.format("Error: Spec is NULL!");
            player.sendMessage(new StringTextComponent(message));
        }
        else{
            String message = "Your specialty is ";
            message += MzSpecialty.cvt_specInt_specStr(playerSpec.getFirstSpecType());
            player.sendMessage(new StringTextComponent(message));
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        event.getPlayer().sendMessage(new StringTextComponent("RES!"));
    }

//
//    @SubscribeEvent
//    public void onPlayerSleep(PlayerSleepInBedEvent event)
//    {
//        EntityPlayer player = event.getEntityPlayer();
//
//        if (player.worldObj.isRemote) return;
//
//        IMana mana = player.getCapability(ManaProvider.MANA_CAP, null);
//
//        mana.fill(50);
//
//        String message = String.format("You refreshed yourself in the bed. You received 50 mana, you have §7%d§r mana left.", (int) mana.getMana());
//        player.addChatMessage(new TextComponentString(message));
//    }
//
//    @SubscribeEvent
//    public void onPlayerFalls(LivingFallEvent event)
//    {
//        Entity entity = event.getEntity();
//
//        if (entity.worldObj.isRemote || !(entity instanceof EntityPlayerMP) || event.getDistance() < 3) return;
//
//        EntityPlayer player = (EntityPlayer) entity;
//        IMana mana = player.getCapability(ManaProvider.MANA_CAP, null);
//
//        float points = mana.getMana();
//        float cost = event.getDistance() * 2;
//
//        if (points > cost)
//        {
//            mana.consume(cost);
//
//            String message = String.format("You absorbed fall damage. It costed §7%d§r mana, you have §7%d§r mana left.", (int) cost, (int) mana.getMana());
//            player.addChatMessage(new TextComponentString(message));
//
//            event.setCanceled(true);
//        }
//    }

    /**
     * Copy data from dead player to the new player
     */
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        event.getPlayer().sendMessage(new StringTextComponent("CLONE SPEC DATA!"));
        PlayerEntity player = event.getPlayer();
        IMzSpecialty newSpec = player.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(new MzSpecialty());
        IMzSpecialty oldSpec = event.getOriginal().getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(new MzSpecialty());
        newSpec.setSpec(oldSpec.getAllSpecData());
    }

}
