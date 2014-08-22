package com.erigitic.jobs;

import com.erigitic.configs.AccountsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.util.ArrayList;

public class Jobs implements Listener, CommandExecutor {

    private String[] jobs = {"Miner"};

    private Plugin p;
    private AccountsManager aManager;

    public Jobs(Plugin p) {
        this.p = p;

        aManager = AccountsManager.getInstance();
    }

    private int getExpTotalToNext(OfflinePlayer p, String job) {
        int curLevel = aManager.getJobLevel(p, job);
        int nextLevel = curLevel + 1;

        return ((nextLevel * nextLevel + nextLevel) / 2) * 100 - (nextLevel * 100);
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        OfflinePlayer player = (OfflinePlayer) sender;

        if (command.getLabel().equalsIgnoreCase("job") && args.length > 0) {

            if (args[0].equalsIgnoreCase("levels")) {
                String curJob = aManager.getCurJob(player);

                sender.sendMessage(String.format("Current Job: %s", curJob));
                sender.sendMessage(String.format("Miner: Level %d Exp: %d exp", aManager.getJobLevel(player, "Miner"), aManager.getJobExp(player, "Miner")));
                return true;
            }

            if (args[0].equalsIgnoreCase("set") && args.length == 2) {
                boolean exists = false;

                for (int i = 0; i < jobs.length; i++) {
                    if (args[1].equalsIgnoreCase(jobs[i])) {
                        exists = true;
                        aManager.setCurJob(player, jobs[i]);
                    }
                }

                if (exists == false) {
                    sender.sendMessage(ChatColor.RED + "That job does not exist. To see the list of jobs do /job list");
                }
            } else if (args[0].equalsIgnoreCase("set") && args.length < 2) {
                sender.sendMessage(ChatColor.RED + "The correct usage is /job set [JOB].");
            }

            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.UNDERLINE + "Jobs");
                sender.sendMessage(" ");

                for (int i = 0; i < jobs.length; i++) {
                    sender.sendMessage(jobs[i]);
                }
            }
        }

        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        OfflinePlayer player = event.getPlayer();
        Block block = event.getBlock();
        String curJob = aManager.getCurJob(player);

        if (curJob.equalsIgnoreCase("Miner")) {
            for (MinerExp minerExp : MinerExp.values()) {
                if (minerExp.name().equalsIgnoreCase(block.getType().toString())) {
                    String blockName = block.getType().toString();
                    int exp = minerExp.getExp(blockName);
                    double pay = minerExp.getPay(blockName);

                    addExp(player, exp, curJob);
                    addPay(player, pay, curJob);
                }
            }
        }

        isLeveled(player, curJob);

    }

    private void addExp(OfflinePlayer player, int exp, String job) {
        int curExp = aManager.getJobExp(player, job);
        int newExp = curExp + exp;

        aManager.setJobExp(player, job, newExp);
    }

    private void addPay(OfflinePlayer player, double pay, String job) {
        double curBalance = aManager.getBalance(player);
        double newBalance = curBalance + pay;

        aManager.setBalance(player, newBalance);
    }

    private void isLeveled(OfflinePlayer player, String job) {
        int curExp = aManager.getJobExp(player, job);
        int expLeft = getExpTotalToNext(player, job) - curExp;

        while (expLeft <= 0) {
            int level = aManager.getJobLevel(player, job);

            aManager.setJobLevel(player, job, level + 1);
            aManager.setJobExpTotalToNext(player, job, getExpTotalToNext(player, job));

            curExp = aManager.getJobExp(player, job);
            expLeft = getExpTotalToNext(player, job) - curExp;
        }
    }

    private enum MinerExp {

        COAL_ORE(5, 0.50),
        QUARTZ_ORE(15, 0.75),
        IRON_ORE(25, 1.00),
        REDSTONE_ORE(35, 3.00),
        GOLD_ORE(50, 10.00),
        LAPIS_ORE(100, 25.00),
        DIAMOND_ORE(200, 50.00),
        EMERALD_ORE(300, 100.00);

        private int exp;
        private double pay;

        private MinerExp(int exp, double pay) {
            this.exp = exp;
            this.pay = pay;
        }

        public int getExp(String item) {
            return MinerExp.valueOf(item).exp;
        }

        public double getPay(String item) {
            return MinerExp.valueOf(item).pay;
        }

    }

}
