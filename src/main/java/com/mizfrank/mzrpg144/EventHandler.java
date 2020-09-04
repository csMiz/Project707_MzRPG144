package com.mizfrank.mzrpg144;

import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzBow.MzBow;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzCrossbow.MzCrossbow;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzSword.MzSword;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;


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
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS){
            ItemStack itemStack = Minecraft.getInstance().player.getHeldItemMainhand();
            if (itemStack.getItem() instanceof MzBow){
                MzBow item = (MzBow)itemStack.getItem();
                IngameGui gui = Minecraft.getInstance().ingameGUI;
                MainWindow mainWindow = Minecraft.getInstance().mainWindow;
                int centerX = (int)(mainWindow.getScaledWidth() / 2);
                int centerY = (int)(mainWindow.getScaledHeight() / 2);

                float finalInacc = item.finalInaccFactor;
                float finalProg = item.pullProgress;
                finalInacc = finalInacc * 2.5f;
                renderMzCrosshair1(finalInacc, centerX, centerY);
                renderMzCrosshair2(finalInacc, finalProg, centerX, centerY);
                event.setCanceled(true);
            }
        }

//        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR){
//            if (Minecraft.getInstance().player.getHeldItemMainhand().getItem() == ItemCollection.MZ_SWORD_IRON.get()){
//                IngameGui gui = Minecraft.getInstance().ingameGUI;
//                FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
//                int centerX = (int)(Minecraft.getInstance().mainWindow.getScaledWidth() / 2);
//                int centerY = (int)(Minecraft.getInstance().mainWindow.getScaledHeight() / 2);
//                gui.drawCenteredString(fontRenderer, "Archer cannot use sword",
//                        centerX, (int)(centerY * 1.4), 0xff3333);
//            }
//        }

    }

    private void renderMzCrosshair1(float inacc, float cx, float cy){
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GL11.glLineWidth(2.0F);
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);

        // V - cross hair
        bufferbuilder.pos(cx-5.0, cy+5.0, 0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(cx, cy, 0).color(255, 255, 255, 255).endVertex();

        bufferbuilder.pos(cx, cy, 0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(cx+5.0, cy+5.0, 0).color(255, 255, 255, 255).endVertex();

        tessellator.draw();

        // outer
        GL11.glLineWidth(1.0F);
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        double lb = inacc;
        if (lb > 70.0) { lb = 70.0; }
        double ub = lb + 10.0;
        bufferbuilder.pos(cx+lb, cy, 0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(cx+ub, cy, 0).color(255, 255, 255, 255).endVertex();

        bufferbuilder.pos(cx-lb, cy, 0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(cx-ub, cy, 0).color(255, 255, 255, 255).endVertex();

        bufferbuilder.pos(cx, cy+lb, 0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(cx, cy+ub, 0).color(255, 255, 255, 255).endVertex();

        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }

    private void renderMzCrosshair2(float inacc, float inProg, float cx, float cy) {
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GL11.glLineWidth(1.0F);
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);

        int colorR = (int)((1.0 - inProg) * 155 + 50);
        int colorG = (int)(inProg * 155 + 50);
        int colorB = 50;
        int colorA = 128;

        int count = 25;
        int drawCount = (int)(25 * inProg);
        double interval = 2.0 * Math.PI / count;
        double lb = inacc;
        if (lb > 70.0) { lb = 70.0; }
        double ub = lb + 2.0;
        for (int i = 0; i < drawCount; i++){
            double alpha = 0.5 * Math.PI - interval * i;
            if (alpha < 0){ alpha += (2.0*Math.PI); }
            double nearX = lb * Math.cos(alpha);
            double nearY = lb * Math.sin(alpha);
            double farX = ub * Math.cos(alpha);
            double farY = ub * Math.sin(alpha);
            bufferbuilder.pos(cx+nearX, cy-nearY, 0).color(colorR, colorG, colorB, colorA).endVertex();
            bufferbuilder.pos(cx+farX, cy-farY, 0).color(colorR, colorG, colorB, colorA).endVertex();
        }

        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }

    @SubscribeEvent
    public void eve(PlayerInteractEvent event){

        //event.getToolTip().clear();
        //event.getToolTip().add(new StringTextComponent("replaced tooltip!"));
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (Keybinds.KEY_RELOAD.isPressed())
        {
            System.out.println("Key RELOAD pressed");
            ItemStack heldItem = Minecraft.getInstance().player.getHeldItemMainhand();
            if (heldItem.getItem() instanceof MzCrossbow){
                MzCrossbow crsbow = (MzCrossbow) heldItem.getItem();
                crsbow.startAutoLoading();
            }
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event){
        Entity target = event.getTarget();
        PlayerEntity player = event.getPlayer();
        ItemStack weapon = player.getHeldItemMainhand();
        DamageSource damageSource = DamageSource.causePlayerDamage(player);
        if (weapon.getItem() instanceof MzSword && target instanceof LivingEntity){
            float targetHP_pre = 0.0f;

            // 冷却条修正
            float coolVal = player.getCooledAttackStrength(0.5F);
            float coolMul = 0.2F + coolVal * coolVal * 0.8F;
            player.resetCooldown();

            // 武器伤害
            float[] dmgInfo = MzSword.getAttackValue(weapon, coolMul);
            float dmg = dmgInfo[0];

            // 播放声音
            if (dmgInfo[1] > 0.5f){
                player.world.playSound((PlayerEntity)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                player.onCriticalHit(target);    // Crt默认事件
            }
            else if (coolMul > 0.9f){
                player.world.playSound((PlayerEntity)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
            }
            else{
                player.world.playSound((PlayerEntity)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
            }

            // 对方反击效果
            // Vanilla荆棘
            targetHP_pre = ((LivingEntity)target).getHealth();
            EnchantmentHelper.applyThornEnchantments((LivingEntity)target, player);

            // 武器效果
            // TODO



            player.setLastAttackedEntity(target);
            boolean attackResult = target.attackEntityFrom(damageSource, dmg);
            if (attackResult){
                // 伤害粒子效果
                float actualDmg = targetHP_pre - ((LivingEntity)target).getHealth();
                if (player.world instanceof ServerWorld && actualDmg > 2.0F) {
                    int k = (int)((double)actualDmg * 0.5D);
                    ((ServerWorld)player.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + (double)(target.getHeight() * 0.5F), target.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                }
                player.addExhaustion(0.1F);
            }
            else{
                player.world.playSound((PlayerEntity)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);
            }
            // 覆盖Vanilla过程
            event.setCanceled(true);
        }

        //这之后会触发 attackTargetEntityWithCurrentItem
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent event){
        // Vanilla弓的FOV会从1.0变化到0.85
        PlayerEntity player = event.getEntity();
        ItemStack itemStack = player.getHeldItemMainhand();
        if (itemStack.getItem() instanceof MzBow){
            // TODO
        }

//        public float getFovModifier() {
//            float f = 1.0F;
//            if (this.abilities.isFlying) {
//                f *= 1.1F;
//            }
//
//            IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
//            f = (float)((double)f * ((iattributeinstance.getValue() / (double)this.abilities.getWalkSpeed() + 1.0D) / 2.0D));
//            if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
//                f = 1.0F;
//            }
//
//            if (this.isHandActive() && this.getActiveItemStack().getItem() instanceof BowItem) {
//                int i = this.getItemInUseMaxCount();
//                float f1 = (float)i / 20.0F;
//                if (f1 > 1.0F) {
//                    f1 = 1.0F;
//                } else {
//                    f1 *= f1;
//                }
//
//                f *= 1.0F - f1 * 0.15F;
//            }
//
//            return ForgeHooksClient.getOffsetFOV(this, f);
//        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event){
//        LivingEntity entity = event.getEntityLiving();
//        entity.stopActiveHand();  // 这条代码使盾放下
//        if (entity.isActiveItemStackBlocking()){    // 不能判断有没有防住
//            System.out.println("BLOCK!!!");
//        }
//        else{
//            System.out.println("FREE!!!");
//        }


//        public void disableShield(boolean p_190777_1_) { //<- PlayerEntity::disableShield
//            float f = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
//            if (p_190777_1_) {
//                f += 0.75F;
//            }
//
//            if (this.rand.nextFloat() < f) {
//                this.getCooldownTracker().setCooldown(this.getActiveItemStack().getItem(), 100);
//                this.resetActiveHand();
//                this.world.setEntityState(this, (byte)30);
//            }
//
//        }
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
