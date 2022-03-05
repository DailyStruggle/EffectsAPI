package io.github.dailystruggle.effectsapi.effects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class SoundEffect extends Effect {
    Sound sound;
    Integer volume=100,pitch=100;

    public SoundEffect() {

    }

    public SoundEffect(Sound sound, Integer volume, Integer pitch) {
        setData(sound,volume,pitch);
    }

    public SoundEffect(Object... data) {
        setData(data);
    }

    @Override
    public void setData(Object... data) throws IllegalArgumentException {
        if(data.length>0) {//type
            if(data[0] instanceof String) {
                sound = Sound.valueOf(((String)data[0]).toUpperCase());
            }
            else if(data[0] instanceof Sound) {
                sound = (Sound) data[0];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid sound: " + data[0]);
            }
        }
        if(data.length>1) {//volume
            if(data[1] instanceof String) {
                volume = Integer.parseInt((String) data[1]);
            }
            else if(data[1] instanceof Integer) {
                volume = (Integer) data[1];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid volume: " + data[1]);
            }
        }
        if(data.length>2) {//pitch
            if(data[2] instanceof String) {
                pitch = Integer.parseInt((String) data[2]);
            }
            else if(data[2] instanceof Integer) {
                pitch = (Integer) data[2];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid pitch: " + data[2]);
            }
        }
        super.setData(sound,volume,pitch);
    }

    @Override
    public Object[] getData() {
        return new Object[]{sound,volume,pitch};
    }

    @Override
    public void trigger(Location location, Plugin caller) {
        Objects.requireNonNull(location.getWorld()).playSound(location,sound,volume,pitch);
    }

    @Override
    public void trigger(Entity entity, Plugin caller) {
        trigger(entity.getLocation(),caller);
    }
}
