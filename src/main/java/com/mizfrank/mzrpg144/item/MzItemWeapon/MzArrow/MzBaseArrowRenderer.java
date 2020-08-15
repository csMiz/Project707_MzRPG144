package com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class MzBaseArrowRenderer<T extends MzArrowEntityEx> extends EntityRenderer<T> {
    public MzBaseArrowRenderer(EntityRendererManager p_i46193_1_) {
        super(p_i46193_1_);
    }

    public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        this.bindEntityTexture(p_76986_1_);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translatef((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
        GlStateManager.rotatef(MathHelper.lerp(p_76986_9_, p_76986_1_.prevRotationYaw, p_76986_1_.rotationYaw) - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(MathHelper.lerp(p_76986_9_, p_76986_1_.prevRotationPitch, p_76986_1_.rotationPitch), 0.0F, 0.0F, 1.0F);
        Tessellator lvt_10_1_ = Tessellator.getInstance();
        BufferBuilder lvt_11_1_ = lvt_10_1_.getBuffer();
        int lvt_12_1_ = 0;
        float lvt_13_1_ = 0.0F;
        float lvt_14_1_ = 0.5F;
        float lvt_15_1_ = 0.0F;
        float lvt_16_1_ = 0.15625F;
        float lvt_17_1_ = 0.0F;
        float lvt_18_1_ = 0.15625F;
        float lvt_19_1_ = 0.15625F;
        float lvt_20_1_ = 0.3125F;
        float lvt_21_1_ = 0.05625F;
        GlStateManager.enableRescaleNormal();
        float lvt_22_1_ = (float)p_76986_1_.arrowShake - p_76986_9_;
        if (lvt_22_1_ > 0.0F) {
            float lvt_23_1_ = -MathHelper.sin(lvt_22_1_ * 3.0F) * lvt_22_1_;
            GlStateManager.rotatef(lvt_23_1_, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(0.05625F, 0.05625F, 0.05625F);
        GlStateManager.translatef(-4.0F, 0.0F, 0.0F);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(p_76986_1_));
        }

        GlStateManager.normal3f(0.05625F, 0.0F, 0.0F);
        lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_11_1_.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        lvt_11_1_.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        lvt_11_1_.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        lvt_11_1_.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        lvt_10_1_.draw();
        GlStateManager.normal3f(-0.05625F, 0.0F, 0.0F);
        lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_11_1_.pos(-7.0D, 2.0D, -2.0D).tex(0.0D, 0.15625D).endVertex();
        lvt_11_1_.pos(-7.0D, 2.0D, 2.0D).tex(0.15625D, 0.15625D).endVertex();
        lvt_11_1_.pos(-7.0D, -2.0D, 2.0D).tex(0.15625D, 0.3125D).endVertex();
        lvt_11_1_.pos(-7.0D, -2.0D, -2.0D).tex(0.0D, 0.3125D).endVertex();
        lvt_10_1_.draw();

        for(int lvt_23_2_ = 0; lvt_23_2_ < 4; ++lvt_23_2_) {
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.normal3f(0.0F, 0.0F, 0.05625F);
            lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_11_1_.pos(-8.0D, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
            lvt_11_1_.pos(8.0D, -2.0D, 0.0D).tex(0.5D, 0.0D).endVertex();
            lvt_11_1_.pos(8.0D, 2.0D, 0.0D).tex(0.5D, 0.15625D).endVertex();
            lvt_11_1_.pos(-8.0D, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
            lvt_10_1_.draw();
        }

        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
