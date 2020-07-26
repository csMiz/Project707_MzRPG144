package com.mizfrank.mzrpg144.item.MzItemWeapon.MzSword;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MzSwordIron extends MzSword {

    TranslationTextComponent txt_desc = new TranslationTextComponent("txt.mz_sword_iron_desc");

    public MzSwordIron(Properties properties) {
        super(ItemTier.IRON, properties, 6.0f, 1.6f, 1.0f, 3.0f);
    }

    @Override
    public float getPlainDamage() {
        return basicAtk + 0.3f * swordWeight;
    }

    @Override
    public float getPlainSpeed() {
        return basicAtkSpeed - 0.07f * swordWeight;
    }

    @Override
    public float getPlainFluc() {
        return basicAtkFluc + 0.2f * swordWeight;
    }

    @Override
    public float getPlainCrt() {
        return basicAtkCrt + 0.3f * (3.5f - swordWeight);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        list.add(1, txt_desc);
    }

}
