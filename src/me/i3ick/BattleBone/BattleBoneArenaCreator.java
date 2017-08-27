package me.i3ick.BattleBone;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Karlo on 4/25/2017.
 */
public class BattleBoneArenaCreator {



    public static ArrayList<BattleBoneArenaCreator> arenaObjects = new ArrayList<BattleBoneArenaCreator>();

    private  String name;
    private  Location spawn;
    private  Location joinLocation;
    private int minPlayers;

    // Location Constructor

    /**
     * Constructor for gathering information necessary to create a new
     * arena.
     */


    public BattleBoneArenaCreator(String arenaName, Location joinLocation,
                                   Location spawnLocation,
                                   int minPlayers) {

        this.name = arenaName;
        this.joinLocation = joinLocation;
        this.spawn = spawnLocation;
        this.minPlayers = minPlayers;
        arenaObjects.add(this);

    }


    public BattleBoneArenaCreator(BattleBoneMain battleBoneMain) {
    }


    public void name(String arenaName) {
        this.name = arenaName;
    }

    public void minPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setSpawn(Location spawnLocation) {
        this.spawn = spawnLocation;
    }


    public void setLobby(Location lobbyLocation) {
        this.joinLocation = lobbyLocation;
    }

    public Location getSpawn(){
        return spawn;

    }


    public Location getLobbySpawn(){
        return joinLocation;

    }

    boolean lobby;
    boolean spawnset;
    boolean green;

    public void lobbySet(boolean lobby){
        this.lobby = lobby;
    }

    public boolean isLobbySet(){
        return lobby;
    }

    public void spawnSet(boolean spawnset){
        this.spawnset = spawnset;
    }

    public boolean isSpawnSet(){
        return spawnset;
    }



    public String getName() {
        return this.name;
    }


}
