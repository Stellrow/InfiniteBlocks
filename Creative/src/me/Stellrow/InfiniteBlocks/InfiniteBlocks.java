package me.Stellrow.InfiniteBlocks;

import me.Stellrow.InfiniteBlocks.commands.InfiniteBlocksCommand;
import me.Stellrow.InfiniteBlocks.events.InfiniteBlocksEvents;
import me.Stellrow.InfiniteBlocks.inventory.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InfiniteBlocks extends JavaPlugin {
    //Dont create more instances/singleton
    private InventoryManager inventoryManager = new InventoryManager(this);
    //
    public HashMap<String, Map<Integer, List<Material>>> menu = new HashMap<>();
    public NamespacedKey pluginKey = new NamespacedKey(this,"infiniteblock");


    public void onEnable(){
        loadConfig();
        getCommand("creative").setExecutor(new InfiniteBlocksCommand(this));
        getServer().getPluginManager().registerEvents(new InfiniteBlocksEvents(this),this);
        getServer().getPluginManager().registerEvents(inventoryManager,this);
        setUpValues();
        inventoryManager.setup(menu);
    }
    public void onDisable(){


    }
    public InventoryManager getInventoryManager(){
        return inventoryManager;
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    //Setup all data from config
    private void setUpValues(){
        for(String category : getConfig().getConfigurationSection("Category").getKeys(false)){
            Map<Integer, List<Material>> pages = new HashMap<>();

            for(String page : getConfig().getConfigurationSection("Category."+category).getKeys(false)){
                List<Material> mats = new ArrayList<>();
                for(String material : getConfig().getStringList("Category."+category+"."+page)){
                    try{
                        mats.add(Material.valueOf(material.toUpperCase()));
                    }catch (IllegalArgumentException exp){
                        Bukkit.getConsoleSender().sendMessage(Utils.asColor("&4[Creative] Found wrong material in the list: "+material+" at page "+ page + " at category "+category));
                    }
                }
                pages.put(Integer.valueOf(page),mats);
            }
            menu.put(category,pages);
        }

    }
    private void log(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

}
