package survivaltweaks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Utilities {
	public static Object[] getChatMessageArguments(Player player, String[] args, boolean nearby) {
		var mentionedPlayrsList = new ArrayList<Player>();
		List<Player> nearbyPlayers = null;
		
		if (nearby) {
			nearbyPlayers = player.getWorld().getNearbyEntities(player.getLocation(), 25f, 25f, 25f)
					.stream().filter(x -> x instanceof Player)
					.map(x -> (Player) x)
					.collect(Collectors.toList());
		}
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (arg.startsWith("@")) {
				var mention = arg.substring(1);
				
				if (!nearby) {
					Player mentionedPlayer;
					
					if ((mentionedPlayer = Bukkit.getOnlinePlayers().stream().filter(x -> x.getName().equalsIgnoreCase(mention))
						.findFirst().orElse(null)) != null) {
						args[i] = "§6" + mention + "§r";
						
						if (!mentionedPlayrsList.contains(mentionedPlayer))
							mentionedPlayrsList.add(mentionedPlayer);
					}
				}
				else {
					Player mentionedPlayer;
					
					if ((mentionedPlayer = nearbyPlayers.stream().filter(x -> x.getName().equalsIgnoreCase(mention)).findFirst()
							.orElse(null)) != null) {
						args[i] = "§6" + mention + "§r";
						
						if (!mentionedPlayrsList.contains(mentionedPlayer))
							mentionedPlayrsList.add(mentionedPlayer);
					}
				}
			}
		}
		
		return new Object[]{String.join(" ", args), mentionedPlayrsList};
	}
	
	public static void sendSystemMessage(Player player, char color, String message){
		if(player == null)
			Bukkit.getOnlinePlayers().forEach(x -> x.sendMessage("§9Sistema » §" + color + message));
		else
			player.sendMessage("§9Sistema » §7" + message);
	}
	
	public static final List<String> EmptyArgumentsList = new ArrayList<>();
}
