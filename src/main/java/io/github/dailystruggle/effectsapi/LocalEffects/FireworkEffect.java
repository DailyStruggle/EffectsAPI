package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import io.github.dailystruggle.effectsapi.LocalEffects.enums.FireworkTypeNames;
import io.github.dailystruggle.effectsapi.SpigotListeners.FireworkSafetyListener;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.EnumMap;
import java.util.Objects;

public class FireworkEffect extends Effect<FireworkTypeNames> {
    public FireworkEffect() {
        super(new EnumMap<>(FireworkTypeNames.class));
        EnumMap<FireworkTypeNames, Object> data = getData();
        data.put(FireworkTypeNames.TYPE, org.bukkit.FireworkEffect.Type.BALL);
        data.put(FireworkTypeNames.NUMBER, 1);
        data.put(FireworkTypeNames.POWER, 0);
        data.put(FireworkTypeNames.DX, 0.0);
        data.put(FireworkTypeNames.DY, 1.0);
        data.put(FireworkTypeNames.DZ, 0.0);
        data.put(FireworkTypeNames.COLOR, Color.WHITE);
        data.put(FireworkTypeNames.FADE, Color.WHITE);
        data.put(FireworkTypeNames.FLICKER, false);
        data.put(FireworkTypeNames.TRAIL, false);
        data.put(FireworkTypeNames.SAFE, true);
        this.data = data;
        this.defaults = data.clone();
    }

    @Override
    public void run() {
        if (target instanceof Entity) target = ((Entity) target).getLocation();
        Location location = (Location) target;

        int numFireworks = (Integer) data.get(FireworkTypeNames.NUMBER);

        double dx, dy, dz;
        dx = (Double) data.get(FireworkTypeNames.DX);
        dy = (Double) data.get(FireworkTypeNames.DY);
        dz = (Double) data.get(FireworkTypeNames.DZ);

        //start with one firework
        Firework f = (Firework) Objects.requireNonNull(location.getWorld())
                .spawnEntity(location.clone().add(0, 1, 0), EntityType.FIREWORK);
        if (dx != 0.0 || dy != 1.0 || dz != 0.0) {
            f.setShotAtAngle(true);
            f.setVelocity(new Vector(dx, dy, dz));
            //todo: is pitch/yaw relevant here?
        }
        FireworkMeta fwm = f.getFireworkMeta();

        org.bukkit.FireworkEffect fireworkEffect = org.bukkit.FireworkEffect.builder()
                .with((org.bukkit.FireworkEffect.Type) data.get(FireworkTypeNames.TYPE))
                .withColor((Color) data.get(FireworkTypeNames.COLOR))
                .withFade((Color) data.get(FireworkTypeNames.FADE))
                .flicker((Boolean) data.get(FireworkTypeNames.FLICKER))
                .trail((Boolean) data.get(FireworkTypeNames.TRAIL))
                .build();
        fwm.addEffect(fireworkEffect);
        fwm.setPower((Integer) data.get(FireworkTypeNames.POWER));
        f.setFireworkMeta(fwm);

        boolean safe = (Boolean) data.get(FireworkTypeNames.SAFE);
        //if more than one, add to the list to duplicate the explosion (cheaper than multiple firework entities)
        if (numFireworks > 1) FireworkSafetyListener.addFirework(f.getEntityId(), numFireworks, safe);
        if (fwm.getPower() == 0) f.detonate();
    }
}
