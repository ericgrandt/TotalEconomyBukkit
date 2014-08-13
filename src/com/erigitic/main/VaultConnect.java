package com.erigitic.main;

import com.erigitic.configs.AccountsManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.List;

public class VaultConnect implements Economy {

    @Override
    public boolean isEnabled() {
        return AccountsManager.getInstance().getPlugin().isEnabled();
    }

    @Override
    public String getName() {
        return "TotalEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        DecimalFormat df = new DecimalFormat("0.00");

        return "$" + df.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        if (!AccountsManager.getInstance().hasAccount(offlinePlayer)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean hasAccount(String s, String s2) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public double getBalance(String s) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return AccountsManager.getInstance().getBalance(offlinePlayer);
    }

    @Override
    public double getBalance(String name, String s2) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String name, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return AccountsManager.getInstance().getBalance(offlinePlayer) >= amount;
    }

    @Override
    public boolean has(String name, String world, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double amount) {
        return has(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        return new EconomyResponse(amount, AccountsManager.getInstance().getBalance(offlinePlayer) - amount, AccountsManager.getInstance().removeBalance(offlinePlayer, amount) ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, "Insufficient Funds.");
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, String world, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        AccountsManager.getInstance().addBalance(offlinePlayer, amount);
        return new EconomyResponse(amount, AccountsManager.getInstance().getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String name, String world, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse createBank(String s, String s2) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s2) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s2) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        AccountsManager.getInstance().setBalance(offlinePlayer, 100);
        return false;
    }

    @Override
    public boolean createPlayerAccount(String name, String world) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer);
    }
}
