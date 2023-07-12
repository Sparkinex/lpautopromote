package com.sparkinex.lpautopromote.utils;

public enum PermissionLevels {
    ALL,
    MOD,
    GAME_MASTER,
    ADMIN,
    OWNER;

    public static int getPermissionLevel(PermissionLevels level) {
        return level.ordinal();
    }
}
