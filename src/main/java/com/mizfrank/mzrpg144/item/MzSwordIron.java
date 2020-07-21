package com.mizfrank.mzrpg144.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MzSwordIron extends MzSword {

    public MzSwordIron(Properties properties) {
        super(ItemTier.IRON, properties, 6.0f, 1.6f);
    }


    @Override
    public float getAttackDamage() {
        return basicAtk + 0.3f * swordWeight;
    }

    @Override
    public float getAttackSpeed() {
        return basicAtkSpeed - 0.07f * swordWeight;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        list.add(new StringTextComponent("A common sword in MzRPG mod."));
    }

}
