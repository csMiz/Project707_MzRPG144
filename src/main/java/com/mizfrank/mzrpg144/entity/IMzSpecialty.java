package com.mizfrank.mzrpg144.entity;

import java.util.List;

public interface IMzSpecialty {

    void learnSpec(int specIdx);

    boolean isSpecAllowed(int specIdx);

    List<Integer> getAllSpecData();

    void setSpec(int[] specData);

    void setSpec(List<Integer> specData);

    Integer getFirstSpecType();

}
