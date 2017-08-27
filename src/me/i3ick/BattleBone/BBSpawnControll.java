package me.i3ick.BattleBone;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Random;

/**
 * Created by Karlo on 4/27/2017.
 */
public class BBSpawnControll {

    private BattleBoneMain plugin = BattleBoneMain.MainPlugin;



    private int areaid;
    private String type;
    private int level;

    private int playerCount;


    private Location InsideLocation;
    private Location OutsideLocation;
    private Location spawnplace;


    public void setIn (Location inLocation){
        this.InsideLocation = inLocation;
    }

    public Location getIn(){return this.InsideLocation;}


    public void setOut (Location outLocation){
        this.OutsideLocation = outLocation;
    }
    public Location getOut(){return this.OutsideLocation;}

    public void increasePCount(Player p){
        this.playerCount = playerCount + 1;
        String wrld = p.getWorld().getName();
        if(doatask == true){
            canDoTask(false);
           // spawnpattern(wrld, 3);
            Bukkit.getScheduler().cancelTask(3);
        }
    }


    public void decreasePCount(Player p){
        this.playerCount = playerCount - 1;
        String wrld = p.getWorld().getName();
      /*  if(doatask == true){
            canDoTask(false);
            spawnpattern(wrld);
        } */

    }
    public int getPCount(){return this.playerCount;}


    public void setID (int areaID){
        this.areaid = areaID;
    }


    public void setType (String sareaType){
        this.type = sareaType;
    }

    boolean doatask = true;
    public void canDoTask(boolean doatask){
        this.doatask = doatask;
    }


    public Integer getArea(){
        Random r = new Random();
        int LowX;
        int HighX;
        int LowZ;
        int HighZ;
        int Zdifference;
        int Xdifference;


        if (getIn().getBlockX() > getOut().getBlockX()) {
            HighX = getIn().getBlockX();
            LowX = getOut().getBlockX();
        } else {
            HighX = getOut().getBlockX();
            LowX = getIn().getBlockX();
        }

        if (getIn().getBlockZ() > getOut().getBlockZ()) {
            HighZ = getIn().getBlockZ();
            LowZ = getOut().getBlockZ();
        } else {
            HighZ = getOut().getBlockZ();
            LowZ = getIn().getBlockZ();
        }


        if (HighZ > 0 && LowZ > 0) {
            Zdifference = HighZ - LowZ;
        } else if (HighZ < 0 && LowZ < 0) {
            Zdifference = LowZ * (-1) - HighZ * (-1);
        } else if (HighZ > 0 && LowZ < 0) {
            Zdifference = HighZ + LowZ * (-1);
        } else {
            Zdifference = 0;
        }

        if (HighX > 0 && LowX > 0) {
            Xdifference = HighX - LowX;
        } else if (HighX < 0 && LowX < 0) {
            Xdifference = LowX * (-1) - HighX * (-1);
        } else if (HighX > 0 && LowX < 0) {
            Xdifference = HighX + LowX * (-1);
        } else {
            Xdifference = 0;
        }

        int RandomZ = r.nextInt(Zdifference);
        int RandomX = r.nextInt(Xdifference);

        return Zdifference * Xdifference;
    }


    /**
     * Spawn certain amount of mobs in an area upon player entering
     * @param world
     * @param amount
     */
    public void spawnEntity(BattleBoneGameController gc, String world, BattleBoneArena arena, EntityType mobtype, Player player,  int amount) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                for(int count=0; count<amount; count++) {

                    getSpawnPlace(world);

                    // We don't want to spawn new mobs too close to the running player.
                    while (isTooClose(spawnplace, player.getLocation()) || isAboveBarrier(spawnplace)){
                        getSpawnPlace(world);
                    }



                    BBParticles particles = new BBParticles(plugin);
                    particles.Fall(spawnplace);

                    //Bukkit.getWorld(world).strikeLightning(spawnplace);
                    Skeleton skele = Bukkit.getWorld(world).spawn(spawnplace, Skeleton.class);
                    delayEquipSkele(skele, player);
                    skele.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100.0);
                    arena.setSkeletonArray(skele.getEntityId());
                    plugin.getLogger().info("spawnentit");
                    gc.getMobID();
                    gc.wasRecentlyHit.put(skele.getEntityId(), false);


                    lifeCounter(skele.getEntityId(), world, gc);
                   // hitReset(gc, skele.getEntityId());


