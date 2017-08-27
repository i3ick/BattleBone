package me.i3ick.BattleBone;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.i3ick.BattleBone.commands.MainCommands;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class BattleBoneMain extends JavaPlugin{

    public static Economy econ = null;
    public static WorldEditPlugin worldEdit = null;
    public static BattleBoneMain MainPlugin;
    private static final Logger log = Logger.getLogger("Minecraft");


    @Override
    public void onDisable() {

        File f = new File(this.getDataFolder() + File.separator + "arenaData.yml");
        FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(f);
        getLogger().info("BattleBone Disabled!");
        try {
            arenaConfig.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onEnable() {

        MainPlugin = this;

        try{
            File f = new File(this.getDataFolder() + File.separator + "arenaData.yml");
            if(f.exists()){
                FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(f);
            }else{
                FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(f);
                arenaConfig.addDefault("Spawn" + ".X", null);
                arenaConfig.addDefault("Worlds" + ".World", null);
                arenaConfig.addDefault("Lobby" + ".X", null);
                arenaConfig.addDefault("arenas" + ".X", null);
                arenaConfig.addDefault("PlayerData" + ".X", null);
                arenaConfig.addDefault("DeathPosition" + ".X", null);
                arenaConfig.addDefault("MinPlayerNumber" + ".X", null);
                arenaConfig.options().copyDefaults(true);
                arenaConfig.save(f);

            }
        } catch(Exception e){
            getLogger().info("Error loading arenaData.yml. File not found!");
        }


        if (!setupEconomy() ) {
            log.severe(String.format("Money disabled due to no Vault dependency found!", getDescription().getName()));
            //  getServer().getPluginManager().disablePlugin(this);
            // return;
        }

        if (!setupWorldEdit() ) {
            log.severe(String.format("WorldEdit not found. You won't be able to generate arena throught schematics.", getDescription().getName()));
        }


        // create instances
        BattleBoneGameController gameController = new BattleBoneGameController(this);
        gameController.loadArenas();
        BattleBoneArenaCreator creator = new BattleBoneArenaCreator(this);
        BBSpawnAreaCreator areacreator = new BBSpawnAreaCreator();


        //regist events
        this.getServer().getPluginManager().registerEvents(new BattleBoneListener(this, gameController), this);

        // register commands
        this.getCommand("bb").setExecutor(new MainCommands(this, creator, gameController, areacreator));

        PluginDescriptionFile pdf = this.getDescription();
        getLogger().info("\n\n#####~~~~~~ BATTLEBONE ~~~~~~##### \n" +
                        "   BattleBone version " + pdf.getVersion() + " is now running!\n" +
                        "   Vault enabled: " + setupEconomy() +
                        "\n\n" + "#####~~~~~~~~ by i3ick ~~~~~~~~#####\n"

        );




    }



    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    private boolean setupWorldEdit() {
        if (getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            return false;
        }
        RegisteredServiceProvider<WorldEditPlugin> wep = getServer().getServicesManager().getRegistration(WorldEditPlugin.class);
        if (wep == null) {
            return false;
        }
        worldEdit = wep.getProvider();
        return worldEdit != null;
    }


    /**Return yaml file contents containing arena data
     *
     * @return
     */
    public FileConfiguration getArenaData(){
        File f = new File(this.getDataFolder() + File.separator + "arenaData.yml");
        FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(f);
        return arenaConfig;
    }

    /**
     * Save arenaData.yml
     */
    public void saveArenaData(){
        File f = new File(this.getDataFolder() + File.separator + "arenaData.yml");
        FileConfiguration arenaConfig = YamlConfiguration.loadConfiguration(f);
        try {
            arenaConfig.save(f);
            getLogger().info("file saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Boolean to check whether player inventory is empty
     * @param p
     * @return
     */
    public static boolean isInventoryEmpty(Player p) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null)
                return true;
        }
        return false;
    }




    // this code was taken from KingFaris11
    public boolean saveInventoryToFile(Inventory inventory, String fileName, GameMode gamemode) {
        if (inventory == null || fileName == null) return false;
        try {
            File invFile = new File(getDataFolder(), fileName + ".invsave");
            if (invFile.exists()) invFile.delete();
            FileConfiguration invConfig = YamlConfiguration.loadConfiguration(invFile);

            invConfig.set("Title", inventory.getTitle());
            invConfig.set("GameMode", gamemode.toString());
            invConfig.set("Size", 36);
            invConfig.set("Max stack size", inventory.getMaxStackSize());
            if (inventory.getHolder() instanceof Player) invConfig.set("Holder", ((Player) inventory.getHolder()).getName());
            ItemStack[] invContents = inventory.getContents();
            for (int i = 0; i < invContents.length; i++) {
                ItemStack itemInInv = invContents[i];
                if (itemInInv != null) if (itemInInv.getType() != Material.AIR) invConfig.set("Slot " + i, itemInInv);
            }

            invConfig.save(invFile);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    // this code was taken from KingFaris11
    public Inventory getInventoryFromFile(File file, Player player) {
        if (file == null) return null;
        if (!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) return null;
        try {
            FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
            Inventory inventory = null;
            String invTitle = invConfig.getString("Title", "Inventory");
            String gm = (String) invConfig.get("GameMode");
            int invSize = invConfig.getInt("Size");
            int invMaxStackSize = invConfig.getInt("Max stack size", 64);
            InventoryHolder invHolder = null;
            if (invConfig.contains("Holder")) invHolder = Bukkit.getPlayer(invConfig.getString("Holder"));
            inventory = Bukkit.getServer().createInventory(invHolder, invSize, ChatColor.translateAlternateColorCodes('&', invTitle));
            inventory.setMaxStackSize(invMaxStackSize);
            try{
                System.out.println("entered try");
                ItemStack[] invContents = new ItemStack[invSize];
                for (int i = 0; i < invSize; i++) {
                    if (invConfig.contains("Slot " + i)) invContents[i] = invConfig.getItemStack("Slot " + i);
                    else invContents[i] = new ItemStack(Material.AIR);
                }
                player.getInventory().setContents(invContents);
            } catch (Exception ex) {
                System.out.println("entered try" + ex);
            }
            try{
                System.out.println("entered try");
                ItemStack[] invContents = new ItemStack[invSize];
                for (int i = 0; i < invSize; i++) {
                    if (invConfig.contains("Armor " + i)) invContents[i] = invConfig.getItemStack("Armor " + i);
                    else invContents[i] = new ItemStack(Material.AIR);
                }
            } catch (Exception ex) {
                System.out.println("entered try" + ex);
            }
            this.getLogger().info("util");
            String gamemode = gm;
            switch (gamemode){
                case "SURVIVAL":
                    player.setGameMode(GameMode.SURVIVAL);
                    break;
                case "CREATIVE":
                    player.setGameMode(GameMode.CREATIVE);
                    break;
                case "ADVENTURE":
                    player.setGameMode(GameMode.ADVENTURE);
                    break;
            }
            System.out.println("entered try");

            return inventory;
        } catch (Exception ex) {
            System.out.println("entered try" + ex);
            return null;
        }
    }


}
