package com.dxzo.bukkit.InfusedEffects;

import org.bukkit.scheduler.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

public class ProjectileRemover extends BukkitRunnable
{
    private InfusedEffects plugin;

    public ProjectileRemover(InfusedEffects instance) {
        this.plugin = instance;
    }

    public void run() {
        try {
            for (final Entity e : this.plugin.proj.keySet()) {
                if (e.isDead()) {
                    this.plugin.proj.remove(e);
                }
            }
        }
        catch (ConcurrentModificationException ex) {}
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("InfusedEffects.use.wear")) {
                ItemStack[] armorContents;
                for (int length = (armorContents = p.getInventory().getArmorContents()).length, j = 0; j < length; ++j) {
                    final ItemStack i = armorContents[j];
                    this.plugin.ApplyIE((LivingEntity)p, i, "§3", true);
                }
                if (p.getItemInHand() == null || !(Utility.isTool(p.getItemInHand().getType()) | p.getItemInHand().getType() == Material.BOW)) {
                    continue;
                }
                this.plugin.ApplyIE((LivingEntity)p, p.getItemInHand(), "§3", true);
            }
        }
    }
}
