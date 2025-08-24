package de.scholle.thriller;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ThreeByThreeListener implements Listener {

    private final Plugin plugin;
    private final Map<UUID, BlockFace> lastFace = new WeakHashMap<>();

    public ThreeByThreeListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getClickedBlock() == null) return;

        lastFace.put(e.getPlayer().getUniqueId(), e.getBlockFace());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
        if (!ThrillerItem.isThriller(hand)) return;

        ItemMeta meta = hand.getItemMeta();
        if (meta == null) return;

        BlockFace face = lastFace.getOrDefault(e.getPlayer().getUniqueId(), BlockFace.UP);
        Block center = e.getBlock();

        int brokenCount = 0;
        int radius = 1;

        if (face == BlockFace.UP || face == BlockFace.DOWN) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    brokenCount += tryBreak(center, center.getX() + dx, center.getY(), center.getZ() + dz, hand);
                }
            }
        } else if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    brokenCount += tryBreak(center, center.getX() + dx, center.getY() + dy, center.getZ(), hand);
                }
            }
        } else {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    brokenCount += tryBreak(center, center.getX(), center.getY() + dy, center.getZ() + dz, hand);
                }
            }
        }

        if (brokenCount > 0 && meta instanceof Damageable dmg) {
            int newDamage = dmg.getDamage() + brokenCount;
            dmg.setDamage(newDamage);
            hand.setItemMeta((ItemMeta) dmg);

            int max = getMaxDurability(hand);
            if (max > 0 && newDamage >= max) {
                e.getPlayer().getInventory().setItemInMainHand(null);
            }
        }
    }

    private int tryBreak(Block center, int x, int y, int z, ItemStack tool) {
        Block b = center.getWorld().getBlockAt(x, y, z);
        if (b.getLocation().equals(center.getLocation())) return 0;
        if (b.isPassable() || b.getType().isAir()) return 0;
        if (b.getType() == org.bukkit.Material.BEDROCK || b.getType() == org.bukkit.Material.BARRIER) return 0;

        try {
            boolean broken = b.breakNaturally(tool);
            return broken ? 1 : 0;
        } catch (Exception ex) {
            plugin.getLogger().warning("Fehler beim natürlichen Abbauen: " + ex.getMessage());
            return 0;
        }
    }

    private int getMaxDurability(ItemStack item) {
        return item.getType().getMaxDurability();
    }
}
