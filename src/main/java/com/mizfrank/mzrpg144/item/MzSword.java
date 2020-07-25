package com.mizfrank.mzrpg144.item;

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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public abstract class MzSword extends TieredItem {

    protected float basicAtk;
    protected float basicAtkSpeed;
    protected float basicAtkFluc;
    protected float basicCrt;
    protected int swordWeight = 0;
    protected MzSwordEnchant enchant = new MzSwordEnchant();


    protected MzSword(IItemTier material, Properties properties, float inAtk, float inAtkSpeed, float inAtkFluc, float inCrt) {
        super(material, properties);
        basicAtk = inAtk;
        basicAtkSpeed = inAtkSpeed;
        basicAtkFluc = inAtkFluc;
        basicCrt = inCrt;
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
            nbt.putFloat("mz_atk", basicAtk);
            nbt.putFloat("mz_atkspd", basicAtkSpeed);
            nbt.putFloat("mz_atkfluc", basicAtkFluc);
            nbt.putFloat("mz_crt", basicAtkFluc);
            stack.setTag(nbt);
        }
        swordWeight = nbt.getInt("mz_weight");
        nbt.putFloat("mz_atk", getPlainDamage());
        nbt.putFloat("mz_atkspd", getPlainSpeed());
        nbt.putFloat("mz_atkfluc", getPlainFluc());
        nbt.putFloat("mz_crt", getPlainCrt());

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

    public static float[] getAttackValue(ItemStack item, float coolMul){
        CompoundNBT tags = item.getTag();
        float basicAtk = tags.getFloat("mz_atk");
        float basicAtkFluc = tags.getFloat("mz_atkfluc");
        float flucAtk = (float)(basicAtk + 2.0 * (Math.random() - 0.5) * basicAtkFluc);

        float hasCrt = 0.0f;
        if (coolMul > 0.9f){
            float basicCrt = tags.getFloat("mz_crt");
            if (Math.random() < basicCrt){
                hasCrt = 1.0f;
                flucAtk *= 3.0f;
            }
        }
        flucAtk *= coolMul;

        return new float[]{ flucAtk, hasCrt };
    }

}
