package io.github.dailystruggle.effectsapi.commands;

import io.github.dailystruggle.commandsapi.common.CommandsAPICommand;
import io.github.dailystruggle.effectsapi.LocalEffects.PotionEffect;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.PotionTypeNames;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PotionCommand extends GenericEffectCommand<PotionEffect> {
    public PotionCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "potion";
    }

    @Override
    public String permission() {
        return "EffectsAPI.test";
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        List<PotionEffect> effects = new ArrayList<>();
        PotionEffect mainEffect = new PotionEffect();
        effects.add(mainEffect);
        mainEffect.setTarget(sender);
        EnumMap<PotionTypeNames, Object> data = mainEffect.getData();
        for (Map.Entry<String, List<String>> entry : parameterValues.entrySet()) {
            List<String> vals = entry.getValue();
            String name = entry.getKey().toLowerCase();
            PotionTypeNames enumLookup = PotionTypeNames.valueOf(name.toUpperCase());
            String value = entry.getValue().get(0);
            data.put(enumLookup,value);
            mainEffect.setData(data);
            while (effects.size() < vals.size()) {
                effects.add((PotionEffect) mainEffect.clone());
            }
            for(int i = 1; i < vals.size(); i++) {
                PotionEffect effect = effects.get(i);
                enumLookup = PotionTypeNames.valueOf(name.toUpperCase());
                value = entry.getValue().get(i);
                data.put(enumLookup,value);
                effect.setData(data);
            }
        }
        for(PotionEffect effect : effects) {
            effect.runTask(plugin);
        }
        return true;
    }
}
