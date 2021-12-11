package com.mizfrank.mzrpg144.item.MzItemWeapon.MzCrossbow;


import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MzCrossbowWood extends MzCrossbow {

    public MzCrossbowWood(Properties properties) {
        super(properties.maxDamage(384));
        // 25.0f, 1.0f, 1.0f, 20.0f, 0.6f
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        if (stack.getTag() == null){
            setMzCrsBowInfo(stack, new float[]{ 25.0f, 1.0f, 1.0f, 20.0f, 0.6f });
        }
        super.addInformation(stack, world, list, flag);
        //list.add(1, txt_desc);
    }

}
