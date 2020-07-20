package com.mizfrank.mzrpg144.entity;

import java.util.ArrayList;
import java.util.List;

public class MzSpecialty implements IMzSpecialty {

    private List<MzSpecialtyType> allowSpec = new ArrayList<>();


    public MzSpecialty(){
    }

    @Override
    public void learnSpec(int specIdx) {
        MzSpecialtyType tmpType = new MzSpecialtyType(specIdx);
        allowSpec.add(tmpType);
    }

    @Override
    public int forgetSpec(int specIdx) {
        int exp = 0;
        int idx = 0;
        for (int i = 0; i < allowSpec.size(); i++){
            MzSpecialtyType spec = allowSpec.get(i);
            if (spec.specType == specIdx){
                exp = spec.specEXP;
                idx = i;
                break;
            }
        }
        allowSpec.remove(idx);
        return (int)(exp * 0.75);
    }

    @Override
    public boolean isSpecAllowed(int specIdx){
        if (specIdx == -1) { return false; }
        for (MzSpecialtyType spec : allowSpec){
            if (spec.specType == specIdx){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Integer> getAllSpecData(){
        List<Integer> list = new ArrayList<>();
        for (MzSpecialtyType spec : allowSpec){
            list.add(spec.specType);
            list.add(spec.specLevel);
            list.add(spec.specEXP);
        }
        return list;
    }

    @Override
    public int[] getAllSpecDataArray() {
        int[] array = new int[allowSpec.size()*3];
        int i = 0;
        for (MzSpecialtyType spec : allowSpec){
            array[i] = spec.specType;
            array[i+1] = spec.specLevel;
            array[i+2] = spec.specEXP;
            i += 3;
        }
        return array;
    }

    @Override
    public void setSpec(int[] specData){
        allowSpec.clear();
        for (int i = 0; i < (specData.length / 3); i++){
            MzSpecialtyType type = new MzSpecialtyType(specData[i*3]);
            type.specLevel = specData[i*3+1];
            type.specEXP = specData[i*3+2];
            allowSpec.add(type);
        }
    }

    @Override
    public void setSpec(List<Integer> specData) {
        allowSpec.clear();
        for (int i = 0; i < (specData.size() / 3); i++){
            MzSpecialtyType type = new MzSpecialtyType(specData.get(i*3));
            type.specLevel = specData.get(i*3+1);
            type.specEXP = specData.get(i*3+2);
            allowSpec.add(type);
        }
    }

    @Override
    public int getFirstType() {
        if (allowSpec.isEmpty()){
            return -1;
        }
        return allowSpec.get(0).specType;
    }

    @Override
    public int getMajorType() {
        int maxLevel = -1;
        int maxType = 0;
        if (allowSpec.size() == 0){
            return -1;
        }
        for (MzSpecialtyType type : allowSpec){
            if (type.specLevel > maxLevel){
                maxLevel = type.specLevel;
                maxType = type.specType;
            }
        }
        return maxType;
    }

    @Override
    public int getMinorType() {
        int minLevel = 32766;
        int minType = 0;
        if (allowSpec.size() == 0){
            return -1;
        }
        for (MzSpecialtyType type : allowSpec){
            if (type.specLevel < minLevel){
                minLevel = type.specLevel;
                minType = type.specType;
            }
        }
        return minType;
    }

    @Override
    public boolean canAddSpec() {
        if (allowSpec.size() == 0){
            return true;
        }
        for (MzSpecialtyType spec : allowSpec){
            if (spec.specLevel < 500){
                return false;
            }
        }
        return true;
    }

    @Override
    public int getSpecLevel(int specIdx) {
        for (MzSpecialtyType spec : allowSpec){
            if (spec.specType == specIdx){
                return spec.specLevel;
            }
        }
        return -1;
    }

    public static String cvt_specInt_specStr(Integer input){
        switch (input){
            case -1:
                return "EMPTY";
            case 0:
                return "WARRIOR";
            case 1:
                return "ARCHER";
            case 2:
                return "MAGE";
        }
        return "OTHER";
    }

}
