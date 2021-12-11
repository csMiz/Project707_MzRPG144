package com.mizfrank.mzrpg144.gen;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.EndCityPieces;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

import static com.mizfrank.mzrpg144.gen.StructurePieceCollection.MZ_TOWN_PIECE;

public class MzTownPiece extends TemplateStructurePiece {

    public String templateName;
    public Rotation rotation;


    public MzTownPiece(TemplateManager templateManager, String pieceName, BlockPos pos, Rotation rot) {
        super(MZ_TOWN_PIECE, 0);
        this.templateName = pieceName;
        this.templatePosition = pos;
        this.rotation = rot;
        this.loadTemplate(templateManager);
    }

    public MzTownPiece(TemplateManager templateManager, CompoundNBT nbt) {
        super(MZ_TOWN_PIECE, nbt);
        this.templateName = nbt.getString("Template");
        this.rotation = Rotation.valueOf(nbt.getString("Rot"));
        this.loadTemplate(templateManager);
    }

    private void loadTemplate(TemplateManager templateManager) {
        Template template = templateManager.getTemplateDefaulted(new ResourceLocation("mzrpg144:" + this.templateName));
        PlacementSettings placementSettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(rotation)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        this.setup(template, this.templatePosition, placementSettings);
    }

    @Override
    protected void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        nbt.putString("Template", this.templateName);
        nbt.putString("Rotation", this.placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String s, BlockPos blockPos, IWorld iWorld, Random random, MutableBoundingBox mbb) {
//        switch (function) {
//            case "grassworld:grassfloor":
//                int grass = rand.nextInt(this.variant.getGrassBlock().length);
//                if(GrassConfigHandler.COMMON.RAINBOWISLANDS.get()){
//                    worldIn.setBlockState(pos, StaticGrassHandlers.randomBlockSelector(rand).getDefaultState(), 2);
//                }
//                else {
//                    worldIn.setBlockState(pos, this.variant.getGrassBlock()[grass].getDefaultState(), 2);
//                }
//                break;
//            case "grassworld:lakefluid":
//                worldIn.setBlockState(pos, this.variant.getLakeFluid(), 2);
//                break;
//            case "grassworld:actualgrass":
//                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
//
//                if (rand.nextBoolean()) {
//                    int grass_random = rand.nextInt(this.variant.getTallGrass().length);
//
//                    BlockState chosenGrass = this.variant.getTallGrass()[grass_random].getDefaultState();
//                    BlockState allGrass = randomGrassSelector(rand).getDefaultState();
//
//                    if (chosenGrass.getBlock() instanceof ActualGrass) {
//                        if (((ActualGrass) chosenGrass.getBlock()).isValidPosition(chosenGrass, worldIn, pos)) {
//                            if(GrassConfigHandler.COMMON.RAINBOWISLANDS.get()){
//                                worldIn.setBlockState(pos, allGrass, 2);
//                            }
//                            else{
//                                worldIn.setBlockState(pos, chosenGrass, 2);
//                            }
//                            worldIn.setBlockState(pos, chosenGrass, 2);
//                        }
//                    }
//                }
//                break;
//        }
    }
}
