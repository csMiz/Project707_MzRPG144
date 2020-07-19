package com.mizfrank.mzrpg144.item;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.block.BlockCollection;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ItemCollection {

    //The ITEMS deferred register in which you can register items.
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MzRPG.MOD_ID);

    //Register the tutorial dust with "tutorial_dust" as registry name and default properties
    public static final RegistryObject<Item> MZ_GEM = ITEMS.register("mz_gem", () -> new Item(
            new Item.Properties().maxStackSize(64).group(MzRPG.MZ_ITEMGROUP)));

    public static final RegistryObject<Item> MZ_GEM_BLOCK = ITEMS.register("mz_gem_block", () -> new BlockItem(
            BlockCollection.MZ_GEM_BLOCK.get(), new Item.Properties().maxStackSize(64).group(MzRPG.MZ_ITEMGROUP)));

    public static final RegistryObject<Item> MZ_MEDAL_BOX = ITEMS.register("mz_medal_box", () -> new BlockItem(
            BlockCollection.MZ_MEDAL_BOX.get(), new Item.Properties().maxStackSize(64).group(MzRPG.MZ_ITEMGROUP)));

    public static final RegistryObject<Item> MZ_SWORD_IRON = ITEMS.register("mz_sword_iron", () -> new MzSwordIron(
            new MzSwordIron.Properties().group(MzRPG.MZ_ITEMGROUP)));



}