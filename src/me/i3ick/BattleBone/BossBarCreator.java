package me.i3ick.BattleBone;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.ArrayList;

/**
 * Created by Karlo on 4/26/2017.
 */
public class BossBarCreator {

    public static ArrayList<BossBarCreator> bossbarObjects = new ArrayList<BossBarCreator>();

    private String revivemsg;
    private String Name;
    private BarColor barColor;
    private BarStyle barStyle;


    //BossBar Namse =  Bukkit.getServer().createBossBar(revivemsg, barStyle, barColor);

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setMSG(String msg) {
        this.revivemsg = msg;
    }

    public String getMSG(){ return this.revivemsg;}

    public void color(BarColor barColor) {
        this.barColor = barColor;
    }

    public void barstyle(BarStyle barStyle) {
        this.barStyle = barStyle;
    }


}
