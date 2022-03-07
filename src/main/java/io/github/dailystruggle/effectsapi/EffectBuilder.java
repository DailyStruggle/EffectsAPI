package io.github.dailystruggle.effectsapi;

import io.github.dailystruggle.effectsapi.LocalEffects.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

//builder class to give and take effects
//  I did this to centralize effects. Call this instead of switching on every possible effect
public class EffectBuilder {
    //store both effect and values enum
    private static class EffectType {
        protected Class<? extends Effect> type;
        protected Class<?> enumeration;

        public EffectType(Class<? extends Effect> type, @Nullable Class<?> enumeration) {
            this.type = type;
            this.enumeration = enumeration;
        }
    }

    //map name to effect type
    //  this is a runtime solution for effectively a switch statement, to allow addition and removal
    private static final ConcurrentHashMap<String,EffectType> effectMap = new ConcurrentHashMap<>();
    static {
        addEffect("FIREWORK",   FireworkEffect.class,   org.bukkit.FireworkEffect.Type.class);
        addEffect("NOTE",       NoteEffect.class,       org.bukkit.Instrument.class);
        addEffect("PARTICLE",   ParticleEffect.class,   org.bukkit.Particle.class);
        addEffect("POTION",     PotionEffect.class,     org.bukkit.potion.PotionEffectType.class);
        addEffect("SOUND",      SoundEffect.class,      org.bukkit.Sound.class);
    }

    // recall list of permissions added for removal on removeEffect
    // key: effect name
    // val: permission names
    private final static ConcurrentHashMap<String, List<String>> initializedPermissions = new ConcurrentHashMap<>();

    public static void addEffect(String effectName, Class<? extends Effect> effect) {
        addEffect(effectName,effect,null);
    }

    public static <T> void addEffect(String effectName, Class<? extends Effect> effect, @Nullable Class<T> enumeration) {
        effectMap.putIfAbsent(effectName.toUpperCase(),new EffectType(effect,enumeration));
    }

    public static Enumeration<String> listEffects() {
        return effectMap.keys();
    }

    public static void removeEffect(String effectName) {
        effectMap.remove(effectName);
    }

    /**
     * this function just adds permissions to the server to make tabcompletion easier
     * @param permissionPrefix - how the permission starts
     * @param effectName - what effect to use
     * @param <T> -  T is presumably an enum class, but potion effects weren't
     *           this function should attempt T.values to get a string array
     */
    public static <T> void initializePermissions(String permissionPrefix, String effectName, @Nullable Class<T> enumeration) {
        if(enumeration == null) {
            try {
                Bukkit.getPluginManager().addPermission(new Permission(permissionPrefix + "." + effectName));
            } catch (NullPointerException | IllegalArgumentException  permissionException) {
                Bukkit.getLogger().log(Level.WARNING,"[EffectsAPI] - failed to create permission:" + permissionPrefix + "." + effectName);
            }
            return;
        }

        String[] types;
        try {
            types = (String[]) enumeration.getMethod("values").invoke(enumeration);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        List<Permission> permissionList = new ArrayList<>();
        for (String type : types) {
            if(type == null) continue;
            try {
                permissionList.add(new Permission(permissionPrefix + "." + effectName + "." + type));
            } catch (NullPointerException | IllegalArgumentException  permissionException) {
                Bukkit.getLogger().log(Level.WARNING,"[EffectsAPI] - failed to create permission:" + permissionPrefix + "." + effectName + "." + type);
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
            effect = effectMap.get(name.toUpperCase()).type.getConstructor().newInstance();
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

    /**
     * @param permissionPrefix - which permissions to check, for contextual effects
     * @param permissions - set of all permissions, typically from player.getEffectivePermissions() or similar
     * @return all effects constructed
     */
    @Nullable
    public static List<Effect> buildEffects(String permissionPrefix, Set<PermissionAttachmentInfo> permissions) {


        return null;
    }
}
