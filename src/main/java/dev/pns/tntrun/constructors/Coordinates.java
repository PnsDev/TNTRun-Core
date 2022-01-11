package dev.pns.tntrun.constructors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
@AllArgsConstructor
public class Coordinates {
    private double x;
    private double y;
    private double z;

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
}
