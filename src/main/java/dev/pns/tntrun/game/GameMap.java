package dev.pns.tntrun.game;

import dev.pns.tntrun.constructors.Coordinates;
import lombok.Getter;
import org.bukkit.Location;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class GameMap {
    private final static Random random = new Random();

    private final String name;
    private final List<String> builders;
    private final File slimeWorld;

    private final List<Coordinates> spawnPoints = new ArrayList<>();

    // Used for map borders
    private final Coordinates minMapCorner;
    private final Coordinates maxMapCorner;
    private final double minY; // Used to kill player

    public GameMap(File file) throws IOException, InvalidConfigurationException {
        YamlFile yamlFile = new YamlFile(new File(file, "data.yml"));
        yamlFile.load();
        this.name = yamlFile.getString("name");
        this.builders = yamlFile.getStringList("builders");
        this.slimeWorld = new File(file, "region.slime");
        if (!slimeWorld.exists()) throw new IllegalArgumentException("Map file does not exist");

        List<List<Integer>> spawnPointsSection = (List<List<Integer>>) yamlFile.getList("spawn-points");
        spawnPointsSection.forEach(list -> spawnPoints.add(new Coordinates(list.get(0), list.get(1), list.get(2))));

        ConfigurationSection minMapCornerSection = yamlFile.getConfigurationSection("min-map-corner");
        this.minMapCorner = new Coordinates(minMapCornerSection.getInt("x"), minMapCornerSection.getInt("y"), minMapCornerSection.getInt("z"));

        ConfigurationSection maxMapCornerSection = yamlFile.getConfigurationSection("max-map-corner");
        this.maxMapCorner = new Coordinates(maxMapCornerSection.getInt("x"), maxMapCornerSection.getInt("y"), maxMapCornerSection.getInt("z"));

        this.minY = yamlFile.getDouble("min-y");
    }

    /**
     * Checks if a location is inside the map
     * (Mainly used for killing players when they exit the map)
     * @param location The location to check
     * @return True if the location is inside the map
     */
    public boolean isLocationInMap(Location location) {
        if (location.getX() < minMapCorner.getX() || location.getX() > maxMapCorner.getX()) return false;
        if (location.getZ() < minMapCorner.getZ() || location.getZ() > maxMapCorner.getZ()) return false;
        return !(location.getY() < minY);
    }

    /**
     * Gets a random map from the list of maps
     * @return A random map
     */
    public static GameMap getRandomMap() {
        List<File> files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("maps").listFiles())));

        files.removeIf(file -> !file.isDirectory() || file.getName().equals("lobby"));
        try {
            return new GameMap(files.get(random.nextInt(files.size())));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

