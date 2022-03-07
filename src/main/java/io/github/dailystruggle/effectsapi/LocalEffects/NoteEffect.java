package io.github.dailystruggle.effectsapi.LocalEffects;

import io.github.dailystruggle.effectsapi.Effect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NoteEffect extends Effect {
    Instrument instrument = Instrument.PIANO;
    Integer tone=0;

    public NoteEffect() {

    }

    public NoteEffect(Instrument instrument, Integer tone) {
        setData(instrument,tone);
    }

    @Override
    public void setData(Object... data) throws IllegalArgumentException {
        if(data.length>0) {//type
            if(data[0] instanceof String) {
                instrument = Instrument.valueOf(((String)data[0]).toUpperCase());
            }
            else if(data[0] instanceof Instrument){
                instrument = (Instrument) data[0];
            }
            else {
                throw new IllegalArgumentException("[EffectAPI] invalid instrument: " + data[0]);
            }
        }
        if(data.length>1) {//tone
            if (data[1] instanceof String) {
                tone = Integer.parseInt(((String) data[1]));
            } else if (data[1] instanceof Integer) {
                tone = (Integer) data[1];
            } else {
                throw new IllegalArgumentException("[EffectAPI] invalid tone: " + data[1]);
            }
        }
        super.setData(instrument,tone);
    }

    @Override
    public Object[] getData() {
        return new Object[]{instrument,tone};
    }

    @Override
    public void trigger(Location location, Plugin caller) {
        if(Bukkit.isPrimaryThread()) {
            triggerSync(location, caller);
        }
        else {
            Bukkit.getScheduler().runTask(caller, ()-> triggerSync(location, caller));
        }
    }

    private void triggerSync(Location location, Plugin caller){
        Predicate<Player> inRange = player -> (player.getLocation().distance(location)<48);
        List<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers()
                .parallelStream().filter(inRange).collect(Collectors.toList());

        NoteBlock noteData = (NoteBlock) Bukkit.createBlockData(Material.NOTE_BLOCK);
        noteData.setInstrument(instrument);
        noteData.setNote(new Note(tone));

        final Block block = location.getBlock();
        final BlockData oldBlockData = block.getBlockData().clone();
        block.setBlockData(noteData);

        //delay 1 tick to ensure note block placement on client side
        Bukkit.getScheduler().runTaskLater(caller,()-> {
            for(Player player : players) {
                player.playNote(location,instrument, new Note(tone));
            }
        },1);

        //delay 1 more tick to ensure this runs after note sound
        Bukkit.getScheduler().runTaskLater(caller,()->block.setBlockData(oldBlockData),2);
    }

    @Override
    public void trigger(Entity entity, Plugin caller) {
        if(entity instanceof Player) {
            ((Player)entity).playNote(entity.getLocation(),instrument,new Note(tone));
        }
        else trigger(entity.getLocation(), caller);
    }
}
