package me.i3ick.BattleBone;

import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Karlo on 5/1/2017.
 */
public class BBPlayerInfo {
    private BattleBoneGameController gameController;

    public BBPlayerInfo(BattleBoneGameController PassPlug){
        this.gameController = PassPlug;
    }


    private String name;
    private String classtype;
    private int killcount;
    private int points;



    /**
     * Hashmap that keeps all temporary
     * player info.
     */
    public ArrayList<BBPlayerInfo> stats = new ArrayList<BBPlayerInfo>();

    private boolean isAlive = true;
    private boolean isinLobby = false;



    public boolean isAlive(){
        return isAlive;
    }

    public void setDead(boolean isAlive){
        this.isAlive = false;
    }

    public void setClasstype(String classtype){this.classtype = classtype;}

    public String getClasstype(){return this.classtype;}

    public void addKill(){
        killcount = killcount + 1; points = points+10;
    }

    public void removePoints(Integer amount){points = points-amount;}
    public int getPoints(){return this.points;}
    public int getKills(){
        return this.killcount;
    }

    public void clearKillstreak(Player p){
        killcount = 0;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setInLobby(boolean lobby){
        this.isinLobby = lobby;
    }

    public boolean isInLobby(){
        return isinLobby;
    }



    // DON'T USE THIS, something isn't working. CBA to fix.
    public boolean isInGame(){
        for (String arenas : gameController.arenaNameList) {
            BattleBoneArena arena = gameController.getArena(arenas);
            if ((arena.getPlayers().contains(name))) {
                return true;
            }
        }
        return false;
    }

}
