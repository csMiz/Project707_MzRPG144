package com.mizfrank.mzrpg144.item;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowEntityEx;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRendererCollection {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MzRPG.MOD_ID);

    public static final RegistryObject<EntityType<MzArrowEntityEx>> MZ_ARROW_ENTITY_EX = ENTITY_TYPES.register("mz_arrow_ex",
            () -> EntityType.Builder.<MzArrowEntityEx>create(MzArrowEntityEx::new, EntityClassification.MISC)
                    .size(0.5F, 0.5F).build(new ResourceLocation(MzRPG.MOD_ID, "mz_arrow").toString()));
}
