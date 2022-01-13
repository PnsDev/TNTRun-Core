package dev.pns.tntrun.game;

import dev.pns.tntrun.constructors.Coordinates;
import lombok.Getter;
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
    private final Coordinates mapCorner;
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
        try {
            return new GameMap(files.get(random.nextInt(files.size())));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

