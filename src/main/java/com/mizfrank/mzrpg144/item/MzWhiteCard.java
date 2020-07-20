package com.mizfrank.mzrpg144.item;

import com.google.common.collect.Lists;
import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MzWhiteCard extends Item {

    public MzWhiteCard() {
        super(new Properties().maxStackSize(1).group(MzRPG.MZ_ITEMGROUP));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        IMzSpecialty playerSpec = player.getCapability(MzSpecialtyProvider.MZ_SPEC_CAP, null).orElse(null);
        int minor = playerSpec.getMinorType();
        if (minor >= 0){
            // remove the white card
            player.getHeldItem(hand).shrink(1);
            int returnEXP = playerSpec.forgetSpec(minor);
            if (returnEXP > 0){
                // give item
                //ItemStack item = new ItemStack(ModItems.goldSatchel);
                //player.inventory.addItemStackToInventory(item);
                // TODO
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Clear!"));
            }
            else{
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Clear with no return!"));
            }
            return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
        }
        else{
            return super.onItemRightClick(world, player, hand);
        }
    }
}
