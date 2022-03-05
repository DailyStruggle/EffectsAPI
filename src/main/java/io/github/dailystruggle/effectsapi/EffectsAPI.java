package io.github.dailystruggle.effectsapi;

import io.github.dailystruggle.effectsapi.effects.*;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Instrument;
import org.bukkit.Particle;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

//        EffectBuilder.initializePermissions("EffectsAPI","FIREWORK", org.bukkit.FireworkEffect.Type.values());
//        EffectBuilder.initializePermissions("EffectsAPI","NOTE",     org.bukkit.Instrument.values());
//        EffectBuilder.initializePermissions("EffectsAPI","PARTICLE", org.bukkit.Particle.values());
//        EffectBuilder.initializePermissions("EffectsAPI","POTION",   org.bukkit.potion.PotionEffectType.values());
//        EffectBuilder.initializePermissions("EffectsAPI","SOUND",    null);

        //todo: set up command and tabcompleter
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
    }

    /**
     * @param permPrefix front of permission, e.g. "rtp.effect.teleport"
     */
    public void registerAllPermissions(String permPrefix, Plugin caller) {
        List<Permission> permissionList = new ArrayList<>();

        try {
            //adding 600+ sounds takes too long at startup, so lets skip that
            permissionList.add(new Permission(permPrefix + ".sound"));

            //all fireworks
            for (FireworkEffect.Type type : FireworkEffect.Type.values()) {
                permissionList.add(new Permission(permPrefix + ".firework." + type.name()));
            }

            //all instruments
            for (Instrument instrument : Instrument.values()) {
                permissionList.add(new Permission(permPrefix + ".note." + instrument.name()));
            }

            //all particles
            if(getServerIntVersion() > 8) {
                for (Particle particle : Particle.values()) {
                    permissionList.add(new Permission(permPrefix + ".particle." + particle.name()));
                }
            }

            //all potions
            for (PotionEffectType effect : PotionEffectType.values()) {
                permissionList.add(new Permission(permPrefix + ".potion." + effect.getName()));
            }
        }
        catch (NullPointerException | IllegalArgumentException permissionException) {
            Bukkit.getLogger().log(Level.WARNING,"[" + caller.getName() + "] - failed to initialize effect permissions. This will not affect gameplay.");
            return;
        }

        try {
            for(Permission permission : permissionList) {
                Bukkit.getPluginManager().addPermission(permission);
            }
        }
        catch (NullPointerException | IllegalArgumentException  permissionException) {
            Bukkit.getLogger().log(Level.WARNING,"[" + caller.getName() + "] - failed to initialize effect permissions. This will not affect gameplay.");
            return;
        }
        permissionList.clear();
    }

    //todo: function for initializing and triggering any effect
    //todo: function for parsing permission text into effect
}
