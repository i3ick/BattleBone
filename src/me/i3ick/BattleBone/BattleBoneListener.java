package me.i3ick.BattleBone;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Map;

/**
 * Created by Karlo on 4/26/2017.
 */
public class BattleBoneListener implements Listener {


    private BattleBoneMain plugin;
    private BattleBoneGameController gameController;

    public BattleBoneListener(BattleBoneMain PassPlug, BattleBoneGameController PassPlug2){
        this.plugin = PassPlug;
        this.gameController = PassPlug2;
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDismountEvent(EntityDismountEvent e) {

        if(!(e.getEntity() instanceof Player)){
            return;
        }


        //prevent player from standing up while downed
        for (Entity entity : e.getDismounted().getPassengers()) {
            Player p = (Player) entity;
            int mobID = 0;
            for (Map.Entry<String, Integer> downedPlayer : gameController.downedPlayers.entrySet()) {
                if (downedPlayer.getKey() == p.getName()) {
                    mobID = downedPlayer.getValue();
                }
            }

            for (Entity en : p.getWorld().getNearbyEntities(p.getLocation(),5.0, 5.0, 5.0)) {
                if (en.getEntityId() == mobID) {
                    Silverfish sfish = (Silverfish) en;
                    mountPlayerBack(sfish,p);
                }

            }

        }
    }

    /**
     * Player needs to wait a tick untill being mounted again, otherwise he doesn't
     * get mounted
     * @param sfish
     * @param p
     */
    public void mountPlayerBack(Silverfish sfish, Player p) {
        Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
                sfish.getPassengers().add(p);
                sfish.setPassenger(p);
            }
        }, 2L);//run code in run() after 2 ticks
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e){
        if(gameController.downedPlayers.containsValue(e.getEntity().getEntityId())) {
            e.setCancelled(true);
        }

        if(e.getDamager() instanceof Player) {
            if (e.getEntity() instanceof WitherSkeleton || e.getEntity() instanceof Skeleton) {
                //e.getDamager().getWorld().createExplosion(e.getDamager().getLocation().getX(), e.getDamager().getLocation().getY(), e.getDamager().getLocation().getZ(), 10F, false, false);
                gameController.wasRecentlyHit.remove(e.getEntity().getEntityId());
                gameController.wasRecentlyHit.put(e.getEntity().getEntityId(), true);
                plugin.getLogger().info(gameController.wasRecentlyHit.get(e.getEntity().getEntityId())+"");
                plugin.getLogger().info("fart");
            }

        }


        gameController.getMobID(); // Don't remove
        if (gameController.mobIDs.containsKey(e.getEntity().getEntityId())) {
        if(e.getDamager() instanceof Entity && !(e.getDamager() instanceof Player)){
            e.setCancelled(true);
        }
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();

            //prevent the player from ever dying
            if (gameController.playersInGame.contains(p.getName())) {
                if (p.getHealth() - e.getDamage() < 1) {
                    e.setCancelled(true);
                }
            }

            //prevent player from taking explosion damage
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent e) {
        gameController.getMobID(); // Don't remove
        if (gameController.mobIDs.containsKey(e.getEntity().getEntityId())) {
            if (e.getEntity().getKiller() instanceof Player) {
                Player pl = e.getEntity().getKiller();
                if (gameController.playersInGame.contains(pl.getName())) {
                    BattleBoneArena arena = gameController.getArenaThatContains(pl);
                    if (arena.getSkeletonArray().contains(e.getEntity().getEntityId())) {
                        arena.removeFromSkeletonArray(e.getEntity().getEntityId());
                        gameController.getPlayerData(pl.getName()).addKill();
                        arena.setupStatboard(pl); // update player scoreboard
                        gameController.GLogic.addKilledMob(arena, pl.getWorld()); //register the mob as dead
                        e.getDrops().clear(); // remove drops
                    }
                }
            }else {
                //Return all mobs that died without players killing them to respawn queue
                BattleBoneArena arena = gameController.getArena(gameController.mobIDs.get(e.getEntity().getEntityId()));
                arena.removeFromSkeletonArray(e.getEntity().getEntityId());
                gameController.GLogic.addToHeaven(arena, e.getEntity().getWorld()); // Ooh heaven is a place on Earth
            }


        }
    }







}
