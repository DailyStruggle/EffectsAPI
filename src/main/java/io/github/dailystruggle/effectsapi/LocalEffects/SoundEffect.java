package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.FireworkTypeNames;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.PotionTypeNames;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.SoundTypeNames;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.EnumMap;

public class SoundEffect extends Effect<SoundTypeNames> {

    public SoundEffect() throws IllegalArgumentException {
        super(new EnumMap<>(SoundTypeNames.class));
        EnumMap<SoundTypeNames, Object> data = getData();
        data.put(SoundTypeNames.TYPE, Sound.ENTITY_GENERIC_EXPLODE);
        data.put(SoundTypeNames.VOLUME, 100);
        data.put(SoundTypeNames.PITCH, 100);
        data.put(SoundTypeNames.DX, 0.0);
        data.put(SoundTypeNames.DY, 0.0);
        data.put(SoundTypeNames.DZ, 0.0);
        this.data = data;
        this.defaults = data.clone();
    }

    @Override
    public void run() {
        if (target instanceof Player player) {
            double dx=0, dy=0, dz=0;
            float volume=100, pitch=1000;

            Object o = data.get(SoundTypeNames.VOLUME);
            if(o instanceof Number n) volume = n.floatValue()/100;

            o = data.get(SoundTypeNames.PITCH);
            if(o instanceof Number n) pitch = n.floatValue()/100;

            o = data.get(SoundTypeNames.DX);
            if(o instanceof Number n) dx = n.floatValue();

            o = data.get(SoundTypeNames.DY);
            if(o instanceof Number n) dy = n.floatValue();

            o = data.get(SoundTypeNames.DZ);
            if(o instanceof Number n) dz = n.floatValue();

            Location location = player.getLocation().add(dx,dy,dz);
            player.playSound(location,
                    (Sound) data.get(SoundTypeNames.TYPE),
                    volume, pitch
            );
        } else {
            if (target instanceof Entity) target = ((Entity) target).getLocation();
        }
    }

    @Override
    public String toPermission() {
        return this.data.get(SoundTypeNames.TYPE).toString().replaceAll("\\.*", "") +
                this.data.get(SoundTypeNames.VOLUME).toString().replaceAll("\\.*", "") +
                this.data.get(SoundTypeNames.PITCH).toString().replaceAll("\\.*", "") +
                this.data.get(SoundTypeNames.DX).toString().replaceAll("\\.*", "") +
                this.data.get(SoundTypeNames.DY).toString().replaceAll("\\.*", "") +
                this.data.get(SoundTypeNames.DZ).toString().replaceAll("\\.*", "");
    }

    @Override
    public void setData(String... data) {
        if(data.length>0) this.data.put(SoundTypeNames.TYPE, data[0]);
        if(data.length>1) this.data.put(SoundTypeNames.VOLUME, data[1]);
        if(data.length>2) this.data.put(SoundTypeNames.PITCH, data[2]);
        if(data.length>3) this.data.put(SoundTypeNames.DX, data[3]);
        if(data.length>4) this.data.put(SoundTypeNames.DY, data[4]);
        if(data.length>5) this.data.put(SoundTypeNames.DZ, data[5]);
        this.data = fixData(this.data);
    }
}
