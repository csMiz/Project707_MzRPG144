package com.mizfrank.mzrpg144.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class MzSwordIron extends MzSword {

    TranslationTextComponent txt_desc = new TranslationTextComponent("txt.mz_sword_iron_desc");
    TranslationTextComponent txt_atk = new TranslationTextComponent("txt.tag_atk");

    public MzSwordIron(Properties properties) {
        super(ItemTier.IRON, properties, 6.0f, 1.6f, 1.0f, 0.03f);
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
        return basicCrt + 0.3f * (3.5f - swordWeight);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        list.add(txt_desc);

        float atk_lb = getPlainDamage() - getPlainFluc();
        if (atk_lb < 0.0f) { atk_lb = 0.0f; }
        float atk_ub = getPlainDamage() + getPlainFluc();
        NumberFormat formatter = new DecimalFormat("0.0");
        String str_atk_lb = formatter.format(atk_lb);
        String str_atk_ub = formatter.format(atk_ub);
        String atkdesc = txt_atk.getUnformattedComponentText() + str_atk_lb + " - " + str_atk_ub;
        list.add((new StringTextComponent(atkdesc)).setStyle((new Style()).setColor(TextFormatting.DARK_GREEN)));
    }

}
