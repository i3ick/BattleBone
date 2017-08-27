package me.i3ick.BattleBone;



import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.generator.InternalChunkGenerator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Karlo on 4/25/2017.
 */
public class BattleBoneGameController {

    static BattleBoneMain plugin;
    public static BattleBoneGameController gController;

    public BattleBoneGameController(BattleBoneMain passPlugin){
        BattleBoneGameController.plugin = passPlugin;
    }



    public HashMap<String, Integer> downedPlayers = new HashMap<String, Integer>();
    public HashMap<String, BossBarCreator> bossbarObjects = new HashMap<String, BossBarCreator>();
    public HashMap<String, BattleBoneArena> arenaObjects = new HashMap<String, BattleBoneArena>();
    public HashMap<Integer, BBSpawnControll> areaObjects = new HashMap<Integer, BBSpawnControll>();
    public HashMap<String, String> ArenaArea = new HashMap<String, String>();
    public ArrayList<Integer> areaIDList = new ArrayList<Integer>();
    public ArrayList<String> arenaNameList = new ArrayList<String>();
    public HashMap<String, Double> awardAmount = new HashMap<String, Double>();
    public ArrayList<String> playersInGame = new ArrayList<String>();
    Map<Player, Location> PlayerInitData = new HashMap<Player, Location>();
    Map<Integer, String> mobIDs = new HashMap<Integer, String>();
    Map<Integer, Boolean> wasRecentlyHit = new HashMap<Integer, Boolean>();
    public HashMap<String, ItemStack[]> PlayerArmor = new HashMap<String, ItemStack[]>();
    public HashMap<String, BBPlayerInfo> stats = new HashMap<String, BBPlayerInfo>();
    public ArrayList<String> Medic = new ArrayList<String>();
    public ArrayList<String> Knight = new ArrayList<String>();
    public ArrayList<String> Heavy = new ArrayList<String>();

    BBRoundLogic GLogic;


    /**GET ID OF MOB TO CHECK IF IT BELONGS TO ANY GAME
     *
     */
    public void getMobID(){
        for (String arenas : arenaNameList) {
            BattleBoneArena arena = this.getArena(arenas);
            for(Integer mobid: arena.getSkeletonArray()){
                mobIDs.put(mobid, arena.getName());
            }
            //TODO add other mobs
        }
    }

    public void removeMobID(Integer id){
        mobIDs.remove(id);
    }

    public void setInitData(Player player, Location loc){
        PlayerInitData.put(player, loc);
    }

    public Location getInitData(Player player){
        return PlayerInitData.get(player);
    }

    /** Get arena by name
     * @param name
     * @return
     */
    public BattleBoneArena getArena(String name){
        BattleBoneArena obj = null;
        if(this.arenaObjects.containsKey(name)){
            obj = this.arenaObjects.get(name);
        }
        return obj;
    }

    /** Get arena spawn area by arena name
     * @param name
     * @return
     */
    public BBSpawnControll getArenaSpawnArea(String name){
        BattleBoneArena obj = null;
        BBSpawnControll sobj = null;
        if(this.arenaObjects.containsKey(name)){
            obj = this.arenaObjects.get(name);
            if(this.areaObjects.containsKey(obj.getSpawnAreaId())){
                sobj = this.areaObjects.get(obj.getSpawnAreaId());

            }
        }
        return sobj;
    }


    /** Get player stats
     * @param name
     * @return
     */
    public BBPlayerInfo getPlayerData(String name){
        BBPlayerInfo obj = null;
        if(this.stats.containsKey(name)){
            obj = this.stats.get(name);
        }
        return obj;
    }

    /** Get arena with player p
     * @param p
     * @return
     */
    public BattleBoneArena getArenaThatContains(Player p) {
        BattleBoneArena obj = null;
        for (String arenas : arenaNameList) {
            BattleBoneArena arena = this.getArena(arenas);
            if(arena.getPlayers().contains(p.getName())){
                obj = arena;
            }
        }
        return obj;
    }

