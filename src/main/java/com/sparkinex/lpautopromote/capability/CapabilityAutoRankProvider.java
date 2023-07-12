package com.sparkinex.lpautopromote.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityAutoRankProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<IAutoRank> REAL_PLAY_TIME_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });

    private final LazyOptional<AutoRank> optional = LazyOptional.of(AutoRank::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == REAL_PLAY_TIME_CAPABILITY) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return optional.orElseThrow(() -> new IllegalArgumentException("Capability not present!")).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        optional.orElseThrow(() -> new IllegalArgumentException("Capability not present!")).deserializeNBT(nbt);
    }
}