package com.sparkinex.lpautopromote.core;

import com.mojang.logging.LogUtils;
import com.sparkinex.lpautopromote.LPAutoPromote;
import com.sparkinex.lpautopromote.capability.CapabilityAutoRankProvider;
import com.sparkinex.lpautopromote.capability.IAutoRank;
import com.sparkinex.lpautopromote.utils.Toolkit;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.track.Track;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = LPAutoPromote.MODID)
public class PromotionRunner {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;

            if (tickCounter >= (Config.promotionCheckTime * 20 * 60)) {
                tickCounter = 0;

                for (UUID uuid : LPAutoPromote.playerDataMap.keySet()) {
                    LPAutoPromote.playerDataMap.get(uuid);
                    ServerPlayer player = event.getServer().getPlayerList().getPlayer(uuid);
                    IAutoRank realPlayTimeCapability = player.getCapability(CapabilityAutoRankProvider.REAL_PLAY_TIME_CAPABILITY).orElseThrow(IllegalStateException::new);

                    int playtimeSeconds = realPlayTimeCapability.getRealPlayTime() / 20;
                    int playtimeMinutes = playtimeSeconds / 60;

                    if (playtimeMinutes >= Config.promotions.get(0)) {
                        int startIndex = realPlayTimeCapability.getLastPromotionIndex() == -1 ? 0 : realPlayTimeCapability.getLastPromotionIndex();
                        for (int i = startIndex; i < Config.promotions.size(); i++) {
                            int promotionTime = Config.promotions.get(i);
                            if (promotionTime <= 0) {
                                LogUtils.getLogger().error("Invalid promotion time: " + promotionTime);
                                return;
                            }

                            if (playtimeMinutes >= promotionTime && (realPlayTimeCapability.getLastPromotionIndex() == -1 || i > realPlayTimeCapability.getLastPromotionIndex())) {
                                LuckPerms lp = LPAutoPromote.getInstance().getLuckPerms();

                                User lpUser = lp.getUserManager().getUser(player.getUUID());

                                try {
                                    Track lpTrack = lp.getTrackManager().getTrack(Config.promotionTrack);
                                    if (lpTrack != null) {
                                        lpTrack.promote(lpUser, lp.getContextManager().getStaticContext());
                                        realPlayTimeCapability.setLastPromotionIndex(i);
                                        player.sendSystemMessage(Component.literal(Toolkit.translateColorCodes("&eYou have been promoted!")));
                                    }
                                } catch (Exception e) {
                                    LogUtils.getLogger().error("Invalid Track!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}