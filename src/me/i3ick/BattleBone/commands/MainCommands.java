package me.i3ick.BattleBone.commands;

import me.i3ick.BattleBone.BBSpawnAreaCreator;
import me.i3ick.BattleBone.BattleBoneArenaCreator;
import me.i3ick.BattleBone.BattleBoneGameController;
import me.i3ick.BattleBone.BattleBoneMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

/**
 * Created by i3ick on 4/25/2017.
 */
public class MainCommands implements CommandExecutor{

    BattleBoneMain plugin;
    SubCommands subcmnds;
    BattleBoneGameController gameController;
    BattleBoneArenaCreator creator;



    public MainCommands(BattleBoneMain passedPlugin, BattleBoneArenaCreator pass1, BattleBoneGameController pass2, BBSpawnAreaCreator pass3) {
        subcmnds = new SubCommands(passedPlugin, pass2, pass1, pass3);
        this.plugin = passedPlugin;
        this.creator = pass1;
        this.gameController = pass2;

    }

    public static boolean isInt(String number){
        try{
            Integer.parseInt(number);
            return true;
        }
        catch(NumberFormatException nfe){
            return false;
        }
    }

    public static boolean isDouble(String number){
        try{
            Double.parseDouble(number);
            return true;
        }
        catch(NumberFormatException nfe){
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if(!(player instanceof Player)){
            plugin.getLogger().info("You can't controll BATTLEBONE commands through the console");
            return true;
        }

        // display help
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            if(sender.hasPermission("battlebone.*")){
                subcmnds.helpMod(player);
            }
            else if(sender.hasPermission("battleboneplayers.*")){
                subcmnds.helpPlayer(player);
            }
            else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb help");
                player.sendMessage(ChatColor.YELLOW + "Do you have permissions to view help?");
            }
            return false;
        }

        // joining the arena
        if (args[0].equalsIgnoreCase("join")) {
            if(!sender.hasPermission("winterslash.join")){
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
               // subcmnds.joinRandom(player);
                return true;
            }
            if(args.length == 2){
                subcmnds.join(player, args[1]);
                return true;
            }else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb join <arenaname>");
                return true;
            }
        }

        // leave a game
        if(args[0].equalsIgnoreCase("leave")){
            if(!sender.hasPermission("winterslash.leave")){
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
                subcmnds.leave(player);
            }
            return true;
        }

        // down a player
        if (args[0].equalsIgnoreCase("down")) {
            if(!sender.hasPermission("battlebone.dev")){
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
                subcmnds.down(player);
                return true;
            } else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb down <arenaname>");
                return true;
            }
        }

        // give item
        if (args[0].equalsIgnoreCase("give")) {
            if(!sender.hasPermission("battlebone.dev")){
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
                subcmnds.give(player);
                return true;
            } else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb down <arenaname>");
                return true;
            }
        }

        //set spawn location
        if (args[0].equalsIgnoreCase("setspawn")) {
            if (!sender.hasPermission("battlebone.setspawn")) {
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
                subcmnds.setSpawn(player);
            }
            else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb setspawn");
            }
            return true;

        }

        //set lobby location
        if (args[0].equalsIgnoreCase("setlobby")) {
            if (!sender.hasPermission("battlebone.setlobby")) {
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
                subcmnds.setLobby(player);
            }
            else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb setlobby");
            }
            return true;

        }

        //create the arena
        if(args[0].equalsIgnoreCase("create")){
            if(!sender.hasPermission("battlebone.create")){
                sender.sendMessage("No permission!");
                return true;
            }

            if(args.length == 3){
                if(!isInt(args[2])){
                    player.sendMessage(ChatColor.RED + args[2] + " is not a number!");
                    return true;
                }
                int playerNumber = Integer.parseInt(args[2]);
                if(playerNumber < 1){
                    player.sendMessage(ChatColor.YELLOW + " Minimum number of players is 1!");
                    return true;
                }
                subcmnds.create(args[1], player, playerNumber);
                return true;
            }
            else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb create <arenaname> <playernumber>");
            }

            return true;
        }

        //SET AREA A LOCATION
        if(args[0].equalsIgnoreCase("setin")){
            if(!sender.hasPermission("battlebone.setin")){
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
                subcmnds.setAIn(player);
            }
            else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb setin");
            }
            return true;
        }



        //SET AREA B LOCATION
        if(args[0].equalsIgnoreCase("setout")){
            if(!sender.hasPermission("battlebone.setout")){
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 1){
                subcmnds.setAOut(player);
            }
            else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb setout");
            }
            return true;
        }


        /**
         * List all arenas
         */
        if (args[0].equalsIgnoreCase("list")) {
            if(!sender.hasPermission("battlebone.list")){
                sender.sendMessage("No permission!");
                return true;
            }
            if ((args.length == 1)) {
                subcmnds.list(player);
                return true;
            }
            player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb list");
            return true;
        }

        /**
         * remove an arena
         */
        if(args[0].equalsIgnoreCase("remove")){
            if(!sender.hasPermission("battlebone.remove")){
                sender.sendMessage("No permission!");
                return true;
            }
            if(args.length == 2){
                subcmnds.removeArena(args[1], player);
            }
            else{
                player.sendMessage(ChatColor.YELLOW + "Proper formulation is: /bb remove <arenaname>");
            }
            return true;
        }


        return false;
    }
}
