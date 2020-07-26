package com.mizfrank.mzrpg144.item.MzItemWeapon.MzBow;

import com.mizfrank.mzrpg144.MzItemGroup;
import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.Predicate;

public abstract class MzBow extends ShootableItem {

    protected float fullPullTick;

    protected float velocityFactor;

    protected float inaccuracyFactor;

    public MzBow(Properties properties, float inFullPullTick, float inVelocity, float inInaccuracy) {
        super(properties.group(MzRPG.MZ_ITEMGROUP));
        fullPullTick = inFullPullTick;
        velocityFactor = inVelocity;
        inaccuracyFactor = inInaccuracy;
        this.addPropertyOverride(new ResourceLocation("pull"), (itemStack, world, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                int pullTick = itemStack.getUseDuration() - livingEntity.getItemInUseCount();
                return !(livingEntity.getActiveItemStack().getItem() instanceof MzBow) ? 0.0F : (float)(pullTick) / fullPullTick;
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), (itemStack, world, livingEntity) -> {
            return livingEntity != null && livingEntity.isHandActive() && livingEntity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
        });
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int currentUseDur) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)livingEntity;
            boolean freeUse = player.abilities.isCreativeMode;  // 或者有Vanilla无限附魔
            ItemStack ammo = player.findAmmo(itemStack);    // TODO: 自定义findAmmo
            int tmpPullTick = this.getUseDuration(itemStack) - currentUseDur;
            tmpPullTick = ForgeEventFactory.onArrowLoose(itemStack, world, player, tmpPullTick, !ammo.isEmpty() || freeUse);
            if (tmpPullTick < 0) {
                return;
            }

            if (!ammo.isEmpty() || freeUse) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }

                float arrowVelocity = getArrowVelocityProgress(tmpPullTick);
                if ((double)arrowVelocity >= 0.1D) {
                    if (!world.isRemote) {
                        ArrowItem ammoItem = (ArrowItem)(ammo.getItem());
                        AbstractArrowEntity arrowEntity = ammoItem.createArrow(world, ammo, player);
                        arrowEntity = customArrow(arrowEntity);
                        arrowEntity.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F,
                                arrowVelocity * 3.0F * velocityFactor, inaccuracyFactor);
                        if (arrowVelocity == 1.0F) {
                            arrowEntity.setIsCritical(true);
                        }

//                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);
//                        if (j > 0) {
//                            arrowEntity.setBasicDamage(arrowEntity.getBasicDamage() + (double)j * 0.5D + 0.5D);
//                        }
//
//                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);
//                        if (k > 0) {
//                            arrowEntity.setKnockbackStrength(k);
//                        }
//
//                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack) > 0) {
//                            arrowEntity.setFire(100);
//                        }

                        itemStack.damageItem(1, player, (p_lambda$onPlayerStoppedUsing$2_1_) -> {
                            p_lambda$onPlayerStoppedUsing$2_1_.sendBreakAnimation(player.getActiveHand());
                        });
                        if (player.abilities.isCreativeMode && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                            arrowEntity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }

                        world.addEntity(arrowEntity);
                    }

                    world.playSound((PlayerEntity)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);
                    if (!freeUse) {
                        ammo.shrink(1);
                        if (ammo.isEmpty()) {
                            player.inventory.deleteStack(ammo);
                        }
                    }

                    player.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }

    }

    // 按照Vanilla的公式，f(t) = 0.33 * (t^2 + 2*t)
    protected float getArrowVelocityProgress(int tmpPullTick) {
        float t = (float)tmpPullTick / fullPullTick;
        t = (t * t + t * 2.0F) / 3.0F;
        if (t > 1.0F) {
            t = 1.0F;
        }
        return t;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = !player.findAmmo(itemstack).isEmpty();
        ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(itemstack, world, player, hand, flag);
        if (ret != null) {
            return ret;
        } else if (!player.abilities.isCreativeMode && !flag) {
            return flag ? new ActionResult(ActionResultType.PASS, itemstack) : new ActionResult(ActionResultType.FAIL, itemstack);
        } else {
            player.setActiveHand(hand);
            return new ActionResult(ActionResultType.SUCCESS, itemstack);
        }
    }

    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return ARROWS;
    }

    public AbstractArrowEntity customArrow(AbstractArrowEntity arrowEntity) {
        return arrowEntity;
    }
}
