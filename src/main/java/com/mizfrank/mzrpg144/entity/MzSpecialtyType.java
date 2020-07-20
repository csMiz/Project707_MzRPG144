package com.mizfrank.mzrpg144.entity;

public class MzSpecialtyType {

    /**
     * 0-WARRIOR
     * 1-ARCHER
     * 2-MAGE
     * */
    public int specType = -1;

    /**
     * 0 - 99 E
     * 100 - 199 D
     * 200 - 299 C
     * 300 - 399 B
     * 400 - 499 A
     * 500+ S
     * */
    public int specLevel = 0;

    public int specEXP = 0;

    public MzSpecialtyType(int typeId){
        specType = typeId;
    }

    public void updateLevelFromEXP(){
        specLevel = (int)(100.0 * Math.log((specEXP + 1.0) / 5.0)/Math.log(2));  // 100?

    }


}
