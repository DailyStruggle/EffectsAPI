package io.github.dailystruggle.effectsapi.commands;

import io.github.dailystruggle.commandsapi.bukkit.localCommands.BukkitTreeCommand;
import io.github.dailystruggle.commandsapi.common.CommandsAPICommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectsAPIMainCommand extends BukkitTreeCommand {
    public EffectsAPIMainCommand(Plugin plugin) {
        super(plugin);
        addSubCommand(new TestCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Map<String, List<String>> parameterValues, CommandsAPICommand nextCommand) {
        return true;
    }

    @Override
    public String name() {
        return "EffectsAPI";
    }

    @Override
    public String permission() {
        return "";
    }
}
