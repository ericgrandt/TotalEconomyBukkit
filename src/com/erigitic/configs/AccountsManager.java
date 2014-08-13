package com.erigitic.configs;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class AccountsManager {

    public AccountsManager() { }

    private static AccountsManager instance = new AccountsManager();

    public static AccountsManager getInstance() {
        return instance;
    }

    private Plugin p;
    private FileConfiguration config;
    private File cfile;

    public void setup(Plugin p) {
        this.p = p;

        if (!p.getDataFolder().exists()) p.getDataFolder().mkdir();

        cfile = new File(p.getDataFolder(), "accounts.yml");

        if (!cfile.exists()) {
            try { cfile.createNewFile(); }
            catch (Exception e) { e.printStackTrace(); }
        }

        config = YamlConfiguration.loadConfiguration(cfile);
    }

    public boolean hasAccount(OfflinePlayer p) {
        return config.contains("money." + p.getUniqueId());
    }

    public double getBalance(OfflinePlayer p) {
        return config.getDouble("money." + p.getUniqueId());
    }

    public void addBalance(OfflinePlayer p, double amount) {
        setBalance(p, getBalance(p) + amount);
    }

    public boolean removeBalance(OfflinePlayer p, double amount) {
        if (getBalance(p) < 0) {
            return false;
        }

        setBalance(p, getBalance(p) - amount);
        return true;
    }

    public void setBalance(OfflinePlayer p, double amount) {
        BigDecimal bdAmount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);

        config.set("money." + p.getUniqueId(), bdAmount);
        save();
    }

    public void setJobLevel(OfflinePlayer p, String job, int level) {
        config.set("job." + p.getUniqueId() + "." + job + ".level", level);
        save();
    }

    public void setJobExp(OfflinePlayer p, String job, int exp) {
        config.set("job." + p.getUniqueId() + "." + job + ".exp", exp);
        save();
    }

    public void setJobExpTotalToNext(OfflinePlayer p, String job, int expTotalToNextLevel) {
        config.set("job." + p.getUniqueId() + "." + job + ".expToLevel", expTotalToNextLevel);
        save();
    }

    public void setCurJob(OfflinePlayer p, String job) {
        config.set("job." + p.getUniqueId() + ".curjob", job);
        save();
    }

    public int getJobLevel(OfflinePlayer p, String job) {
        return config.getInt("job." + p.getUniqueId() + "." + job + ".level");
    }


    public int getJobExp(OfflinePlayer p, String job) {
        return config.getInt("job." + p.getUniqueId() + "." + job + ".exp");
    }

    public String getCurJob(OfflinePlayer p) {
        return config.getString("job." + p.getUniqueId() + ".curjob");
    }

    private void save() {
        try { config.save(cfile); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public Plugin getPlugin() {
        return p;
    }
}