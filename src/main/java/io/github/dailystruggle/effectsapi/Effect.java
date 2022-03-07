package io.github.dailystruggle.effectsapi;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public abstract class Effect {
    private Object[] data;

    //apply parameters
    public void setData(Object... data) throws IllegalArgumentException {
        this.data = data;
    }

    //get parameters. Make sure to use setData to make changes
    public Object[] getData() {
        return data;
    }

    /**
     * @param location - location to run the effect. May get a list of entities within effect range
     * @param caller - who's calling this? important for task scheduling and debugging
     */
    //run effect at that location
    // may search for entities where needed
    abstract public void trigger(Location location, Plugin caller);

    //run effect on entity or player
    abstract public void trigger(Entity entity, Plugin caller);
}
