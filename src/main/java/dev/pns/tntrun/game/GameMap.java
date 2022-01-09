package dev.pns.tntrun.game;

import dev.pns.tntrun.constructors.Coordinates;
import lombok.Getter;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameMap {
    private final String name;
    private final List<String> builders;
    private final String slimeWorldName;

    private final List<Coordinates> spawnPoints = new ArrayList<>();

    // Used for map borders
    private final Coordinates mapCorner;
    private final double minY; // Used to kill player

    public GameMap(File file) {
        if (!file.getName().endsWith(".yml")) throw new IllegalArgumentException("File must be a .yml file");
        YamlFile yamlFile = new YamlFile(file);
        this.name = yamlFile.getString("name");
        this.builders = yamlFile.getStringList("builders");
        this.slimeWorldName = yamlFile.getString("slime-world-name");

        ConfigurationSection spawnPointsSection = yamlFile.getConfigurationSection("spawn-points");
        for (String key : spawnPointsSection.getKeys(false)) {
            ConfigurationSection spawnPointSection = spawnPointsSection.getConfigurationSection(key);
            spawnPoints.add(new Coordinates(spawnPointSection.getInt("x"), spawnPointSection.getInt("y"), spawnPointSection.getInt("z")));
        }

        ConfigurationSection mapCornerSection = yamlFile.getConfigurationSection("map-corner");
        this.mapCorner = new Coordinates(mapCornerSection.getInt("x"), mapCornerSection.getInt("y"), mapCornerSection.getInt("z"));

        this.minY = yamlFile.getDouble("min-y");
    }
}

