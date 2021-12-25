package me.fnfal113.sfchunkinfo.commands;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.setup.SlimefunItemSetup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScanChunk implements CommandExecutor {

    private final Map<String, Integer> AMOUNT = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasPermission("sfchunkinfo.scan")) {
                Chunk chunk = player.getLocation().getChunk();

                getAmount(chunk, player);

            } else {
                player.sendMessage("You don't have permission to use this command (perm mode: sfchunkinfo.scan)");
            }
        }

        return true;
    }

    public void getAmount(Chunk chunk, Player player){
        for(int y = 0; y <= 319; y++) {
            for(int x = 0; x <= 15; x++) {
                for(int z = 0; z <= 15; z++) {
                    Block itemStack = chunk.getBlock(x, y, z);

                    if (!Slimefun.getProtectionManager().hasPermission(
                            Bukkit.getOfflinePlayer(player.getUniqueId()),
                            itemStack,
                            Interaction.PLACE_BLOCK)
                    ) {
                        player.sendMessage("You don't have the permission to scan this chunk (Grief Protected)");
                        return;
                    }

                    if(BlockStorage.hasBlockInfo(itemStack)) {
                        AMOUNT.put(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(),  AMOUNT.getOrDefault(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(), 0) + 1);
                    }
                }
            }
        }

        player.sendMessage(ChatColor.GOLD + "# of Slimefun items on this chunk:");

        if (AMOUNT.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No Slimefun items on this chunk");
            return;
        }

        for (Map.Entry<String, Integer> entry : AMOUNT.entrySet()) {
            player.sendMessage(entry.getKey() + ": " + ChatColor.GREEN + entry.getValue());
        }

        AMOUNT.clear();

    }

}
