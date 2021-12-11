package com.mizfrank.mzrpg144.item.MzItemWeapon.MzCrossbow;

import com.mizfrank.mzrpg144.MzAutoLoader;
import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.item.ItemCollection;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public abstract class MzCrossbow extends ShootableItem {

    protected boolean progressPass2 = false;
    protected boolean progressPass5 = false;

    public float finalInaccFactor = 1.0f;

    public float pullProgress = 0.0f;

    public boolean autoLoading = false;
    public boolean flag_quitAmmo = false;

    protected ItemStack loadingAmmo = null;
    protected ItemStack nextAmmo = null;


    public MzCrossbow(Properties properties) {
        super(properties.group(MzRPG.MZ_ITEMGROUP));
        this.addPropertyOverride(new ResourceLocation("pull"), (itemStack, worldIn, livingEntity) -> {
            if (livingEntity != null && itemStack.getItem() instanceof MzCrossbow) {
                if (isAmmoLoaded(itemStack)){
                    setPullProgress(worldIn, 1.0f);
                    return 0.0f;
                }
                else{
//                    pullProgress = (float)(itemStack.getUseDuration() - livingEntity.getItemInUseCount()) / fullPullTick;
//                    if (pullProgress > 1.0f){ pullProgress = 1.0f; }
                    if (itemStack.equals(livingEntity.getHeldItemMainhand())){
                        if (pullProgress >= 0.0f && pullProgress <= 1.0f){
                            return pullProgress;
                        }
                    }
                }
            }
            return 0.0F;
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), (itemStack, worldIn, livingEntity) -> {
            if (livingEntity != null && livingEntity instanceof PlayerEntity && itemStack.getItem() instanceof MzCrossbow){
                if (!isAmmoLoaded(itemStack)){
                    PlayerEntity player = (PlayerEntity) livingEntity;
                    if (itemStack.equals(player.getHeldItemMainhand())){
                        if (pullProgress > 0.001f){
                            return 1.0f;
                        }
                    }
                }
            }
            return 0.0f;
        });
        this.addPropertyOverride(new ResourceLocation("charged"), (itemStack, worldIn, livingEntity) -> {
            return livingEntity != null && isAmmoLoaded(itemStack) ? 1.0F : 0.0F;
        });
//        this.addPropertyOverride(new ResourceLocation("firework"), (itemStack, worldIn, livingEntity) -> {
//            return livingEntity != null && isCharged(itemStack) &&
//                    hasChargedProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
//        });
    }


    public float[] getMzCrsBowInfo(ItemStack itemStack){
        float[] result = new float[5];
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null){
            result[0] = 25.0f;
            result[1] = 1.0f;
            result[2] = 1.0f;
            result[3] = 20.0f;
            result[4] = 0.6f;
            setMzCrsBowInfo(itemStack, result);
        }
        else{
            result[0] = nbt.getFloat("mz_bow_fptick");
            result[1] = nbt.getFloat("mz_bow_vel");
            result[2] = nbt.getFloat("mz_bow_inacc");
            result[3] = nbt.getFloat("mz_bow_dyn");
            result[4] = nbt.getFloat("mz_bow_recv");
        }
        return result;
    }

    public float getFullPullTick(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null){
            return 25.0f;
        }
        float r = nbt.getFloat("mz_bow_fptick");
        if (r < 1.0f) { r = 25.0f; }
        return r;
    }

    public void setMzCrsBowInfo(ItemStack itemStack, float[] info){
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.putFloat("mz_bow_fptick", info[0]);
        nbt.putFloat("mz_bow_vel", info[1]);
        nbt.putFloat("mz_bow_inacc", info[2]);
        nbt.putFloat("mz_bow_dyn", info[3]);
        nbt.putFloat("mz_bow_recv", info[4]);
    }

    /**
     * 从NBT获取弹药信息
     */
    public ItemStack getCurrentAmmo(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getTag();
        if (nbt != null){
            if (nbt.contains("mz_ammo")){
                int[] ammoInfo = nbt.getIntArray("mz_ammo");
                ItemStack ammo = null;
                if (ammoInfo.length > 0){
                    if (ammoInfo[0] == 1){
                        ammo = MzArrowAP.parseAmmo(ammoInfo);
                    }
                    else if (ammoInfo[0] == 2){
                        ammo = MzArrowAPCR.parseAmmo(ammoInfo);
                    }
                    else if (ammoInfo[0] == 3){
                        ammo = MzArrowHE.parseAmmo(ammoInfo);
                    }
                    else{
                        ammo = MzArrowAP.parseAmmo(ammoInfo);
                    }
                }

                return ammo;
            }
        }
        return null;
    }

    /**
     * 装载弹药，写入NBT
     */
    public void setCurrentAmmo(ItemStack itemStackCrossbow, ItemStack itemStackAmmo) {
        CompoundNBT nbt = itemStackCrossbow.getOrCreateTag();
        if (itemStackAmmo == null){
            nbt.putIntArray("mz_ammo", new int[]{});
        }
        else{
            if (itemStackAmmo.isEmpty()){
                nbt.putIntArray("mz_ammo", new int[]{});
            }
            else{
                nbt.putIntArray("mz_ammo", ((MzArrow)(itemStackAmmo.getItem())).getBasicInfo(itemStackAmmo));
            }
        }
    }

    public boolean isAmmoLoaded(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getTag();
        if (nbt != null){
            int loaded = nbt.getInt("mz_ammo_loaded");
            if (loaded >= 1){
                return true;
            }
        }
        return false;
    }

    public void setPullProgress(World worldIn, float value){
        if (!worldIn.isRemote){
            pullProgress = value;
        }
    }

    public Predicate<ItemStack> getAmmoPredicate() {
        return ARROWS;
    }

    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return ARROWS;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        float fullPullTick = getFullPullTick(stack);
        float tmpP = (float)(stack.getUseDuration() - player.getItemInUseCount()) / fullPullTick;
        if (tmpP > 1.0f){ tmpP = 1.0f; }
        setPullProgress(player.world, tmpP);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (isAmmoLoaded(itemStack)) {    // 射击
            if (!worldIn.isRemote) {
                float[] crsbowInfo = getMzCrsBowInfo(itemStack);
                float velocityFactor = crsbowInfo[1];
                float basicInaccFactor = crsbowInfo[2];
                float dynamicInaccFactor = crsbowInfo[3];
                fireProjectiles(worldIn, player, hand, itemStack, 3.15f * velocityFactor, 1.0F * finalInaccFactor);
                // 测试连发
                //startAutoLoading();

                // set inacc after shooting
                float tmpInacc = basicInaccFactor +  dynamicInaccFactor * 0.3f;
                if (tmpInacc > finalInaccFactor){
                    finalInaccFactor = tmpInacc;
                }
            }
            // 清空装填
            CompoundNBT nbt = itemStack.getOrCreateTag();
            nbt.putInt("mz_ammo_loaded", 0);
            nbt.putIntArray("mz_ammo", new int[]{});
            setPullProgress(worldIn, 0.0f);
            setCurrentAmmo(itemStack, null);
            return new ActionResult(ActionResultType.SUCCESS, itemStack);
        }
        else if (autoLoading) {    // 自动装填中
            // 不取消自动装填
            // 取消自动装填的工作绑定到C键
            return new ActionResult(ActionResultType.FAIL, itemStack);
        }
        else if (playerFindAmmo(player)) {    // 开始手动拉弦
            startLoadingAmmo(worldIn, player);
            this.progressPass2 = false;
            this.progressPass5 = false;
            player.setActiveHand(hand);
            return new ActionResult(ActionResultType.SUCCESS, itemStack);
        } else {
            return new ActionResult(ActionResultType.FAIL, itemStack);
        }
    }

    public void startAutoLoading(){
        // This event is called in Client only
        pullProgress = -1.0f;
        autoLoading = true;
    }

    public void switchAmmo(){
        // This event is called in Client only
        if (autoLoading){
            System.out.println("Quit Auto-loading");
            pullProgress = -2.0f;
        }
        else{
            System.out.println("Quit Ammo");
            pullProgress = 0.0f;
            flag_quitAmmo = true;
        }
    }

    public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, LivingEntity livingEntity, int tickIn) {
        if (pullProgress >= 1.0F && !isAmmoLoaded(itemStack)) {
            endLoadingAmmo(worldIn, itemStack);
            SoundCategory soundCategory = livingEntity instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            worldIn.playSound((PlayerEntity)null, livingEntity.posX, livingEntity.posY, livingEntity.posZ,
                    SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0F,
                    1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
        else if (pullProgress < 1.0F){
            setPullProgress(worldIn, 0.0f);
        }

    }

    private static void createArrowEntity(World worldIn, LivingEntity livingEntity, Hand hand, ItemStack itemStackCrossbow,
                                          ItemStack itemStackAmmo, boolean isCreative,
                                          float vel, float inacc) {
        if (!worldIn.isRemote) {
            MzArrow arrowItem = (MzArrow)itemStackAmmo.getItem();
            // MzArrowEntityEx arrowEntity = arrowItem.createArrowEx(worldIn, itemStackAmmo, livingEntity);
            MzArrowEntityEx arrowEntity = arrowItem.createArrowEx(worldIn, itemStackAmmo, livingEntity);
            if (livingEntity instanceof PlayerEntity) {
                //arrowEntity.setIsCritical(true);
            }
            arrowEntity.setHitSound(SoundEvents.ITEM_CROSSBOW_HIT);
            //arrowEntity.func_213865_o(true);

            if (isCreative) {
                arrowEntity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }

            if (livingEntity instanceof ICrossbowUser) {
                // Enermy AI using crossbow
                // ICrossbowUser lvt_12_1_ = (ICrossbowUser)livingEntity;
                // lvt_12_1_.shoot(lvt_12_1_.getAttackTarget(), itemStackCrossbow, (IProjectile)arrowEntity, p_220016_9_);
            } else {
                Vec3d lvt_12_2_ = livingEntity.func_213286_i(1.0F);
                Quaternion lvt_13_1_ = new Quaternion(new Vector3f(lvt_12_2_), 0.0f, true);
                Vec3d lvt_14_1_ = livingEntity.getLook(1.0F);
                Vector3f lvt_15_1_ = new Vector3f(lvt_14_1_);
                lvt_15_1_.func_214905_a(lvt_13_1_);
                arrowEntity.shoot((double)lvt_15_1_.getX(),
                        (double)lvt_15_1_.getY(),
                        (double)lvt_15_1_.getZ(),
                        vel, inacc);
            }

            itemStackCrossbow.damageItem(1, livingEntity, (livingEntity1) -> {
                livingEntity1.sendBreakAnimation(hand);
            });
            worldIn.addEntity(arrowEntity);
            worldIn.playSound(null, livingEntity.posX, livingEntity.posY, livingEntity.posZ,
                    SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0f);
        }
    }

    public void fireProjectiles(World worldIn, LivingEntity livingEntity, Hand hand, ItemStack itemStack, float vel, float inacc) {
        ItemStack ammo = getCurrentAmmo(itemStack);

        boolean isCreative = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.isCreativeMode;
        if (ammo != null) {
            createArrowEntity(worldIn, livingEntity, hand, itemStack, ammo, isCreative, vel, inacc);

            addStats(worldIn, livingEntity, itemStack);

        }

    }

    private void addStats(World worldIn, LivingEntity player, ItemStack itemStack) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity lvt_3_1_ = (ServerPlayerEntity)player;
            if (!worldIn.isRemote) {
                CriteriaTriggers.SHOT_CROSSBOW.func_215111_a(lvt_3_1_, itemStack);
            }

            lvt_3_1_.addStat(Stats.ITEM_USED.get(itemStack.getItem()));
        }
    }

    public void func_219972_a(World worldIn, LivingEntity livingEntity, ItemStack itemStack, int useTick) {
        // Play sound
        if (!worldIn.isRemote) {
            SoundEvent sound1 = SoundEvents.ITEM_CROSSBOW_LOADING_START;
            SoundEvent sound2 = SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE;
            float fullPullTick = getFullPullTick(itemStack);
            float progress = (float)(itemStack.getUseDuration() - useTick) / fullPullTick;
            if (progress < 0.2F) {
                this.progressPass2 = false;
                this.progressPass5 = false;
            }

            if (progress >= 0.2F && !this.progressPass2) {
                this.progressPass2 = true;
                worldIn.playSound(null, livingEntity.posX, livingEntity.posY, livingEntity.posZ,
                        sound1, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }

            if (progress >= 0.5F && sound2 != null && !this.progressPass5) {
                this.progressPass5 = true;
                worldIn.playSound(null, livingEntity.posX, livingEntity.posY, livingEntity.posZ,
                        sound2, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.CROSSBOW;
    }

    // 自定义识别弹药
    public boolean playerFindAmmo(PlayerEntity player){
        for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack tmpItem = player.inventory.getStackInSlot(i);
            if (tmpItem.getItem() instanceof MzArrow) {
                nextAmmo = tmpItem;
                return true;
            }
        }
        //nextAmmo = new ItemStack(ItemCollection.MZ_ARROW_AP.get());
        return false;
    }

    // 开始装填，减少背包里的弹药
    public void startLoadingAmmo(World worldIn, PlayerEntity player){
        if (!worldIn.isRemote){
            ItemStack itemStackAmmo = nextAmmo;
            loadingAmmo = itemStackAmmo.copy();

            // deleteStack will remove all ammo in one stack!
            // player.inventory.deleteStack(itemStackAmmo);
            int tmpSlot = player.inventory.getSlotFor(itemStackAmmo);
            player.inventory.getStackInSlot(tmpSlot).shrink(1);

            nextAmmo = null;
        }
    }

    public void endLoadingAmmo(World worldIn, ItemStack itemStackCrossbow){
        CompoundNBT nbt = itemStackCrossbow.getOrCreateTag();
        nbt.putInt("mz_ammo_loaded", 1);
        setCurrentAmmo(itemStackCrossbow, loadingAmmo);
        if (!worldIn.isRemote){
            loadingAmmo = null;
        }

    }

    // 归还没有装填完的弹药
    public void revertAmmo(World worldIn, PlayerEntity player){
        if (!worldIn.isRemote){
            if (loadingAmmo != null){
                player.inventory.addItemStackToInventory(loadingAmmo);
                loadingAmmo = null;
            }
        }
        ItemStack crsbow = player.getHeldItemMainhand();
        if (isAmmoLoaded(crsbow)){
            ItemStack ammo = getCurrentAmmo(crsbow);
            if ((ammo != null) && (!worldIn.isRemote)){
                player.inventory.addItemStackToInventory(ammo);
            }
            CompoundNBT nbt = crsbow.getOrCreateTag();
            nbt.putInt("mz_ammo_loaded", 0);
            setCurrentAmmo(crsbow, null);
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int slot, boolean inHand) {
        if (autoLoading && entity != null){
            if (entity instanceof PlayerEntity){
                PlayerEntity player = (PlayerEntity)entity;
                if (itemStack.equals(player.getHeldItemMainhand())){
                    float tmpP = pullProgress;
                    if (tmpP == -1.0f){  // initialize auto load
                        if (playerFindAmmo(player)){
                            if (!world.isRemote){ MzAutoLoader.TARGET = itemStack; }
                            startLoadingAmmo(world, player);
                            this.progressPass2 = false;
                            this.progressPass5 = false;
                            tmpP = 0.0f;
                        }
                        else{
                            if (!world.isRemote){
                                autoLoading = false;
                            }
                        }
                    }
                    else if (tmpP == -2.0f){
                        // 退弹
                        if (!world.isRemote){
                            autoLoading = false;
                            revertAmmo(world, player);
                        }
                        tmpP = 0.0f;
                    }
                    if (!world.isRemote){
                        if (!MzAutoLoader.TARGET.equals(itemStack)){  // 切换物品时退出自动装填模式
                            autoLoading = false;
                            setPullProgress(world, 0.0f);
                            return;
                        }
                    }
                    boolean loadFinished = false;
                    tmpP += (1.0f/getFullPullTick(itemStack));
                    if (tmpP > 1.0f){
                        tmpP = 1.0f;
                        loadFinished = true;
                    }
                    setPullProgress(world, tmpP);
                    if (loadFinished){
                        if (!world.isRemote){
                            autoLoading = false;
                        }
                        onPlayerStoppedUsing(itemStack, world, (LivingEntity)entity, 0);
                    }
                }
            }
        }
        if (flag_quitAmmo && entity != null){
            if (entity instanceof PlayerEntity){
                PlayerEntity player = (PlayerEntity)entity;
                if (itemStack.equals(player.getHeldItemMainhand())){
                    if (!world.isRemote){
                        flag_quitAmmo = false;
                        autoLoading = false;
                        revertAmmo(world, player);
                    }
                }
            }
        }
        if (entity != null){
            if (entity instanceof  PlayerEntity){
                if (world.isRemote){
                    // for cross-hair, client only
                    float[] info = getMzCrsBowInfo(itemStack);
                    float accRecoverFactor = info[4];
                    float basicInaccFactor = info[2];
                    float dynamicInaccFactor = info[3];

                    finalInaccFactor -= accRecoverFactor;
                    if (finalInaccFactor < basicInaccFactor){
                        finalInaccFactor = basicInaccFactor;
                    }
                    float tmpInacc = basicInaccFactor +  dynamicInaccFactor*(float)(entity.getMotion().length());
                    if (tmpInacc > finalInaccFactor){
                        finalInaccFactor = tmpInacc;
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
//        List<ItemStack> lvt_5_1_ = getChargedProjectiles(p_77624_1_);
//        if (ammoLoaded && !lvt_5_1_.isEmpty()) {
//            ItemStack lvt_6_1_ = (ItemStack)lvt_5_1_.get(0);
//            p_77624_3_.add((new TranslationTextComponent("item.minecraft.crossbow.projectile", new Object[0])).appendText(" ").appendSibling(lvt_6_1_.getTextComponent()));
//            if (p_77624_4_.isAdvanced() && lvt_6_1_.getItem() == Items.FIREWORK_ROCKET) {
//                List<ITextComponent> lvt_7_1_ = Lists.newArrayList();
//                Items.FIREWORK_ROCKET.addInformation(lvt_6_1_, p_77624_2_, lvt_7_1_, p_77624_4_);
//                if (!lvt_7_1_.isEmpty()) {
//                    for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_.size(); ++lvt_8_1_) {
//                        lvt_7_1_.set(lvt_8_1_, (new StringTextComponent("  ")).appendSibling((ITextComponent)lvt_7_1_.get(lvt_8_1_)).applyTextStyle(TextFormatting.GRAY));
//                    }
//
//                    p_77624_3_.addAll(lvt_7_1_);
//                }
//            }
//
//        }
    }

}
