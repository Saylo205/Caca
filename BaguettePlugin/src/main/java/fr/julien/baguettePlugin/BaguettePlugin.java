package com.baguette.smp;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class BaguettePlugin extends JavaPlugin implements Listener {

    private HashMap<UUID, Integer> playerBaguettes = new HashMap<>();
    private HashMap<UUID, HashMap<String, Integer>> playerBoosts = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Baguette Plugin activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Baguette Plugin désactivé !");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack baguette = new ItemStack(Material.BREAD, 1);
        ItemMeta meta = baguette.getItemMeta();
        meta.setDisplayName("§6🥖 Baguette Française 🥖");
        baguette.setItemMeta(meta);
        player.getWorld().dropItemNaturally(player.getLocation(), baguette);
        playerBaguettes.put(player.getUniqueId(), playerBaguettes.getOrDefault(player.getUniqueId(), 0) + 1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("boost")) {
                openBoostMenu(player);
                return true;
            } else if (cmd.getName().equalsIgnoreCase("mesboosts")) {
                showBoosts(player);
                return true;
            }
        }
        return false;
    }

    private void openBoostMenu(Player player) {
        Inventory inv = getServer().createInventory(null, 27, "§6Améliorations");

        addBoostItem(inv, 2, Material.SUGAR, "§bSpeed Boost", 5);
        addBoostItem(inv, 3, Material.BLAZE_POWDER, "§cStrength Boost", 7);
        addBoostItem(inv, 4, Material.IRON_CHESTPLATE, "§9Resistance", 3);
        addBoostItem(inv, 5, Material.HEART_OF_THE_SEA, "§3Dolphin’s Grace", 2);
        addBoostItem(inv, 6, Material.FERN, "§aLuck", 4);
        addBoostItem(inv, 7, Material.GOLDEN_APPLE, "§eHealth Boost", 4);
        addBoostItem(inv, 8, Material.FEATHER, "§fDash", 1);

        player.openInventory(inv);
    }

    private void addBoostItem(Inventory inv, int slot, Material material, String name, int maxLevel) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name + " (Max: " + maxLevel + ")");
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§6Améliorations")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();
                handleBoostPurchase(player, itemName);
            }
        }
    }

    private void handleBoostPurchase(Player player, String boostName) {
        // Extraire le nom du boost et le niveau à partir du nom complet de l'item
        String[] parts = boostName.split(" ");
        String boostType = parts[1];  // Exemple : Speed, Strength, etc.
        int maxLevel = Integer.parseInt(parts[parts.length - 1].replace(")", "").replace("Max:", "").trim());  // Récupère le niveau max

        // Vérifie si le joueur a assez de baguettes pour acheter le boost au niveau 1
        int cost = getBoostCost(boostType, 1); // On suppose que le joueur achète le premier niveau pour l'instant
        int playerBaguettesCount = playerBaguettes.getOrDefault(player.getUniqueId(), 0);

        if (playerBaguettesCount >= cost) {
            // Retirer le coût en baguettes et donner l'amélioration au joueur
            playerBaguettes.put(player.getUniqueId(), playerBaguettesCount - cost);
            player.sendMessage("§aTu as acheté : " + boostName + " pour " + cost + " 🥖!");
            // Ajouter le boost actif au joueur (si nécessaire)
            playerBoosts.putIfAbsent(player.getUniqueId(), new HashMap<>());
            playerBoosts.get(player.getUniqueId()).put(boostType, 1);  // On attribue le niveau 1 du boost
        } else {
            player.sendMessage("§cPas assez de baguettes !");
        }
    }

    private void showBoosts(Player player) {
        player.sendMessage("§aTes boosts actifs : (à compléter)");
    }

    private int getBoostCost(String boostName, int level) {
        switch (boostName) {
            case "Speed":
                switch (level) {
                    case 1: return 3;
                    case 2: return 6;
                    case 3: return 9;
                    case 4: return 12;
                    case 5: return 15;
                    default: return 0;
                }

            case "Strength":
                switch (level) {
                    case 1: return 3;
                    case 2: return 6;
                    case 3: return 9;
                    case 4: return 12;
                    case 5: return 15;
                    case 6: return 18;
                    case 7: return 21;
                    default: return 0;
                }

            case "Resistance":
                switch (level) {
                    case 1: return 3;
                    case 2: return 6;
                    case 3: return 9;
                    default: return 0;
                }

            case "Dolphin’s Grace":
                switch (level) {
                    case 1: return 3;
                    case 2: return 6;
                    default: return 0;
                }

            case "Luck":
                switch (level) {
                    case 1: return 3;
                    case 2: return 6;
                    case 3: return 9;
                    case 4: return 12;
                    default: return 0;
                }

            case "Health Boost":
                switch (level) {
                    case 1: return 3;
                    case 2: return 6;
                    case 3: return 9;
                    case 4: return 12;
                    default: return 0;
                }

            case "Dash":
                return 10;

            default:
                return 0;
        }
    }
}


