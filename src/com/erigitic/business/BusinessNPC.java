package com.erigitic.business;

import org.bukkit.*;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BusinessNPC implements Listener {

    public BusinessNPC() {

    }

    public void createNPC(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        World world = player.getPlayer().getWorld();
        Villager villager = world.spawn(player.getPlayer().getLocation(), Villager.class);//Spawn villager at the player's location
    }

    @EventHandler
    public void onVillagerClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            OfflinePlayer p = event.getPlayer();
            ItemStack license = createLicense();

            p.getPlayer().getInventory().addItem(license);
            p.getPlayer().updateInventory();
        }
    }

    private ItemStack createLicense() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(ChatColor.GOLD + "Business License");
        meta.setAuthor("TEBusiness");
        ArrayList<String> pages = new ArrayList<String>();
        pages.add("COST: $100,000");
        meta.setPages(pages);
        book.setItemMeta(meta);

        return book;
    }

}
