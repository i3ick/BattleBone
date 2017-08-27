package me.i3ick.BattleBone;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Karlo on 7/29/2017.
 */
public class BBParticles {

        BattleBoneMain plugin;

        public BBParticles(BattleBoneMain passPlug1) {
            plugin = passPlug1;
        }


        public void ParticleEffect(Location Loc, String EffectName) {

            switch (EffectName) {
                case "Heart": Heart(Loc);
                    break;

            }


        }


        public void Heart(Location Loc) {
            new BukkitRunnable() {
                Location loc = Loc;
                double r = 1;
                Vector direction = loc.getDirection().normalize();

                public void run() {

                    for (int t = 0; t < 20; t++) {
                        double z = (Math.cos(t)) / 10;
                        double y = ((13 * Math.cos(t) - 5 * Math.cos(2 * t) - 3 * Math.cos(3 * t) - Math.cos(4 * t)) / 13) + 2;
                        double x = r * Math.sin(t);
                        loc.add(x, y, z);
                        loc.getWorld().playEffect(loc, Effect.HEART, 1);
                        loc.subtract(x, y, z);

                        this.cancel();

                    }


                }
            }.runTaskTimer(plugin, 0, 10);


        }



        public void Spire(Location Loc) {
            new BukkitRunnable() {
                Location loc = Loc;
                double r = 2;
                double t = 0;

                public void run() {

                    t = t + Math.PI/16;
                    double x = r*Math.cos(t);
                    double y = r*Math.sin(t);
                    double z = r*Math.sin(t);


                    loc.add(x, y, z);
                    loc.getWorld().playEffect(loc, Effect.FLAME, 1);
                    loc.subtract(x, y, z);
                    this.cancel();
                    if (t > Math.PI * 8) {
                        this.cancel();
                    }




                }
            }.runTaskTimer(plugin, 0, 10);


        }



    public void Fall(Location Loc) {
        new BukkitRunnable() {
            Location loc = Loc.add(0, 10, 0);
            double t = 0;
            boolean startExplosion = false;
            Vector direction  = new Vector(0, -1, 0).normalize();

            public void run() {

                t = t +0.5;
                double x = direction.getX();
                double y = direction.getY()*t + 0.5;
                double z = direction.getZ();


                loc.add(x, y, z);
                loc.setYaw(0);
                loc.setPitch(0);

                if(!startExplosion) {
                    loc.getWorld().playEffect(loc.subtract(0, -10, 0), Effect.EXPLOSION_LARGE, 3);
                    startExplosion = true;
                }

                loc.getWorld().playEffect(loc, Effect.HEART, 3);
                loc.subtract(x, y, z);
                if (t > 30) {
                    this.cancel();
                }




            }


        }.runTaskTimer(plugin, 0, 1);


    }

}
