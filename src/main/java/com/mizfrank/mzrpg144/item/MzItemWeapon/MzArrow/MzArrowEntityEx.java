package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import com.mizfrank.mzrpg144.item.ItemCollection;
import com.mizfrank.mzrpg144.item.ItemRendererCollection;
import com.mizfrank.mzrpg144.util.MzDamageSourceCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

public class MzArrowEntityEx extends Entity implements IProjectile {

    private BlockState inBlockState;
    protected boolean inGround;
    protected int timeInGround;
    public net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus pickupStatus;
    public int arrowShake;
    public UUID shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private int knockbackStrength;
    private SoundEvent soundEvent = SoundEvents.ENTITY_ARROW_HIT;
    private IntOpenHashSet field_213878_az;
    private List<Entity> field_213875_aA;    // 贯穿

    /**
     * 普通箭头（重质）：质量中->伤害高，速度中，穿深中
     * 轻质箭头：质量小->伤害低，速度中偏高，穿深高（近）-中（远）
     * HE：质量大，速度偏低
     *
     * */

    protected int pen_decay_tick = 0;

    protected static final DataParameter<Optional<UUID>> senderID = EntityDataManager.createKey
            (MzArrowEntityEx.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected static final DataParameter<Float> dm_calib = EntityDataManager.createKey(MzArrowEntityEx.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> dm_wgtfac = EntityDataManager.createKey(MzArrowEntityEx.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> dm_dmgfac = EntityDataManager.createKey(MzArrowEntityEx.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> dm_penfac = EntityDataManager.createKey(MzArrowEntityEx.class, DataSerializers.FLOAT);


    public MzArrowEntityEx(EntityType<MzArrowEntityEx> p_i48546_1_, World p_i48546_2_) {
        super(p_i48546_1_, p_i48546_2_);
        this.pickupStatus = net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.DISALLOWED;
        this.soundEvent = SoundEvents.ENTITY_ARROW_HIT;
    }

    public MzArrowEntityEx(EntityType<MzArrowEntityEx> p_i48547_1_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
        this(p_i48547_1_, p_i48547_8_);
        this.setPosition(p_i48547_2_, p_i48547_4_, p_i48547_6_);
    }

    public MzArrowEntityEx(EntityType<MzArrowEntityEx> p_i48548_1_, LivingEntity p_i48548_2_, World p_i48548_3_) {
        this(p_i48548_1_, p_i48548_2_.posX, p_i48548_2_.posY + (double)p_i48548_2_.getEyeHeight() - 0.10000000149011612D, p_i48548_2_.posZ, p_i48548_3_);
        this.setShooter(p_i48548_2_);
        if (p_i48548_2_ instanceof PlayerEntity) {
            this.pickupStatus = net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.ALLOWED;
        }
    }

    public MzArrowEntityEx(World p_i46757_1_, double p_i46757_2_, double p_i46757_4_, double p_i46757_6_) {
        this(ItemRendererCollection.MZ_ARROW_ENTITY_EX.get(), p_i46757_2_, p_i46757_4_, p_i46757_6_, p_i46757_1_);
    }

    public MzArrowEntityEx(World worldIn, LivingEntity livingEntity) {
        this(ItemRendererCollection.MZ_ARROW_ENTITY_EX.get(), livingEntity, worldIn);
    }

    public MzArrowEntityEx(LivingEntity shooter, World world, Item referenceItemIn) {
        this(ItemRendererCollection.MZ_ARROW_ENTITY_EX.get(), shooter, world);
        //this.referenceItem = referenceItemIn;
    }

    protected ItemStack getArrowStack() {
        return new ItemStack(ItemCollection.MZ_ARROW_AP.get());
    }

    public void setMzArrowEntityProperties(float calibIn, float wgtIn, float dmgIn, float penIn){
        dataManager.set(dm_calib, calibIn);
        dataManager.set(dm_wgtfac, wgtIn);
        dataManager.set(dm_dmgfac, dmgIn);
        dataManager.set(dm_penfac, penIn);
    }

    public void setMzArrowEntityProperties(int[] info){
        dataManager.set(dm_calib, info[1] / 1000.0f);
        dataManager.set(dm_wgtfac, info[2] / 1000.0f);
        dataManager.set(dm_dmgfac, info[3] / 1000.0f);
        dataManager.set(dm_penfac, info[4] / 1000.0f);
    }

    protected float getVelocity(){
        return (float)this.getMotion().length();
    }

    protected float getWeight(){
        float calibre = dataManager.get(dm_calib);
        float weight_factor = dataManager.get(dm_wgtfac);
        return calibre*calibre*calibre*weight_factor;
    }

    protected float getPenetration(){
        if (this.inGround){
            return 0.0f;
        }
        float calibre = dataManager.get(dm_calib);
        float weight_factor = dataManager.get(dm_wgtfac);
        float penetrate_factor = dataManager.get(dm_penfac);
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
        float damage_factor = dataManager.get(dm_dmgfac);
        float tmpDamage = wgt*damage_factor;
        double dmgOffset = 0.9 + 0.2*Math.random();
        tmpDamage = (float)(tmpDamage*dmgOffset);
        return tmpDamage;
    }


    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return p_70112_1_ < d0 * d0;
    }

    // 初始化data manager
    protected void registerData() {
        dataManager.register(senderID, Optional.empty());
        dataManager.register(dm_calib, 1.0f);
        dataManager.register(dm_wgtfac, 1.0f);
        dataManager.register(dm_dmgfac, 1.0f);
        dataManager.register(dm_penfac, 1.0f);

    }

    public void shoot(Entity p_184547_1_, float p_184547_2_, float p_184547_3_, float p_184547_4_, float p_184547_5_, float p_184547_6_) {
        float f = -MathHelper.sin(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
        float f1 = -MathHelper.sin(p_184547_2_ * 0.017453292F);
        float f2 = MathHelper.cos(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, p_184547_5_, p_184547_6_);
        this.setMotion(this.getMotion().add(p_184547_1_.getMotion().x, p_184547_1_.onGround ? 0.0D : p_184547_1_.getMotion().y, p_184547_1_.getMotion().z));
    }

    public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
        Vec3d vec3d = (new Vec3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_, this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_).scale((double)p_70186_7_);
        this.setMotion(vec3d);
        float f = MathHelper.sqrt(func_213296_b(vec3d));
        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
        this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.ticksInGround = 0;
    }

    public void setHitSound(SoundEvent sound){
        this.soundEvent = sound;
    }

    @OnlyIn(Dist.CLIENT)
    public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
        this.setPosition(p_180426_1_, p_180426_3_, p_180426_5_);
        this.setRotation(p_180426_7_, p_180426_8_);
    }

    @OnlyIn(Dist.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        this.setMotion(p_70016_1_, p_70016_3_, p_70016_5_);
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            this.rotationPitch = (float)(MathHelper.atan2(p_70016_3_, (double)f) * 57.2957763671875D);
            this.rotationYaw = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * 57.2957763671875D);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }

    }

