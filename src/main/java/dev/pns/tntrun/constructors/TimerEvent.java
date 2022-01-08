package dev.pns.tntrun.constructors;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerEvent extends Event {
    private final TickTimer timer;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public TimerEvent(TickTimer timer) {
        this.timer = timer;
    }

    public TickTimer getTimer() {return timer;}

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {return HANDLERS_LIST;}

}
