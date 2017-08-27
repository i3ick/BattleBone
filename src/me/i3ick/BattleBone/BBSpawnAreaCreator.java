package me.i3ick.BattleBone;

import org.bukkit.Location;

/**
 * Created by Karlo on 4/27/2017.
 */
public class BBSpawnAreaCreator {


    private int areaId;
    private String type;
    private int level;

    private Location InsideLocation;
    private Location OutsideLocation;



    public void setID (int sareaID){
        this.areaId = sareaID;
    }


    public void setLevel (int setlevel){
        this.level = setlevel;
    }

    public void setIn (Location inLocation){
        this.InsideLocation = inLocation;
    }
    public Location getIn(){return this.InsideLocation;}



    public void setOut (Location outLocation){
        this.OutsideLocation = outLocation;
    }
    public Location getOut(){return this.OutsideLocation;}






    boolean outside;
    public void OutSet(boolean outside){
        this.outside = outside;
    }

    public boolean isOutSet(){return outside;}

    boolean inside;
    public void InSet(boolean inside){
        this.inside = inside;
    }

    public boolean isInSet(){return inside;}
}
