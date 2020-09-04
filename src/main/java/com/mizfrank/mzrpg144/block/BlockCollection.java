package com.mizfrank.mzrpg144.block;

import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BlockCollection {

    //The BLOCKS deferred register in which you can register blocks.
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MzRPG.MOD_ID);

    //Register the tutorial block with "tutorial_block" as registry name and default ROCK properties
    public static final RegistryObject<Block> MZ_GEM_BLOCK = BLOCKS.register(
            "mz_gem_block", () -> new Block(Block.Properties.create(Material.ROCK).
                    hardnessAndResistance(1.5f, 6.0f)));

    public static final RegistryObject<Block> MZ_MEDAL_BOX = BLOCKS.register(
            "mz_medal_box", () -> new MzMedalBox());

    public static final RegistryObject<Block> MZ_MAGIC_TABLE = BLOCKS.register(
            "mz_magic_table", () -> new MzMagicTable());

}
