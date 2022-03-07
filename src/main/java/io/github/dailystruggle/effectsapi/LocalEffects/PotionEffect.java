package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public class PotionEffect extends Effect {
    PotionEffectType potionEffectType = PotionEffectType.ABSORPTION; //default to a safe type
    Integer duration = 255, amplifier = 1;
    Boolean ambient = false, particles = true, icon = false;

    public PotionEffect() {

    }

    public PotionEffect(PotionEffectType potionEffectType,
                        Integer duration, Integer amplifier,
                        Boolean ambient, Boolean particles, Boolean icon) {
        setData(potionEffectType,duration,amplifier,ambient, particles,icon);
    }

    @Override
    public void setData(Object... data) throws IllegalArgumentException {
        if(data.length>0) {//type
            if(data[0] instanceof String) {
                potionEffectType = PotionEffectType.getByName((String)data[0]);
            }
            else if(data[0] instanceof PotionEffectType) {
                potionEffectType = (PotionEffectType) data[0];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid potion type: " + data[0]);
            }
        }
        if(data.length>1) {//duration
            if(data[1] instanceof String) {
                duration = Integer.parseInt((String) data[1]);
            }
            else if(data[1] instanceof Integer) {
                duration = (Integer) data[1];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid potion duration: " + data[1]);
            }
        }
        if(data.length>2) {//amplifier
            if(data[2] instanceof String) {
                amplifier = Integer.parseInt((String) data[2]);
            }
            else if(data[2] instanceof Integer) {
                amplifier = (Integer) data[2];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid potion amplifier: " + data[2]);
            }
        }
        if(data.length>3) {//ambient
            if(data[3] instanceof String) {
                ambient = Boolean.parseBoolean((String) data[3]);
            }
            else if(data[3] instanceof Boolean) {
                ambient = (Boolean) data[3];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid duration: " + data[3]);
            }
        }
        if(data.length>4) {//potionParticles
            if(data[4] instanceof String) {
                particles = Boolean.parseBoolean((String) data[4]);
            }
            else if(data[4] instanceof Boolean) {
                particles = (Boolean) data[4];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid potion particles: " + data[4]);
            }
        }
        if(data.length>5) {//icon
            if(data[5] instanceof String) {
                icon = Boolean.parseBoolean((String) data[5]);
            }
            else if(data[5] instanceof Boolean) {
                icon = (Boolean) data[5];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid duration: " + data[5]);
            }
        }
        super.setData(potionEffectType,duration,amplifier,ambient, particles,icon);
    }

    @Override
    public void trigger(Location location, Plugin caller) {

    }

    @Override
    public void trigger(Entity entity, Plugin caller) {

    }
}
