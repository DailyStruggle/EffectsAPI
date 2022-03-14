package io.github.dailystruggle.effectsapi.commands;

import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.commandsapi.common.CommandParameter;
import io.github.dailystruggle.commandsapi.common.CommandsAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestCommand extends BukkitTreeCommand {

    public TestCommand(Plugin plugin) {
        super(plugin);
        addSubCommand(new FireworkCommand(plugin));
        addSubCommand(new NoteCommand(plugin));
        addSubCommand(new ParticleCommand(plugin));
        addSubCommand(new PotionCommand(plugin));
        addSubCommand(new SoundCommand(plugin));
    }

    @Override
    public String name() {
        return "Test";
    }

    @Override
    public String permission() {
        return "EffectsAPI.test";
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        return true;
    }
}