    public void tick() {
        super.tick();
        boolean flag = this.isNoClip();
        Vec3d vec3d = this.getMotion();
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(func_213296_b(vec3d));
            this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
            this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
        BlockState blockstate = this.world.getBlockState(blockpos);
        if (!blockstate.isAir(this.world, blockpos) && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.world, blockpos);
            if (!voxelshape.isEmpty()) {
                Iterator var6 = voxelshape.toBoundingBoxList().iterator();

                while(var6.hasNext()) {
                    AxisAlignedBB axisalignedbb = (AxisAlignedBB)var6.next();
                    if (axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.isWet()) {
            this.extinguish();
        }

        if (this.inGround && !flag) {
            if (this.inBlockState != blockstate && this.world.areCollisionShapesEmpty(this.getBoundingBox().grow(0.06D))) {
                this.inGround = false;
                this.setMotion(vec3d.mul((double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F), (double)(this.rand.nextFloat() * 0.2F)));
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else if (!this.world.isRemote) {
                this.tryDespawn();
            }

            ++this.timeInGround;
        } else {
            this.timeInGround = 0;
            ++this.ticksInAir;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d2 = vec3d1.add(vec3d);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
            if (((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS) {
                vec3d2 = ((RayTraceResult)raytraceresult).getHitVec();
            }

            while(!this.removed) {
                EntityRayTraceResult entityraytraceresult = this.func_213866_a(vec3d1, vec3d2);
                if (entityraytraceresult != null) {
                    raytraceresult = entityraytraceresult;
                }

                if (raytraceresult != null && ((RayTraceResult)raytraceresult).getType() == RayTraceResult.Type.ENTITY) {
                    Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
                    Entity entity1 = this.getShooter();
                    if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canAttackPlayer((PlayerEntity)entity)) {
                        raytraceresult = null;
                        entityraytraceresult = null;
                    }
                }

                if (raytraceresult != null && ((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS && !flag && !ForgeEventFactory.onProjectileImpact(this, (RayTraceResult)raytraceresult)) {
                    this.onHit((RayTraceResult)raytraceresult);
                    this.isAirBorne = true;
                }

                if (entityraytraceresult == null) {
                    break;
                }

                raytraceresult = null;
            }

            vec3d = this.getMotion();
            double d1 = vec3d.x;
            double d2 = vec3d.y;
            double d0 = vec3d.z;
//            if (this.getIsCritical()) {
//                for(int i = 0; i < 4; ++i) {
//                    this.world.addParticle(ParticleTypes.CRIT, this.posX + d1 * (double)i / 4.0D, this.posY + d2 * (double)i / 4.0D, this.posZ + d0 * (double)i / 4.0D, -d1, -d2 + 0.2D, -d0);
//                }
//            }

            this.posX += d1;
            this.posY += d2;
            this.posZ += d0;
            float f4 = MathHelper.sqrt(func_213296_b(vec3d));
            if (flag) {
                this.rotationYaw = (float)(MathHelper.atan2(-d1, -d0) * 57.2957763671875D);
            } else {
                this.rotationYaw = (float)(MathHelper.atan2(d1, d0) * 57.2957763671875D);
            }

            for(this.rotationPitch = (float)(MathHelper.atan2(d2, (double)f4) * 57.2957763671875D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
                ;
            }

            while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
            this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
            float f1 = 0.99F;
            float f2 = 0.05F;
            if (this.isInWater()) {
                for(int j = 0; j < 4; ++j) {
                    float f3 = 0.25F;
                    this.world.addParticle(ParticleTypes.BUBBLE, this.posX - d1 * 0.25D, this.posY - d2 * 0.25D, this.posZ - d0 * 0.25D, d1, d2, d0);
                }

                f1 = this.getWaterDrag();
            }

            this.setMotion(vec3d.scale((double)f1));
            if (!this.hasNoGravity() && !flag) {
                Vec3d vec3d3 = this.getMotion();
                this.setMotion(vec3d3.x, vec3d3.y - 0.05000000074505806D, vec3d3.z);
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }

    }

    protected void tryDespawn() {
        ++this.ticksInGround;
        if (this.ticksInGround >= 1200) {
            this.remove();
        }

    }

    protected void onHit(RayTraceResult p_184549_1_) {
        RayTraceResult.Type raytraceresult$type = p_184549_1_.getType();
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            this.func_213868_a((EntityRayTraceResult)p_184549_1_);
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_184549_1_;
            BlockState blockstate = this.world.getBlockState(blockraytraceresult.getPos());
            this.inBlockState = blockstate;
            Vec3d vec3d = blockraytraceresult.getHitVec().subtract(this.posX, this.posY, this.posZ);
            this.setMotion(vec3d);
            Vec3d vec3d1 = vec3d.normalize().scale(0.05000000074505806D);
            this.posX -= vec3d1.x;
            this.posY -= vec3d1.y;
            this.posZ -= vec3d1.z;
            this.playSound(this.getHitGroundSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;
            this.func_213870_w();
            blockstate.onProjectileCollision(this.world, blockstate, blockraytraceresult, this);
        }

    }

    private void func_213870_w() {
        if (this.field_213875_aA != null) {
            this.field_213875_aA.clear();
        }

        if (this.field_213878_az != null) {
            this.field_213878_az.clear();
        }

    }

    protected void func_213868_a(EntityRayTraceResult p_213868_1_) {
        Entity entity = p_213868_1_.getEntity();
        float tmpDamage = getBasicDamage();

//        if (this.func_213874_s() > 0) {   // 穿透数>0
//            if (this.field_213878_az == null) {
//                this.field_213878_az = new IntOpenHashSet(5);
//            }
//
//            if (this.field_213875_aA == null) {
//                this.field_213875_aA = Lists.newArrayListWithCapacity(5);
//            }
//
//            if (this.field_213878_az.size() >= this.func_213874_s() + 1) {
//                this.remove();
//                return;
//            }
//
//            this.field_213878_az.add(entity.getEntityId());
//        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = MzDamageSourceCollection.causeMzAPCRDamage(this, this);
        } else {
            damagesource = MzDamageSourceCollection.causeMzAPCRDamage(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity)entity1).setLastAttackedEntity(entity);
            }
        }

        int j = entity.func_223314_ad();
        if (this.isBurning() && !(entity instanceof EndermanEntity)) {
            entity.setFire(5);
        }

        if (entity.attackEntityFrom(damagesource, tmpDamage)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;
                if (!this.world.isRemote) {
                    livingentity.setArrowCountInEntity(livingentity.getArrowCountInEntity() + 1);
                }

                if (this.knockbackStrength > 0) {
                    Vec3d vec3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockbackStrength * 0.6D);
                    if (vec3d.lengthSquared() > 0.0D) {
                        livingentity.addVelocity(vec3d.x, 0.1D, vec3d.z);
                    }
                }

                if (!this.world.isRemote && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity);
                }

                this.arrowHit(livingentity);
                if (entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)entity1).connection.sendPacket(new SChangeGameStatePacket(6, 0.0F));
                }

                if (!entity.isAlive() && this.field_213875_aA != null) {
                    this.field_213875_aA.add(livingentity);
                }

//                if (!this.world.isRemote && entity1 instanceof ServerPlayerEntity) {
//                    ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity1;
//                    if (this.field_213875_aA != null && this.func_213873_r()) {
//                        CriteriaTriggers.KILLED_BY_CROSSBOW.func_215105_a(serverplayerentity, this.field_213875_aA, this.field_213875_aA.size());
//                    } else if (!entity.isAlive() && this.func_213873_r()) {
//                        CriteriaTriggers.KILLED_BY_CROSSBOW.func_215105_a(serverplayerentity, Arrays.asList(entity), 0);
//                    }
//                }
            }

            this.playSound(this.soundEvent, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (!(entity instanceof EndermanEntity)) {
                this.remove();
            }
        } else {
            entity.func_223308_g(j);
            this.setMotion(this.getMotion().scale(-0.1D));
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            if (!this.world.isRemote && this.getMotion().lengthSquared() < 1.0E-7D) {
                if (this.pickupStatus == net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.entityDropItem(this.getArrowStack(), 0.1F);
                }

                this.remove();
            }
        }

    }

    protected final SoundEvent getHitGroundSound() {
        return this.soundEvent;
    }

    protected void arrowHit(LivingEntity p_184548_1_) {
    }

    @Nullable
    protected EntityRayTraceResult func_213866_a(Vec3d p_213866_1_, Vec3d p_213866_2_) {
        return ProjectileHelper.func_221271_a(this.world, this, p_213866_1_, p_213866_2_, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (p_lambda$func_213866_a$0_1_) -> {
            return !p_lambda$func_213866_a$0_1_.isSpectator() && p_lambda$func_213866_a$0_1_.isAlive() && p_lambda$func_213866_a$0_1_.canBeCollidedWith() && (p_lambda$func_213866_a$0_1_ != this.getShooter() || this.ticksInAir >= 5) && (this.field_213878_az == null || !this.field_213878_az.contains(p_lambda$func_213866_a$0_1_.getEntityId()));
        });
    }

    public void writeAdditional(CompoundNBT nbt) {
        nbt.putShort("life", (short)this.ticksInGround);
        if (this.inBlockState != null) {
            nbt.put("inBlockState", NBTUtil.writeBlockState(this.inBlockState));
        }

        nbt.putByte("shake", (byte)this.arrowShake);
        nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        nbt.putByte("pickup", (byte)this.pickupStatus.ordinal());
//        nbt.putByte("PierceLevel", this.func_213874_s());
        if (this.shootingEntity != null) {
            nbt.putUniqueId("OwnerUUID", this.shootingEntity);
        }

        nbt.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());

        nbt.putFloat("mz_calib", dataManager.get(dm_calib));
        nbt.putFloat("mz_wgtfac", dataManager.get(dm_wgtfac));
        nbt.putFloat("mz_dmgfac", dataManager.get(dm_dmgfac));
        nbt.putFloat("mz_penfac", dataManager.get(dm_penfac));
    }

