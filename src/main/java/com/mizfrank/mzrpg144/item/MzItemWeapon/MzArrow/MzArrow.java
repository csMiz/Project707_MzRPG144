package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import com.mizfrank.mzrpg144.MzItemGroup;
import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.List;

public abstract class MzArrow extends Item {

    /**
     * 0 - Vanilla计算方式，计算护甲，不穿透盾牌
     * 1 - APCR，根据穿透力判断是否穿透盾牌
     * 2 -
     * */

    public MzArrow(Item.Properties prop) {
        super(prop.group(MzRPG.MZ_ITEMGROUP));
    }

//    public static List<Integer> getBasicInfo(ItemStack itemStack){
//        CompoundNBT nbt = itemStack.getTag();
//        if (nbt == null){ return new float[]{1.0f, 1.0f, 9.0f, 3.0f}; }
//        return new float[]{ nbt.getFloat("mz_calib"), nbt.getFloat("mz_wgtfac"),
//                nbt.getFloat("mz_dmgfac"), nbt.getFloat("mz_penfac") };
//    }

    public AbstractArrowEntity createArrow(World worldIn, ItemStack itemStack, LivingEntity livingEntity) {
//        MzArrowEntity entity = new MzArrowEntity(worldIn, livingEntity);
//        float[] info = getBasicInfo(itemStack);
//        entity.setMzArrowEntityProperties(info);
//        return entity;
        return null;
    }

    public MzArrowEntityEx createArrowEx(World worldIn, ItemStack itemStack, LivingEntity livingEntity) {
//        MzArrowEntityEx entity = new MzArrowEntityEx(worldIn, livingEntity);
//        float[] info = getBasicInfo(itemStack);
//        entity.setMzArrowEntityProperties(info);
//        return entity;
        return null;
    }

    public boolean isInfinite(ItemStack p_isInfinite_1_, ItemStack launcher, PlayerEntity player) {
        return false;
    }

}
