package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import com.mizfrank.mzrpg144.item.ItemCollection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MzArrowAP extends MzArrow {


    public MzArrowAP(Properties prop) {
        super(prop);
    }

    public static int[] getBasicInfo(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null){ return new int[]{ 1, 1000, 1000, 9000, 3000}; }
        int[] r = new int[5];
        r[0] = 1;
        r[1] = nbt.getInt("mz_calib");
        r[2] = nbt.getInt("mz_wgtfac");
        r[3] = nbt.getInt("mz_dmgfac");
        r[4] = nbt.getInt("mz_penfac");

        return r;
    }

    @Override
    public AbstractArrowEntity createArrow(World worldIn, ItemStack itemStack, LivingEntity livingEntity) {
        MzArrowEntity entity = new MzArrowEntity(worldIn, livingEntity);
        int[] info = getBasicInfo(itemStack);
        entity.setMzArrowEntityProperties(info);
        return entity;
    }

    @Override
    public MzArrowEntityEx createArrowEx(World worldIn, ItemStack itemStack, LivingEntity livingEntity) {
        MzArrowEntityEx entity = new MzArrowEntityEx(worldIn, livingEntity);
        int[] info = getBasicInfo(itemStack);
        entity.setMzArrowEntityProperties(info);
        return entity;
    }

    public static ItemStack parseAmmo(int[] info){
        ItemStack r = new ItemStack(ItemCollection.MZ_ARROW_AP.get());
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("mz_arwtype", info[0]);
        nbt.putInt("mz_calib", info[1]);
        nbt.putInt("mz_wgtfac", info[2]);
        nbt.putInt("mz_dmgfac", info[3]);
        nbt.putInt("mz_penfac", info[4]);
        r.setTag(nbt);
        return r;
    };

}
