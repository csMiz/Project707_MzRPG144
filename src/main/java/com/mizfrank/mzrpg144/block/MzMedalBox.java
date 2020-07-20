package com.mizfrank.mzrpg144.block;

import com.mizfrank.mzrpg144.Networking;
import com.mizfrank.mzrpg144.entity.IMzSpecialty;
import com.mizfrank.mzrpg144.entity.MzSpecialtyProvider;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


public class MzMedalBox extends HorizontalBlock {

    public MzMedalBox() {
        super(Properties.create(Material.WOOD).sound(SoundType.WOOD));

    }

    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            INamedContainerProvider container = state.getContainer(worldIn, blockPos);

            // can get playerSpec here!

            player.openContainer(container);
            //NetworkHooks.openGui((ServerPlayerEntity) player, container, blockPos);
        }
        return true;
    }

    public INamedContainerProvider getContainer(BlockState p_220052_1_, World world, BlockPos pos) {

        INamedContainerProvider containerProvider = new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                // new TranslationTextComponent("screen.mytutorial.firstblock");
                return new StringTextComponent("mz.specialty selection");
            }

            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new MzMedalBoxContainer(i, world, pos, playerInventory, playerEntity);
            }
        };
        return containerProvider;

    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return (BlockState)this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(new IProperty[]{HORIZONTAL_FACING});
    }

}
