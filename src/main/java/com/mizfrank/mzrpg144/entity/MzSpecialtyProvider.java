package com.mizfrank.mzrpg144.entity;

import com.google.common.base.Optional;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * MzSpecialty provider
 *
 * This class is responsible for providing a capability. Other modders may
 * attach their own provider with implementation that returns another
 * implementation of IMzSpecialty to the target's (Entity, TE, ItemStack, etc.) disposal.
 */
public class MzSpecialtyProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IMzSpecialty.class)
    public static Capability<IMzSpecialty> MZ_SPEC_CAP = null;
    private LazyOptional<IMzSpecialty> instance = LazyOptional.of(MZ_SPEC_CAP::getDefaultInstance);

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        return capability == MZ_SPEC_CAP ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return MZ_SPEC_CAP.getStorage().writeNBT(MZ_SPEC_CAP, this.instance.orElseThrow(
                () -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT inbt) {
        MZ_SPEC_CAP.getStorage().readNBT(MZ_SPEC_CAP, this.instance.orElseThrow(
                () -> new IllegalArgumentException("LazyOptional must not be empty!")), null, inbt);
    }
}
