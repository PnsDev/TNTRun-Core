package dev.pns.tntrun.game.constructors;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public class TickPosition {
    private Location location;
    private int ticks = 0;

    public TickPosition(Location location) {this.location = location;}

    public void addTick() {ticks++;}

    public void reset(Location location) {
        this.location = location;
        ticks = 0;
    }
}
