package de.scholle.thriller;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new ThreeByThreeListener(this), this);
        getServer().getPluginManager().registerEvents(new ResourcePackJoinListener(this), this);
        getCommand("thriller").setExecutor(new ThrillerCommand(this));

        if (getConfig().getBoolean("recipe.enabled", true)) {
            registerThrillerRecipe();
        }

        getLogger().info("Thriller enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Thriller disabled.");
    }

    public static Main getInstance() {
        return instance;
    }

    private void registerThrillerRecipe() {
        ItemStack result = ThrillerItem.create();
        if (result == null) return;

        NamespacedKey key = new NamespacedKey(this, "thriller_pickaxe");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        List<String> shape = getConfig().getStringList("recipe.shape");
        if (shape.size() == 3) recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = getConfig().getConfigurationSection("recipe.ingredients");
        if (ingredients != null) {
            for (Map.Entry<String, Object> entry : ingredients.getValues(false).entrySet()) {
                String charKey = entry.getKey();
                String matName = entry.getValue().toString();
                if (charKey.length() != 1) continue;

                Material mat = Material.matchMaterial(matName);
                if (mat != null) {
                    recipe.setIngredient(charKey.charAt(0), mat);
                } else {
                    getLogger().warning("Ungültiges Material: " + matName);
                }
            }
        }

        Bukkit.addRecipe(recipe);
        getLogger().info("Thriller-Crafting-Recipe registriert.");
    }
}