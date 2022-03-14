package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.ParticleTypeNames;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

import java.util.EnumMap;
import java.util.Objects;

public class ParticleEffect extends Effect<ParticleTypeNames> {
    public ParticleEffect() throws IllegalArgumentException {
        super(new EnumMap<>(ParticleTypeNames.class));
        EnumMap<ParticleTypeNames, Object> data = getData();
        data.put(ParticleTypeNames.TYPE,Particle.EXPLOSION_NORMAL);
        data.put(ParticleTypeNames.NUMBER,1);
        this.data = data;
        this.defaults = data.clone();
    }

    @Override
    public void run() {
        if(target instanceof Entity) target = ((Entity) target).getLocation();
        Location location = (Location) target;
        Objects.requireNonNull(location.getWorld()).spawnParticle(
                (Particle) data.get(ParticleTypeNames.TYPE),
                location,
                (Integer) data.get(ParticleTypeNames.NUMBER));
    }
}
