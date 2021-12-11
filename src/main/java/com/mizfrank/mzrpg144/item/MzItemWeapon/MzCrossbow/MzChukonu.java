package com.mizfrank.mzrpg144.item.MzItemWeapon.MzCrossbow;


import com.mizfrank.mzrpg144.MzAutoLoader;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrow;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowAP;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowAPCR;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowHE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

/**
 * mz_bow_fptick 用于短装填：
 * full pull tick - full load tick
 *
 * */
public class MzChukonu extends MzCrossbow {

    // 长装填进度条
    public float singleLoadProgress = 0.0f;

    public int loadingMode = 0;  // 0-none 1-long load 2-short load

    public MzChukonu(Properties properties) {
        super(properties.maxDamage(384));
    }

    @Override
    public float[] getMzCrsBowInfo(ItemStack itemStack){
        float[] result = new float[7];
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null){
            result[0] = 16.0f;
            result[1] = 0.8f;
            result[2] = 1.8f;
            result[3] = 28.0f;
            result[4] = 0.6f;
            result[5] = 60.0f;
            result[6] = 6.0f;
            setMzCrsBowInfo(itemStack, result);
        }
        else{
            result[0] = nbt.getFloat("mz_bow_fptick");
            result[1] = nbt.getFloat("mz_bow_vel");
            result[2] = nbt.getFloat("mz_bow_inacc");
            result[3] = nbt.getFloat("mz_bow_dyn");
            result[4] = nbt.getFloat("mz_bow_recv");
            result[5] = nbt.getFloat("mz_ckn_fltick");
            result[6] = (float)nbt.getInt("mz_ckn_maxammo");
        }
        return result;
    }

    @Override
    public void setMzCrsBowInfo(ItemStack itemStack, float[] info){
        CompoundNBT nbt = itemStack.getOrCreateTag();
        nbt.putFloat("mz_bow_fptick", info[0]);
        nbt.putFloat("mz_bow_vel", info[1]);
        nbt.putFloat("mz_bow_inacc", info[2]);
        nbt.putFloat("mz_bow_dyn", info[3]);
        nbt.putFloat("mz_bow_recv", info[4]);
        nbt.putFloat("mz_ckn_fltick", info[5]);
        nbt.putInt("mz_ckn_maxammo", (int)info[6]);
    }

    public float getFullLoadTick(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null){
            float[] info = getMzCrsBowInfo(itemStack);
            return (int)info[5];
        }
        float r = nbt.getFloat("mz_ckn_fltick");
        if (r < 1.0f) { r = 20.0f; }
        return r;
    }

    /**
     * 获取装弹(1)+备弹(n)数量
     * */
    public int getCurrentAmmoCount(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getOrCreateTag();
        if (nbt.contains("mz_ckn_ammocount")){
            return nbt.getInt("mz_ckn_ammocount");
        }
        else{
            nbt.putInt("mz_ckn_ammocount", 0);
            return 0;
        }
    }

    public int getMaxAmmoCount(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getOrCreateTag();
        if (nbt.contains("mz_ckn_maxammo")){
            return nbt.getInt("mz_ckn_maxammo");
        }
        else{
            float[] info = getMzCrsBowInfo(itemStack);
            return (int)info[6];
        }
    }

    /**
     * 获取弹夹最下方的弹药
     * */
    @Override
    public ItemStack getCurrentAmmo(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getTag();
        if (nbt != null){
            ListNBT ammoList = nbt.getList("mz_ammo", 11);  // see INBT: 11 is IntArray
            if (!ammoList.isEmpty()){
                int[] ammoInfo = ammoList.getIntArray(0);
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
     * 装载弹药至弹夹最上方，写入NBT
     */
    @Override
    public void setCurrentAmmo(ItemStack itemStackCrossbow, ItemStack itemStackAmmo) {
        CompoundNBT nbt = itemStackCrossbow.getOrCreateTag();
        ListNBT ammoList = nbt.getList("mz_ammo", 11);
        if (itemStackAmmo != null){
            IntArrayNBT arr = new IntArrayNBT(((MzArrow)(itemStackAmmo.getItem())).getBasicInfo(itemStackAmmo));
            ammoList.add(arr);
            nbt.put("mz_ammo", ammoList);
            int count = nbt.getInt("mz_ckn_ammocount");
            nbt.putInt("mz_ckn_ammocount", count+1);
        }
    }

    public void setSingleLoadProgress(World worldIn, float value){
        if (!worldIn.isRemote){
            singleLoadProgress = value;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (isAmmoLoaded(itemStack)) {    // 射击
            if (!worldIn.isRemote) {
                float[] crsbowInfo = getMzCrsBowInfo(itemStack);
                float velocityFactor = crsbowInfo[1];
                fireProjectiles(worldIn, player, hand, itemStack, 3.15f * velocityFactor, 1.0F * finalInaccFactor);
            }
            // 清空装填 清除弹夹底部
            CompoundNBT nbt = itemStack.getOrCreateTag();
            nbt.putInt("mz_ammo_loaded", 0);
            ListNBT ammoList = nbt.getList("mz_ammo", 11);
            if (!ammoList.isEmpty()){
                ammoList.remove(0);
            }
            int count = nbt.getInt("mz_ckn_ammocount");
            count -= 1;
            nbt.putInt("mz_ckn_ammocount", count);
            setPullProgress(worldIn, -1.0f);
            // 中断备弹装填
            if (singleLoadProgress <= 0.99f){
                setSingleLoadProgress(worldIn, 0.0f);
            }
            return new ActionResult(ActionResultType.SUCCESS, itemStack);
        }
        else{
            if (!worldIn.isRemote){
                if (loadingMode == 0){
                    startAutoLoading();
                }
            }
            return new ActionResult(ActionResultType.FAIL, itemStack);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, LivingEntity livingEntity, int tickIn) {
    }

    @Override
    public void startAutoLoading(){
        if (pullProgress == 0.0f) {  // initialize auto load
            pullProgress = -1.0f;
        }
        else if (pullProgress == 1.0f && singleLoadProgress == 0.0f){
            singleLoadProgress = -1.0f;
        }
    }

    // 完成装填到弹夹
    @Override
    public void endLoadingAmmo(World worldIn, ItemStack itemStackCrossbow){
        setCurrentAmmo(itemStackCrossbow, loadingAmmo);
        if (!worldIn.isRemote){
            loadingAmmo = null;
        }
    }

    // 开始短装填
    public void startLoadingAmmoShort(World worldIn, PlayerEntity player){
    }

    // 完成短装填
    public void endLoadingAmmoShort(World worldIn, ItemStack itemStackCrossbow){
        CompoundNBT nbt = itemStackCrossbow.getOrCreateTag();
        nbt.putInt("mz_ammo_loaded", 1);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int slot, boolean inHand) {
        if (entity != null && entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity)entity;
            if (itemStack.equals(player.getHeldItemMainhand())){
                if (pullProgress == -1.0f) {  // initialize auto load
                    if (getCurrentAmmoCount(itemStack) == 0) {
                        // 装填备弹模式
                        if (!world.isRemote) {
                            if (playerFindAmmo(player)){
                                loadingMode = 1;
                                MzAutoLoader.TARGET = itemStack;
                                startLoadingAmmo(world, player);
                                pullProgress = 0.0f;
                                singleLoadProgress = 0.0f;
                            }
                            else{
                                singleLoadProgress = 0.0f;
                                loadingMode = 0;
                            }
                        }
                    } else {
                        // 短装填模式
                        if (!world.isRemote) {
                            loadingMode = 2;
                            MzAutoLoader.TARGET = itemStack;
                            startLoadingAmmoShort(world, player);
                            pullProgress = 0.0f;
                        }
                    }
                }
                else if (pullProgress == -2.0f){
                    // 退弹
                    // TODO
                }
                if (singleLoadProgress == -1.0f){
                    // 装填备弹模式
                    if (!world.isRemote) {
                        if (playerFindAmmo(player)){
                            loadingMode = 1;
                            MzAutoLoader.TARGET = itemStack;
                            startLoadingAmmo(world, player);
                            singleLoadProgress = 0.0f;
                        }
                        else{
                            singleLoadProgress = 0.0f;
                            loadingMode = 0;
                        }
                    }
                }

                if (!world.isRemote){
                    if (MzAutoLoader.TARGET == null){
                        setPullProgress(world, 0.0f);
                        setSingleLoadProgress(world, 0.0f);
                        return;
                    }
                    else if (!MzAutoLoader.TARGET.equals(itemStack)){  // 切换物品时退出自动装填模式
                        setPullProgress(world, 0.0f);
                        setSingleLoadProgress(world, 0.0f);
                        return;
                    }
                }
                if (loadingMode == 1){  // long load
                    boolean loadFinished = false;
                    float tmpP = singleLoadProgress;
                    tmpP += (1.0f/getFullLoadTick(itemStack));
                    if (tmpP > 1.0f){
                        tmpP = 1.0f;
                        loadFinished = true;
                    }
                    setSingleLoadProgress(world, tmpP);
                    if (loadFinished){
                        endLoadingAmmo(world, itemStack);
                        if (pullProgress < 1.0f){  // 短装填未完成，优先进行短装填
                            setPullProgress(world, -1.0f);
                        }
                        else if (getCurrentAmmoCount(itemStack) < getMaxAmmoCount(itemStack)){
                            // 短装填已完成，补充备弹
                            setSingleLoadProgress(world, -1.0f);
                        }
                    }
                }
                else if (loadingMode == 2){  // short load
                    boolean loadFinished = false;
                    float tmpP = pullProgress;
                    tmpP += (1.0f/getFullPullTick(itemStack));
                    if (tmpP > 1.0f){
                        tmpP = 1.0f;
                        loadFinished = true;
                    }
                    setPullProgress(world, tmpP);
                    if (loadFinished){
                        endLoadingAmmoShort(world, itemStack);
                        // 短装填完成，继续进行长装填
                        if (getCurrentAmmoCount(itemStack) < getMaxAmmoCount(itemStack)){
                            setSingleLoadProgress(world, -1.0f);
                        }
                    }
                }
            }
        }
        if (flag_quitAmmo && entity != null){
            // TODO
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

}
