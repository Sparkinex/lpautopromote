package com.sparkinex.lpautopromote;

import com.sparkinex.lpautopromote.core.Config;
import com.sparkinex.lpautopromote.core.PlayerData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(LPAutoPromote.MODID)
public class LPAutoPromote {
    public static Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    public static final String MODID = "lpautopromote";

    private static LPAutoPromote INSTANCE = null;

    private LuckPerms lp = null;

    public LPAutoPromote() {
        INSTANCE = this;

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static @Nullable PlayerData getOrCreatePlayerData(ServerPlayer player) {
        if (player == null) return null;
        if (playerDataMap.containsKey(player.getUUID())) {
            return playerDataMap.get(player.getUUID());
        } else {
            return playerDataMap.put(player.getUUID(), new PlayerData(player.getX(), player.getY(), player.getZ(), player.getLookAngle()));
        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent e) {
        lp = LuckPermsProvider.get();
        if (lp == null) {
            throw new NullPointerException("LuckPerms must be present!");
        }
    }

    public static void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public LuckPerms getLuckPerms() {
        return lp;
    }

    public static LPAutoPromote getInstance() {
        return INSTANCE;
    }
}