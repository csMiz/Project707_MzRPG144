package com.mizfrank.mzrpg144.block;

import com.mizfrank.mzrpg144.MzRPG;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerCollection {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, MzRPG.MOD_ID);

    public static final RegistryObject<ContainerType<MzMedalBoxContainer>> MZ_MEDAL_BOX_CONTAINER = CONTAINERS.register(
            "mz_medal_box_container", () -> IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos;
                if (data == null){
                    pos = new BlockPos(0,0,0);
                }
                else{
                     pos = data.readBlockPos();
                }
                World world = inv.player.getEntityWorld();
                return new MzMedalBoxContainer(windowId, world, pos, inv, inv.player);
    }));

}