                    /**
                    // SKELETON RIDERS
                    Skeleton skeleRider = Bukkit.getWorld(world).spawn(spawnplace, Skeleton.class);
                    SkeletonHorse skeleHorse = Bukkit.getWorld(world).spawn(spawnplace, SkeletonHorse.class);
                    skeleHorse.setPassenger(skeleRider);
                    skeleHorse.setDomestication(3);
                    skeleHorse.setTamed(true);

                     */
                    //spawnMobs

                    /*Zombie zom = (Zombie) Bukkit.getWorld(world).spawnEntity(spawnplace, EntityType.ZOMBIE);
                    zom.setBaby(true);
                    zom.setCustomName("Wookie");
                    c.setPassenger(zom);
                    PotionEffect speed = PotionEffectType.SPEED.createEffect(9999, 7);
                    c.addPotionEffect(speed); */


                    canDoTask(true);
                }
            }
        }, 20);
    }

    public int doubleToInt(Double d) {
        return d.intValue();
    }


    public Block toTen = null;
    public boolean isAboveBarrier(Location spawnplace){
        int LY = spawnplace.getWorld().getHighestBlockYAt(spawnplace);
        Block block =  spawnplace.getWorld().getHighestBlockAt(spawnplace);

        toTen = block;
        for(int i=0; i<30; i++){
            if(toTen.getType().equals(Material.BARRIER)){
                return true;
            }
            toTen = toTen.getRelative(BlockFace.UP);
        }
        toTen = null;

        return false;
    }

    public boolean  isTooClose(Location spawnLocation, Location playerLocation){
        int slX = doubleToInt(spawnLocation.getX());
        int slY = doubleToInt(spawnLocation.getY());
        int slZ = doubleToInt(spawnLocation.getZ());

        int plX = doubleToInt(playerLocation.getX());
        int plY = doubleToInt(playerLocation.getY());
        int plZ = doubleToInt(playerLocation.getZ());

        int LowX;
        int HighX;
        int LowZ;
        int HighZ;
        int Zdifference;
        int Xdifference;

        if (slX > plX) {
            HighX = slX;
            LowX = plX;
        } else {
            HighX = plX;
            LowX = slX;
        }


        if (slZ > plZ) {
            HighZ = slZ;
            LowZ = plZ;
        } else {
            HighZ = plZ;
            LowZ = slZ;
        }


        if (HighZ > 0 && LowZ > 0) {
            Zdifference = HighZ - LowZ;
        } else if (HighZ < 0 && LowZ < 0) {
            Zdifference = LowZ * (-1) - HighZ * (-1);
        } else if (HighZ > 0 && LowZ < 0) {
            Zdifference = HighZ + LowZ * (-1);
        } else {
            Zdifference = 0;

        }



        if (HighX > 0 && LowX > 0) {
            Xdifference = HighX - LowX;
        } else if (HighX < 0 && LowX < 0) {
            Xdifference = LowX * (-1) - HighX * (-1);
        } else if (HighX > 0 && LowX < 0) {
            Xdifference = HighX + LowX * (-1);
        } else {
            Xdifference = 0;
        }


        int sqr = 2;
        int sum = Xdifference*sqr + Zdifference*sqr;
        int Hypotenuse = doubleToInt(Math.sqrt(sum));

        if(Hypotenuse < 4){
            return true;
        }

        return false;
    }

    public Location getSpawnPlace(String world){
        Random r = new Random();
        int LowX;
        int HighX;
        int LowZ;
        int HighZ;
        int Zdifference;
        int Xdifference;
        int finalPosX;
        int finalPosZ;


        if (getIn().getBlockX() > getOut().getBlockX()) {
            HighX = getIn().getBlockX();
            LowX = getOut().getBlockX();
        } else {
            HighX = getOut().getBlockX();
            LowX = getIn().getBlockX();
        }

        if (getIn().getBlockZ() > getOut().getBlockZ()) {
            HighZ = getIn().getBlockZ();
            LowZ = getOut().getBlockZ();
        } else {
            HighZ = getOut().getBlockZ();
            LowZ = getIn().getBlockZ();
        }


        if (HighZ > 0 && LowZ > 0) {
            Zdifference = HighZ - LowZ;
        } else if (HighZ < 0 && LowZ < 0) {
            Zdifference = LowZ * (-1) - HighZ * (-1);
        } else if (HighZ > 0 && LowZ < 0) {
            Zdifference = HighZ + LowZ * (-1);
        } else {
            Zdifference = 0;
        }

        if (HighX > 0 && LowX > 0) {
            Xdifference = HighX - LowX;
        } else if (HighX < 0 && LowX < 0) {
            Xdifference = LowX * (-1) - HighX * (-1);
        } else if (HighX > 0 && LowX < 0) {
            Xdifference = HighX + LowX * (-1);
        } else {
            Xdifference = 0;
        }

        int RandomZ = r.nextInt(Zdifference);
        int RandomX = r.nextInt(Xdifference);

        finalPosX = LowX + RandomX;
        finalPosZ = LowZ + RandomZ;

        int finalPosY = Bukkit.getWorld(world).getHighestBlockYAt(finalPosX, finalPosZ);


        return spawnplace = new Location(Bukkit.getWorld(world), finalPosX, finalPosY, finalPosZ);
    }

    public void delayEquipSkele(Skeleton skele, Player player) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                BattleBoneClasses classes = new BattleBoneClasses();
                classes.skeleSoldier(skele);
                skele.setTarget(player);
            }


        }, 1L);
    }



    public void lifeCounter(int mobId, String World, BattleBoneGameController gc) {
        plugin.getLogger().info("isrun");
        new BukkitRunnable() {
            int t = 0;
            World world = Bukkit.getWorld(World);
            public void run() {

                t = t + 1;

                if(t == 10){
                    for(Entity e : world.getEntities()){
                            if (e.getEntityId() == mobId) {
                                if(gc.wasRecentlyHit.get(mobId)== false) {
                                    plugin.getLogger().info(gc.wasRecentlyHit.get(mobId) + "");
                                    Skeleton skele = (Skeleton) e;
                                    plugin.getLogger().info("WAOFAINMF");
                                    BattleBoneArena arena = gc.getArena(gc.mobIDs.get(mobId));
                                    arena.removeFromSkeletonArray(mobId);
                                    gc.GLogic.addToHeaven(arena, world);
                                    skele.remove();
                                    this.cancel();
                                }else{
                                    plugin.getLogger().info("wimpy");
                                    BattleBoneArena arena = gc.getArena(gc.mobIDs.get(mobId));
                                    arena.removeFromSkeletonArray(mobId);
                                    gc.GLogic.addToHeaven(arena, world);
                                    gc.wasRecentlyHit.remove(mobId);
                                    gc.wasRecentlyHit.put(mobId, false);
                                    lifeCounter2(mobId, World, gc);
                                    this.cancel();
                                }


                            }
                    }
                }

            }
        }.runTaskTimer(plugin, 0, 20);


    }

    public void lifeCounter2(int mobId, String World, BattleBoneGameController gc) {
        plugin.getLogger().info("isrun");
        new BukkitRunnable() {
            int t = 0;
            World world = Bukkit.getWorld(World);
            public void run() {

                t = t + 1;

                if(t == 10){
                    plugin.getLogger().info("1");
                    for(Entity e : world.getEntities()){
                        if (e.getEntityId() == mobId) {
                            if(gc.wasRecentlyHit.get(mobId) == false) {
                                plugin.getLogger().info("2");
                                plugin.getLogger().info("3");
                                Skeleton skele = (Skeleton) e;
                                plugin.getLogger().info("WAOFAINMF");
                                BattleBoneArena arena = gc.getArena(gc.mobIDs.get(mobId));
                                arena.removeFromSkeletonArray(mobId);
                                gc.GLogic.addToHeaven(arena, world);
                                skele.remove();
                                this.cancel();
                            }else{
                                gc.wasRecentlyHit.remove(mobId);
                                gc.wasRecentlyHit.put(mobId, false);
                                lifeCounter(mobId, World, gc);
                                this.cancel();
                            }


                        }
                    }
                }

            }
        }.runTaskTimer(plugin, 0, 20);


    }

    /**

    public void hitReset(BattleBoneGameController gc, int mobId) {
        new BukkitRunnable() {
            public void run() {
                plugin.getLogger().info(gc.wasRecentlyHit.get(mobId)+"");
                gc.wasRecentlyHit.remove(mobId);
                gc.wasRecentlyHit.put(mobId, false);
                plugin.getLogger().info(gc.wasRecentlyHit.get(mobId)+"");
                BattleBoneArena arena = gc.getArena(gc.mobIDs.get(mobId));
                if(!arena.isInGame()){
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 200);


    }
     **/



}
