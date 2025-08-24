package de.scholle.thriller;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThrillerCommand implements CommandExecutor {

    private final Main plugin;

    public ThrillerCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Nur Spieler können diesen Befehl ausführen.");
            return true;
        }

        if (!sender.hasPermission("thriller.op")) {
            sender.sendMessage("§cDafür hast du keine Rechte.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("give")) {
            p.getInventory().addItem(ThrillerItem.create());
            p.sendMessage("§aDu hast die Thriller Pickaxe erhalten!");
            return true;
        }

        sender.sendMessage("§7/thriller give");
        return true;
    }
}
