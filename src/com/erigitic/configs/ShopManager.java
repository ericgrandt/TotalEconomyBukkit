package com.erigitic.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ShopManager {

    public ShopManager() { }

    private static ShopManager instance = new ShopManager();

    public static ShopManager getInstance() {
        return instance;
    }

    private Plugin p;
    private FileConfiguration config;
    private File cfile;

    public void setup(Plugin p) {
        this.p = p;

        if (!p.getDataFolder().exists()) p.getDataFolder().mkdir();

        cfile = new File(p.getDataFolder(), "shops.yml");

        if (!cfile.exists()) {
            try { cfile.createNewFile(); }
            catch (Exception e) { e.printStackTrace(); }
        }

        config = YamlConfiguration.loadConfiguration(cfile);
    }

    public void save() {
        try { config.save(cfile); }
        catch (Exception e) { e.printStackTrace(); }
    }

}
