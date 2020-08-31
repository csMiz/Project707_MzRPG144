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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.function.Predicate;

public abstract class MzArrow extends Item {


    public static Predicate<ItemStack> MZ_ARROW_PREDICATE = (itemStack) -> {
        return itemStack.getItem() instanceof MzArrow;
    };

    public static boolean isHE(ItemStack itemStack){
        Item tmpItem = itemStack.getItem();
        if (tmpItem instanceof MzArrow){
            CompoundNBT nbt = itemStack.getTag();
            if (nbt == null){
                return (tmpItem instanceof MzArrowHE);
            }
            else{
                byte arwType = nbt.getByte("mz_arwtype");
                return (arwType == 3 || arwType == 4);
            }
        }
        return false;
    }
    /**
     * 0 - Vanilla计算方式，计算护甲，不穿透盾牌
     * 1 - APCR，根据穿透力判断是否穿透盾牌
     * 2 -
     * */

    public MzArrow(Item.Properties prop) {
        super(prop.group(MzRPG.MZ_ITEMGROUP));
    }

    public abstract int[] getBasicInfo(ItemStack itemStack);

    public abstract MzArrowEntityEx createArrowEx(World worldIn, ItemStack itemStack, LivingEntity livingEntity);

    public boolean isInfinite(ItemStack p_isInfinite_1_, ItemStack launcher, PlayerEntity player) {
        return false;
    }

}
