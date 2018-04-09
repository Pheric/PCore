package me.pheric.pcore.util;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class YamlConfig extends YamlConfiguration {
    private Path configPath;

    public YamlConfig(Path configToLoad) {
        this.configPath = configToLoad;

        try {
            this.load(configToLoad.toFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public YamlConfig(JavaPlugin plugin, Path saveLocation, String defaultsFileName, boolean overwrite) {
        this.configPath = saveLocation;

        try {
            if (!Files.exists(saveLocation, LinkOption.NOFOLLOW_LINKS) || overwrite) {
                Files.createDirectories(saveLocation.getParent());
                Files.deleteIfExists(saveLocation);
                Files.createFile(saveLocation);
                FileUtils.copyInputStreamToFile(plugin.getResource(defaultsFileName), saveLocation.toFile());
            }
            this.load(saveLocation.toFile());
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAndReload() {
        try {
            this.save(configPath.toFile());
            this.load(configPath.toFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
