package survivaltweaks.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import survivaltweaks.SurvivalTweaks;

import java.util.concurrent.ThreadLocalRandom;

public class MiscEvents implements Listener {
	private SurvivalTweaks plugin;
	private BukkitTask updateMobsTask;
	
	public MiscEvents(SurvivalTweaks plugin) {
		this.plugin = plugin;
		this.updateMobsTask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this::UpdateMobs, 0, 20L);
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	public void release(){
		if(this.updateMobsTask != null){
			this.updateMobsTask.cancel();
			this.updateMobsTask = null;
		}
	}
	
	void UpdateMobs(){
		for(World w : Bukkit.getServer().getWorlds()){
			for(Entity e : w.getEntities()){
				if(e instanceof LivingEntity && e.hasMetadata("spawn_loop_tag")){
					var ent = (LivingEntity)e;
					var count = ent.getMetadata("spawn_count").get(0).asInt();
					ent.setCustomName("§c♥ §7" + ent.getHealth() + "/" + ent.getMaxHealth() + " - §6☀" + count);
					ent.setCustomNameVisible(true);
				}
			}
		}
	}
	
	@EventHandler
	void onBowShoot(EntityShootBowEvent e) {
		var ent = e.getProjectile();
		ent.setVelocity(ent.getVelocity().multiply(2));
		e.setProjectile(ent);
	}
	
	@EventHandler
	void onEntitySpawn(EntitySpawnEvent e) {
		var entity = e.getEntity();
		
		if (!(entity instanceof LivingEntity))
			return;
		
		if (entity instanceof Player)
			return;
		
		var spawnCount = ThreadLocalRandom.current().nextInt(2, 7);
		entity.setMetadata("spawn_count", new FixedMetadataValue(this.plugin, spawnCount));
		entity.setMetadata("spawn_count_max", new FixedMetadataValue(this.plugin, spawnCount));
		entity.setMetadata("spawn_loop_tag", new FixedMetadataValue(this.plugin, true));
	}
	
	@EventHandler
	void onEntityDeath(EntityDeathEvent e) {
		var entity = e.getEntity();
		
		if (!(entity instanceof LivingEntity))
			return;
		
		if (entity instanceof Player)
			return;
		
		if (!entity.hasMetadata("spawn_count"))
			return;
		
		var spawnCount = entity.getMetadata("spawn_count").get(0).asInt();
		var spawnCountMax = entity.getMetadata("spawn_count_max").get(0).asInt();
		
		if (spawnCount > 1) {
			e.setDroppedExp(0);
			
			var pos = entity.getLocation();
			var world = pos.getWorld();
			
			var tempEntity = world.spawnEntity(pos, entity.getType());
			tempEntity.setMetadata("spawn_count", new FixedMetadataValue(this.plugin, spawnCount - 1));
			tempEntity.setMetadata("spawn_count_max", new FixedMetadataValue(this.plugin, spawnCountMax));
			tempEntity.setMetadata("spawn_loop_tag", new FixedMetadataValue(this.plugin, true));
		}
		else {
			e.setDroppedExp(spawnCountMax + ThreadLocalRandom.current().nextInt(2, 5));
		}
	}
}
