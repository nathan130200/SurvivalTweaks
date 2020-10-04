package survivaltweaks.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import survivaltweaks.SurvivalTweaks;
import survivaltweaks.Utilities;
import survivaltweaks.data.ChannelType;
import survivaltweaks.data.event.CustomChatEvent;

import java.util.List;
import java.util.stream.Collectors;

public class LocalCommand implements CommandExecutor, TabCompleter {
	private SurvivalTweaks plugin;
	
	public LocalCommand(SurvivalTweaks plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("[SurvivalTweaks::LocalCommand] Somente jogadores podem executar isso.");
			return true;
		}
		
		if (args.length < 1) {
			return true;
		}
		
		var player = (Player) sender;
		var params = Utilities.getChatMessageArguments(player, args, true);
		var evt = new CustomChatEvent(player, (String)params[0], ChannelType.LOCAL);
		evt.setMentionedPlayers((List<Player>)params[1]);
		Bukkit.getPluginManager().callEvent(evt);
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player))
			return Utilities.EmptyArgumentsList;
		
		var player = (Player) sender;
		
		var entities = player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)
				.stream().filter(x -> x instanceof Player && !x.equals(player))
				.collect(Collectors.toList());
		
		return entities.stream().map(x -> "@" + x.getName())
				.collect(Collectors.toList());
	}
}
