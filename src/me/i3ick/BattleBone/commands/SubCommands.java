package me.i3ick.BattleBone.commands;

import me.i3ick.BattleBone.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * Created by Karlo on 4/25/2017.
 */
public class SubCommands {

    BattleBoneMain plugin;
    BattleBoneGameController gameController;
    BattleBoneArenaCreator creator;
    BBSpawnAreaCreator areaCreator;

    public SubCommands(BattleBoneMain passPlugin, BattleBoneGameController passPlug2, BattleBoneArenaCreator passplug3,
                       BBSpawnAreaCreator areaCreator) {
        this.plugin = passPlugin;
        this.gameController = passPlug2;
        this.creator = passplug3;
        this.areaCreator = areaCreator;

    }


    public void down(Player p){
        gameController.down(p);
    }


    public void give(Player p){
        gameController.give(p);
    }


    /** SET ARE A CORNERPOINT
     * Set a cornerpoint to define an area (to use with CreateArea())
     * @param player
     */
    public void setAIn(Player player){
        World world = player.getLocation().getWorld();
        int x = (int) player.getLocation().getX();
        int y = (int) player.getLocation().getY();
        int z = (int) player.getLocation().getZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        Location InLocation = new Location(world, x, y, z, yaw, pitch);
        areaCreator.setIn(InLocation);
        areaCreator.InSet(true);
        player.sendMessage(ChatColor.YELLOW + "Area A corner selected.");
    }

    /** SET ARE B CORNERPOINT
     * Set a cornerpoint to define an area (to use with CreateArea())
     * @param player
     */
    public void setAOut(Player player){
        World world = player.getLocation().getWorld();
        int x = (int) player.getLocation().getX();
        int y = (int) player.getLocation().getY();
        int z = (int) player.getLocation().getZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        Location InLocation = new Location(world, x, y, z, yaw, pitch);
        areaCreator.setOut(InLocation);
        areaCreator.OutSet(true);
        player.sendMessage(ChatColor.YELLOW + "Area B corner selected.");
    }



    /**
     * Set spawn location
     * @param player
     */
    public void setSpawn(Player player){
        World world = player.getLocation().getWorld();
        double x = player.getLocation().getBlockX();
        double y = player.getLocation().getBlockY();
        double z = player.getLocation().getBlockZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
        creator.setSpawn(spawnLocation);
        creator.spawnSet(true);
        player.sendMessage(ChatColor.YELLOW + "Main arena spawn selected.");
    }

    /**
     * Set spawn location
     * @param player
     */
    public void setLobby(Player player){
        World world = player.getLocation().getWorld();
        double x = player.getLocation().getBlockX();
        double y = player.getLocation().getBlockY();
        double z = player.getLocation().getBlockZ();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        Location lobbyLocation = new Location(world, x, y, z, yaw, pitch);
        creator.setLobby(lobbyLocation);
        creator.lobbySet(true);
        player.sendMessage(ChatColor.YELLOW + "Lobby area selected.");
    }



    /**
     * Create an arena
     * @param arenaName
     * @param player
     * @param minimumPlayerNumber
     */
    public void create(String arenaName,Player player, int minimumPlayerNumber){


        Location Spawn = creator.getSpawn();
        Location lobbySpawn = creator.getLobbySpawn();

        //spawn area
        Location inLoc = areaCreator.getIn();
        Location outLoc = areaCreator.getOut();

        String world = player.getLocation().getWorld().getName();
        plugin.getArenaData().set("Worlds" + ".World", world);
        plugin.saveArenaData();

        if(world != null)
        {
            if(creator.isSpawnSet() && creator.isLobbySet() && areaCreator.isInSet() && areaCreator.isOutSet()) {
                gameController.createArena(arenaName, lobbySpawn, Spawn, minimumPlayerNumber, inLoc, outLoc);
                player.sendMessage(ChatColor.GREEN + (arenaName + " successfully created!"));
                creator.spawnSet(false);
                creator.lobbySet(false);
                areaCreator.OutSet(false);
                areaCreator.InSet(false);
            }
            else{
                player.sendMessage(ChatColor.RED + "You didn't set all the spawnpoints or both cornerpoints!");
            }
        }
        else
        {
            // Bukkit.getServer().createWorld(new WorldCreator(playerWorld).environment(World.Environment.NORMAL));
            plugin.getLogger().warning("The '" + world + "' world from arenaData.yml does not exist or is not loaded !");
        }


    }



