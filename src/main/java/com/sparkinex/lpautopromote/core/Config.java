package com.sparkinex.lpautopromote.core;

import com.sparkinex.lpautopromote.LPAutoPromote;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = LPAutoPromote.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Integer> CHECK_FOR_PROMOTION_EVERY_X_MINUTES = BUILDER
            .comment("Time in minutes to check for a promotion.")
            .define("promotionCheckTime", 5);

    public static final ForgeConfigSpec.ConfigValue<Integer> AFK_THRESHOLD_IN_MINUTES = BUILDER
            .comment("Time in minutes till a player is considered afk if they are not doing anything.")
            .define("afkThreshold", 5);

    public static final ForgeConfigSpec.ConfigValue<Boolean> PROTECT_AGAINST_AFK_WELLS = BUILDER
            .comment("Protect against players using afk wells to stop themselves from going afk. Disabling this may\n" +
                    " increase performance but overrall it should not be a issue.")
            .define("protectAgainstAfkWells", true);

    public static final ForgeConfigSpec.ConfigValue<String> PROMOTION_TRACK = BUILDER
            .comment("The name of the track to promote players on.")
            .define("promotionTrack", "players");

    public static final ForgeConfigSpec.ConfigValue<List<Integer>> PROMOTIONS = BUILDER
            .comment("This is a list of the times in minutes when a player should be promoted. These should be in order of least to greatest and ideally you will have one less then the total ranks in the track (Because you would be the first rank by default.)\n" +
                    " In the default example players will be promoted after 180 minutes of non afk playtime (3 hours) and again at 6000 minutes (100 hours).\n")
            .define("promotions", Arrays.asList(180, 6000));

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int promotionCheckTime;
    public static boolean protectAgainstAfkWells;
    public static List<Integer> promotions;

    public static int afkThreshold;

    public static String promotionTrack;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        promotionCheckTime = CHECK_FOR_PROMOTION_EVERY_X_MINUTES.get();
        protectAgainstAfkWells = PROTECT_AGAINST_AFK_WELLS.get();
        promotionTrack = PROMOTION_TRACK.get();
        promotions = PROMOTIONS.get();

        afkThreshold = AFK_THRESHOLD_IN_MINUTES.get() * 60 * 20;
    }
}
