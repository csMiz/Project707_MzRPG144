package com.mizfrank.mzrpg144.item.MzItemEnchant;

import net.minecraft.item.Item;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.potion.Effect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MzSwordEnchant {

    private HashMap<Integer, Float> content = new HashMap<>();

    private Set<Integer> element = new HashSet<>();

    public boolean passElement = true;
    public float breakRate = 0.0f;

    public MzSwordEnchant(){
        // TODO
    }

    public MzSwordEnchant(int[] data){
        // TODO
    }

    public HashMap<Integer, Float> getAllEnchant(){
        // TODO
        return null;
    }

    public List<Effect> getAllEnchantEffect(){
        // TODO
        return null;
    }

    public Set<Integer> getElement(){
        // TODO
        return null;
    }

    public float getAllMPCost(){
        // TODO
        return 0.0f;
    }

    public void prepare(Item mat){
        // TODO
    }

    public IntArrayNBT toNBTIntArray(){
        // TODO
        return null;
    }

    public static Effect cvt_enchant_effect(int enchantIdx, float enchantLevel){
        // TODO
        return null;
    }

    public static float getSingleMPCost(int enchantIdx, float enchantLevel){
        // TODO
        return 0.0f;
    }

}
