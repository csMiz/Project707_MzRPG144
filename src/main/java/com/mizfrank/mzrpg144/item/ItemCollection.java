package com.mizfrank.mzrpg144.item;

import com.mizfrank.mzrpg144.MzRPG;
import com.mizfrank.mzrpg144.block.BlockCollection;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowAP;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowAPCR;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowHE;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzBow.MzBowWood;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzCrossbow.MzChukonu;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzCrossbow.MzCrossbowWood;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzSword.MzSwordIron;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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

    public static final RegistryObject<Item> MZ_MAGIC_TABLE = ITEMS.register("mz_magic_table", () -> new BlockItem(
            BlockCollection.MZ_MAGIC_TABLE.get(), new Item.Properties().maxStackSize(64).group(MzRPG.MZ_ITEMGROUP)));

    public static final RegistryObject<Item> MZ_MAGIC_BOOK = ITEMS.register("mz_magic_book", () -> new Item(
            new Item.Properties().maxStackSize(64).group(MzRPG.MZ_ITEMGROUP)));

    public static final RegistryObject<Item> MZ_MAGIC_BOOK_COMPILED = ITEMS.register("mz_magic_book_c", () -> new Item(
            new Item.Properties().maxStackSize(1).group(MzRPG.MZ_ITEMGROUP)));

    public static final RegistryObject<Item> MZ_WHITE_CARD = ITEMS.register("mz_white_card", () -> new MzWhiteCard());

    public static final RegistryObject<Item> MZ_SWORD_IRON = ITEMS.register("mz_sword_iron", () -> new MzSwordIron(
            new MzSwordIron.Properties().group(MzRPG.MZ_ITEMGROUP)));

    public static final RegistryObject<Item> MZ_BOW_WOOD = ITEMS.register("mz_bow_wood", () -> new MzBowWood(
            new MzBowWood.Properties()));


    public static final RegistryObject<Item> MZ_CRSBOW_WOOD = ITEMS.register("mz_crsbow_wood", () -> new MzCrossbowWood(
            new MzCrossbowWood.Properties()));

    public static final RegistryObject<Item> MZ_CHUKONU = ITEMS.register("mz_ckn", () -> new MzChukonu(
            new MzChukonu.Properties()));


    public static final RegistryObject<Item> MZ_ARROW_AP = ITEMS.register("mz_arrow_ap", () -> new MzArrowAP(
            new MzArrowAP.Properties()) {
    });

    public static final RegistryObject<Item> MZ_ARROW_APCR = ITEMS.register("mz_arrow_apcr", () -> new MzArrowAPCR(
            new MzArrowAPCR.Properties()) {
    });

    public static final RegistryObject<Item> MZ_ARROW_HE = ITEMS.register("mz_arrow_he", () -> new MzArrowHE(
            new MzArrowHE.Properties()) {
    });

}
