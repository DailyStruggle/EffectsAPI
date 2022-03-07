import io.github.dailystruggle.effectsapi.Effect;
import io.github.dailystruggle.effectsapi.EffectBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class EffectBuilderTest {
    public static class testEffect extends Effect {
        public testEffect() {

        }

        @Override
        public void trigger(Location location, Plugin caller) {

        }

        @Override
        public void trigger(Entity entity, Plugin caller) {

        }
    }

    @Test
    public void storeEffectTest() { //test that effects are added, referenced, and removed
        String testEffectName = "TESTEFFECT";
        EffectBuilder.addEffect(testEffectName,testEffect.class);

        List<String> effects = new ArrayList<>();
        for (Enumeration<String> e = EffectBuilder.listEffects(); e.hasMoreElements();)
            effects.add(e.nextElement());
        assert(effects.contains(testEffectName));

        Effect effect = EffectBuilder.buildEffect(testEffectName);
        assert(effect != null);

        EffectBuilder.removeEffect(testEffectName);
        effects.clear();
        for (Enumeration<String> e = EffectBuilder.listEffects(); e.hasMoreElements();)
            effects.add(e.nextElement());
        assert(!effects.contains(testEffectName));
    }
}
