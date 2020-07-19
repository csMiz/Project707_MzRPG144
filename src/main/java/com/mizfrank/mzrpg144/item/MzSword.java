package com.mizfrank.mzrpg144.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MzSword extends TieredItem {

    public float basicAtk;
    public float basicAtkSpeed;

    public MzSword(IItemTier material, Properties properties, float inAtk, float inAtkSpeed) {
        super(material, properties);
        basicAtk = inAtk;
        basicAtkSpeed = inAtkSpeed;
    }

    public float getAttackDamage() {
        return this.basicAtk;
    }

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

    public boolean hitEntity(ItemStack self, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
        self.damageItem(1, p_77644_3_, (p_220045_0_) -> {
            p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
        return true;
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

    public boolean canHarvestBlock(BlockState targetBlockState) {
        return targetBlockState.getBlock() == Blocks.COBWEB;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> mm = HashMultimap.<String, AttributeModifier>create();
        if (slot == EquipmentSlotType.MAINHAND) {
            mm.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER,
                    "Weapon modifier", (double)basicAtk - 1.0,
                    AttributeModifier.Operation.ADDITION));
            mm.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER,
                    "Weapon modifier", (double)basicAtkSpeed - 4.0,
                    AttributeModifier.Operation.ADDITION));
        }
        return mm;
    }


}
