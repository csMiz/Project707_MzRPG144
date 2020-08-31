package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import com.mizfrank.mzrpg144.item.ItemCollection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class MzArrowHE extends MzArrow {

    public MzArrowHE(Item.Properties prop) {
        super(prop);
    }

    @Override
    public int[] getBasicInfo(ItemStack itemStack){
        CompoundNBT nbt = itemStack.getTag();
        if (nbt == null){ return new int[]{ 3, 1000, 1000, 6000, 1000 }; }
        int[] r = new int[5];
        r[0] = nbt.getByte("mz_arwtype");
        r[1] = nbt.getInt("mz_calib");
        r[2] = nbt.getInt("mz_wgtfac");
        r[3] = nbt.getInt("mz_dmgfac");
        r[4] = nbt.getInt("mz_penfac");

        return r;
    }

    @Override
    public MzArrowEntityEx createArrowEx(World worldIn, ItemStack itemStack, LivingEntity livingEntity) {
        MzArrowEntityEx entity = new MzArrowEntityEx(worldIn, livingEntity);
        int[] info = getBasicInfo(itemStack);
        entity.setMzArrowEntityProperties(info);
        return entity;
    }

    public static ItemStack parseAmmo(int[] info){
        ItemStack r = new ItemStack(ItemCollection.MZ_ARROW_HE.get());
        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("mz_arwtype", (byte)info[0]);
        nbt.putInt("mz_calib", info[1]);
        nbt.putInt("mz_wgtfac", info[2]);
        nbt.putInt("mz_dmgfac", info[3]);
        nbt.putInt("mz_penfac", info[4]);
        r.setTag(nbt);
        return r;
    }

}
