package me.fnfal113.sfchunkinfo.commands;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class ScanChunk implements TabExecutor {

    private final Map<String, Integer> AMOUNT = new HashMap<>();
    private final Map<String, String> INFO = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0) {
                if (player.hasPermission("sfchunkinfo.scan")) {
                    Chunk chunk = player.getLocation().getChunk();

                    getAmount(chunk, player);

                } else {
                    player.sendMessage("You don't have permission to use this command (perm mode: sfchunkinfo.scan)");
                }
            } else {
                if (player.hasPermission("sfchunkinfo.scan.others")) {
                    Player target = Bukkit.getPlayer(args[0]);

                    if(target == null){
                        player.sendMessage("Player cannot be null or offline");
                        return true;
                    }

                    Chunk chunk = target.getLocation().getChunk();

                    getAmountOthers(chunk, target, player);

                } else {
                    player.sendMessage("You don't have permission to use this command (perm mode: sfchunkinfo.scan.others)");
                }
            }
        }

        return true;
    }

    public void getAmount(Chunk chunk, Player player){
        for(int y = 0; y <= chunk.getWorld().getMaxHeight(); y++) {
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
                        INFO.put(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(), Objects.requireNonNull(BlockStorage.check(itemStack)).getAddon().getName());
                        AMOUNT.put(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(),  AMOUNT.getOrDefault(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(), 0) + 1);
                    }
                }
            }
        }

        player.sendMessage(ChatColor.GOLD + "# of Slimefun items on this chunk:", "");

        if (AMOUNT.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No Slimefun items on this chunk");
            return;
        }

        AMOUNT.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(e -> player.sendMessage(e.getKey() + ": " + ChatColor.GREEN + e.getValue()));

        player.spigot().sendMessage(hoverInfo(INFO));

        INFO.clear();
        AMOUNT.clear();

    }

    public void getAmountOthers(Chunk chunk, Player player, Player sender){
        for(int y = 0; y <= chunk.getWorld().getMaxHeight(); y++) {
            for(int x = 0; x <= 15; x++) {
                for(int z = 0; z <= 15; z++) {
                    Block itemStack = chunk.getBlock(x, y, z);
                    if(BlockStorage.hasBlockInfo(itemStack)) {
                        INFO.put(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(), Objects.requireNonNull(BlockStorage.check(itemStack)).getAddon().getName());
                        AMOUNT.put(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(),  AMOUNT.getOrDefault(Objects.requireNonNull(BlockStorage.check(itemStack)).getItemName(), 0) + 1);
                    }
                }
            }
        }

        sender.sendMessage(ChatColor.GOLD + "# of Slimefun items on " + ChatColor.WHITE + player.getName() + ChatColor.GOLD + " chunk:", "");

        if (AMOUNT.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No Slimefun items on " + ChatColor.WHITE + player.getName() + ChatColor.GOLD + " chunk");
            return;
        }

        AMOUNT.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(e -> sender.sendMessage(e.getKey() + ": " + ChatColor.GREEN + e.getValue()));

        sender.spigot().sendMessage(hoverInfo(INFO));

        INFO.clear();
        AMOUNT.clear();

    }

    public TextComponent hoverInfo(Map<String, String> info){
        TextComponent infoChunk = new TextComponent( "\nHover for some info" );
        infoChunk.setColor(net.md_5.bungee.api.ChatColor.WHITE);
        infoChunk.setItalic(true);
        infoChunk.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new Text(info.toString().replace("{","").replace("}","").replace(", ", "\n").replace("=", ChatColor.WHITE + " | from: "))));

        return infoChunk;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(args.length == 1){
            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (Player player : players) {
                playerNames.add(player.getName());
            }

            return playerNames;
        }


        return null;
    }
}
