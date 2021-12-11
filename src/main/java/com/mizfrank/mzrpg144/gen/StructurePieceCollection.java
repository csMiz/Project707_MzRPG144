package com.mizfrank.mzrpg144.gen;

import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.util.registry.Registry;


public class StructurePieceCollection {

    public static final IStructurePieceType MZ_TOWN_PIECE = register("mz_town_piece", MzTownPiece::new);

    private static IStructurePieceType register(String key, IStructurePieceType type) {
        return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(MzRPG.MOD_ID, key), type);
    }



}