    public void makeBossBar(){
        BossBarCreator bossbarobject = new BossBarCreator();
        bossbarobject.setName("death");
        bossbarobject.barstyle(BarStyle.SOLID);
        bossbarobject.color(BarColor.PINK);
        bossbarobject.setMSG("help");
        addToBossHash("death", bossbarobject);
    }

    public BossBarCreator getBossBar(String name){
        BossBarCreator obj = null;
        if(this.bossbarObjects.containsKey(name)){
            obj = this.bossbarObjects.get(name);
        }
        return obj;
    }


    public void addToBossHash(String name, BossBarCreator bossbar){
        bossbarObjects.put(name, bossbar);
    }

    public void addToHash(String name, BattleBoneArena arena){
        arenaObjects.put(name, arena);
    }

    public void addToAreaHash(Integer id, BBSpawnControll arena){
        areaObjects.put(id, arena);
    }

    public void addToPlayerHash(String name, BBPlayerInfo data){stats.put(name, data);}

    public void addName(String arenaName){
        this.arenaNameList.add(arenaName);
    }

    public void addAreaID(int areaID){
        this.areaIDList.add(areaID);
    }



    /** ADD PLAYERS TO AN ARENA AND TELEPORT THEM TO THE LOBBY
     *
     * @param player
     * @param arenaName
     */
    public void addPlayers(Player player, String arenaName) {

        if (getArena(arenaName) != null) {
            BattleBoneArena arena = getArena(arenaName);
            if (!arena.isInGame()) {


                arena.setSign(player.getName()); //dunno enable sign interaction?
                arena.setPlayers(player);
                Location initLoc = player.getLocation(); //get loc before teleporting
                setInitData(player, initLoc); //store loc in hashmap
                playersInGame.add(player.getName());
                arena.disableFire.add(player.getName()); //disable friendly fire
                Bukkit.getPlayer(player.getUniqueId()).teleport(arena.getLobbyLocation()); //teleport to lobby
                plugin.saveInventoryToFile(player.getInventory(), player.getName(), player.getGameMode()); //save inv
                player.getInventory().clear(); //clear inv
                player.setGameMode(GameMode.SURVIVAL); //set gamemode
                PlayerArmor.put(player.getName(), player.getInventory().getArmorContents()); //save armor

                //remove armor inventory
                ItemStack nothing = new ItemStack(Material.AIR, 1);
                player.getInventory().setHelmet(nothing);
                player.getInventory().setChestplate(nothing);
                player.getInventory().setLeggings(nothing);
                player.getInventory().setBoots(nothing);

                //feed and heal
                player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(60.0);
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(60.0);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60.0);
                player.setHealth(60.0);


                int playersLeft = arena.getMinPl() - arena.getPlayers().size();
                if (!(arena.getMinPl() <= arena.getPlayers().size())) {

                    player.sendMessage("Connected to " + arena.getName() + " arena.");
                    arena.sendHotMessage(ChatColor.BLUE + player.getName() + " joined the arena!");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " times 5 18 5");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " title [{\"text\":\"Winter\",\"color\":\"aqua\",\"bold\":\"true\"},{\"text\":\"Slash\",\"color\":\"white\",\"bold\":\"false\"}] ");
                    if(playersLeft > 0){
                        arena.sendHotMessage(ChatColor.BLUE + "waiting for " + playersLeft + " more players!");
                        BBActionBar.sendHotBarMessage(player, ChatColor.BLUE + "waiting for " + playersLeft + " more players!");
                    }
                    return;
                }


