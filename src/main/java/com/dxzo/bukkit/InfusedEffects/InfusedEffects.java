package com.dxzo.bukkit.InfusedEffects;

import org.bukkit.plugin.java.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.*;
import org.bukkit.entity.*;

public class InfusedEffects extends JavaPlugin implements Listener
{
    protected HashMap<Projectile, ItemStack> proj;
    public static String FXID;
    private static List<String> numbers0To255;
    private static List<String> numbers0To10000;

    static {
        InfusedEffects.FXID = "§9» Effects:";
    }

    public void onEnable() {
        InfusedEffects.numbers0To255 = new ArrayList<String>();
        InfusedEffects.numbers0To10000 = new ArrayList<String>();
        for (int i = 0; i < 256; ++i) {
            InfusedEffects.numbers0To255.add(String.valueOf(i));
        }
        for (int base = 0; base < 5; ++base) {
            for (int num = 1; num < 10; ++num) {
                InfusedEffects.numbers0To10000.add(String.valueOf((int)(Math.pow(10.0, base) * num)));
            }
        }
        this.proj = new HashMap<Projectile, ItemStack>();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        new ProjectileRemover(this).runTaskTimer((Plugin)this, 1L, 10L);
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("addeffect")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cYou must be a player to use this command.");
                return true;
            }
            if (args.length > 4 | args.length < 3) {
                sender.sendMessage("§6Usage: §c/addeffect <effect type> <level> <duration> [-wear]");
                return true;
            }
            int lvl;
            int dur;
            PotionEffectType type;
            try {
                lvl = Integer.valueOf(args[1]);
                dur = Integer.valueOf(args[2]);
                type = PotionEffectType.getByName(args[0]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage("§cError: invalid number");
                return true;
            }
            if (type == null) {
                sender.sendMessage("§cError: invalid potion effect");
                return true;
            }
            if (!sender.hasPermission("InfusedEffects.command.addeffect.*") && !sender.hasPermission("InfusedEffects.command.addeffect." + type.getName().toLowerCase())) {
                sender.sendMessage("§4You do not have permission to use this type of effect.");
                return true;
            }
            final ItemStack out = ((Player)sender).getItemInHand();
            if (out == null) {
                sender.sendMessage("§cYou must be holding an item.");
                return true;
            }
            if (!canApplyEffect(out.getType())) {
                sender.sendMessage("§cYou cannot apply an effect to this item.");
                return true;
            }
            final ItemMeta meta = out.getItemMeta();
            String color = "§c";
            if (args.length == 4 && args[3].equalsIgnoreCase("-wear")) {
                color = "§3";
            }
            else if (args.length == 4) {
                sender.sendMessage("§cInvalid parameter");
                return true;
            }
            if (!meta.hasLore()) {
                final List<String> lore = new ArrayList<String>();
                lore.add(InfusedEffects.FXID);
                lore.add(String.valueOf(color) + type.getName().charAt(0) + type.getName().toLowerCase().substring(1) + ", " + lvl + ", " + dur);
                meta.setLore((List)lore);
            }
            else if (!meta.getLore().get(0).contains("Effects")) {
                final List<String> lore = new ArrayList<String>();
                lore.add(InfusedEffects.FXID);
                lore.add(String.valueOf(color) + type.getName().charAt(0) + type.getName().toLowerCase().substring(1) + ", " + lvl + ", " + dur);
                meta.setLore((List)lore);
            }
            else {
                boolean exist = false;
                final List<String> lore2 = (List<String>)meta.getLore();
                for (int i = 1; i < lore2.size(); ++i) {
                    if (lore2.get(i).length() > 2 && lore2.get(i).substring(2).split(", ").length == 3 && (type == PotionEffectType.getByName(lore2.get(i).substring(2).split(", ")[0]) & lore2.get(i).startsWith(color))) {
                        exist = true;
                        lore2.set(i, String.valueOf(color) + type.getName().charAt(0) + type.getName().toLowerCase().substring(1) + ", " + lvl + ", " + dur);
                        break;
                    }
                }
                if (!exist) {
                    lore2.add(String.valueOf(color) + type.getName().charAt(0) + type.getName().toLowerCase().substring(1) + ", " + lvl + ", " + dur);
                }
                meta.setLore((List)lore2);
            }
            out.setItemMeta(meta);
            sender.sendMessage("§bYou have added:\n" + type.getName().toLowerCase() + ", level: " + lvl + ", duration: " + dur + " seconds\nto: " + out.getType().toString());
            return true;
        }
        else {
            if (!cmd.getName().equalsIgnoreCase("deleffect")) {
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cYou must be a player to use this command.");
                return true;
            }
            if (args.length > 2 | args.length < 1) {
                sender.sendMessage("§6Usage: §c/deleffect <effect type|all> [-wear]");
                return true;
            }
            if (!sender.hasPermission("InfusedEffects.command.deleffect")) {
                sender.sendMessage("§4You do not have permission to use this command.");
                return true;
            }
            final ItemStack out2 = ((Player)sender).getItemInHand();
            if (out2 == null) {
                sender.sendMessage("§cYou must be holding an item.");
                return true;
            }
            if (!canApplyEffect(out2.getType())) {
                sender.sendMessage("§cYou cannot delete an effect from this item.");
                return true;
            }
            final ItemMeta meta2 = out2.getItemMeta();
            String color2 = "§c";
            if (args.length == 2 && args[1].equalsIgnoreCase("-wear")) {
                color2 = "§3";
            }
            else if (args.length == 2) {
                sender.sendMessage("§cInvalid parameter");
                return true;
            }
            if (args[0].equalsIgnoreCase("all")) {
                final List<String> lore3 = (List<String>)meta2.getLore();
                if (lore3.size() == 0) {
                    sender.sendMessage("§cThere are no effects to delete.");
                    return true;
                }
                lore3.clear();
                meta2.setLore((List)lore3);
                out2.setItemMeta(meta2);
                sender.sendMessage("§aYou have deleted all effects.");
                return true;
            }
            else {
                final PotionEffectType type2 = PotionEffectType.getByName(args[0]);
                if (type2 == null) {
                    sender.sendMessage("§cError: invalid potion effect");
                    return true;
                }
                if (!meta2.hasLore()) {
                    sender.sendMessage("§cThere are no effects to delete.");
                    return true;
                }
                if (!meta2.getLore().get(0).contains("Effects")) {
                    sender.sendMessage("§cThere are no effects to delete.");
                    return true;
                }
                final List<String> lore4 = (List<String>)meta2.getLore();
                boolean exist2 = false;
                for (int j = 1; j < lore4.size(); ++j) {
                    if (lore4.get(j).length() > 2 && lore4.get(j).substring(2).split(", ").length == 3 && (type2 == PotionEffectType.getByName(lore4.get(j).substring(2).split(", ")[0]) & lore4.get(j).startsWith(color2))) {
                        lore4.remove(j);
                        exist2 = true;
                        break;
                    }
                }
                if (!exist2) {
                    sender.sendMessage("§cThat effect was not on the item.");
                    return true;
                }
                meta2.setLore((List)lore4);
                out2.setItemMeta(meta2);
                sender.sendMessage("§aYou have deleted " + type2.getName() + " from " + out2.getType().toString());
                return true;
            }
        }
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        List<String> toreturn = new ArrayList<String>();
        toreturn.clear();
        if (command.getName().equalsIgnoreCase("addeffect") | command.getName().equalsIgnoreCase("deleffect")) {
            if ((args.length == 4 & command.getName().equalsIgnoreCase("addeffect")) | (args.length == 2 & command.getName().equalsIgnoreCase("deleffect"))) {
                toreturn.add("-wear");
            }
            if (args.length == 2 & command.getName().equalsIgnoreCase("addeffect")) {
                toreturn = new ArrayList<String>(InfusedEffects.numbers0To255);
            }
            if (args.length == 3 & command.getName().equalsIgnoreCase("addeffect")) {
                toreturn = new ArrayList<String>(InfusedEffects.numbers0To10000);
            }
            if (args.length == 1) {
                for (int i = 1; i < PotionEffectType.values().length; ++i) {
                    toreturn.add(PotionEffectType.values()[i].getName());
                }
            }
            int ind = 0;
            while (ind < toreturn.size()) {
                if (!toreturn.get(ind).toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    toreturn.remove(ind);
                }
                else {
                    ++ind;
                }
            }
        }
        return toreturn;
    }

    protected static void ApplyFX(final LivingEntity en, final ItemStack item, final String pref, final boolean tmp) {
        if (item.getItemMeta() == null) {
            return;
        }
        final List<String> Lore = (List<String>)item.getItemMeta().getLore();
        if (Lore != null && Lore.size() > 1 && Lore.get(0).contains("Effects")) {
            for (int i = 1; i < Lore.size(); ++i) {
                if (Lore.get(i).startsWith(pref)) {
                    String[] elem;
                    if (Lore.get(i).startsWith("§")) {
                        elem = Lore.get(i).substring(2).split(", ");
                    }
                    else {
                        elem = Lore.get(i).split(", ");
                    }
                    if (elem.length == 3) {
                        try {
                            if (tmp) {
                                AddPotionFX(en, new PotionEffect(PotionEffectType.getByName(elem[0].toUpperCase()), 20, (int)Integer.valueOf(elem[1]), false, true));
                            }
                            else {
                                AddPotionFX(en, new PotionEffect(PotionEffectType.getByName(elem[0].toUpperCase()), Integer.valueOf(elem[2]) * 20, (int)Integer.valueOf(elem[1]), false, true));
                            }
                        }
                        catch (NumberFormatException ex) {}
                        catch (IllegalArgumentException ex2) {}
                    }
                }
            }
        }
    }

    private static void AddPotionFX(final LivingEntity en, final PotionEffect e) {
        boolean exist = false;
        for (final PotionEffect p : en.getActivePotionEffects()) {
            if (p.getType() == e.getType()) {
                exist = true;
                if (!(e.getAmplifier() > p.getAmplifier() | (e.getAmplifier() == p.getAmplifier() & e.getDuration() > p.getDuration()) | p.getDuration() < 20)) {
                    continue;
                }
                en.addPotionEffect(e, true);
            }
        }
        if (!exist) {
            en.addPotionEffect(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
        if (event.getItem().getType().isEdible()) {
            if (!event.getPlayer().hasPermission("InfusedEffects.use.food")) {
                return;
            }
            ApplyFX((LivingEntity)event.getPlayer(), event.getItem(), "§c", false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player & event.getEntity() instanceof LivingEntity) {
            final Player damager = (Player)event.getDamager();
            if (isTool(damager.getItemInHand().getType())) {
                if (!damager.hasPermission("InfusedEffects.use.weapon")) {
                    return;
                }
                ApplyFX((LivingEntity)event.getEntity(), damager.getItemInHand(), "§c", false);
            }
        }
        if (event.getDamager() instanceof LivingEntity & event.getEntity() instanceof Player) {
            final Player damaged = (Player)event.getEntity();
            if (!damaged.hasPermission("InfusedEffects.use.armor")) {
                return;
            }
            ItemStack[] armorContents;
            for (int length = (armorContents = damaged.getInventory().getArmorContents()).length, j = 0; j < length; ++j) {
                final ItemStack i = armorContents[j];
                ApplyFX((LivingEntity)event.getDamager(), i, "§c", false);
            }
        }
        if (event.getDamager() instanceof Projectile & event.getEntity() instanceof LivingEntity) {
            final Projectile proj = (Projectile)event.getDamager();
            if (proj.getShooter() instanceof Player & this.proj.get(proj) != null) {
                ApplyFX((LivingEntity)event.getEntity(), this.proj.get(proj), "§c", false);
                this.proj.remove(proj);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player & isProjectile(event.getEntityType())) {
            final Player shooter = (Player)event.getEntity().getShooter();
            if (!shooter.hasPermission("InfusedEffects.use.weapon")) {
                return;
            }
            if (shooter.getItemInHand().getItemMeta().hasLore()) {
                this.proj.put(event.getEntity(), shooter.getItemInHand());
            }
        }
    }

    protected static boolean canApplyEffect(final Material m) {
        if (isTool(m) | m.isEdible()) {
            return true;
        }
        switch (m) {
            case BOW:
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case GOLD_HELMET:
            case GOLD_CHESTPLATE:
            case GOLD_LEGGINGS:
            case GOLD_BOOTS:
            case SNOW_BALL:
            case EGG:
            case FISHING_ROD: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    protected static boolean isProjectile(final EntityType e) {
        switch (e) {
            case ARROW:
            case SNOWBALL:
            case EGG:
            case FISHING_HOOK: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    protected static boolean isTool(final Material m) {
        switch (m) {
            case IRON_SPADE:
            case IRON_PICKAXE:
            case IRON_AXE:
            case FLINT_AND_STEEL:
            case IRON_SWORD:
            case WOOD_SWORD:
            case WOOD_SPADE:
            case WOOD_PICKAXE:
            case WOOD_AXE:
            case STONE_SWORD:
            case STONE_SPADE:
            case STONE_PICKAXE:
            case STONE_AXE:
            case DIAMOND_SWORD:
            case DIAMOND_SPADE:
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case STICK:
            case GOLD_SWORD:
            case GOLD_SPADE:
            case GOLD_PICKAXE:
            case GOLD_AXE:
            case WOOD_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case DIAMOND_HOE:
            case GOLD_HOE:
            case SHEARS: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
