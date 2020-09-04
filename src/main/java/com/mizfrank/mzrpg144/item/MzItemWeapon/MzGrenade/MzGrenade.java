package com.mizfrank.mzrpg144.item.MzItemWeapon.MzGrenade;

import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MzGrenade extends Item {

    public MzGrenade(Properties prop) {
        super(prop.maxStackSize(4).group(MzRPG.MZ_ITEMGROUP));

    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int currentUseDur) {

    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        return null;
    }


}
