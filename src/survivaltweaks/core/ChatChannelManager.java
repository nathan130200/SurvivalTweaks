package survivaltweaks.core;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import survivaltweaks.SurvivalTweaks;
import survivaltweaks.Utilities;
import survivaltweaks.data.ChannelType;
import survivaltweaks.data.event.CustomChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatChannelManager implements Listener {
	private SurvivalTweaks plugin;
	private ArrayList<ChannelType> channels;
	private ArrayList<PlayerChannelEntry> players;
	
	public ChatChannelManager(SurvivalTweaks plugin) {
		this.plugin = plugin;
		
		this.channels = new ArrayList<>();
		this.channels.add(ChannelType.GLOBAL);
		this.channels.add(ChannelType.LOCAL);
		
		this.players = new ArrayList<>();
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	public synchronized void destroy() {
		if (this.channels != null) {
			this.channels.clear();
			this.channels = null;
		}
		
		if (this.players != null) {
			this.players.clear();
			this.players = null;
		}
		
		HandlerList.unregisterAll(this);
		this.plugin = null;
	}
	
	public synchronized boolean registerChannel(String name, char prefix, boolean persistent) {
		if (this.channels.stream().anyMatch(x -> x.getName().equalsIgnoreCase(name)))
			return false;
		
		var ch = ChannelType.createNew(prefix, name, persistent);
		this.channels.add(ch);
		return true;
	}
	
	public synchronized PlayerChannelEntry getPlayerChannel(Player player) {
		PlayerChannelEntry entry = this.players.stream().filter(x -> x.uuid.equals(player.getUniqueId()))
				.findFirst().orElse(null);
		
		if(entry == null){
			entry = new PlayerChannelEntry() {{
				this.uuid = player.getUniqueId();
				this.channel = ChannelType.LOCAL;
			}};
			
			this.players.add(entry);
		}
		
		return entry;
	}
	
	public synchronized int switchChannel(Player player, String name) {
		var ch = this.channels.stream().filter(x -> x.getName().equalsIgnoreCase(name))
				.findFirst().orElse(null);
		
		if (ch == null)
			return 1;
		
		var entry = this.getPlayerChannel(player);
		
		if(entry.channel == ch)
			return 2;
		
		
		entry.channel = ch;
		return 0;
	}
	
	@EventHandler
	synchronized void onChat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		
		var player = e.getPlayer();
		
		/*ChannelType chn = null;
		
		if (this.playersInChannels.containsKey(player.getUniqueId()))
			chn = this.channels.stream().filter(x -> x.getId() == this.playersInChannels.get(player.getUniqueId()))
					.findFirst().orElse(null);
		
		if (chn == null)
			chn = ChannelType.LOCAL;*/
		
		var entry = this.getPlayerChannel(player);
		var params = Utilities.getChatMessageArguments(e.getPlayer(), e.getMessage().split(" "), true);
		var evt = new CustomChatEvent(e.getPlayer(), (String) params[0], entry.channel);
		evt.setMentionedPlayers((List<Player>) params[1]);
		Bukkit.getPluginManager().callEvent(evt);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onCustomChat(CustomChatEvent e) {
		var player = e.getPlayer();
		
		if (e.getChannel() == ChannelType.LOCAL) {
			var nearbyPlayers = player.getLocation().getWorld().getNearbyEntities(e.getPlayer().getLocation(), 5, 50, 50)
					.stream().filter(x -> x instanceof Player)
					.map(x -> (Player) x).collect(Collectors.toList());
			
			for (Player p : nearbyPlayers) {
				String msg = e.getChannel().getPrefix() + (p.equals(e.getPlayer()) ? "§d" : "§7")
						+ " " + e.getPlayer().getName() + "§f: " + e.getMessage();
				
				p.sendMessage(msg);
			}
			
			for (Player p : e.getMentionedPlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 0.5f, 1f);
			}
			
			if (nearbyPlayers.size() == 1) {
				Utilities.sendSystemMessage(player, 'c', "Ninguém por perto pode lhe ouvir");
			}
		}
		else if (e.getChannel() == ChannelType.GLOBAL) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				String msg = e.getChannel().getPrefix() + (p.equals(e.getPlayer()) ? "§d" : "§7")
						+ " " + e.getPlayer().getName() + "§f: " + e.getMessage();
				
				p.sendMessage(msg);
			}
			
			for (Player p : e.getMentionedPlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 0.5f, 1f);
			}
		}
		else {
			var playersToSend = this.getPlayersInChannel(e.getChannel());
			
			for (Player p : playersToSend) {
				String msg = e.getChannel().getPrefix() + (p.equals(e.getPlayer()) ? "§d" : "§7")
						+ " " + e.getPlayer().getName() + "§f: " + e.getMessage();
				
				p.sendMessage(msg);
			}
			
			for (Player p : e.getMentionedPlayers()) {
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 0.5f, 1f);
			}
		}
	}
	
	private synchronized List<Player> getPlayersInChannel(ChannelType channel) {
		return this.players.stream().filter(x -> x.channel.equals(channel))
				.map(x -> Bukkit.getPlayer(x.uuid))
				.collect(Collectors.toList());
	}
	
	public synchronized List<String> getRegistedChannels() {
		return this.channels.stream().map(x -> x.getName()).collect(Collectors.toList());
	}
	
	public class PlayerChannelEntry {
		public UUID uuid;
		public ChannelType channel;
	}
}
