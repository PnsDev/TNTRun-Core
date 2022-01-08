package dev.pns.tntrun.tasks;

import dev.pns.tntrun.constructors.TickTimer;
import dev.pns.tntrun.constructors.TimerEvent;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Iterator;

public class TimerEventRunnable implements Runnable {
    private int count = 0;

    @Override
    public void run() {
        Iterator<TickTimer> it = Arrays.stream(TickTimer.values()).iterator();
        while (it.hasNext()) {
            count++; // Ticks added for total amount passed
            TickTimer tt = it.next();
            if (count % tt.getTicks() != 0) continue;
            Bukkit.getPluginManager().callEvent(new TimerEvent(tt));
            if (!it.hasNext()) count = 0;
        }
    }
}
