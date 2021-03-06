package com.mizfrank.mzrpg144;

import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowEntityEx;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;

public class MzDamageSourceCollection {

    public static DamageSource causeMzAPCRDamage(MzArrowEntityEx arrowEntity, @Nullable Entity shooter) {
        return (new IndirectEntityDamageSource("arrow", arrowEntity, shooter))
                .setDamageBypassesArmor().setDamageIsAbsolute();
    }

    public static DamageSource causeMzAPDamage(MzArrowEntityEx arrowEntity, @Nullable Entity shooter) {
        return (new IndirectEntityDamageSource("arrow", arrowEntity, shooter))
                .setProjectile().setDamageIsAbsolute();
    }

    public static DamageSource causeMzHEDamage(MzArrowEntityEx arrowEntity, @Nullable Entity shooter) {
        return (new IndirectEntityDamageSource("arrow", arrowEntity, shooter))
                .setProjectile().setExplosion();
    }

}
