package com.mizfrank.mzrpg144.entity;


import java.util.List;

public interface IMzSpecialty {

    void learnSpec(int specIdx);

    int forgetSpec(int specIdx);

    boolean isSpecAllowed(int specIdx);

    List<Integer> getAllSpecData();

    int[] getAllSpecDataArray();

    void setSpec(int[] specData);

    void setSpec(List<Integer> specData);

    int getFirstType();

    int getMajorType();

    int getMinorType();

    boolean canAddSpec();

    int getSpecLevel(int specIdx);

}
