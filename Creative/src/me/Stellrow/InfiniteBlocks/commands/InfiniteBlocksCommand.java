package me.Stellrow.InfiniteBlocks.commands;

import me.Stellrow.InfiniteBlocks.InfiniteBlocks;
import me.Stellrow.InfiniteBlocks.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class InfiniteBlocksCommand implements CommandExecutor {
    private final InfiniteBlocks pl;

    public InfiniteBlocksCommand(InfiniteBlocks pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String sa, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(Utils.asColor("&cOnly players can use this command!"));
            return true;
        }
        if(!sender.hasPermission("creative.use")){
            sender.sendMessage(Utils.asColor(pl.getConfig().getString("CommandMessages.no-permission")));
            return true;
        }
        if(args.length==1&&args[0].equalsIgnoreCase("breaker")){
            giveBreaker((Player) sender);
            return true;
        }
        //open ui
        pl.getInventoryManager().openInventory((Player) sender);
        return true;
    }

    private void giveBreaker(Player sender){
        ItemStack breaker = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta im = breaker.getItemMeta();
        im.setDisplayName(Utils.asColor(pl.getConfig().getString("ItemConfig.name").replaceAll("%type","Breaker")));
        List<String> lore = Utils.getTranslatedLore(pl.getConfig().getStringList("ItemConfig.lore"));
        im.setLore(Utils.replacePlayer(lore,sender));
        im.setUnbreakable(true);
        im.getPersistentDataContainer().set(pl.pluginKey,PersistentDataType.STRING,"breaker");
        im.addEnchant(Enchantment.DIG_SPEED,100,true);
        breaker.setItemMeta(im);
        sender.getInventory().addItem(breaker);
    }
}
