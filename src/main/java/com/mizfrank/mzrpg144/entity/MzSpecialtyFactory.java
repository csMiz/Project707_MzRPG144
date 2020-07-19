package com.mizfrank.mzrpg144.entity;

import java.util.concurrent.Callable;

public class MzSpecialtyFactory implements Callable<MzSpecialty> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public MzSpecialty call() throws Exception {
        return new MzSpecialty();
    }
}
