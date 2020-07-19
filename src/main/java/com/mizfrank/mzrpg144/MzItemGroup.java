package com.mizfrank.mzrpg144;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class MzItemGroup extends ItemGroup {

    private final Supplier<ItemStack> iconSupplier;

    public MzItemGroup(final String name, final Supplier<ItemStack> icon) {
        super(name);
        this.iconSupplier = icon;
    }

    @Override
    public ItemStack createIcon() {
        return iconSupplier.get();
    }
}
