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

public class GlobalCommand implements CommandExecutor, TabCompleter {
	private SurvivalTweaks plugin;
	
	public GlobalCommand(SurvivalTweaks plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("[SurvivalTweaks::GlobalCOmmand] Somente jogadores podem executar isso.");
			return true;
		}
		
		var player = (Player)sender;
		var params = Utilities.getChatMessageArguments(player, args, false);
		var evt = new CustomChatEvent(player, (String) params[0], ChannelType.GLOBAL);
		evt.setMentionedPlayers((List<Player>)params[1]);
		Bukkit.getPluginManager().callEvent(evt);
		return true;
	}
	
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		return Bukkit.getOnlinePlayers().stream().map(x -> "@" + x.getName()).collect(Collectors.toList());
	}
}