    public void readAdditional(CompoundNBT nbt) {
        this.ticksInGround = nbt.getShort("life");
        if (nbt.contains("inBlockState", 10)) {
            this.inBlockState = NBTUtil.readBlockState(nbt.getCompound("inBlockState"));
        }

        this.arrowShake = nbt.getByte("shake") & 255;
        this.inGround = nbt.getByte("inGround") == 1;

        if (nbt.contains("pickup", 99)) {
            this.pickupStatus = net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.getByOrdinal(nbt.getByte("pickup"));
        } else if (nbt.contains("player", 99)) {
            this.pickupStatus = nbt.getBoolean("player") ? net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.ALLOWED : net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.DISALLOWED;
        }

        if (nbt.hasUniqueId("OwnerUUID")) {
            this.shootingEntity = nbt.getUniqueId("OwnerUUID");
        }

        dataManager.set(dm_calib, nbt.getFloat("mz_calib"));
        dataManager.set(dm_wgtfac, nbt.getFloat("mz_wgtfac"));
        dataManager.set(dm_dmgfac, nbt.getFloat("mz_dmgfac"));
        dataManager.set(dm_penfac, nbt.getFloat("mz_penfac"));
    }

    public void setShooter(@Nullable Entity p_212361_1_) {
        this.shootingEntity = p_212361_1_ == null ? null : p_212361_1_.getUniqueID();
        if (p_212361_1_ instanceof PlayerEntity) {
            this.pickupStatus = ((PlayerEntity)p_212361_1_).abilities.isCreativeMode ? net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.CREATIVE_ONLY : net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.ALLOWED;
        }

    }

