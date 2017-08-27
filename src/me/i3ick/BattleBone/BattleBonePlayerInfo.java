package me.i3ick.BattleBone;

import org.bukkit.entity.Player;

import java.security.PublicKey;

/**
 * Created by Karlo on 4/26/2017.
 */
public class BattleBonePlayerInfo {

    private BattleBoneGameController gameController;

    public BattleBonePlayerInfo(BattleBoneGameController PassPlug){
        this.gameController = PassPlug;
    }

    private String Name;

    public void setName(String name){
        this.Name = name;
    }

    public String getName(String playername){
        return this.Name;
    }

}
