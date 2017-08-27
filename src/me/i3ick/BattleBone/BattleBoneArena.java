package me.i3ick.BattleBone;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Karlo on 4/26/2017.
 */
public class BattleBoneArena {


    BattleBoneGameController gameController;

    public BattleBoneArena(BattleBoneGameController passPlug2) {
        this.gameController = passPlug2;
    }



    //LOGIC

    //BBRoundLogic GLogic = new BBRoundLogic(this, gameController);


    // Arena Name
    private String name;


    public ArrayList<BattleBoneArenaCreator> arenaObjects = new ArrayList<BattleBoneArenaCreator>();


    Map<Player, Location> PlayerDeathData = new HashMap<Player, Location>();


    private ArrayList<String> ingamePlayers = new ArrayList<String>();
    private ArrayList<String> alive = new ArrayList<String>();
    private ArrayList<String> spectating = new ArrayList<String>();
    public ArrayList<String> disableFire = new ArrayList<String>();
    private ArrayList<String> clickedSign = new ArrayList<String>();
    private ArrayList<String> sign = new ArrayList<String>();
    private ArrayList<Integer> skeleArray = new ArrayList<Integer>();
    private int roundNumber;
    private int thisRoundMaxMonsters;


    private Location spawn;
    private int spawnAreaId;
    private Location lobby;
    private Location joinLocation;
    private int minPlayers;
    public World world;
    public int startMobNumber = 80;

    public void reset() {
        this.ingamePlayers.clear();
        this.alive.clear();
        this.sign.clear();
        this.spectating.clear();
        this.disableFire.clear();
        this.clickedSign.clear();
        this.killMobs();
    }

    public void killMobs(){
        killSkeletons();
    }

    public String getRandomPlayer(){
        Random rand = new Random();
        Integer size = this.ingamePlayers.size();
        int RandIndex = rand.nextInt(size);
        return this.ingamePlayers.get(RandIndex);
    }

    public void killSkeletons(){
        for(Entity entity:  this.world.getEntities()){
            if(this.getSkeletonArray().contains(entity.getEntityId())){
                Skeleton skele = (Skeleton) entity;
                skele.remove();
            }
        }
        this.getSkeletonArray().clear();
    }



    public void removePlayerFromArrays(String name){
        ingamePlayers.remove(name);
        alive.remove(name);
        sign.remove(name);
        spectating.remove(name);
        disableFire.remove(name);
        clickedSign.remove(name);

    }

    public int getStartMobNumber(){
        return this.startMobNumber;
    }

