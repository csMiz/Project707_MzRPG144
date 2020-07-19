package com.mizfrank.mzrpg144.entity;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Capability handler
 *
 * This class is responsible for attaching our capabilities
 */
public class CapabilityCollection {

    public static final ResourceLocation MZ_SPEC_CAP = new ResourceLocation(MzRPG.MOD_ID, "mz_specialty");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent event)
    {
        if (!(event.getObject() instanceof PlayerEntity)) return;
        event.addCapability(MZ_SPEC_CAP, new MzSpecialtyProvider());
    }

    @SubscribeEvent
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        event.getPlayer().sendMessage(new StringTextComponent("PICK!"));
    }

}
