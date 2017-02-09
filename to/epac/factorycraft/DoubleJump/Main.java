package to.epac.factorycraft.DoubleJump;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener {
	ArrayList<Player> isWaiting = new ArrayList<Player>();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onFall(EntityDamageEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			Player player = (Player) e;
			if (isWaiting.contains(player))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));
		player.setExp(0.00F);
		isWaiting.add(player);
		player.playSound(player.getLocation(), Sound.ZOMBIE_INFECT, 1, 2);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		BukkitScheduler scheduler = getServer().getScheduler();
		Player player = event.getPlayer();
		
		if ((player.getGameMode() == GameMode.CREATIVE))
			return;
		if (player.isFlying())
			return;
		if (!(player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()))
			return;
		if (player.getExp() > 0.00F)
			return;

		if (isWaiting.contains(player)) {

			scheduler.scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					if (player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()){
						player.setExp(0.99F);
						player.setAllowFlight(true);
						isWaiting.remove(player);
					}
				}
			}, 10L);
		} else {
			player.setExp(0.99F);
			player.setAllowFlight(true);
		}
	}
}
