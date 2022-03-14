package io.github.dailystruggle.effectsapi.commands;

import io.github.dailystruggle.commandsapi.bukkit.BukkitParameter;
import io.github.dailystruggle.commandsapi.bukkit.LocalParameters.*;
import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.effectsapi.Effect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public abstract class GenericEffectCommand<T extends Effect<?>> extends BukkitTreeCommand {
    protected final Class<T> persistentClass;

    public GenericEffectCommand(Plugin plugin) {
        super(plugin);
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];

        Effect<?> effect;
        try {
            effect = persistentClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        for (Map.Entry<? extends Enum<?>, Object> entry : effect.getData().entrySet()) {
            Object val = entry.getValue();
            Bukkit.getLogger().warning("a- " + entry.getKey() + entry.getValue());

            if (val instanceof Integer || val instanceof Long) {
                addParameter(entry.getKey().toString().toLowerCase(), new IntegerParameter((sender1, s) -> true, 0, 1));
            } else if (val instanceof Float || val instanceof Double) {
                addParameter(entry.getKey().toString().toLowerCase(), new FloatParameter((sender1, s) -> true, 0.0, 1.0));
            } else if (val instanceof Boolean) {
                addParameter(entry.getKey().toString().toLowerCase(), new BooleanParameter((sender1, s) -> true));
            } else if (val instanceof Color) {
                addParameter(entry.getKey().toString().toLowerCase(), new ColorParameter((sender1, s) -> true));
            } else if (val instanceof PotionEffectType) {
                addParameter(entry.getKey().toString().toLowerCase(), new PotionParameter((sender1, s) -> true));
            } else if (val instanceof Enum<?>) {
                Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) val.getClass();
                addParameter(entry.getKey().toString().toLowerCase(Locale.ROOT), new BukkitParameter((sender1, s) -> true) {
                    private final List<String> values = Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toList());

                    @Override
                    public Collection<String> values() {
                        return values;
                    }
                });
            } else throw new IllegalArgumentException("bad type for - " + val);
        }
    }
}
