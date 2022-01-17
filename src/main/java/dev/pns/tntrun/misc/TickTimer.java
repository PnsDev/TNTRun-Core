package dev.pns.tntrun.misc;

import lombok.Getter;

public enum TickTimer {
    TICK_1(1),
    TICK_2(2),
    TICK_4(4),
    TICK_6(6),
    TICK_8(8),
    TICK_10(10),
    SECOND_1(20),
    SECOND_2(40),
    SECOND_4(80),
    SECOND_10(200),
    SECOND_20(400),
    SECOND_30(600),
    MINUTE_1(1200),
    MINUTE_2(2400),
    MINUTE_5(6000),
    MINUTE_7(8400),
    MINUTE_10(12000),
    MINUTE_15(18000),
    MINUTE_30(36000),
    HOUR_1(72000),
    HOUR_2(144000),
    HOUR_3(216000);

    @Getter
    private final int ticks;
    TickTimer(int ticks) {
        this.ticks = ticks;
    }

}
