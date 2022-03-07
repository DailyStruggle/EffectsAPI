package io.github.dailystruggle.effectsapi;

import io.github.dailystruggle.effectsapi.SpigotListeners.FireworkSafetyListener;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Instrument;
import org.bukkit.Particle;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
        if(fireworkSafetyListener == null) {
            //on first initialization, register firework safety events
            Bukkit.getPluginManager().registerEvents(
                    new FireworkSafetyListener(this),
                    caller);
        }
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
        // Plugin startup logic, in case of standalone usage
        if(fireworkSafetyListener == null) {
            //on first initialization, register firework safety events
            fireworkSafetyListener = new FireworkSafetyListener(this);
            Bukkit.getPluginManager().registerEvents(fireworkSafetyListener,this);
        }

        //todo: set up command and tabcompleter
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }
}
