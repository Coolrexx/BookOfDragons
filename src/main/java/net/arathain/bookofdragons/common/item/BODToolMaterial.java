package net.arathain.bookofdragons.common.item;

import net.arathain.bookofdragons.common.init.BODObjects;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum BODToolMaterial implements ToolMaterial {
    GRONKLE_IRON(3, 1752, 8.5F, 3.0F, 15, Ingredient.ofItems(BODObjects.GRONCKLE_IRON_INGOT));
    private final int level;
    private final int durability;
    private final float speed;
    private final float damage;
    private final int enchantability;
    private final Ingredient repairIngredient;

    BODToolMaterial(int level, int durability, float speed, float damage, int enchantability, Ingredient ingredient) {
        this.level = level;
        this.durability = durability;
        this.speed = speed;
        this.damage = damage;
        this.enchantability = enchantability;
        this.repairIngredient = ingredient;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return speed;
    }

    @Override
    public float getAttackDamage() {
        return damage;
    }

    @Override
    public int getMiningLevel() {
        return level;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }
}
