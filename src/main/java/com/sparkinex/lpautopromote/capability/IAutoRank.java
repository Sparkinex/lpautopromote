package com.sparkinex.lpautopromote.capability;

import com.sparkinex.lpautopromote.core.PlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface IAutoRank {
    void tick(ServerPlayer player, PlayerData playerData);

    int getRealPlayTime();

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);

    int getIdleTime();

    void setRealPlayTime(int newTimeInMinutes);

    int getLastPromotionIndex();

    void setLastPromotionIndex(int lastPromotionIndex);
}