    @Nullable
    public Entity getShooter() {
        return this.shootingEntity != null && this.world instanceof ServerWorld ? ((ServerWorld)this.world).getEntityByUuid(this.shootingEntity) : null;
    }

    public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
        if (!this.world.isRemote && (this.inGround || this.isNoClip()) && this.arrowShake <= 0) {
            boolean flag = this.pickupStatus == net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.ALLOWED || this.pickupStatus == net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.CREATIVE_ONLY && p_70100_1_.abilities.isCreativeMode || this.isNoClip() && this.getShooter().getUniqueID() == p_70100_1_.getUniqueID();
            if (this.pickupStatus == net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus.ALLOWED && !p_70100_1_.inventory.addItemStackToInventory(this.getArrowStack())) {
                flag = false;
            }

            if (flag) {
                p_70100_1_.onItemPickup(this, 1);
                this.remove();
            }
        }

    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public void setKnockbackStrength(int p_70240_1_) {
        this.knockbackStrength = p_70240_1_;
    }

    public boolean canBeAttackedWithItem() {
        return false;
    }

    protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
        return 0.0F;
    }


    public void setEnchantmentEffectsFromEntity(LivingEntity p_190547_1_, float p_190547_2_) {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, p_190547_1_);
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, p_190547_1_);

        if (j > 0) {
            this.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, p_190547_1_) > 0) {
            this.setFire(100);
        }

    }

    protected float getWaterDrag() {
        return 0.6F;
    }


    public boolean isNoClip() {    // FIXME: 这里写错了，收回动画丢失？
        if (!this.world.isRemote) {
            return this.noClip;
        }
        return false;
    }


    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
