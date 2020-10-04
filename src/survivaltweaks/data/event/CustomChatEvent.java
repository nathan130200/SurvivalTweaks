package survivaltweaks.data.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import survivaltweaks.data.ChannelType;

import java.util.ArrayList;
import java.util.List;

public class CustomChatEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();
	
	private Player player;
	private String message;
	private ChannelType channel;
	private List<Player> mentionedPlayers;
	
	public CustomChatEvent(Player player, String message, ChannelType channel){
		this.player = player;
		this.message = message;
		this.channel = channel;
		this.mentionedPlayers = new ArrayList<>();
	}
	
	public void setMentionedPlayers(List<Player> mentionedPlayers) {
		this.mentionedPlayers = mentionedPlayers;
	}
	
	public List<Player> getMentionedPlayers() {
		return mentionedPlayers;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getMessage() {
		return message;
	}
	
	public ChannelType getChannel() {
		return channel;
	}
	
	public HandlerList getHandlers() {
		return handlerList;
	}
	
	public static HandlerList getHandlerList(){
		return handlerList;
	}
}
