package io.github.dailystruggle.effectsapi;

import io.github.dailystruggle.effectsapi.LocalEffects.*;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
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
     */
    public static void initializePermissions(String permissionPrefix) {
        List<Permission> permissionList = new ArrayList<>();
        for (Enumeration<String> effectEnum = listEffects(); effectEnum.hasMoreElements();) {
            String effectName = effectEnum.nextElement();
            EffectType effect = effectMap.get(effectName);
            Class<?> enumeration = effect.enumeration;

            if(enumeration == null) {
                try {
                    Bukkit.getPluginManager().addPermission(new Permission(permissionPrefix + "." + effectName));
                } catch (NullPointerException | IllegalArgumentException  permissionException) {
                    Bukkit.getLogger().log(Level.WARNING,"[EffectsAPI] - failed to create permission:" + permissionPrefix + "." + effectName);
                }
                continue;
            }

            String[] types;
            try {
                types = (String[]) enumeration.getMethod("values").invoke(enumeration);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }

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
     * @param permissions - set of all permissions, typically from player.getEffectivePermissions()
     * @return all effects constructed
     */
    public static List<Effect> buildEffects(@NotNull String permissionPrefix, @NotNull Collection<PermissionAttachmentInfo> permissions) {

        List<Effect> res = new ArrayList<>();

        if(!permissionPrefix.endsWith(".")) permissionPrefix += ".";

        for (PermissionAttachmentInfo perm : permissions) {
            if (!perm.getValue()) continue;
            String node = perm.getPermission();
            if(!node.startsWith(permissionPrefix)) continue;

            String[] val = node.replace(permissionPrefix,"").split("\\.");

            EffectType effectType = effectMap.get(val[0]);
            Effect effect;
            try {
                effect = effectType.type.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }
            effect.setData((Object[]) Arrays.copyOfRange(val,1,val.length));
            res.add(effect);
        }

        return res;
    }

    @NotNull
    public static List<Effect> buildEffects(@NotNull Collection<Object[]> effects) {
        List<Effect> res = new ArrayList<>();
        for(Object[] effectArr : effects) {
            Effect effect;
            if (effectArr[0] instanceof Effect) {
                effect = (Effect) effectArr[0];
            }
            else if (effectArr[0] instanceof Class<?> effectClass) { //assuming it extends effect class
                if(!effectClass.isInstance(Effect.class)) {
                    throw new IllegalArgumentException("input effect: " + effectArr[0] + " is not an instance of EffectsAPI.Effect");
                }
                try {
                    effect = (Effect) ((Class<?>) effectArr[0]).getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            else {
                EffectType effectType = effectMap.get(effectArr[0].toString());
                try {
                    effect = effectType.type.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            effect.setData(Arrays.copyOfRange(effectArr,1, effectArr.length));
            res.add(effect);
        }
        return res;
    }
}
