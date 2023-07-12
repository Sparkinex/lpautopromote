package com.sparkinex.lpautopromote.capability;

import com.sparkinex.lpautopromote.core.Config;
import com.sparkinex.lpautopromote.core.PlayerData;
import com.sparkinex.lpautopromote.utils.Toolkit;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class AutoRank implements IAutoRank {
    private int realPlayTime;
    private int idleTime;

    private int lastPromotionIndex = -1;

    @Override
    public void tick(ServerPlayer player, PlayerData playerData) {
        if (isPlayerAFK(player, playerData)) {
            idleTime++;
        } else {
            idleTime = 0;
        }

        if (idleTime < Config.afkThreshold) {
            realPlayTime++;
        }
    }

    private boolean isPlayerAFK(ServerPlayer player, PlayerData playerData) {
        final int MS_BUFFER_FROM_LAST_ACTION = 150;
        final float MOVE_SENSE = 0.05f;
        final float LOOK_SENSE = 0.01f;

        if (Math.abs(Util.getMillis() - player.getLastActionTime()) <= MS_BUFFER_FROM_LAST_ACTION) {
            return false;
        }

        boolean hasMoved = !Toolkit.almostEqual(playerData.getPrevX(), playerData.getCurrX(), MOVE_SENSE)
                || !Toolkit.almostEqual(playerData.getPrevY(), playerData.getCurrY(), MOVE_SENSE)
                || !Toolkit.almostEqual(playerData.getPrevZ(), playerData.getCurrZ(), MOVE_SENSE);

        boolean hasLooked = !Toolkit.almostEqual(playerData.getPrevLookAngle(), playerData.getCurrLookAngle(), LOOK_SENSE);

        if (hasLooked) {
            return false;
        }

        if (hasMoved) {
            if (player.isPassenger()) {
                return true;
            }

            if (Config.protectAgainstAfkWells) {
                BlockPos playerPos = player.blockPosition();
                for (BlockPos checkPos : BlockPos.betweenClosed(playerPos.offset(-1, -1, -1), playerPos.offset(1, 1, 1))) {
                    BlockState blockState = player.level.getBlockState(checkPos);
                    FluidState fluidState = blockState.getFluidState();
                    if (!fluidState.isEmpty()) {
                        boolean isInFlowingFluid = !fluidState.isSource();
                        if (isInFlowingFluid) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        return true;
    }

    @Override
    public void setRealPlayTime(int newTimeInMinutes) {
        this.realPlayTime = newTimeInMinutes * 60 * 20;
    }

    @Override
    public int getRealPlayTime() {
        return realPlayTime;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("RealPlayTime", realPlayTime);
        nbt.putInt("LastPromotionIndex", lastPromotionIndex);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        realPlayTime = nbt.getInt("RealPlayTime");
        lastPromotionIndex = nbt.getInt("LastPromotionIndex");
    }

    @Override
    public int getIdleTime() {
        return idleTime;
    }

    @Override
    public int getLastPromotionIndex() {
        return lastPromotionIndex;
    }

    @Override
    public void setLastPromotionIndex(int lastPromotionIndex) {
        this.lastPromotionIndex = lastPromotionIndex;
    }
}
