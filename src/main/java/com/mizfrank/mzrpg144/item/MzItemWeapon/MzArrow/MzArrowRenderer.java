package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class MzArrowRenderer extends MzBaseArrowRenderer<MzArrowEntityEx> {

    public MzArrowRenderer(EntityRendererManager p_i46193_1_) {
        super(p_i46193_1_);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(MzArrowEntityEx mzArrowEntity) {
        Item refItem = mzArrowEntity.getArrowStack().getItem();
        return new ResourceLocation(MzRPG.MOD_ID, "textures/entity/projectile/mz_arrow.png");
    }
}
