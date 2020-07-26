package com.mizfrank.mzrpg144.item.MzItemWeapon.MzSword;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MzSwordWood extends MzSword {

    TranslationTextComponent txt_desc = new TranslationTextComponent("txt.mz_sword_wood_desc");

    public MzSwordWood(Properties properties) {
        super(ItemTier.WOOD, properties, 4.0f, 2.0f, 1.2f, 2.2f);
    }

    @Override
    public float getPlainDamage() {
        return basicAtk + 0.1f * swordWeight;
    }

    @Override
    public float getPlainSpeed() {
        return basicAtkSpeed - 0.02f * swordWeight;
    }

    @Override
    public float getPlainFluc() {
        return basicAtkFluc + 0.05f * swordWeight;
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