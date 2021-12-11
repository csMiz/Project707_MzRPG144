package com.mizfrank.mzrpg144.gen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidPiece;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;
import java.util.function.Function;

public class MzTownStructure extends ScatteredStructure<NoFeatureConfig> {

    public MzTownStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> feature) {
        super(feature);
    }

    @Override
    protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z,
                                                   int spacingOffsetsX, int spacingOffsetsZ) {
        random.setSeed(this.getSeedModifier());
        int distance = 20;
        int separation = 11;
        int x1 = x + distance * spacingOffsetsX;
        int z1 = z + distance * spacingOffsetsZ;
        int x2 = x1 < 0 ? x1 - distance + 1 : x1;
        int z2 = z1 < 0 ? z1 - distance + 1 : z1;
        int x3 = x2 / distance;
        int z3 = z2 / distance;
        ((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), x3, z3, this.getSeedModifier());
        x3 = x3 * distance;
        z3 = z3 * distance;
        x3 = x3 + random.nextInt(distance - separation);
        z3 = z3 + random.nextInt(distance - separation);

        return new ChunkPos(x3, z3);    }

    @Override
    protected int getSeedModifier() {
        return 14357800;
    }

    @Override
    public IStartFactory getStartFactory() {
        return MzTownStructure.Start::new;
    }

    @Override
    public String getStructureName() {
        return "mzrpg144:mz_town";
    }

    @Override
    public int getSize() {
        return 8;
    }

    public static class Start extends StructureStart {

        public Start(Structure<?> structureIn, int p_i51341_2_, int p_i51341_3_, Biome p_i51341_4_, MutableBoundingBox p_i51341_5_, int p_i51341_6_, long p_i51341_7_) {
            super(structureIn, p_i51341_2_, p_i51341_3_, p_i51341_4_, p_i51341_5_, p_i51341_6_, p_i51341_7_);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManager, int chunkX, int chunkZ, Biome biomeIn) {
            MzTownPiece piece = new MzTownPiece(templateManager, "default_town",
                    new BlockPos(chunkX*16,68,chunkZ*16), Rotation.NONE);
            this.components.add(piece);
            this.recalculateStructureSize();
        }
    }

}
