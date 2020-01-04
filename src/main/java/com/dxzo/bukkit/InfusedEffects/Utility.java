package com.dxzo.bukkit.InfusedEffects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public final class Utility {
    public static boolean canApplyEffect(final Material m) {
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

    public static boolean isProjectile(final EntityType e) {
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

    public static boolean isTool(final Material m) {
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
