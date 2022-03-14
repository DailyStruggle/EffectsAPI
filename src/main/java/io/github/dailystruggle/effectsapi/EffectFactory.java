package io.github.dailystruggle.effectsapi;

import io.github.dailystruggle.effectsapi.LocalEffects.*;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//factory to give and take effects
//  I did this to centralize effects. Call this instead of switching on every possible effect
public class EffectFactory {
    //map name to effect type
    //  this is a runtime solution for effectively a switch statement, to allow addition and removal
    private static final ConcurrentHashMap<String, Class<? extends Effect<?>>> effectMap = new ConcurrentHashMap<>();
    // recall list of permissions added for removal on removeEffect
    // key: effect name
    // val: permission names
    private final static ConcurrentHashMap<String, List<String>> initializedPermissions = new ConcurrentHashMap<>();

    static {
        addEffect("FIREWORK", FireworkEffect.class);
        addEffect("NOTE", NoteEffect.class);
        addEffect("PARTICLE", ParticleEffect.class);
        addEffect("POTION", PotionEffect.class);
        addEffect("SOUND", SoundEffect.class);
    }

    public static void addEffect(String effectName, Class<? extends Effect<?>> effect) {
        effectMap.putIfAbsent(effectName.toUpperCase(), effect);
    }

    public static Enumeration<String> listEffects() {
        return effectMap.keys();
    }

    public static void removeEffect(String effectName) {
        effectMap.remove(effectName.toUpperCase());
    }

    @Nullable
    public static <T extends Enum<T>> Effect<T> buildEffect(String name) {
        Effect<T> effect;
        try {
            effect = (Effect<T>) effectMap.get(name.toUpperCase()).getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            //todo: figure out how these are triggered and log how to fix them
            e.printStackTrace();
            return null;
        }
        return effect;
    }

    /**
     * @param name name of effect to build
     * @param data what data the effect should have
     * @return a newly constructed effect, or null if there's no effect by that name
     */
    @Nullable
    public static <T extends Enum<T>> Effect<T> buildEffect(String name, EnumMap<T, Object> data) {
        Effect<T> effect = buildEffect(name);
        if (effect == null) return null;
        effect.setData(data);
        return effect;
    }

    /**
     * build effects from permissions
     *
     * @param permissionPrefix - which permissions to check, for contextual effects
     * @param permissions      - set of permissions, typically from player.getEffectivePermissions()
     * @return all effects constructed
     */
    public static List<Effect<?>> buildEffects(@NotNull String permissionPrefix, @NotNull Collection<PermissionAttachmentInfo> permissions) {
        List<Effect<?>> res = new ArrayList<>();
        if (!permissionPrefix.endsWith(".")) permissionPrefix += ".";

        for (PermissionAttachmentInfo perm : permissions) {
            if (!perm.getValue()) continue;
            String node = perm.getPermission();
            if (!node.startsWith(permissionPrefix)) continue;

            String[] val = node.replace(permissionPrefix, "").split("\\.");

            Effect<?> effect;
            try {
                effect = effectMap.get(val[0]).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }
            //todo: convert parameters to data
//            effect.setData((Object[]) Arrays.copyOfRange(val,1,val.length));
            res.add(effect);
        }

        return res;
    }
}
