package me.Stellrow.InfiniteBlocks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String asColor(String toBeTranslated){
        return ChatColor.translateAlternateColorCodes('&',toBeTranslated);
    }
    public static List<String> getTranslatedLore(List<String> toBeTranslated){
        List<String> toReturn = new ArrayList<String>();
        for(String s : toBeTranslated){
            toReturn.add(ChatColor.translateAlternateColorCodes('&',s));
        }
        return toReturn;
    }
    public static List<String> replacePlayer(List<String> toBeConverted, Player toReplaceWith){
        List<String> toReturn = new ArrayList<String>();
        for(String s : toBeConverted){
            toReturn.add(s.replaceAll("%playerName",toReplaceWith.getName()));
        }
        return toReturn;
    }
}