                if (playersLeft == 0) {
                    arena.sendMessage("Game starting in 15 seconds!");
                    runDelayArena(arenaName);
                }
            }else{
                player.sendMessage(ChatColor.YELLOW + "Match in progress!");
            }
        }else{
            player.sendMessage(ChatColor.RED + "The arena you are looking for could not be found!");
        }
    }


    public void runDelayArena(final String arenaName) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                BattleBoneArena arena = getArena(arenaName);
                if (!(arena.isInGame())) {
                    startArena(arenaName);

                }
            }
        }, 100L);
    }


    /**
     * ACTUALLY START THE GAME
     *
     * @param arenaName
     */
    public void startArena(String arenaName){
        if(getArena(arenaName) != null){
            BattleBoneArena arena = getArena(arenaName);
            arena.setInGame(true);
            GLogic.TargetSeeker(arena);
            Bukkit.getWorld(arena.world.getName()).setTime(16000);

            BattleBoneClasses classes = new BattleBoneClasses();

            for (String p: arena.getPlayers()) {
                Player pl = Bukkit.getPlayer(p);
                BBPlayerInfo stats = new BBPlayerInfo(this);
                stats.setName(pl.getName());
                addToPlayerHash(pl.getName(), stats);
                arena.setupStatboard(pl);


                if(!Medic.contains(pl.getName()) && !Heavy.contains(pl.getName()) && !Knight.contains(pl.getName())){
                    classes.setDefault(pl);
                    }

                pl.teleport(arena.getSpawn());
                arena.BBTitle();
               // arena.sendBroadcastMessage(1); TODO add wait delay

            }
            //START GAME LOGIC
            this.GLogic.beginLogic(arena);

        }else{
            plugin.getLogger().info("Can't start arena which doesn't exist!");
        }
    }

    /**
     * Removes the player from the arena, game, all arrays.
     * The player is returned his inventory and teleported to
     * his initial position.
     * @param player
     * @param arenaname
     */

    public void removePlayer(Player player, String arenaname) {
        if (getArena(arenaname) != null) {
            BattleBoneArena arena = this.getArena(arenaname);
            if (arena.getPlayers().contains(player.getName())) {

                //remove from game arrays
                arena.removePlayerFromArrays(player.getName());
                playersInGame.remove(player.getName());

                //clear inventory slots and reset health
                player.getInventory().clear();
                player.setHealth(20.0);
                player.setFoodLevel(20);

                if(Medic.contains(player.getName())){
                    Medic.remove(player.getName());
                }

                if(Heavy.contains(player.getName())){
                    Heavy.remove(player.getName());
                }

                if(Knight.contains(player.getName())){
                    Knight.remove(player.getName());
                }

                ItemStack nothing = new ItemStack(Material.AIR, 1);
                player.getInventory().setHelmet(nothing);
                player.getInventory().setChestplate(nothing);
                player.getInventory().setLeggings(nothing);
                player.getInventory().setBoots(nothing);
                player.getInventory().setArmorContents(PlayerArmor.get(player));

                for (PotionEffect effect : player.getActivePotionEffects())
                    player.removePotionEffect(effect.getType());

                Bukkit.getPlayer(player.getUniqueId()).teleport(getInitData(player)); //teleport to initial location
                arena.sendHotMessage(ChatColor.BLUE + "Player " + player.getName() + " disconnected!");
                plugin.getInventoryFromFile(new File(plugin.getDataFolder(), player.getName() + ".invsave"), player);
                player.sendMessage(ChatColor.BLUE + "Disconnected.");

                if (arena.isInGame()){
                    if(arena.getPlayers().isEmpty()){

                        endArena(arenaname);
                    } }
                return;
            }

        }
        else{
            player.sendMessage(ChatColor.RED + "The arena you are looking for could not be found!");
        }
    }

    /** DOWN A PLAYER
     * Down a specific player. Call again to un-down.
     * This puts the player in a sitting position on a slab.
     * He can still cause/take damage.
     * @param p
     */
    public void down(Player p){


        if(!downedPlayers.containsKey(p.getName())) {

            //reset food and health
            p.setFoodLevel(Integer.MAX_VALUE);
            p.setHealth(20);

            //set up block to sit on
            int plX = doubleToInt(p.getLocation().getX());
            int plZ = doubleToInt(p.getLocation().getZ());
            Block block = p.getWorld().getHighestBlockAt(plX, plZ);
            Location loc = new Location(block.getWorld(), block.getX() + 0.5, block.getY()-0.5, block.getZ()+ 0.5);
           // p.getWorld().getBlockAt(block.getLocation()).setType(Material.PURPUR_SLAB);

            //set up sitting
            Silverfish sfish = p.getWorld().spawn(loc, Silverfish.class);
            sfish.setMaximumAir(Integer.MAX_VALUE);
            sfish.getPassengers().add(p);
            sfish.setPassenger(p); //even tho deprecated seems to work better than above. TODO Stop using deprecated methods
            sfish.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            sfish.setInvulnerable(true);
            sfish.setGravity(false);
            sfish.setSilent(true);
            sfish.setAI(false);

            //Glow the player
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));

            downedPlayers.put(p.getName(), sfish.getEntityId());
            plugin.getLogger().info("das" + sfish.getEntityId());



        }else{
            int mobID = 0;
            for(Map.Entry<String, Integer> downedPlayer: downedPlayers.entrySet()) {
                if(downedPlayer.getKey() == p.getName()) {
                    mobID = downedPlayer.getValue();
                }
            }

             for(Entity entity:  p.getWorld().getLivingEntities()){
                 if(entity.getEntityId() == mobID){

                     //remove sitting
                     Silverfish sfish = (Silverfish) entity;
                     sfish.remove();

                     //remove block sitting on
                     double plX = p.getEyeLocation().getX();
                     double plZ = p.getEyeLocation().getZ();
                     double plY = p.getLocation().getY() + 1;
                     Location loc = new Location(p.getWorld(),plX, plY, plZ);
                     p.getWorld().getBlockAt(loc).setType(Material.AIR);

                     //Glow the player
                     p.removePotionEffect(PotionEffectType.GLOWING);

                 }
             }
            downedPlayers.remove(p.getName(), mobID);

        }
    }


    /**MIGHTY GAME ENDER
     * Ends the arena match
     * @param arenaName
     */
    public void endArena(String arenaName) {
        if (getArena(arenaName) != null) {
            BattleBoneArena arena = getArena(arenaName);
            arena.reset();
            arena.setInGame(false);
        } else {
            return;
        }
    }

    /**GIVE WEAPONS
     * Give weapons - testing
     * @param p
     */
    public void give(Player p){

        p.damage(0.1);

        int newItemSlot = p.getInventory().firstEmpty();
        ItemStack knife = new ItemStack(Material.DIAMOND_SWORD, 1);
        knife.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
        knife.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 100);
        knife.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 100);
        p.getInventory().setItem(newItemSlot, knife);
    }




    /**AWARD MONEY TO WINNERS
     * This method awards the winners with money
     * @param player
     */
    public void awardMoney(Player player){

        if(plugin.econ == null){
            plugin.getLogger().info("can't award money, no Vault or Economy plugin detected");
            return;
        }
        EconomyResponse r = plugin.econ.depositPlayer(player, this.awardAmount.get("amount"));
        if(r.transactionSuccess()) {
            player.sendMessage(String.format(ChatColor.GREEN + "You were awarded %s for winning the round and now you have a total of %s", plugin.econ.format(r.amount), plugin.econ.format(r.balance)));
        } else {
            player.sendMessage(String.format("An error occured: %s", r.errorMessage));
        }
    }




    public int doubleToInt(Double d) {
        return d.intValue();
    }



    /** LOAD ARENA DATA FROM FILE TO OBJECTS
     * Get all the spawn data from arenaData config file and puts them into an
     * object called "arenaobject"
     */
    public void loadArenas() {

        FileConfiguration arenaData = plugin.getArenaData();


        // LOAD ARENAS
        if (arenaData.getConfigurationSection("arenas") == null) {
            plugin.getLogger().info("There are no arenas.");
            return;
        }
        for (String arenaName : arenaData.getConfigurationSection("arenas").getKeys(
                false)) {

            String name = arenaData.getString("Worlds." + "World");
            World world = Bukkit.getServer().getWorld(name);

            //Arena names are keys
            double joinX = arenaData.getDouble("arenas." + arenaName + "." + "joinX");
            double joinY = arenaData.getDouble("arenas." + arenaName + "." + "joinY");
            double joinZ = arenaData.getDouble("arenas." + arenaName + "." + "joinZ");
            float jYaw = (float) arenaData.getDouble("arenas." + arenaName + "." + "jYaw");
            float jP = (float) arenaData.getDouble("arenas." + arenaName + "." + "jP");
            Location joinLocation = new Location(world, joinX, joinY, joinZ, jYaw, jP);


            double spawnX = arenaData.getDouble("arenas." + arenaName + "." + "spawnX");
            double spawnY = arenaData.getDouble("arenas." + arenaName + "." + "spawnY");
            double spawnZ = arenaData.getDouble("arenas." + arenaName + "." + "spawnZ");
            float sYaw = (float) arenaData.getDouble("arenas." + arenaName + "." + "sYaw");
            float sP = (float) arenaData.getDouble("arenas." + arenaName + "." + "sP");
            Location spawnLocation = new Location(world, spawnX, spawnY, spawnZ, sYaw, sP);

            BattleBoneArena arenaobject = new BattleBoneArena(this);
            int minPlayers = arenaData.getInt("arenas." + arenaName + ".minPlayers");
            arenaobject.setSpawn(spawnLocation);
            arenaobject.setLobby(joinLocation);
            arenaobject.setName(arenaName);
            arenaobject.setWorld(world);
            arenaobject.setMinPl(minPlayers);

            GLogic = new BBRoundLogic(this);

            this.addName(arenaName);
            Double number = (Double) plugin.getConfig().get("Settings." + "Award");
            this.awardAmount.put("amount", number);


            // LOAD SPAWN AREAS
            if(arenaData.getConfigurationSection("arenas." + arenaName + "."+"Areas") == null){
                plugin.getLogger().info("There are no Areas!");
                return;
            }
            for(String areaID: arenaData.getConfigurationSection("arenas." + arenaName + "."+"Areas").getKeys(false)){
                String worldName = arenaData.getString("Worlds." + "World");


                String path = "arenas." + arenaName + "." + "Areas." +areaID + ".";
                int inX = arenaData.getInt(path + "insideX");
                int inY = arenaData.getInt(path + "insideY");
                int inZ = arenaData.getInt(path + "insideZ");
                float inYaw = (float)arenaData.getDouble(path + "insideYaw");
                float inP = (float) arenaData.getDouble(path + "insideP");
                Location startLocation = new Location(world, inX, inY, inZ, inYaw, inP);


                int outX = arenaData.getInt(path + "outsideX");
                int outY = arenaData.getInt(path + "outsideY");
                int outZ = arenaData.getInt(path + "outsideZ");
                float outYaw = (float) arenaData.getDouble(path + "outsideYaw");
                float outP = (float) arenaData.getDouble(path + "outsideP");
                Location endLocation = new Location(world, outX, outY, outZ, outYaw, outP);


                int id = Integer.parseInt(areaID);

                // add id to arena
                arenaobject.setSpawnAreaId(id);
                addToHash(arenaName, arenaobject);

                BBSpawnControll spawnAreasObject = new BBSpawnControll();
                spawnAreasObject.setIn(startLocation);
                spawnAreasObject.setOut(endLocation);
                spawnAreasObject.setID(id);
                addToAreaHash(id, spawnAreasObject);
                addAreaID(id);
                plugin.getLogger().info( "" + startLocation + " dataaa.");

                plugin.getLogger().info(this.getArenaSpawnArea(arenaName) + "AREAFFfAGWG");


                plugin.getLogger().info("Area " + id + " loaded.");

            }
        }
        plugin.getLogger().info("WinterSlash: Arenas are now loaded!");

    }




    /** CREATE AN ARENA, WRITE IT TO FILE AND TO OBJECT
     * This class creates the arena
     * @param arenaName
     * @param joinLocation
     * @param spawnLocation
     * @param minPlayers
     */
    public void createArena(String arenaName, Location joinLocation,
                            Location spawnLocation, int minPlayers, Location startLocation,
                            Location endLocation) {


        //player spawn points

        FileConfiguration arenaData = plugin.getArenaData();

        arenaData.set("arenas." + arenaName, null);

        String path = "arenas." + arenaName + ".";
        arenaData.set(path + "joinX", joinLocation.getX());
        arenaData.set(path + "joinY", joinLocation.getY());
        arenaData.set(path + "joinZ", joinLocation.getZ());
        arenaData.set(path + "jYaw", joinLocation.getYaw());
        arenaData.set(path + "jP", joinLocation.getPitch());

        arenaData.set(path + "spawnX", spawnLocation.getX());
        arenaData.set(path + "spawnY", spawnLocation.getY());
        arenaData.set(path + "spawnZ", spawnLocation.getZ());
        arenaData.set(path + "sYaw", spawnLocation.getYaw());
        arenaData.set(path + "sP", spawnLocation.getPitch());


        arenaData.set("Worlds." + "World", spawnLocation.getWorld().getName());
        arenaData.set(path + "minPlayers", minPlayers);

        BattleBoneArena arenaobject = new BattleBoneArena(this);
        arenaobject.setSpawn(spawnLocation);
        arenaobject.setLobby(joinLocation);
        arenaobject.setName(arenaName);
        arenaobject.setMinPl(minPlayers);

        this.addName(arenaName);

        GLogic = new BBRoundLogic(this);





        //spawn area

        int first = new Random().nextInt(10) + 1;
        int second = new Random().nextInt(10) + 1;
        int third = new Random().nextInt(10) + 1;
        int forth = new Random().nextInt(10) + 1;
        int fifth = new Random().nextInt(10) + 1;

        int areaID = (first * 10000) + (second * 1000) + (third * 100) + (forth * 10) + fifth;
        plugin.getLogger().info(areaID + "hey");

        arenaData.set("Areas." + areaID, null);


        String path2 = "arenas." + arenaName + "." + "Areas." + areaID + ".";

        arenaData.set(path2 + "insideX", (int) startLocation.getX());
        arenaData.set(path2 + "insideY", (int)  startLocation.getY());
        arenaData.set(path2 + "insideZ", (int) startLocation.getZ());
        arenaData.set(path2 + "insideYaw", startLocation.getYaw());
        arenaData.set(path2 + "insideP", startLocation.getPitch());

        arenaData.set(path2 + "outsideX", endLocation.getX());
        arenaData.set(path2 + "outsideY", endLocation.getY());
        arenaData.set(path2 + "outsideZ", endLocation.getZ());
        arenaData.set(path2 + "outsideYaw", endLocation.getYaw());
        arenaData.set(path2 + "outsideP", endLocation.getPitch());

        arenaData.set("Worlds." + "World", endLocation.getWorld().getName());


        arenaobject.setSpawnAreaId(areaID);
        addToHash(arenaName, arenaobject);

        BBSpawnControll spawnAreasObject = new BBSpawnControll();
        spawnAreasObject.setIn(startLocation);
        spawnAreasObject.setOut(endLocation);
        spawnAreasObject.setID(areaID);
        addToAreaHash(areaID, spawnAreasObject);
        addAreaID(areaID);




        File f = new File(plugin.getDataFolder() + File.separator + "arenaData.yml");
        try {
            arenaData.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
