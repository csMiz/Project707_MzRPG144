package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mizfrank.mzrpg144.item.ItemCollection;
import com.mizfrank.mzrpg144.item.ItemRendererCollection;
import com.mizfrank.mzrpg144.util.MzDamageSourceCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Arrays;
import java.util.Iterator;

public class MzArrowEntity extends AbstractArrowEntity {

    /**
     * 普通箭头（重质）：质量中->伤害高，速度中，穿深中
     * 轻质箭头：质量小->伤害低，速度中偏高，穿深高（近）-中（远）
     * HE：质量大，速度偏低
     *
     * */
    protected float calibre = 1.0f;
    protected float weight_factor = 1.0f;
    protected float damage_factor = 1.0f;
    protected float penetrate_factor = 1.0f;

    protected int pen_decay_tick = 0;

    public MzArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48546_1_, World p_i48546_2_) {
        super(p_i48546_1_, p_i48546_2_);
    }

    public MzArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48547_1_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
        super(p_i48547_1_, p_i48547_2_, p_i48547_4_, p_i48547_6_, p_i48547_8_);
    }

    public MzArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48548_1_, LivingEntity p_i48548_2_, World p_i48548_3_) {
        super(p_i48548_1_, p_i48548_2_, p_i48548_3_);
    }

    public MzArrowEntity(World p_i46757_1_, double p_i46757_2_, double p_i46757_4_, double p_i46757_6_) {
        super(EntityType.ARROW, p_i46757_2_, p_i46757_4_, p_i46757_6_, p_i46757_1_);
    }

    public MzArrowEntity(World worldIn, LivingEntity livingEntity) {
        super(ItemRendererCollection.MZ_ARROW_ENTITY.get(), livingEntity, worldIn);
    }

    public MzArrowEntity(LivingEntity shooter, World world, Item referenceItemIn) {
        super(ItemRendererCollection.MZ_ARROW_ENTITY.get(), shooter, world);
        //this.referenceItem = referenceItemIn;
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ItemCollection.MZ_ARROW_AP.get());
    }

    public void setMzArrowEntityProperties(float calibIn, float wgtIn, float dmgIn, float penIn){
        calibre = calibIn;
        weight_factor = wgtIn;
        damage_factor = dmgIn;
        penetrate_factor = penIn;
    }

    public void setMzArrowEntityProperties(int[] info){
        calibre = info[1] / 1000.0f;
        weight_factor = info[2] / 1000.0f;
        damage_factor = info[3] / 1000.0f;
        penetrate_factor = info[4] / 1000.0f;
    }

    protected float getVelocity(){
        return (float)this.getMotion().length();
    }

    protected float getWeight(){
        return calibre*calibre*calibre*weight_factor;
    }

    protected float getPenetration(){
        if (this.inGround){
            return 0.0f;
        }
        float tmpPen = calibre*weight_factor*getVelocity()*penetrate_factor;
        // 如果是APCR
        // f(t) = -(1/800)t^2 + 1  经过(0,1)和(20,0.5)
        // 如果是AP
        // f(t) = -(1/1600)t^2 + 1  经过(0,1)和(20,0.75)
        float tmpDecay = (-1.0f/800.0f)*pen_decay_tick*pen_decay_tick + 1.0f;
        if (tmpDecay < 0.0f){tmpDecay = 0.0f;}

        return tmpPen * tmpDecay;
    }

    protected float getBasicDamage(){
        float wgt = getWeight();
        float tmpDamage = wgt*damage_factor;
        double dmgOffset = 0.9 + 0.2*Math.random();
        tmpDamage = (float)(tmpDamage*dmgOffset);
        return tmpDamage;
    }

    @Override
    public void tick() {
        super.tick();
//        if (!this.inGround){
//            pen_decay_tick += 1;
//            System.out.println(Float.toString(getPenetration()));
//        }
    }

    /**
     * 三叉戟的
     * */
    @Override
    protected void func_213868_a(EntityRayTraceResult collideResult) {
        Entity target = collideResult.getEntity();
        float tmpDamage = getBasicDamage();

        Entity shooter = this.getShooter();
        DamageSource dmgSrc;
        if (shooter == null) {
            dmgSrc = MzDamageSourceCollection.causeMzAPCRDamageOld(this, this);
        } else {
            dmgSrc = MzDamageSourceCollection.causeMzAPCRDamageOld(this, shooter);
            if (shooter instanceof LivingEntity) {
                ((LivingEntity)shooter).setLastAttackedEntity(target);
            }
        }
        SoundEvent soundEvent = SoundEvents.ENTITY_ARROW_HIT;
        if (target.attackEntityFrom(dmgSrc, tmpDamage) && target instanceof LivingEntity) {
            LivingEntity livingTarget = (LivingEntity)target;
            if (shooter instanceof LivingEntity) {
                EnchantmentHelper.applyThornEnchantments(livingTarget, shooter);
                EnchantmentHelper.applyArthropodEnchantments((LivingEntity)shooter, livingTarget);
            }

            this.arrowHit(livingTarget);
        }

        this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
        this.playSound(soundEvent, 1.0F, 1.0F);
    }

