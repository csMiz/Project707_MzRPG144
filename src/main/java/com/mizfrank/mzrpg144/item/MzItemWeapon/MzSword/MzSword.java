package com.mizfrank.mzrpg144.item.MzItemWeapon.MzSword;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mizfrank.mzrpg144.item.MzItemEnchant.MzSwordEnchant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Consumer;

public abstract class MzSword extends TieredItem {

    protected TranslationTextComponent txt_atk_desc = new TranslationTextComponent("txt.tag_atk");
    protected TranslationTextComponent txt_atkCrt_desc = new TranslationTextComponent("txt.tag_atkcrt");

    protected float basicAtk;
    protected float basicAtkSpeed;
    protected float basicAtkFluc;
    protected float basicAtkCrt;
    protected int swordWeight = 0;
    protected MzSwordEnchant enchant = new MzSwordEnchant();


    protected MzSword(IItemTier material, Properties properties, float inAtk, float inAtkSpeed, float inAtkFluc, float inCrt) {
        super(material, properties);
        basicAtk = inAtk;
        basicAtkSpeed = inAtkSpeed;
        basicAtkFluc = inAtkFluc;
        basicAtkCrt = inCrt;
    }

    public abstract float getPlainDamage();

    public abstract float getPlainSpeed();

    public abstract float getPlainFluc();

    public abstract float getPlainCrt();


    public boolean canPlayerBreakBlockWhileHolding(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_,
                                                   PlayerEntity p_195938_4_) {
        return !p_195938_4_.isCreative();
    }

    public float getDestroySpeed(ItemStack p_150893_1_, BlockState targetBlockState) {
        Block targetBlock = targetBlockState.getBlock();
        if (targetBlock == Blocks.COBWEB) {
            return 15.0F;
        } else {
            Material targetBlockMat = targetBlockState.getMaterial();
            return targetBlockMat != Material.PLANTS && targetBlockMat != Material.TALL_PLANTS &&
                    targetBlockMat != Material.CORAL && !targetBlockState.isIn(BlockTags.LEAVES) &&
                    targetBlockMat != Material.GOURD ? 1.0F : 1.5F;
        }
    }

    // attackTargetEntityWithCurrentItem内部会调用此方法
    public boolean hitEntity(ItemStack self, LivingEntity enemy, LivingEntity playerSelf) {
        self.damageItem(1, playerSelf, (p_220045_0_) -> {
            p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
        return true;  // True就会记录到成就里
    }

    public boolean onBlockDestroyed(ItemStack self, World p_179218_2_, BlockState targetBlockState,
                                    BlockPos p_179218_4_, LivingEntity p_179218_5_) {
        if (targetBlockState.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0F) {
            self.damageItem(2, p_179218_5_, (p_220044_0_) -> {
                p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
            });
        }
        return true;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int durRemain = stack.getMaxDamage() - stack.getDamage();
        CompoundNBT tags = stack.getTag();
        float decDur = tags.getFloat("mz_dec_dur");
        float tmpDamage = amount * decDur;
        if (durRemain < tmpDamage){
            // break item
            int tmpStatus = tags.getInt("mz_status");
            if (tmpStatus % 2 != 1){
                tmpStatus += 1;
                tags.putInt("mz_status", tmpStatus);
            }
            return 0;
        }
        else{
            return (int)(tmpDamage);
        }
    }

    public boolean canHarvestBlock(BlockState targetBlockState) {
        return targetBlockState.getBlock() == Blocks.COBWEB;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        if (nbt == null){
            nbt = new CompoundNBT();
            nbt.putInt("mz_type", 1);  // 1-sword
            nbt.putInt("mz_weight", 0);
            nbt.putFloat("mz_dec_dur", 1.0f);
            nbt.putInt("mz_status", 0);  // 0-normal 1-break 2-specialty limit 4-?
            nbt.putIntArray("mz_enchant", new int[0]);
            int basicAtkInt = (int)(basicAtk * 100);
            int basicAtkSpeedInt = (int)(basicAtkSpeed * 100);
            int basicAtkFlucInt = (int)(basicAtkFluc * 100);
            int basicAtkCrtInt = (int)(basicAtkCrt * 100);
            nbt.putIntArray("mz_atkinfo", new int[]{basicAtkInt, basicAtkSpeedInt, basicAtkFlucInt, basicAtkCrtInt});
            stack.setTag(nbt);
        }
        swordWeight = nbt.getInt("mz_weight");
        int weightedAtkInt = (int)(getPlainDamage() * 100);
        int weightedAtkSpeedInt = (int)(getPlainSpeed() * 100);
        int weightedAtkFlucInt = (int)(getPlainFluc() * 100);
        int weightedAtkCrtInt = (int)(getPlainCrt() * 100);
        nbt.putIntArray("mz_atkinfo", new int[]{weightedAtkInt, weightedAtkSpeedInt, weightedAtkFlucInt,
                weightedAtkCrtInt});

        Multimap<String, AttributeModifier> mm = HashMultimap.<String, AttributeModifier>create();
        if (slot == EquipmentSlotType.MAINHAND) {
            // 不使用Vanilla伤害计算
//            mm.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER,
//                    "Weapon modifier", getAttackDamage() - 1.0,
//                    AttributeModifier.Operation.ADDITION));
            mm.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER,
                    "Weapon modifier", getPlainSpeed() - 4.0,
                    AttributeModifier.Operation.ADDITION));
        }
        return mm;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        float atk_lb = getPlainDamage() - getPlainFluc();
        if (atk_lb < 0.0f) { atk_lb = 0.0f; }
        float atk_ub = getPlainDamage() + getPlainFluc();
        NumberFormat formatter = new DecimalFormat("0.0");
        String str_atk_lb = formatter.format(atk_lb);
        String str_atk_ub = formatter.format(atk_ub);
        String atkdesc = txt_atk_desc.getUnformattedComponentText() + str_atk_lb + " - " + str_atk_ub;
        list.add((new StringTextComponent(atkdesc)).setStyle((new Style()).setColor(TextFormatting.DARK_GREEN)));

        float atkCrt = getPlainCrt();
        String str_atkCrt = formatter.format(atkCrt);
        String atkCrtdesc = txt_atkCrt_desc.getUnformattedComponentText() + str_atkCrt + "%";
        list.add((new StringTextComponent(atkCrtdesc)).setStyle((new Style()).setColor(TextFormatting.DARK_GREEN)));
    }

    public static float[] getAttackValue(ItemStack item, float coolMul){
        CompoundNBT tags = item.getTag();
        int[] atkInfo = tags.getIntArray("mz_atkinfo");

        float basicAtk = atkInfo[0] / 100.0f;
        float basicAtkFluc = atkInfo[2] / 100.0f;
        float flucAtk = (float)(basicAtk + 2.0 * (Math.random() - 0.5) * basicAtkFluc);

        float hasCrt = 0.0f;
        if (coolMul > 0.9f){
            float basicCrt = atkInfo[3] / 10000.0f;
            if (Math.random() < basicCrt){
                hasCrt = 1.0f;
                flucAtk *= 3.0f;
            }
        }
        flucAtk *= coolMul;

        return new float[]{ flucAtk, hasCrt };
    }

}
