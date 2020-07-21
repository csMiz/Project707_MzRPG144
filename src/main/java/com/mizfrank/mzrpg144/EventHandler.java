package com.mizfrank.mzrpg144;

import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import com.mizfrank.mzrpg144.item.ItemCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class EventHandler {
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();
        IMzSpecialty playerSpec = player.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(null);
        // Sync spec from server to client
        Networking.sendToClient(playerSpec.getAllSpecDataArray(), (ServerPlayerEntity) player);

        String message = "Your specialty is ";
        message += MzSpecialty.cvt_specInt_specStr(playerSpec.getMajorType());
        player.sendMessage(new StringTextComponent(message));
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        event.getPlayer().sendMessage(new StringTextComponent("RES!"));
    }

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event){
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR){
//            if (Minecraft.getInstance().player.getHeldItemMainhand().getItem() == ItemCollection.MZ_SWORD_IRON.get()){
//                IngameGui gui = Minecraft.getInstance().ingameGUI;
//                FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
//                int centerX = (int)(Minecraft.getInstance().mainWindow.getScaledWidth() / 2);
//                int centerY = (int)(Minecraft.getInstance().mainWindow.getScaledHeight() / 2);
//                gui.drawCenteredString(fontRenderer, "Archer cannot use sword",
//                        centerX, (int)(centerY * 1.4), 0xff3333);
//            }
        }

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
        IMzSpecialty newSpec = player.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(null);
        IMzSpecialty oldSpec = event.getOriginal().getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null)
                .orElseThrow(() -> new IllegalArgumentException("OldSpec lost!"));
        newSpec.setSpec(oldSpec.getAllSpecData());

        // Sync spec from server to client
        Networking.sendToClient(newSpec.getAllSpecDataArray(), (ServerPlayerEntity) player);

        String message = "Your specialty is ";
        message += MzSpecialty.cvt_specInt_specStr(newSpec.getMajorType());
        player.sendMessage(new StringTextComponent(message));
    }

}
