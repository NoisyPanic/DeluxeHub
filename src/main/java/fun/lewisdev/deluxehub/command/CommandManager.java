package fun.lewisdev.deluxehub.command;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.SimpleInjector;
import fun.lewisdev.deluxehub.DeluxeHub;
import fun.lewisdev.deluxehub.command.commands.*;
import fun.lewisdev.deluxehub.command.commands.gamemode.*;
import fun.lewisdev.deluxehub.config.ConfigType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class CommandManager {

    private DeluxeHub plugin;
    private FileConfiguration config;

    private CommandsManager commands;
    private CommandsManagerRegistration commandRegistry;

    private List<CustomCommand> customCommands;

    public CommandManager(DeluxeHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getFile(ConfigType.COMMANDS).getConfig();
        this.customCommands = new ArrayList<>();
    }

    public void reload() {
        if (commandRegistry != null) commandRegistry.unregisterCommands();

        commands = new BukkitCommandsManager();
        commandRegistry = new CommandsManagerRegistration(plugin, commands);
        commands.setInjector(new SimpleInjector(plugin));

        commandRegistry.register(DeluxeHubCommand.class);

        for (String command : config.getConfigurationSection("commands").getKeys(false)) {
            if (!config.getBoolean("commands." + command + ".enabled")) continue;

            registerCommand(command, config.getStringList("commands." + command + ".aliases").toArray(new String[0]));
        }

        reloadCustomCommands();
    }

    public void execute(String cmd, String[] args, CommandSender sender) throws CommandException {
        commands.execute(cmd, args, sender, sender);
    }

    public void reloadCustomCommands() {
        if (!customCommands.isEmpty()) customCommands.clear();
        if (!config.isSet("custom_commands")) return;

        for (String entry : config.getConfigurationSection("custom_commands").getKeys(false)) {

            CustomCommand customCommand = new CustomCommand(entry, config.getStringList("custom_commands." + entry + ".actions"));

            if (config.contains("custom_commands." + entry + ".aliases")) {
                customCommand.addAliases(config.getStringList("custom_commands." + entry + ".aliases"));
            }

            if (config.contains("custom_commands." + entry + ".permission")) {
                customCommand.setPermission(config.getString("custom_commands." + entry + ".permission"));
            }

            customCommands.add(customCommand);
        }
    }


    private void registerCommand(String cmd, String[] aliases) {
        switch (cmd.toUpperCase()) {
            case "GAMEMODE":
                commandRegistry.register(GamemodeCommand.class, aliases);
                break;
            case "GMS":
                commandRegistry.register(SurvivalCommand.class, aliases);
                break;
            case "GMC":
                commandRegistry.register(CreativeCommand.class, aliases);
                break;
            case "GMA":
                commandRegistry.register(AdventureCommand.class, aliases);
                break;
            case "GMSP":
                commandRegistry.register(SpectatorCommand.class, aliases);
                break;
            case "CLEARCHAT":
                commandRegistry.register(ClearchatCommand.class, aliases);
                break;
            case "FLY":
                commandRegistry.register(FlyCommand.class, aliases);
                break;
            case "LOCKCHAT":
                commandRegistry.register(LockchatCommand.class, aliases);
                break;
            case "SETLOBBY":
                commandRegistry.register(SetLobbyCommand.class, aliases);
                break;
            case "LOBBY":
                commandRegistry.register(LobbyCommand.class, aliases);
                break;
            case "VANISH":
                commandRegistry.register(VanishCommand.class, aliases);
                break;
        }
    }

    public List<CustomCommand> getCustomCommands() {
        return customCommands;
    }
}