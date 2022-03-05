package io.github.dailystruggle.effectsapi.effects;

import io.github.dailystruggle.effectsapi.EffectsAPI;
import io.github.dailystruggle.effectsapi.FireworkSafetyListener;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Objects;

public class FireworkEffect extends Effect {
    private org.bukkit.FireworkEffect.Type type=org.bukkit.FireworkEffect.Type.BALL;
    private Integer numFireworks=1;
    private Integer power=0;
    private final Integer dx=0;
    private final Integer dy=1;
    private final Integer dz=0;
    private Color color =Color.WHITE, fade=Color.WHITE;
    private Boolean flicker=false, trail=false, safe=true;

    public FireworkEffect() {

    }

    public FireworkEffect(org.bukkit.FireworkEffect.Type type,
                          Integer numFireworks, Integer power,
                          Color main, Color fade,
                          Boolean flicker, Boolean trail, Boolean safe,
                          Integer dx, Integer dy, Integer dz) {
        setData(type, numFireworks, power, main, fade, flicker, trail, safe, dx, dy, dz);
    }

    public FireworkEffect(Object... data) throws IllegalArgumentException {
        setData(data);
    }

    @Override
    public void setData(Object... data) throws IllegalArgumentException {
        //todo: extend these checks like the other effects
        if(data.length>0) //type
            type = (data[0] instanceof String) ? org.bukkit.FireworkEffect.Type.valueOf(((String)data[0]).toUpperCase())
                    : (org.bukkit.FireworkEffect.Type) data[0];
        if(data.length>1) //number
            numFireworks = (data[1] instanceof String) ? Integer.parseInt((String)data[1])
                    : (Integer) data[1];
        if(data.length>2) //power
            power = (data[2] instanceof String) ? Integer.parseInt((String)data[2])
                    : (Integer) data[2];
        if(data.length>3) //color
            color = (data[3] instanceof String) ? Color.fromRGB(Integer.parseInt((String) data[3], 16))
                    : (Color) data[3];
        if(data.length>4) //fade
            fade = (data[4] instanceof String) ? Color.fromRGB(Integer.parseInt((String) data[4], 16))
                    : (Color) data[4];
        if(data.length>5) //flicker
            flicker = (data[5] instanceof String) ? Boolean.parseBoolean((String)data[5])
                    : (Boolean) data[5];
        if(data.length>6) //trail
            trail = (data[6] instanceof String) ? Boolean.parseBoolean((String)data[6])
                    :  (Boolean) data[6];
        if(data.length>7) //safe, i.e. prevent firework damage to nearby entities
            safe = (data[7] instanceof String) ? Boolean.parseBoolean((String)data[7])
                    : (Boolean) data[7];
        if(data.length>8) //dx (direction)
            numFireworks = (data[8] instanceof String) ? Integer.parseInt((String)data[8])
                    : (Integer) data[8];
        if(data.length>9) //dy (direction)
            numFireworks = (data[9] instanceof String) ? Integer.parseInt((String)data[9])
                    : (Integer) data[9];
        if(data.length>10) //dz (direction)
            numFireworks = (data[10] instanceof String) ? Integer.parseInt((String)data[10])
                    : (Integer) data[10];
        super.setData(type, numFireworks, power, color, fade, flicker, trail, safe, dx, dy, dz);
    }

    @Override
    public Object[] getData() {
        return new Object[]{type, numFireworks, power, color, fade, flicker, trail, safe, dx, dy, dz};
    }

    @Override
    public void trigger(Location location, Plugin caller) {
        //start with one firework
        Firework f = (Firework) Objects.requireNonNull(location.getWorld())
                .spawnEntity(location.clone().add(0, 1, 0), EntityType.FIREWORK);
        if(dx!=0 || dy!=1 || dz!=0) {
            f.setShotAtAngle(true);
            f.setVelocity(new Vector(dx,dy,dz));
            //todo: is pitch/yaw relevant here?
        }
        FireworkMeta fwm = f.getFireworkMeta();

        org.bukkit.FireworkEffect fireworkEffect = org.bukkit.FireworkEffect.builder()
                .with(type)
                .withColor(color)
                .withFade(fade)
                .flicker(flicker)
                .trail(flicker)
                .build();
        fwm.addEffect(fireworkEffect);
        fwm.setPower(power);
        f.setFireworkMeta(fwm);

        //if more than one, add to the list to duplicate the explosion (cheaper than multiple firework entities)
        if(numFireworks > 1) FireworkSafetyListener.addFirework(f.getEntityId(),numFireworks,safe);

        if(power == 0) {
            if(Bukkit.isPrimaryThread()) {
                f.detonate();
            }
            else {
                Bukkit.getScheduler().runTask(caller, f::detonate);
            }
        }
    }

    @Override
    public void trigger(Entity entity, Plugin caller) {
        trigger(entity.getLocation(), caller);
    }
}
