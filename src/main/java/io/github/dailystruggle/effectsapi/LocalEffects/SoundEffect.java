package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.SoundTypeNames;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class SoundEffect extends Effect<SoundTypeNames> {

    public SoundEffect() throws IllegalArgumentException {
        super(new EnumMap<>(SoundTypeNames.class));
        EnumMap<SoundTypeNames, Object> data = getData();
        data.put(SoundTypeNames.TYPE, Sound.ENTITY_GENERIC_EXPLODE);
        data.put(SoundTypeNames.VOLUME, 1.0);
        data.put(SoundTypeNames.PITCH, 1.0);
        data.put(SoundTypeNames.DX, 0.0);
        data.put(SoundTypeNames.DY, 0.0);
        data.put(SoundTypeNames.DZ, 0.0);
        this.data = data;
        this.defaults = data.clone();
    }

    @Override
    public void run() {
        if(target instanceof Player) {
            Player player = (Player) target;
            Location location = player.getLocation().add(
                    (Double) data.get(SoundTypeNames.DX),
                    (Double) data.get(SoundTypeNames.DY),
                    (Double) data.get(SoundTypeNames.DZ));
            player.playSound(location,
                    (Sound) data.get(SoundTypeNames.TYPE),
                    (Float) data.get(SoundTypeNames.VOLUME),
                    (Float) data.get(SoundTypeNames.PITCH)
                    );
        } else {
            if(target instanceof Entity) target = ((Entity) target).getLocation();
        }
    }
}
