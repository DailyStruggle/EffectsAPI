package io.github.dailystruggle.effectsapi;

import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.effectsapi.SpigotListeners.FireworkSafetyListener;
import io.github.dailystruggle.effectsapi.commands.EffectsAPIMainCommand;
import io.github.dailystruggle.effectsapi.commands.FireworkCommand;
import io.github.dailystruggle.effectsapi.commands.TestCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

//reference point for
public final class EffectsAPI extends JavaPlugin {
    public static EffectsAPI instance = null;

    /**
     * @return instance of plugin if loaded, otherwise null
     */
    @Nullable
    public static EffectsAPI getInstance(){
        return instance;
    }

    //referentiable listener, for updating
    public static FireworkSafetyListener fireworkSafetyListener = null;

    //server version checking
    private static String version = null;
    private static Integer intVersion = null;

    public EffectsAPI() {

    }

    public EffectsAPI(Plugin caller) {
        init(caller);
    }

    private static String getServerVersion() {
        if(version == null) {
            version = Bukkit.getServer().getClass().getPackage().getName();
            version = version.replaceAll("[-+^.a-zA-Z]*","");
        }

        return version;
    }

    private static Integer getServerIntVersion() {
        if(intVersion == null) {
            String[] splitVersion = getServerVersion().split("_");
            if(splitVersion.length == 0) {
                intVersion = 0;
            }
            else if (splitVersion.length == 1) {
                intVersion = Integer.valueOf(splitVersion[0]);
            }
            else {
                intVersion = Integer.valueOf(splitVersion[1]);
            }
        }
        return intVersion;
    }

    @Override
    public void onEnable() {
        init(this);

        //set up testCommand
        BukkitTreeCommand mainCommand = new EffectsAPIMainCommand(this);
        PluginCommand command = Objects.requireNonNull(getCommand("effectsapi"));
        command.setExecutor(mainCommand);
        command.setTabCompleter(mainCommand);


        //todo: set up command and tabcompleter
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }

    private static void init(Plugin caller) {
        // Plugin startup logic, in case of standalone usage
        if(fireworkSafetyListener == null) {
            //on first initialization, register firework safety events
            fireworkSafetyListener = new FireworkSafetyListener(caller);
            Bukkit.getPluginManager().registerEvents(fireworkSafetyListener,caller);
        }

        //construct commands
    }
}
