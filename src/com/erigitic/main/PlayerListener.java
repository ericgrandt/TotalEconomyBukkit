package com.erigitic.main;

import com.erigitic.configs.AccountsManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private TotalEconomy plugin;
    private AccountsManager aManager;

    public PlayerListener(TotalEconomy plugin) {
        this.plugin = plugin;

        aManager = AccountsManager.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OfflinePlayer player = event.getPlayer();

        if (!aManager.hasAccount(player)) {
            TotalEconomy.econ.createPlayerAccount(player);
            int expTotalToNextLevel = (2^2 + 2)/2 * 100 - (2 * 100);

            aManager.setCurJob(player, "Unemployed");
            aManager.setJobLevel(player, "Miner", 1);
            aManager.setJobExp(player, "Miner", 0);
            aManager.setJobExpTotalToNext(player, "Miner", expTotalToNextLevel);
        }
    }

}
