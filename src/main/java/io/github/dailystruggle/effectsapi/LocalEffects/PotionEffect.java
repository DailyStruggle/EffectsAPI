package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.FireworkTypeNames;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.NoteTypeNames;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.ParticleTypeNames;
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
        int duration=0, amp=0;
        boolean amb=false, part=false, icon=false;

        Object o = data.get(PotionTypeNames.DURATION);
        if(o instanceof Number n) duration = n.intValue();

        o = data.get(PotionTypeNames.AMPLIFIER);
        if(o instanceof Number n) amp = n.intValue();

        o = data.get(PotionTypeNames.AMBIENT);
        if(o instanceof Boolean b) amb = b;

        o = data.get(PotionTypeNames.PARTICLES);
        if(o instanceof Boolean b) part = b;

        o = data.get(PotionTypeNames.ICON);
        if(o instanceof Boolean b) icon = b;


        org.bukkit.potion.PotionEffect potionEffect = new org.bukkit.potion.PotionEffect(
                (PotionEffectType) data.get(PotionTypeNames.TYPE),
                duration, amp, amb, part, icon
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

    @Override
    public String toPermission() {
        return this.data.get(PotionTypeNames.TYPE).toString().replaceAll("\\.*", "") +
                this.data.get(PotionTypeNames.DURATION).toString().replaceAll("\\.*", "") +
                this.data.get(PotionTypeNames.AMPLIFIER).toString().replaceAll("\\.*", "") +
                this.data.get(PotionTypeNames.AMBIENT).toString().replaceAll("\\.*", "") +
                this.data.get(PotionTypeNames.PARTICLES).toString().replaceAll("\\.*", "") +
                this.data.get(PotionTypeNames.ICON).toString().replaceAll("\\.*", "");
    }

    @Override
    public void setData(String... data) {
        if(data.length>0) this.data.put(PotionTypeNames.TYPE, data[0]);
        if(data.length>1) this.data.put(PotionTypeNames.AMPLIFIER, data[1]);
        if(data.length>2) this.data.put(PotionTypeNames.AMBIENT, data[2]);
        if(data.length>3) this.data.put(PotionTypeNames.PARTICLES, data[3]);
        if(data.length>4) this.data.put(PotionTypeNames.ICON, data[4]);
        this.data = fixData(this.data);
    }
}
