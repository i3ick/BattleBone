package me.i3ick.BattleBone;

import net.minecraft.server.v1_11_R1.Item;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Karlo on 4/26/2017.
 */
public class BattleBoneClasses {

    public ItemStack getRandomArraylist(ArrayList list){
        Random rand = new Random();
        Integer size = list.size();
        int RandIndex = rand.nextInt(size);
        return (ItemStack) list.get(RandIndex);
    }


public void setDefault(Player p){

    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 5));
    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 5));
    int newItemSlot = p.getInventory().firstEmpty();
    ItemStack knife = new ItemStack(Material.DIAMOND_SWORD, 1);
    knife.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
    knife.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 100);
    knife.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 100);
    p.getInventory().setItem(newItemSlot, knife);

    ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
    ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
    p.getInventory().addItem(knife);
    p.getInventory().setChestplate(chest);
    p.getInventory().setBoots(boots);
    p.updateInventory();

}


    public void skeleSoldier(Skeleton skele){

        ArrayList<ItemStack> Helmets = new ArrayList<ItemStack>();

        ItemStack pumpkinHelmet = new ItemStack(Material.PUMPKIN, 1);
        ItemStack skullHelmet = new ItemStack(Material.SKULL, 1);
        ItemStack goldHelmet = new ItemStack(Material.GOLD_HELMET, 1);
        Helmets.add(pumpkinHelmet);
        Helmets.add(goldHelmet);
        Helmets.add(skullHelmet);
        Helmets.add(skullHelmet);
        Helmets.add(skullHelmet);

        ArrayList<ItemStack> Cplates = new ArrayList<ItemStack>();

        ItemStack goldCplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
        ItemStack airCplate = new ItemStack(Material.AIR, 1);
        Cplates.add(goldCplate);
        Cplates.add(airCplate);
        Cplates.add(airCplate);
        Cplates.add(airCplate);



        skele.getEquipment().clear();
        skele.getEquipment().setHelmet(getRandomArraylist(Helmets));
        skele.getEquipment().setChestplate(getRandomArraylist(Cplates));

    }




}
