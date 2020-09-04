package com.mizfrank.mzrpg144.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class MzMagicTable extends HorizontalBlock {

    protected MzMagicTable() {
        super(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(1.5f, 6.0f));

    }

    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        //if (!worldIn.isRemote) {
            INamedContainerProvider container = state.getContainer(worldIn, blockPos);
            player.openContainer(container);
        //}
        return true;
    }

    public INamedContainerProvider getContainer(BlockState blockState, World world, BlockPos pos) {

        INamedContainerProvider containerProvider = new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new StringTextComponent("mz magic table");
            }

            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new MzMagicTableContainer(i, world, pos, playerInventory, playerEntity);
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