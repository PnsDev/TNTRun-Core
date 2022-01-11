package dev.pns.tntrun.game;

import dev.pns.tntrun.constructors.Coordinates;
import lombok.Getter;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.util.*;

@Getter
public class GameMap {
    private final static Random random = new Random();

    private final String name;
    private final List<String> builders;
    private final File slimeWorld;

    private final List<Coordinates> spawnPoints = new ArrayList<>();

    // Used for map borders
    private final Coordinates mapCorner;
    private final double minY; // Used to kill player

    public GameMap(File file) {
        YamlFile yamlFile = new YamlFile(new File(file, "data.yml"));
        this.name = yamlFile.getString("name");
        this.builders = yamlFile.getStringList("builders");
        this.slimeWorld =new File(file, "map.slime");
        if (!slimeWorld.exists()) throw new IllegalArgumentException("Map file does not exist");

        ConfigurationSection spawnPointsSection = yamlFile.getConfigurationSection("spawn-points");
        for (String key : spawnPointsSection.getKeys(false)) {
            ConfigurationSection spawnPointSection = spawnPointsSection.getConfigurationSection(key);
            spawnPoints.add(new Coordinates(spawnPointSection.getInt("x"), spawnPointSection.getInt("y"), spawnPointSection.getInt("z")));
        }

        ConfigurationSection mapCornerSection = yamlFile.getConfigurationSection("map-corner");
        this.mapCorner = new Coordinates(mapCornerSection.getInt("x"), mapCornerSection.getInt("y"), mapCornerSection.getInt("z"));

        this.minY = yamlFile.getDouble("min-y");
    }

    /**
     * Gets a random map from the list of maps
     * @return A random map
     */
    public static GameMap getRandomMap() {
        List<File> files = Arrays.asList(Objects.requireNonNull(new File("maps").listFiles()));
        files.removeIf(file -> !file.isDirectory() || file.getName().equals("lobby"));
        return new GameMap(files.get(random.nextInt(files.size())));
    }
}

