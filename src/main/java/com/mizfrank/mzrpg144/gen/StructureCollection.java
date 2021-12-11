package com.mizfrank.mzrpg144.gen;

import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureCollection {

    public static final DeferredRegister<Feature<?>> STRUCTS = new DeferredRegister<>(ForgeRegistries.FEATURES, MzRPG.MOD_ID);

    public static final RegistryObject<Feature<?>> MZ_TOWN_STRUCT = STRUCTS.register(
            "mz_town_struct", () -> new MzTownStructure(NoFeatureConfig::deserialize));


}
