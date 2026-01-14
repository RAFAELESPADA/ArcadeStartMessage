package me.rafaelauler.arcadeaddon;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import ro.fr33styler.arcade.api.engine.addon.Addon;
import ro.fr33styler.arcade.api.engine.addon.annotation.Description;
import ro.fr33styler.arcade.api.engine.config.annotation.Setting;
import ro.fr33styler.arcade.api.engine.config.serialize.Configuration;
import ro.fr33styler.arcade.api.engine.event.game.GameStartEvent;
import ro.fr33styler.arcade.api.engine.event.game.player.GameJoinEvent;
import ro.fr33styler.arcade.api.engine.game.Game;

@Description(id = "StartMessage", author = "Rafael Auler", version = "1.01")
public class BroadCastEvent extends Addon implements Listener, Configuration {

private int countdown = 60;
private BiFunction<Player, String, String> placeholderAPI = (player, string) -> string;
private BukkitTask task;
@Setting("startingmessage")	
private String startingmessage = "&eA game room of %1 with id %2 is about to start with %3 players. Click here to join it!";
@Setting("startmessage")	
private String startmessage = "&eA game room of %1 with id %2 started with %3 players.";
@Setting("Sound")	
private String sound = "ENTITY_ARROW_HIT_PLAYER";
@Setting("worlds")
private Set<String> lobbyworlds = new HashSet<>(List.of("enabled-worlds"));

private final Map<Game, String> gamesave = new HashMap<>();

@EventHandler
public void onDeatht(GameJoinEvent event)
{
	  if (event.getGame().getGamers().size() >= event.getGame().getMap().getMinimumPlayers()) {
    {
    	  for (World lobbyWorld : Bukkit.getWorlds()) {
          if (lobbyworlds.contains(lobbyWorld.getName())) {
        	  if (countdown % 5 == 0) {
        		  gamesave.put(event.getGame(), event.getGame().getMap().getId());
        		  task = Bukkit.getScheduler().runTaskTimer(
        		            this.getEngine().getPlugin(),
        		            () -> {
        		            	   String msg = startingmessage.replace("%1", event.getGame().getManager().getName()).replace("%2", event.getGame().getManager().getId()).replace("%3", String.valueOf(event.getGame().getGamers().size()));
        		               	TextComponent txt = new TextComponent(msg);
        		               	txt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + event.getGame().getManager().getName() + " join " + event.getGame().getManager().getId()));
        		               	    for (Player p1 : lobbyWorld.getPlayers()) {
        		               	        p1.spigot().sendMessage(txt);
        		               	        HelixActionBar.send(p1, msg);
        		               	        p1.playSound(p1.getLocation(), Sound.valueOf(sound), 10f, 1f);
        		               	    }    
        		               	 countdown--;
        		            },
        		            0L, 20L
        		        );
        		    }
    
          }}}
    	 
    	  }
} 
@EventHandler
public void onDeathytt(GameStartEvent event) {
	if (gamesave.get(event.getGame()) == event.getGame().getMap().getId()) {
		countdown = 0;
		 for (World lobbyWorld : Bukkit.getWorlds()) {
	          if (lobbyworlds.contains(lobbyWorld.getName())) {
	        	  String msg = startmessage.replace("%1", event.getGame().getManager().getName()).replace("%2", event.getGame().getManager().getId()).replace("%3", String.valueOf(event.getGame().getGamers().size()));
	               	TextComponent txt = new TextComponent(msg);
	               	    for (Player p1 : lobbyWorld.getPlayers()) {
	               	        p1.spigot().sendMessage(txt);
	               	        HelixActionBar.send(p1, msg);
	               	        p1.playSound(p1.getLocation(), Sound.valueOf(sound), 10f, 1f);
	               	    }     
	          }
	}
	}
}



@Override
public void onLoad() {
    getEngine().getConfigManager().tryLoad(this, new File(getDataFolder(), "config.yml"));
    getEngine().getAddonManager().registerListener(this);
    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
        placeholderAPI = PlaceholderAPI::setPlaceholders;
    }
}
@Override
public void onUnload() {
task.cancel();
}
}
