package survivaltweaks.commands.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import survivaltweaks.SurvivalTweaks;
import survivaltweaks.Utilities;

import java.util.List;

public class SwitchCommand implements CommandExecutor, TabCompleter {
	private SurvivalTweaks plugin;
	
	public SwitchCommand(SurvivalTweaks p) {
		this.plugin = p;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("[SurvivalTweaks::SwitchCommand] Somente jogadores podem executar isso.");
			return true;
		}
		
		var player = (Player)sender;
		
		if(alias != null && !alias.isEmpty()){
			if(alias.equalsIgnoreCase("leave")) {
				// TODO: this.plugin.getChatChannelManager().leaveChannel(player);
				return true;
			}
		}
		
		if(args.length < 1){
			Utilities.sendSystemMessage(player, 'c', "Você precisa fornecer o nome do canal ou subcomando para executar!");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("create")){
			if(!player.hasPermission("st.channels.create")){
				Utilities.sendSystemMessage(player, '4', "Sem permissão para criar canais!");
				return true;
			}
			
			if(args.length < 3) {
				Utilities.sendSystemMessage(player, 'c', "Uso incorreto! §fPara criar o canal: /channel create §6<nome> §a<prefixo> §c[--persistent|-p]");
				return true;
			}
			else {
				if(args[2].length() == 0 || args[2].length() > 1){
					Utilities.sendSystemMessage(player, 'c', "O prefixo pode ter somente um caractere!");
					return true;
				}
				
				String name = args[1];
				String prefix = args[2];
				
				boolean persistent = true;
				
				if(args.length >= 4){
					if(args[3].equalsIgnoreCase("-p") || args[3].equalsIgnoreCase("--persistent"))
						persistent = true;
				}
				
				if(!this.plugin.getChatChannelManager().registerChannel(name, prefix.charAt(0), persistent)){
					Utilities.sendSystemMessage(player, 'c', "Canal com mesmo nome já existe!");
					return true;
				}
			}
		}
		
		int code;
		
		if((code = this.plugin.getChatChannelManager().switchChannel(player, args[0])) != 0){
			String msg;
			
			if(code == 1) { msg = "Canal não existe!"; }
			else if(code == 2) { msg = "Você já está em um canal de chat."; }
			else { msg = "Erro desconhecido."; }
			
			Utilities.sendSystemMessage(player, 'c', msg);
		}
		else {
			Utilities.sendSystemMessage(player, '7', "Você entrou no canal §a" + args[0] + "§7.");
		}
		
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (args.length >= 1) {
			return this.plugin.getChatChannelManager().getRegistedChannels();
		}
		
		return Utilities.EmptyArgumentsList;
	}
}
