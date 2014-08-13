package com.erigitic.shops;

import com.erigitic.configs.AccountsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class ChestShop implements Listener {

    private Plugin p;
    private AccountsManager aManager;

    public ChestShop(Plugin p) {
        this.p = p;

        aManager = AccountsManager.getInstance();
    }

    /*
     Checks if a chest was placed underneath a sign containing text relating to
     a chest shop.
     */
    @EventHandler
    public void onChestPlace(BlockPlaceEvent event) {
        OfflinePlayer player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Block b = event.getBlock();

        if (b.getType().equals(Material.CHEST)) {
            Block blockAbove = b.getRelative(BlockFace.UP);

            if (blockAbove.getType().equals(Material.SIGN) || blockAbove.getType().equals(Material.WALL_SIGN)) {
                Sign sign = (Sign) blockAbove.getState();
                String[] lines = sign.getLines();

                if (lines[0].equals(ChatColor.DARK_BLUE + "[TEShop]") && !lines[1].equals(ChatColor.RED + "NO PRICE SET") && !lines[2].equals(ChatColor.RED + "MAT ERROR") && !lines[2].equals(ChatColor.RED + "NO MATERIAL")) {
                    player.getPlayer().sendMessage("Chest Shop created.");
                }
            }
        }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        OfflinePlayer player = Bukkit.getPlayer(playerUUID);

        if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && (event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST))) {
            boolean validShop = false;
            boolean validInv = false;
            Block b = event.getClickedBlock();
            Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);
            Sign sign = (Sign) b.getState();
            String[] lines = sign.getLines();

            if (lines[0].equals(ChatColor.DARK_BLUE + "[TEShop]") && !lines[1].equals(ChatColor.RED + "NO PRICE SET") && !lines[2].equals(ChatColor.RED + "MAT ERROR") && !lines[2].equals(ChatColor.RED + "NO MATERIAL")
                    && below.getType() == Material.CHEST)
                validShop = true;

            if (player.getPlayer().getInventory().firstEmpty() != -1) {
                validInv = true;
            }

            if (validShop == true) {
                if (below.getType().equals(Material.CHEST)) {
                    Chest shop = (Chest) below.getState();
                    Inventory shopInv = shop.getBlockInventory();
                    Inventory playerInv = player.getPlayer().getInventory();
                    Material shopItem = getShopItem(lines[2]);
                    OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
                    Player[] onlinePlayers = Bukkit.getOnlinePlayers();
                    OfflinePlayer shopOwner = null;


                    //Finds shop owner and converts to player. The loop below may not be necessary.
                    //Needs testing
                    for (int i = 0; i < onlinePlayers.length; i++) {
                        if (onlinePlayers[i].getDisplayName().equalsIgnoreCase(lines[3])) {
                            shopOwner = onlinePlayers[i];
                            break;
                        }
                    }

                    for (int i = 0; i < offlinePlayers.length; i++) {
                        if (offlinePlayers[i].getName().equalsIgnoreCase(lines[3])) {
                            shopOwner = offlinePlayers[i];
                            break;
                        }
                    }

                    boolean hasItem = false;
                    boolean shopHasSpace = false;
                    boolean canOwnerAfford = false;
                    boolean canCustAfford = false;
                    int buyPrice = getBuyPrice(lines[1]);
                    int sellPrice = getSellPrice(lines[1]);

                    if (player.getPlayer().getInventory().contains(shopItem)) {
                        hasItem = true;
                    }

                    if (aManager.getBalance(shopOwner) >= sellPrice) {
                        canOwnerAfford = true;
                    }

                    if (aManager.getBalance(player) >= buyPrice) {
                        canCustAfford = true;
                    }

                    if (shopInv.firstEmpty() == -1) {
                        for (ItemStack itemStack : shopInv.getContents()) {
                            if (itemStack.getType() == Material.DIAMOND && itemStack.getAmount() < 64) {
                                shopHasSpace = true;
                                break;
                            }
                        }
                    } else {
                        shopHasSpace = true;
                    }

                    //Buy sign
                    if (buyPrice != -1 && sellPrice == -1 && canCustAfford) {

                        if (shopInv.contains(shopItem)) {

                            if (validInv == true) {
                                aManager.removeBalance(player, buyPrice);
                                aManager.addBalance(shopOwner, buyPrice);
                                playerInv.addItem(new ItemStack(shopItem, 1));
                                player.getPlayer().updateInventory();
                                shopInv.removeItem(new ItemStack(shopItem, 1));

                                player.getPlayer().sendMessage("You bought a " + shopItem + " for $" + buyPrice);
                            } else if (validInv == false) {
                                player.getPlayer().sendMessage(ChatColor.RED + "No free inventory slot.");
                            }

                        } else if (!shopInv.contains(shopItem)) {
                            player.getPlayer().sendMessage(ChatColor.RED + "Shop is out of stock");
                        }

                    } else if (buyPrice != -1 && sellPrice == -1 && !canCustAfford) {
                        player.getPlayer().sendMessage(ChatColor.RED + "You can not afford to buy a " + shopItem);
                    }

                    //Sell sign
                    if (sellPrice != -1 && buyPrice == -1 && canOwnerAfford) {

                        if (shopHasSpace) {

                            if (hasItem == true && shopHasSpace == true) {
                                aManager.addBalance(player, sellPrice);
                                aManager.removeBalance(shopOwner, sellPrice);
                                playerInv.removeItem(new ItemStack(shopItem, 1));
                                player.getPlayer().updateInventory();
                                shopInv.addItem(new ItemStack(shopItem, 1));

                                player.getPlayer().sendMessage("You sold a " + shopItem + " for $" + sellPrice);
                            } else if (hasItem == false) {
                                player.getPlayer().sendMessage(ChatColor.RED + "You do not have any " + shopItem + " to sell.");
                            }

                        } else if (hasItem == false) {
                            player.getPlayer().sendMessage(ChatColor.RED + "You do not have any " + shopItem + " to sell.");
                        } else if (shopHasSpace == false) {
                            player.getPlayer().sendMessage(ChatColor.RED + "Shop does not have space");
                        }

                    } else if (sellPrice != -1 && buyPrice == -1 && !canOwnerAfford) {
                        player.getPlayer().sendMessage(ChatColor.RED + "Shop owner can not afford to buy a " + shopItem);
                    }

                    //Buy/Sell sign
                    if (buyPrice != -1 && sellPrice != -1) {

                        if (canCustAfford && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                            if (shopInv.contains(shopItem)) {

                                if (validInv == true) {
                                    aManager.removeBalance(player, buyPrice);
                                    aManager.addBalance(shopOwner, buyPrice);
                                    playerInv.addItem(new ItemStack(shopItem, 1));
                                    player.getPlayer().updateInventory();
                                    shopInv.removeItem(new ItemStack(shopItem, 1));

                                    player.getPlayer().sendMessage("You bought a " + shopItem + " for $" + buyPrice);
                                } else if (validInv == false) {
                                    player.getPlayer().sendMessage(ChatColor.RED + "No free inventory slot.");
                                }

                            } else if (!shopInv.contains(shopItem)) {
                                player.getPlayer().sendMessage(ChatColor.RED + "Shop is out of stock");
                            }

                        } else if (!canCustAfford && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                            player.getPlayer().sendMessage(ChatColor.RED + "You can not afford to buy a " + shopItem);
                        }

                        if (canOwnerAfford && event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {

                            if (shopHasSpace) {

                                if (hasItem == true) {
                                    aManager.addBalance(player, sellPrice);
                                    aManager.removeBalance(shopOwner, sellPrice);
                                    playerInv.removeItem(new ItemStack(shopItem, 1));
                                    player.getPlayer().updateInventory();
                                    shopInv.addItem(new ItemStack(shopItem, 1));

                                    player.getPlayer().sendMessage("You sold a " + shopItem + " for $" + sellPrice);
                                } else if (hasItem == false) {
                                    player.getPlayer().sendMessage(ChatColor.RED + "You do not have any " + shopItem + " to sell.");
                                }

                            } else if (shopHasSpace == false) {
                                player.getPlayer().sendMessage(ChatColor.RED + "Shop does not have space");
                            }

                        } else if (!canOwnerAfford && event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                            player.getPlayer().sendMessage(ChatColor.RED + "Shop owner can not afford to buy a " + shopItem);
                        }

                    }


                } else if (!below.getType().equals(Material.CHEST)) {
                    player.getPlayer().sendMessage(ChatColor.RED + "No single chest below.");
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String[] lines = e.getLines();
        Material below = e.getBlock().getRelative(BlockFace.DOWN).getType();

        if (lines[0].equals("[TEShop]")) {
            e.setLine(0, ChatColor.DARK_BLUE + "[TEShop]");
            e.setLine(3, e.getPlayer().getDisplayName());
        }

        //Error Checking
        if (!below.equals(Material.CHEST) && lines[0].equals(ChatColor.DARK_BLUE + "[TEShop]")) {
                boolean priceError = false;

                if (!lines[1].contains(":S") && getBuyPrice(lines[1]) == null) {
                    priceError = true;
                } else if (lines[1].contains(":S") && (getBuyPrice(lines[1]) == null || getSellPrice(lines[1]) == null)) {
                    priceError = true;
                } else if (lines[1].contains("S") && lines[1].contains(":") && !lines[1].contains("B")) {
                    priceError = true;
                } else if (lines[1].contains("B") && lines[1].contains(":") && !lines[1].contains("S")) {
                    priceError = true;
                } else if (!lines[1].contains("B") && getSellPrice(lines[1]) == null) {
                    priceError = true;
                }

                if (priceError == false) {
                    if ((lines[1].contains("B") && getBuyPrice(lines[1]) == -1) || (lines[1].contains("S") && getSellPrice(lines[1]) == -1)) {
                        e.setLine(1, ChatColor.RED + "ERROR");
                    } else if ((lines[1].contains("B") && getBuyPrice(lines[1]) == -2) || (lines[1].contains("S") && getSellPrice(lines[1]) == -2)) {
                        e.setLine(1, ChatColor.RED + "NO DECIMAL #");
                    } else if ((lines[1].contains("B") && getBuyPrice(lines[1]) == -3) || (lines[1].contains("S") && getSellPrice(lines[1]) == -3)) {
                        e.setLine(1, ChatColor.RED + "# TOO LARGE");
                    } else if (lines[1].contains("B") && !lines[1].contains("S")) {
                        e.setLine(1, "B " + getBuyPrice(lines[1]));
                    } else if (lines[1].contains("S") && !lines[1].contains("B")) {
                        e.setLine(1, "S " + getSellPrice(lines[1]));
                    } else if (lines[1].contains("B") && lines[1].contains("S")) {
                        e.setLine(1, "B " + getBuyPrice(lines[1]) + ":" + "S " + getSellPrice(lines[1]));
                    }
                } else {
                    e.setLine(1, ChatColor.RED + "NO PRICE SET");
                }

                if (lines[1].isEmpty()) {
                    e.setLine(1, ChatColor.RED + "NO PRICE SET");
                }

                if (lines[2].isEmpty()) {
                    e.setLine(2, ChatColor.RED + "NO MATERIAL");
                } else {
                    Material material = Material.getMaterial(lines[2].toUpperCase());

                    try {
                        material.getData();
                        e.setLine(2, material.toString().toUpperCase());
                    } catch (NullPointerException ex) {
                        e.setLine(2, ChatColor.RED + "MAT ERROR");
                    }
                }
        } else if (below.equals(Material.CHEST) && lines[0].equals(ChatColor.DARK_BLUE + "[TEShop]")) {
            e.setLine(0, ChatColor.RED + "CAN'T PLACE");
            e.setLine(1, ChatColor.RED + "ABOVE CHEST");
            e.setLine(2, ChatColor.RED + "PLACE SIGN");
            e.setLine(3, ChatColor.RED + "THEN CHEST");
        }
    }

    private Material getShopItem(String line) {
        Material material = Material.getMaterial(line.toUpperCase());

        return material;
    }

    private Integer getBuyPrice(String line) {
        String price = "";
        String buyLine;

        if (line.contains(":")) {
            buyLine = line.split(":")[0];
        } else {
            buyLine = line;
        }

        if (!buyLine.isEmpty()) {
            if (buyLine.charAt(0) != 'B')
                return -1;
            else if (buyLine.contains("."))
                return -2;

            for (int i = 0; i < buyLine.length(); i++) {
                char lineChar = buyLine.charAt(i);

                if (Character.isDigit(lineChar)) {
                    price += lineChar;
                }
            }
        } else {
            return null;
        }

        if (price != "" && price.length() < 10)
            return Integer.parseInt(price);
        else if (price.length() >= 10)
            return -3;
        else
            return null;
    }

    private Integer getSellPrice(String line) {
        String price = "";
        String sellLine;

        if (line.contains(":")) {
            sellLine = line.split(":")[1];
        } else {
            sellLine = line;
        }

        if (sellLine.charAt(0) != 'S')
            return -1;
        else if (sellLine.contains("."))
            return -2;

        for (int i = 0; i < sellLine.length(); i++) {
            char lineChar = sellLine.charAt(i);

            if (Character.isDigit(lineChar)) {
                price += lineChar;
            }
        }

        if (price != "" && price.length() < 10)
            return Integer.parseInt(price);
        else if (price.length() >= 10)
            return -3;
        else
            return null;
    }

}
