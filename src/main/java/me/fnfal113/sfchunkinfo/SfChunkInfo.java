package me.fnfal113.sfchunkinfo;

import io.github.bakedlibs.dough.updater.BlobBuildUpdater;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.fnfal113.sfchunkinfo.commands.ScanChunk;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SfChunkInfo extends JavaPlugin implements SlimefunAddon {

    private static SfChunkInfo instance;

    @Override
    public void onEnable() {
        setInstance(this);
        new Metrics(this, 13713);

        getLogger().info("******************************************************");
        getLogger().info("*         SfChunkInfo - Created by FN_FAL113         *");
        getLogger().info("*                 Addon for Slimefun                 *");
        getLogger().info("*         Scan your chunk for # of sf blocks         *");
        getLogger().info("******************************************************");


        Objects.requireNonNull(getCommand("sfchunkinfo")).setExecutor(new ScanChunk());

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        if (getConfig().getBoolean("auto-update", true) && getDescription().getVersion().startsWith("Dev - ")) {
            new BlobBuildUpdater(this, getFile(), "SfChunkInfo").start();
        }
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/FN-FAL113/SfChunkInfo/issues";
    }

    private static void setInstance(SfChunkInfo ins) {
        instance = ins;
    }

    public static SfChunkInfo getInstance() {
        return instance;
    }

}
