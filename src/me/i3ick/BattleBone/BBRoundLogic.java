package me.i3ick.BattleBone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.material.Wood;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

/**
 * Created by Karlo on 4/26/2017.
 */
public class BBRoundLogic {

    private BattleBoneMain plugin;
    private BattleBoneGameController gameController;

    public BBRoundLogic( BattleBoneGameController gameController){
        this.plugin = BattleBoneMain.MainPlugin;
        this.gameController = gameController;
    }


    private int divisionInt;
    private int maxDisplayMobNumber;
    private int totalRoundMonsterNumber;
    private int killedMobsNumber;
    private int aliveMobs;
    private int countdown;

    public void setMaxDisplayMobNumber(BattleBoneArena arena){
        Integer area = gameController.getArenaSpawnArea(arena.getName()).getArea();
        if(area < 500){
            maxDisplayMobNumber = 15;
            return;
        }
        if(area < 2000){
            maxDisplayMobNumber = 25;
            return;
        }
        if(area < 2000){
            maxDisplayMobNumber = 37;
            return;
        }
        if(area < 8000){
            maxDisplayMobNumber = 50;
            return;
        }
    }



    public void beginLogic(BattleBoneArena arena) {
        if(maxDisplayMobNumber == 0){
            setMaxDisplayMobNumber(arena);
        }


        countdown = 2;
        killedMobsNumber =0;
        aliveMobs =0;
        arena.setRoundNumber(arena.getRoundNumber() + 1);
        arena.setStartMobNumber(arena.getStartMobNumber() + 2);
        decideEnemyNo(arena.getRoundNumber(), arena);


        plugin.getLogger().info(arena.getPlayers() + "");
        Player ply = (Player) Bukkit.getPlayer(arena.getRandomPlayer());

        addMobs(arena, ply.getWorld());
        plugin.getLogger().info("NEW ROUND");
        arena.sendBroadcastMessage(arena.getRoundNumber());

    }

    public int doubleToInt(Double d) {
        return d.intValue();
    }

    public void decideEnemyNo(int rn, BattleBoneArena arena){
        int PNumber = arena.getPlayers().size();
        int startMnumber = arena.getStartMobNumber();
        double multiplier = 1.2;
        double monsterNumber = startMnumber * multiplier * PNumber;
        //monsterNumber = 16;

        int monsterNumberInt = doubleToInt(monsterNumber);
        totalRoundMonsterNumber = monsterNumberInt;
        arena.thisRoundMaxMonsters(monsterNumberInt);

        //every 3 rounds introduce a new mob, starting from round 2
        if(arena.getRoundNumber() > 2 || arena.getRoundNumber() % 3 ==0){
            arena.introduceNewMob();
            plugin.getLogger().info("Introducing new mob");
        }



       // double division = monsterNumberInt / arena.mobTypes().size();
      //  divisionInt = gameController.doubleToInt(division);

    }

    public void addToHeaven(BattleBoneArena arena, World world){//this.totalRoundMonsterNumber++;
        addKilledMob(arena, world);}


    //after every kill increase number of killed, check if all mobs are killed and if not add more
    public void addKilledMob(BattleBoneArena arena, World world){killedMobsNumber++; aliveMobs--; checkForRoundEnd(arena, world);}
    public void checkForRoundEnd(BattleBoneArena arena, World world){
        if(aliveMobs == 0)
            CooldownTime(arena);
        addMobs(arena, world);
    }


    public void addMobs(BattleBoneArena arena, World world){
        if(aliveMobs < maxDisplayMobNumber){
            int tillMax = maxDisplayMobNumber - aliveMobs;

            if(totalRoundMonsterNumber-killedMobsNumber <= maxDisplayMobNumber){
                return;
            }

            for (int fill=1; fill < tillMax; fill++){
                aliveMobs++;
                //plugin.getLogger().info(aliveMobs + " " + maxDisplayMobNumber + " " + killedMobsNumber + " " + totalRoundMonsterNumber);

                if(tillMax - fill == 1) {
                    Player randPlayer = Bukkit.getPlayer(arena.getRandomPlayer());
                    gameController.getArenaSpawnArea(arena.getName()).spawnEntity(gameController, world.getName(), arena, EntityType.SKELETON, randPlayer, fill);
                }

            }
            return;
        }

    }


    public void TargetSeeker(BattleBoneArena arena) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private int id=8;
            public void run() {

                if(!arena.isInGame()){
                    cancel();
                }

                for (Integer id : arena.getSkeletonArray()) {

                    for (Entity entity : arena.world.getLivingEntities()) {
                        if (entity.getEntityId() == id) {
                            Skeleton skele = (Skeleton) entity;
                            for(Entity ent : skele.getNearbyEntities(10,10,10)){
                                if(ent instanceof Player){
                                    Player pl = (Player)ent;
                                    skele.setTarget(pl);
                                }
                            }
                        }
                    }
                }
            }

            private void cancel(){
                Bukkit.getScheduler().cancelTask(id);
            }

            public void setTaskID(int id){
                this.id = id;
            }
        }, 20, 80);
    }



    public void CooldownTime(BattleBoneArena arena) {
        new BukkitRunnable() {

            public void run() {

                if(!arena.isInGame()){
                    cancel();
                }

                if(countdown <=0){
                    beginLogic(arena);
                    this.cancel();
                    return;
                }
                countdown--;
                arena.sendBroadcastNumber(countdown);
            }
        }.runTaskTimer(plugin, 0, 20);
    }




}
