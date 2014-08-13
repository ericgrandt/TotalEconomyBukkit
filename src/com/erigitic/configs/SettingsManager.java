package com.erigitic.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class SettingsManager {

    public SettingsManager() { }

    private static SettingsManager instance = new SettingsManager();

    public static SettingsManager getInstance() {
        return instance;
    }

    private Plugin p;
    private FileConfiguration config;
    private File cfile;

    public void setup(Plugin p) {
        this.p = p;

        if (!p.getDataFolder().exists()) p.getDataFolder().mkdir();

        cfile = new File(p.getDataFolder(), "settings.yml");

        if (!cfile.exists()) {
            try { cfile.createNewFile(); }
            catch (Exception e) { e.printStackTrace(); }
        }

        config = YamlConfiguration.loadConfiguration(cfile);
    }

    private void save() {
        try { config.save(cfile); }
        catch (Exception e) { e.printStackTrace(); }
    }

}
