package com.mizfrank.mzrpg144.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class MzSpecialtyStorage implements Capability.IStorage<IMzSpecialty> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IMzSpecialty> capability, IMzSpecialty mzSpecialty, Direction direction) {
        CompoundNBT result = new CompoundNBT();
        result.putIntArray("mz_specialty",mzSpecialty.getAllSpecData());
        return result;
    }

    @Override
    public void readNBT(Capability<IMzSpecialty> capability, IMzSpecialty mzSpecialty, Direction direction, INBT inbt) {
        mzSpecialty.setSpec(((CompoundNBT)inbt).getIntArray("mz_specialty"));
    }
}