    public void setStartMobNumber(int nr){
        this.startMobNumber = nr;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Integer> getSkeletonArray(){
        return skeleArray;
    }


    public void removeFromSkeletonArray(Integer id){
        this.skeleArray.remove(Integer.valueOf(id));
    }


    public void setSkeletonArray(int mobID){
        skeleArray.add(mobID);
    }


    public void setName(String name){
        this.name = name;
    }

    public void setSpawnAreaId(Integer id){
        this.spawnAreaId = id;
    }

    public int getSpawnAreaId(){
        return spawnAreaId;
    }

    public int getMinPl() {
        return this.minPlayers;
    }

    public void setMinPl(int minPlayers){
        this.minPlayers = minPlayers;
    }

    public ArrayList<String> getAlive() {
        return alive;
    }
    public void setAlive(Player p) {
        alive.add(p.getName());
    }

    public ArrayList<String> getSpectating() {
        return spectating;
    }

    public void setSpectating(Player p) {
        spectating.add(p.getName());
    }

    public void removeSpectating(Player p) {
        spectating.remove(p.getName());
    }

    public ArrayList<String> getClickedSign() {
        return clickedSign;
    }

    public void setClicked(Player p) {
        clickedSign.add(p.getName());
    }

    public void setPlayers(Player p) {
        ingamePlayers.add(p.getName());
    }

    public void removePlayers(Player p) {
        ingamePlayers.remove(p.getName());
    }

    public ArrayList<String> getPlayers() {
        return this.ingamePlayers;
    }

    public void switchAliveState(Player p) {
        if (!alive.contains(p)) {
            alive.remove(p.getName());
            spectating.add(p.getName());
        }
    }

    public Location getLobbyLocation() {
        return joinLocation;
    }

    public void setLobby(Location lobbyLocation){this.joinLocation = lobbyLocation;}


    public void setSpawn(Location spawn){
        this.spawn = spawn;
    }
    public Location getSpawn() {
        return spawn;
    }


    public void setSign(String player) {
        sign.add(player);
    }

    public void clearSign(String player) {
        sign.remove(player);
    }

    public ArrayList<String> getSign() {
        return sign;
    }


    /**
     * Boolean to determine if an arena is ingame or not (auto false)
     */
    private boolean inGame = false;
    private boolean isLast = false;
    private boolean introduceNewMob = false;

    public boolean isInGame() {
        return inGame;
    }

    public boolean introduceNewMob() {
        return introduceNewMob;
    }

    public boolean isLastRound() {
        return isLast;
    }

    public void setLastRound(boolean isLast) {
        this.isLast = isLast;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void setRoundNumber(int rn){
        this.roundNumber = rn;
    }
    public int getRoundNumber(){
        return this.roundNumber;
    }

    public void thisRoundMaxMonsters(int monsterNumberInt) {
        this.thisRoundMaxMonsters = monsterNumberInt;
    }


    /**
     * returns whether game is full or not.
     *
     * @return
     */
    public boolean isFull() {
        if (ingamePlayers.size() >= minPlayers) {
            return true;
        } else {
            return false;
        }
    }


    public boolean ifContains(Player player) {
        if (ingamePlayers.contains(player.getName())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Send a message to all ingame players
     *
     * @param message
     */
    public void sendMessage(String message) {
        for (String s : ingamePlayers) {
            Player player = Bukkit.getServer().getPlayerExact(s);
            if (player != null) {
                Bukkit.getPlayer(s).sendMessage(message);
            }
        }
    }

    /**
     * Send a hotbar message to all ingame players
     *
     * @param message
     */
    public void sendHotMessage(String message) {
        for (String s : ingamePlayers) {
            Player player = Bukkit.getServer().getPlayerExact(s);
            if (player != null) {
                sendActionBar(Bukkit.getPlayer(s), message);
            }
        }
    }


    /**
     * Send Hotbar Message
     *
     * @param player
     * @param message
     */
    public static void sendActionBar(Player player, String message) {
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }

    /**
     * Show a title over everyones screen
     *
     * @param roundNumber
     */
    public void sendBroadcastMessage(int roundNumber) {
        for (String s : ingamePlayers) {
            Player player = Bukkit.getServer().getPlayerExact(s);
            if (player != null) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " times 5 18 5");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() +
                        " title [{\"text\":\"Round-\",\"color\":\"red\",\"bold\":\"true\"},{\"text\":\" " +
                        roundNumber + "\",\"color\":\"gold\",\"bold\":\"false\"}]");
            }
        }
    }



    /**
     * Show a title over everyones screen
     *
     * @param timeNumber
     */
    public void sendBroadcastNumber(int timeNumber) {
        for (String s : ingamePlayers) {
            Player player = Bukkit.getServer().getPlayerExact(s);
            if (player != null) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " times 5 18 5");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() +
                        " subtitle  [\"\",{\"text\":\""+ timeNumber+"\",\"color\":\"yellow\",\"bold\":true,\"insertion\":\"daw\"}]");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() +
                        " title  [\"\",{\"text\":\"_\",\"color\":\"yellow\",\"bold\":true,\"insertion\":\"daw\"}]");
            }
        }
    }

    public void BBTitle() {
        for (String s : ingamePlayers) {
            Player player = Bukkit.getServer().getPlayerExact(s);
            if (player != null) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " times 5 30 5");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() +
                        " title [\"\",{\"text\":\"G\",\"color\":\"dark_purple\",\"bold\":true,\"insertion\":\"daw\"}" +
                        ",{\"text\":\"E\",\"color\":\"red\",\"bold\":true},{\"text\":\"T\",\"color\":\"yellow\",\"bold\":true" +
                        "},{\"text\":\" \",\"color\":\"green\",\"bold\":false},{\"text\":\"R\",\"color\":\"green\",\"bold\":true" +
                        "},{\"text\":\"E\",\"color\":\"gold\",\"bold\":true},{\"text\":\"A\",\"color\":\"blue\",\"bold\":true},{\"" +
                        "text\":\"D\",\"color\":\"dark_aqua\",\"bold\":true},{\"text\":\"Y\",\"color\":\"light_purple\",\"bold\":true}]");

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() +
                        " subtitle [\"\",{\"text\":\"BATTLEBONE\",\"color\":\"dark_purple\",\"bold\":true,\"insertion\":\"daw\"}]");

            }
    }

    }


    /**
     * SCORE BOARDS
     */
    HashMap<Score, String> players = new HashMap<Score, String>();
    public void setupStatboard(Player p) {


        ScoreboardManager statManager = Bukkit.getScoreboardManager();
        Scoreboard onJoin = statManager.getNewScoreboard();
        Objective o = onJoin.registerNewObjective("test", "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName("~BATTLEBONE~");
        Score score = o.getScore(ChatColor.YELLOW + "Round:" + ChatColor.RED + this.getRoundNumber() + ChatColor.GRAY);
        score.setScore(0);
        Score score2 = o.getScore(ChatColor.YELLOW + "Kills:" + ChatColor.RED + gameController.getPlayerData(p.getName()).getKills() + ChatColor.GRAY);
        score2.setScore(0);

        Score score3 = o.getScore(ChatColor.GREEN + "--------------");
        score3.setScore(16);

        Score score4 = o.getScore("");
        score4.setScore(15);

        Score p1= o.getScore("");
        Score p2= o.getScore("");
        Score p3= o.getScore("");
        Score p4= o.getScore("");
        for (String pl :getPlayers()){
            if(!p1.isScoreSet() || players.get(p1) == pl){
                p1 = o.getScore(ChatColor.AQUA + pl);
                if(!players.containsKey(p1)){players.put(p1, pl);}
                p1.setScore(gameController.getPlayerData(pl).getPoints());
            }
            if(!p2.isScoreSet()|| players.get(p2) == pl){
                p2 = o.getScore(ChatColor.LIGHT_PURPLE + pl);
                if(!players.containsKey(p2)){players.put(p2, pl);}
                p2.setScore(gameController.getPlayerData(pl).getPoints());
            }
            if(!p3.isScoreSet()|| players.get(p3) == pl){
                p3 = o.getScore(ChatColor.GOLD + pl);
                if(!players.containsKey(p3)){players.put(p3, pl);}
                p3.setScore(gameController.getPlayerData(pl).getPoints());
            }
            if(!p4.isScoreSet()|| players.get(p4) == pl){
                p4 = o.getScore(ChatColor.WHITE + pl);
                if(!players.containsKey(p4)){players.put(p4, pl);
                }
                p4.setScore(gameController.getPlayerData(pl).getPoints());
            }

        }

        Score score5 = o.getScore(ChatColor.GREEN + "POINTS:" + ChatColor.RED + gameController.getPlayerData(p.getName()).getPoints());
        score5.setScore(0);
        p.setScoreboard(onJoin);




        p.setScoreboard(onJoin);
    }

    /**
    public void setPointsName(Player p) {
        ScoreboardManager statManager = Bukkit.getScoreboardManager();
        Scoreboard pointBoard = statManager.getNewScoreboard();
        Objective o = pointBoard.registerNewObjective("score", "dummy");
        o.setDisplaySlot(DisplaySlot.BELOW_NAME);
        o.setDisplayName(ChatColor.GREEN + "POINTS:" + ChatColor.RED + gameController.getPlayerData(p.getName()).getPoints());

        p.setScoreboard(pointBoard);
        Score s = o.getScore(p);
        s.setScore(0);

    }
     **/

    public void setWorld(World world) {
        this.world = world;
    }
}
