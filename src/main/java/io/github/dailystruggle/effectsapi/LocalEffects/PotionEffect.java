package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.PotionTypeNames;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PotionEffect extends Effect<PotionTypeNames> {
    public PotionEffect() throws IllegalArgumentException {
        super(new EnumMap<>(PotionTypeNames.class));
        EnumMap<PotionTypeNames, Object> data = getData();
        data.put(PotionTypeNames.TYPE, org.bukkit.FireworkEffect.Type.BALL);
        data.put(PotionTypeNames.DURATION, 1);
        data.put(PotionTypeNames.AMPLIFIER, 1);
        data.put(PotionTypeNames.AMBIENT, false);
        data.put(PotionTypeNames.PARTICLES, true);
        data.put(PotionTypeNames.ICON, true);
        this.data = data;
        this.defaults = data.clone();
    }

    @Override
    public void run() {
        org.bukkit.potion.PotionEffect potionEffect = new org.bukkit.potion.PotionEffect(
                (PotionEffectType) data.get(PotionTypeNames.TYPE),
                (Integer) data.get(PotionTypeNames.DURATION),
                (Integer) data.get(PotionTypeNames.AMPLIFIER),
                (Boolean) data.get(PotionTypeNames.AMBIENT),
                (Boolean) data.get(PotionTypeNames.PARTICLES),
                (Boolean) data.get(PotionTypeNames.ICON)
        );
        if (target instanceof Player) {
            ((Player) target).addPotionEffect(potionEffect);
        } else {
            if (target instanceof Entity) target = ((Entity) target).getLocation();
            Location location = (Location) target;
            List<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers()
                    .parallelStream().filter(player -> (player.getLocation().distance(location) < 48))
                    .collect(Collectors.toList());
            for (Player player : players) {
                player.addPotionEffect(potionEffect);
            }
        }
    }
}
