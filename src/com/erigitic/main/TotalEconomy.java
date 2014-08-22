package com.erigitic.main;

import com.erigitic.business.BusinessNPC;
import com.erigitic.configs.AccountsManager;
import com.erigitic.configs.SettingsManager;
import com.erigitic.jobs.Jobs;
import com.erigitic.shops.ChestShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public class TotalEconomy extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;

    private String VERSION = "8.0.2";

    @Override
    public void onEnable() {
        PluginManager pM = getServer().getPluginManager();

        AccountsManager.getInstance().setup(this);
        SettingsManager.getInstance().setup(this);

        Jobs jobs = new Jobs(this);
        ChestShop shop = new ChestShop(this);
        PlayerListener pListener = new PlayerListener(this);
        BusinessNPC businessNPC = new BusinessNPC();

        try {
            Metrics metrics = new Metrics(this);

            if (!metrics.isOptOut()) {
                metrics.start();
                log.info("[Total Economy]Metrics enabled. To disable, set opt out to true in the PluginMetrics config file.");
            } else {
                log.info("[Total Economy]Metrics disabled. To enable, set opt out to false in the PluginMetrics config file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServer().getServicesManager().register(Economy.class, new VaultConnect(), this, ServicePriority.Highest);
        }

        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            log.info(String.format("[TotalEconomy] - Enabled Version %s", VERSION));
        }

        getCommand("job").setExecutor(jobs);

        pM.registerEvents(pListener, this);
        pM.registerEvents(shop, this);
        pM.registerEvents(jobs, this);
        pM.registerEvents(businessNPC, this);
    }

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        OfflinePlayer player = (Player) sender;

        if (command.getLabel().equals("bal")) {
            sender.sendMessage(String.format("You have %s", econ.format(econ.getBalance(player))));
            return true;
        }

        return false;
    }

}
