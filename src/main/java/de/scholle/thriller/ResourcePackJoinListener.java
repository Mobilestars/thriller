package de.scholle.thriller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Base64;

public class ResourcePackJoinListener implements Listener {

    private final Main plugin;

    public ResourcePackJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String url = plugin.getConfig().getString("resourcepack.url", "");
        String sha1 = plugin.getConfig().getString("resourcepack.sha1", "");
        if (url == null || url.isEmpty()) return;

        try {
            if (sha1 != null && !sha1.isEmpty()) {
                byte[] hash = Base64.getDecoder().decode(sha1);
                e.getPlayer().setResourcePack(url, hash);
            } else {
                e.getPlayer().setResourcePack(url);
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("Resourcepack konnte nicht gesendet werden: " + ex.getMessage());
        }
    }
}
