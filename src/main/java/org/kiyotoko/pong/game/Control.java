package org.kiyotoko.pong.game;

public enum Control {
    PLAYER_1_UP(1),
    PLAYER_1_DOWN(1),
    PLAYER_2_UP(2),
    PLAYER_2_DOWN(2),
    PLAYER_3_UP(3),
    PLAYER_3_DOWN(3),
    PLAYER_4_UP(4),
    PLAYER_4_DOWN(4);

    private final int player;

    Control(int player) {
        this.player = player;
    }

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";

    public static Control getControl(int player, String direction) {
        return valueOf("PLAYER_" + player + "_" + direction);
    }

    public int getPlayer() {
        return player;
    }
}
