package com.mizfrank.mzrpg144.item.MzItemWeapon.MzSword;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MzSwordSteel extends MzSword {

    TranslationTextComponent txt_desc = new TranslationTextComponent("txt.mz_sword_steel_desc");

    public MzSwordSteel(Properties properties) {
        super(ItemTier.IRON, properties, 8.0f, 1.8f, 1.0f, 3.0f);
    }

    @Override
    public float getPlainDamage() {
        return basicAtk + 0.28f * swordWeight;
    }

    @Override
    public float getPlainSpeed() {
        return basicAtkSpeed - 0.08f * swordWeight;
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
