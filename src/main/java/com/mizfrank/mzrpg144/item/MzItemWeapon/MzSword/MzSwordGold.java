package com.mizfrank.mzrpg144.item.MzItemWeapon.MzSword;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MzSwordGold extends MzSword {

    TranslationTextComponent txt_desc = new TranslationTextComponent("txt.mz_sword_gold_desc");

    public MzSwordGold(Properties properties) {
        super(ItemTier.GOLD, properties, 5.0f, 1.3f, 0.8f, 2.5f);
    }

    @Override
    public float getPlainDamage() {
        return basicAtk + 0.4f * swordWeight;
    }

    @Override
    public float getPlainSpeed() {
        return basicAtkSpeed - 0.07f * swordWeight;
    }

    @Override
    public float getPlainFluc() {
        return basicAtkFluc + 0.16f * swordWeight;
    }

    @Override
    public float getPlainCrt() {
        return basicAtkCrt + 0.15f * (3.5f - swordWeight);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        list.add(1, txt_desc);
    }

}
