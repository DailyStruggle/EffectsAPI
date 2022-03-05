package io.github.dailystruggle.effectsapi;

import io.github.dailystruggle.effectsapi.effects.*;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

//builder class to give and take effects
//  I did this to centralize effects
public class EffectBuilder {
    //map name to effect type
    //  this is a runtime solution for effectively a switch statement. I want to be able to add effects.
    protected static ConcurrentHashMap<String,Class<? extends Effect>> effectMap = new ConcurrentHashMap<>();
    static {
        EffectBuilder.effectMap.put("FIREWORK",   io.github.dailystruggle.effectsapi.effects.FireworkEffect.class);
        EffectBuilder.effectMap.put("NOTE",       NoteEffect.class);
        EffectBuilder.effectMap.put("PARTICLE",   ParticleEffect.class);
        EffectBuilder.effectMap.put("POTION",     PotionEffect.class);
        EffectBuilder.effectMap.put("SOUND",      SoundEffect.class);
    }

    public static void addEffect(String effectName, Class<? extends Effect> effectType) {
        effectMap.putIfAbsent(effectName.toUpperCase(),effectType);
    }

    public static Enumeration<String> listEffects() {
        return effectMap.keys();
    }

    public static void removeEffect(String effectName) {
        effectMap.remove(effectName);
    }

    // recall list of permissions added for removal on removeEffect
    // key: effect name
    // val: permission names
    protected static ConcurrentHashMap<String, List<String>> initializedPermissions = new ConcurrentHashMap<>();

    //T is presumably an enum, but potion effects weren't so...
    //  this should attempt name(), getName(), and toString() in that order
    public static <T> void initializePermissions(String permPrefix, String effectName, @Nullable T[] types) {
        if(types == null) {
            try {
                Bukkit.getPluginManager().addPermission(new Permission(permPrefix + "." + effectName));
            } catch (NullPointerException | IllegalArgumentException  permissionException) {
                Bukkit.getLogger().log(Level.WARNING,"[EffectsAPI] - failed to create permission:" + permPrefix + "." + effectName);
            }
            return;
        }

        List<Permission> permissionList = new ArrayList<>();
        for (T type : types) {
            if(type == null) continue;

            String name;

            //attempt type.name()
            try {
                name = (String) type.getClass().getMethod("name").invoke(type);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
                //attempt type.getName()
                try {
                    name = (String) type.getClass().getMethod("getName").invoke(type);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignoredAgain) {
                    //if none of the above, just toString
                    name = type.toString();
                }
            }

            try {
                permissionList.add(new Permission(permPrefix + "." + effectName + "." + name));
            } catch (NullPointerException | IllegalArgumentException  permissionException) {
                Bukkit.getLogger().log(Level.WARNING,"[EffectsAPI] - failed to create permission:" + permPrefix + "." + effectName + "." + name);
                return;
            }
        }

        for(Permission permission : permissionList) {
            try {
                Bukkit.getPluginManager().addPermission(permission);
            } catch (NullPointerException | IllegalArgumentException permissionException) {
                Bukkit.getLogger().log(Level.WARNING, "[EffectsAPI] - failed to add permission:" + permission.getName());
                return;
            }
        }

        permissionList.clear();
    }

    @Nullable
    public static Effect buildEffect(String name) {
        Effect effect;
        try {
            effect = effectMap.get(name.toUpperCase()).getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //todo: figure out how these are triggered and log how to fix them
            e.printStackTrace();
            return null;
        }
        return effect;
    }

    @Nullable
    public static Effect buildEffect(String name, Object... data) {
        Effect effect = buildEffect(name);
        if(effect == null) return null;
        effect.setData(data);
        return effect;
    }

    @Nullable
    public static Effect buildEffect(Permission permission) {
        return null;
    }
}
