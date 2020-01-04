package com.dxzo.bukkit.InfusedEffects;

import org.bukkit.plugin.java.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;
import org.bukkit.event.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;
import java.util.stream.Collectors;

public class InfusedEffects extends JavaPlugin implements Listener {

    protected HashMap<Projectile, ItemStack> proj;
    private static List<String> numbers0To255;
    private static List<String> numbers0To10000;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);

        InfusedEffects.numbers0To255 = new ArrayList<String>();
        InfusedEffects.numbers0To10000 = new ArrayList<String>();

        for (int i = 0; i < 256; ++i) {
            InfusedEffects.numbers0To255.add(String.valueOf(i));
        }

        for (int base = 0; base < 5; ++base) {
            for (int num = 1; num < 10; ++num) {
                InfusedEffects.numbers0To10000.add(String.valueOf((int) (Math.pow(10.0, base) * num)));
            }
        }

        this.proj = new HashMap<Projectile, ItemStack>();
        Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) this);
        new ProjectileRemover(this).runTaskTimer((Plugin) this, 1L, 10L);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("addeffect")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cYou must be a player to use this command.");
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage("§6Usage: §c/addeffect <effect type>");
                return true;
            }

            PotionEffectType type;
            try {
                type = PotionEffectType.getByName(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cError: invalid number");
                return true;
            }
            if (type == null) {
                sender.sendMessage("§cError: invalid potion effect");
                return true;
            }
            if (!sender.hasPermission("InfusedEffects.command.addeffect.*")
                    && !sender.hasPermission("InfusedEffects.command.addeffect." + type.getName().toLowerCase())) {
                sender.sendMessage("§4You do not have permission to use this type of effect.");
                return true;
            }
            final ItemStack out = ((Player) sender).getItemInHand();
            if (out == null) {
                sender.sendMessage("§cYou must be holding an item.");
                return true;
            }
            if (!Utility.canApplyEffect(out.getType())) {
                sender.sendMessage("§cYou cannot apply an effect to this item.");
                return true;
            }

            // NIGHT_VISION
            // INVISIBILITY
            // INCREASE_DAMAGE
            // SPEED

            String nameType = type.getName();

            if (nameType.equals(PotionEffectType.NIGHT_VISION.getName())
                    || nameType.equals(PotionEffectType.INVISIBILITY.getName())
                    || nameType.equals(PotionEffectType.INCREASE_DAMAGE.getName())
                    || nameType.equals(PotionEffectType.SPEED.getName())) {

                final ItemMeta meta = out.getItemMeta();

                List<String> lore = (meta.getLore() == null ? new ArrayList<String>() : meta.getLore());
                List<String> newLore = new ArrayList<String>();

                if (out.getType().name().endsWith("_HELMET")) {
                    newLore.add(ChatColor.DARK_GREEN + "Infused with Night Vision");
                    meta.setDisplayName(ChatColor.AQUA + "Apollos Crest");
                } else if (out.getType().name().endsWith("_CHESTPLATE")) {
                    newLore.add(ChatColor.DARK_GREEN + "Infused with Strength");
                    meta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Aegis");
                } else if (out.getType().name().endsWith("_LEGGINGS")) {
                    newLore.add(ChatColor.DARK_GREEN + "Infused with Invisibility");
                    meta.setDisplayName(ChatColor.DARK_GRAY + "Ethereal Leggings");
                } else if (out.getType().name().endsWith("_BOOTS")) {
                    newLore.add(ChatColor.DARK_GREEN + "Infused with Speed");
                    meta.setDisplayName(ChatColor.GOLD + "Hermes Boots");
                }

                lore.addAll(newLore);
                meta.setLore(lore.stream().distinct().collect(Collectors.toList()));

                out.setItemMeta(meta);
                sender.sendMessage("§bYou have added:\n" + type.getName().toLowerCase()
                        + ", level (Static for this moment): ..., duration: **:** \nto: " + out.getType().toString());
            } else {
                sender.sendMessage("§bYou cannot Infused with this effect");
            }

            return true;
        }

        return true;
    }

    @EventHandler
    public void onEquippedOrUnequipped(ArmorEquipEvent event) {

        if (event.getType() == ArmorType.HELMET || event.getType() == ArmorType.LEGGINGS
                || event.getType() == ArmorType.CHESTPLATE || event.getType() == ArmorType.BOOTS) {

            if (event.getNewArmorPiece() == null || event.getNewArmorPiece().getType() == Material.AIR) {

                ItemStack armor = event.getOldArmorPiece();
                final List<String> lores = armor.getItemMeta().getLore();

                if (lores != null && lores.size() > 0) {
                    for (final String lore : lores) {
                        if (lore.contains("Infused")) {
                            // this.getLogger().info(ChatColor.AQUA + "[DEBUG] IS LEGEND ARMOR: " + armor);

                            Player player = event.getPlayer();
                            if (event.getType() == ArmorType.HELMET) {
                                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            } else if (event.getType() == ArmorType.CHESTPLATE) {
                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            } else if (event.getType() == ArmorType.LEGGINGS) {
                                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                            } else if (event.getType() == ArmorType.BOOTS) {
                                player.removePotionEffect(PotionEffectType.SPEED);
                            }
                        }
                    }
                }
            }

        }

    }

    protected void ApplyIE(final LivingEntity en, final ItemStack item, final String pref, final boolean tmp) {
        // this.getLogger().info(ChatColor.AQUA + "[DEBUG] Ingreso ApplyIE");

        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            return;
        }

        if (item.getType().name().endsWith("_HELMET") || item.getType().name().endsWith("_CHESTPLATE")
                || item.getType().name().endsWith("_LEGGINGS") || item.getType().name().endsWith("_BOOTS")) {

            final List<String> lores = itemMeta.getLore();
            // this.getLogger().info(ChatColor.AQUA + "[DEBUG] lores: " + lores);

            if (lores != null && lores.size() > 0) {
                for (final String lore : lores) {

                    int potionEffectLevel = 0;
                    PotionEffectType potionEffectType = null;

                    if (lore.contains(LegendArmorLoreDescriptionType.HELMET)) {
                        potionEffectType = PotionEffectType.NIGHT_VISION;
                        potionEffectLevel = 3;
                    } else if (lore.contains(LegendArmorLoreDescriptionType.CHESTPLATE)) {
                        potionEffectType = PotionEffectType.INCREASE_DAMAGE;
                        potionEffectLevel = 2;
                    } else if (lore.contains(LegendArmorLoreDescriptionType.LEGGINGS)) {
                        potionEffectType = PotionEffectType.INVISIBILITY;
                        potionEffectLevel = 2;
                    } else if (lore.contains(LegendArmorLoreDescriptionType.BOOTS)) {
                        potionEffectType = PotionEffectType.SPEED;
                        potionEffectLevel = 2;
                    } else {
                        continue;
                    }

                    try {
                        AddPotionIE(en, new PotionEffect(potionEffectType, Integer.MAX_VALUE, potionEffectLevel - 1, false, true));
                    }
                    catch (NumberFormatException ex) {}
                    catch (IllegalArgumentException ex2) {}
                }
            }
        }
    }

    private static void AddPotionIE(final LivingEntity en, final PotionEffect e) {
        boolean exist = false;
        for (final PotionEffect p : en.getActivePotionEffects()) {
            if (p.getType() == e.getType()) {
                exist = true;
                if (!(e.getAmplifier() > p.getAmplifier()
                        | (e.getAmplifier() == p.getAmplifier() & e.getDuration() > p.getDuration())
                        | p.getDuration() < 20)) {
                    continue;
                }
                en.addPotionEffect(e, true);
            }
        }
        if (!exist) {
            en.addPotionEffect(e);
        }
    }

}

final class LegendArmorLoreDescriptionType {
    public static String HELMET = "Infused with Night Vision";
    public static String CHESTPLATE = "Infused with Strength";
    public static String LEGGINGS = "Infused with Invisibility";
    public static String BOOTS = "Infused with Speed";
}
