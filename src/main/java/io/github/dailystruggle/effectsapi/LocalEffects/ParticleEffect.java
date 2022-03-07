package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class ParticleEffect extends Effect {
    Particle particle;
    Integer num = 0;

    public ParticleEffect() {

    }

    public ParticleEffect(Particle particle, Integer numParticles) throws IllegalArgumentException {
        setData(particle,numParticles);
    }

    public ParticleEffect(Object... data) {
        setData(data);
    }

    @Override
    public void setData(Object... data) throws IllegalArgumentException {
        if(data.length>0) {//type
            if(data[0] instanceof String) {
                particle = Particle.valueOf(((String)data[0]).toUpperCase());
            }
            else if(data[0] instanceof Particle) {
                particle = (Particle) data[0];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid particle: " + data[0]);
            }
        }
        if(data.length>1) {//number
            if(data[1] instanceof String) {
                num = Integer.parseInt((String) data[1]);
            }
            else if(data[1] instanceof Integer) {
                num = (Integer) data[1];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid num: " + data[1]);
            }
        }
        super.setData(particle,num);
    }

    @Override
    public void trigger(Location location, Plugin caller) {
        Objects.requireNonNull(location.getWorld()).spawnParticle(particle,location,num);
    }

    @Override
    public void trigger(Entity entity, Plugin caller) {
        trigger(entity.getLocation(),caller);
    }
}
