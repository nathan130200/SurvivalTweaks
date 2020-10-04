package survivaltweaks;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import survivaltweaks.commands.chat.GlobalCommand;
import survivaltweaks.commands.chat.LocalCommand;
import survivaltweaks.commands.chat.SwitchCommand;
import survivaltweaks.core.ChatChannelManager;

public class SurvivalTweaks extends JavaPlugin {
	private ChatChannelManager chatChannelManager;
	
	public void onEnable() {
		chatChannelManager = new ChatChannelManager(this);
		this.getCommand("local").setExecutor(new LocalCommand(this));
		this.getCommand("global").setExecutor(new GlobalCommand(this));
		this.getCommand("switch").setExecutor(new SwitchCommand(this));
	}
	
	public ChatChannelManager getChatChannelManager() {
		return chatChannelManager;
	}
	
	public void onDisable() {
		if (chatChannelManager != null) {
			chatChannelManager.destroy();
			chatChannelManager = null;
		}
		
		HandlerList.unregisterAll(this);
	}
}
