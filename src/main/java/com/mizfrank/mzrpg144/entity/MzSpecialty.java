package com.mizfrank.mzrpg144.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
    public boolean isSpecAllowed(int specIdx){
        for (MzSpecialtyType spec : allowSpec){
            if (spec.SpecType == specIdx){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Integer> getAllSpecData(){
        List<Integer> list = new ArrayList<>();
        for (MzSpecialtyType spec : allowSpec){
            list.add(spec.SpecType);
            list.add(spec.SpecLevel);
            list.add(spec.SpecEXP);
        }
        return list;
    }

    @Override
    public void setSpec(int[] specData){
        allowSpec.clear();
        for (int i = 0; i < (specData.length / 3); i++){
            MzSpecialtyType type = new MzSpecialtyType(specData[i*3]);
            type.SpecLevel = specData[i*3+1];
            type.SpecEXP = specData[i*3+2];
            allowSpec.add(type);
        }
    }

    @Override
    public void setSpec(List<Integer> specData) {
        allowSpec.clear();
        for (int i = 0; i < (specData.size() / 3); i++){
            MzSpecialtyType type = new MzSpecialtyType(specData.get(i*3));
            type.SpecLevel = specData.get(i*3+1);
            type.SpecEXP = specData.get(i*3+2);
            allowSpec.add(type);
        }
    }

    @Override
    public Integer getFirstSpecType() {
        if (allowSpec.isEmpty()){
            return -1;
        }
        return allowSpec.get(0).SpecType;
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