//    /**
//     * onHitEntity
//     * */
//    @Override
//    protected void func_213868_a(EntityRayTraceResult collideResult) {
//        Entity target = collideResult.getEntity();
//        float wgt = getWeight();
//        float tmpDamage = wgt*damage_factor;
//        double dmgOffset = 0.9 + 0.2*Math.random();
//        tmpDamage = (float)(tmpDamage*dmgOffset);
//
//        Entity shooter = this.getShooter();
//        DamageSource damagesource;
//        if (shooter == null) {
//            damagesource = DamageSource.causeArrowDamage(this, this);
//        } else {
//            damagesource = DamageSource.causeArrowDamage(this, shooter);
//            if (shooter instanceof LivingEntity) {
//                ((LivingEntity)shooter).setLastAttackedEntity(target);
//            }
//        }
//
//        int fireInt = target.func_223314_ad();
//        if (this.isBurning() && !(target instanceof EndermanEntity)) {
//            target.setFire(5);
//        }
//
//        if (target.attackEntityFrom(damagesource, tmpDamage)) {
//            if (target instanceof LivingEntity) {
//                LivingEntity livingentity = (LivingEntity)target;
//                if (!this.world.isRemote && this.func_213874_s() <= 0) {
//                    livingentity.setArrowCountInEntity(livingentity.getArrowCountInEntity() + 1);
//                }
//
//                // 击退
////                if (this.knockbackStrength > 0) {
////                    Vec3d vec3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockbackStrength * 0.6D);
////                    if (vec3d.lengthSquared() > 0.0D) {
////                        livingentity.addVelocity(vec3d.x, 0.1D, vec3d.z);
////                    }
////                }
//
//                if (!this.world.isRemote && shooter instanceof LivingEntity) {
//                    EnchantmentHelper.applyThornEnchantments(livingentity, shooter);
//                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)shooter, livingentity);
//                }
//
//                this.arrowHit(livingentity);
//                if (shooter != null && livingentity != shooter && livingentity instanceof PlayerEntity && shooter instanceof ServerPlayerEntity) {
//                    ((ServerPlayerEntity)shooter).connection.sendPacket(new SChangeGameStatePacket(6, 0.0F));
//                }
//
//            }
//
//            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//            if (this.func_213874_s() <= 0 && !(target instanceof EndermanEntity)) {
//                this.remove();
//            }
//        } else {
//            target.func_223308_g(fireInt);  // setFire
//            this.setMotion(this.getMotion().scale(-0.1D));
//            this.rotationYaw += 180.0F;
//            this.prevRotationYaw += 180.0F;
//            if (!this.world.isRemote && this.getMotion().lengthSquared() < 1.0E-7D) {
//                if (this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
//                    this.entityDropItem(this.getArrowStack(), 0.1F);
//                }
//                this.remove();
//            }
//        }
//
//    }


    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putFloat("mz_calib", this.calibre);
        nbt.putFloat("mz_wgtfac", this.weight_factor);
        nbt.putFloat("mz_dmgfac", this.damage_factor);
        nbt.putFloat("mz_penfac", this.penetrate_factor);
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        this.calibre = nbt.getFloat("mz_calib");
        this.weight_factor = nbt.getFloat("mz_wgtfac");
        this.damage_factor = nbt.getFloat("mz_dmgfac");
        this.penetrate_factor = nbt.getFloat("mz_penfac");
    }

    @Override
    public IPacket<?> createSpawnPacket() {
//        Entity entity = this.getShooter();
//        return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getEntityId());

        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