    /**
     * Removes an arena
     * @param Arenaname
     * @param sender
     */

    public void removeArena(String Arenaname, Player sender){
        FileConfiguration arenaData = plugin.getArenaData();
        for(String arenas: arenaData.getConfigurationSection("arenas").getKeys(false)){
            if(arenas.equals(Arenaname)){
                arenaData.getConfigurationSection("arenas").set(Arenaname, null);
                File f = new File(plugin.getDataFolder() + File.separator + "arenaData.yml");
                try {
                    arenaData.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gameController.arenaObjects.remove(Arenaname);
                gameController.arenaNameList.remove(Arenaname);
                sender.sendMessage(ChatColor.YELLOW + "Arena " + Arenaname + " sucessfully deleted!");
                return;
            }
        }
        sender.sendMessage(ChatColor.RED + "No such arena.");

    }

    /**
     * This method returns a list of all arenas available.
     * @param player
     */
    public void list(Player player){

        if(gameController.arenaNameList.isEmpty()){
            player.sendMessage(ChatColor.RED + "There are no arenas.");
            return;
        }else{
            player.sendMessage(ChatColor.GRAY + "The following arenas are available:");
            for(String arena : gameController.arenaNameList){
                player.sendMessage(arena);
            }
        }
    }

    /**
     * This method returns a list of all arenas available.
     * @param player
     */
    public void join(Player player, String arenaName){

        FileConfiguration config = plugin.getConfig();

        BattleBoneArena arena = gameController.getArena(arenaName);
        int maxplayers = config.getInt("arenas." + arenaName + ".maxPlayers");


        if (arena == null) {
            player.sendMessage(ChatColor.RED + "This arena doesn't exist");
            return;
        }

        if (gameController.playersInGame.contains(player.getName())){
            player.sendMessage(ChatColor.YELLOW + "You are already in a game!");
            return;
        }

        if (arena.getPlayers().contains(player.getName())) {
            player.sendMessage(ChatColor.YELLOW + "You are already in this arena!");
            return;
        }
        if (arena.isInGame()) {
            player.sendMessage(ChatColor.YELLOW + "There is a game currently running in this arena!");
            return;
        }

        else {
            player.sendMessage(ChatColor.YELLOW + "You have been put on the games waiting list.");
            gameController.addPlayers(player, arenaName);
        }
    }

    /**
     * Removes the player from the game and teleports him to his initial
     * location.
     * @param player
     */

    public void leave(Player player){
        for (String arenas: gameController.arenaNameList) {
            BattleBoneArena arena = gameController.getArena(arenas);
            if(arena.getPlayers() == null){
                return;
            }
            if(arena.getPlayers().contains(player.getName())){
                gameController.removePlayer(player, arena.getName());
                gameController.stats.remove(player.getName());
                return;
            }
        }
        player.sendMessage(ChatColor.YELLOW + "You are not in an arena!");
    }

    /**
     * Display mod commands.
     * @param player
     */
    public void helpMod(Player player){
        player.sendMessage(ChatColor.GOLD + "WINTERSLASH MODERATOR COMMANDS");
        player.sendMessage("/bb list");
        player.sendMessage("/bb join");
        player.sendMessage("/bb join <arenaname>");
        player.sendMessage("/bb leave");
        player.sendMessage("/bb remove <arenaname>");
        player.sendMessage("/bb setred");
        player.sendMessage("/bb setgreen");
        player.sendMessage("/bb setlobby");
        player.sendMessage("/bb award <amount>");
        player.sendMessage("/bb create <arenaname> <minimim playernumber>");
        player.sendMessage("/bb fs <arenaname>");
        player.sendMessage("/bb end <arenaname>");
        player.sendMessage("/bb joinall <arenaname>" + ChatColor.RED + " --> DEBUG ONLY!!!");

    }

    /**
     * Display user commands
     * @param player
     */
    public void helpPlayer(Player player){
        player.sendMessage(ChatColor.BLUE + "WINTERSLASH USER COMMANDS");
        player.sendMessage("/bb list");
        player.sendMessage("/bb join");
        player.sendMessage("/bb join <arenaname>");
        player.sendMessage("/bb leave");
    }


}
