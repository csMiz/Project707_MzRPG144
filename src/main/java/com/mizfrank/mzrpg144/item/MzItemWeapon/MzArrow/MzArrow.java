package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class MzArrow extends Item {

    public MzArrow(Item.Properties p_i48531_1_) {
        super(p_i48531_1_);
    }

    public AbstractArrowEntity createArrow(World p_200887_1_, ItemStack p_200887_2_, LivingEntity p_200887_3_) {
        ArrowEntity arrowentity = new ArrowEntity(p_200887_1_, p_200887_3_);
        arrowentity.setPotionEffect(p_200887_2_);
        return arrowentity;
    }

    public boolean isInfinite(ItemStack p_isInfinite_1_, ItemStack launcher, PlayerEntity player) {
//        int enchant = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, launcher);
//        return enchant <= 0 ? false : true;
        return false;
    }

}
