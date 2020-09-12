package me.Stellrow.InfiniteBlocks.events;

import me.Stellrow.InfiniteBlocks.InfiniteBlocks;
import me.Stellrow.InfiniteBlocks.Utils;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InfiniteBlocksEvents implements Listener {
    private final InfiniteBlocks pl;

    public InfiniteBlocksEvents(InfiniteBlocks pl) {
        this.pl = pl;
    }


    //Inventory pickup item
    @EventHandler
    public void onPickupInv(InventoryPickupItemEvent event){
        if(event.getInventory().getType()== InventoryType.HOPPER){
            Item item = event.getItem();
            ItemStack im = item.getItemStack();
            if(im.hasItemMeta()){
                if(im.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
                    event.setCancelled(true);
                }
            }
        }
    }
    //Inventory move item event
    @EventHandler
    public void onMove(InventoryMoveItemEvent event){
        ItemStack item = event.getItem();
        if(item.hasItemMeta()){
            if(item.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
                event.setCancelled(true);
            }
        }
    }
    //Pickup check
    @EventHandler
    public void onPickup(EntityPickupItemEvent event){
        Item itemraw = event.getItem();
        ItemStack item = itemraw.getItemStack();
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        Player p = (Player) event.getEntity();
        if(item.hasItemMeta()){
            if(item.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
                if(!p.hasPermission("creative.use")){
                    itemraw.remove();
                    event.setCancelled(true);
                }
            }
        }
    }
    //Drop check
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
       Item itemraw = event.getItemDrop();
       ItemStack item = itemraw.getItemStack();
       if(item.hasItemMeta()){
           if(item.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
               item.setAmount(0);
           }
       }
    }
    //Stop unpermissioned players from touching the items
    @EventHandler
    public void oninvclick(InventoryClickEvent event){
        ItemStack clicked = event.getCurrentItem();
        Player whoClicked = (Player) event.getWhoClicked();
        if(clicked!=null&&clicked.hasItemMeta()){
            if(clicked.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
                if(!whoClicked.hasPermission("creative.use")){
                    whoClicked.sendMessage(Utils.asColor(pl.getConfig().getString("BlockPlacing.no-permission")));
                    event.setCancelled(true);
                    clicked.setAmount(0);
                }
            }
        }


    }

    //Remove drops from players with these items
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        ItemStack used = event.getPlayer().getInventory().getItemInMainHand();
        Player whoBroke = event.getPlayer();
        if(used!=null&&used.hasItemMeta()){
            if(used.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
                event.setDropItems(false);
                return;
            }
        }

    }
    //Check the clicks and give haste to simulate creative instab-break
    @EventHandler
    public void onClick(PlayerInteractEvent event){
    ItemStack used = event.getItem();
    Player whoUsed = event.getPlayer();
    if(used!=null&&used.hasItemMeta()){
        if(used.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
            if(!whoUsed.hasPermission("creative.use")){
                whoUsed.sendMessage(Utils.asColor(pl.getConfig().getString("BlockPlacing.no-permission")));
                event.setCancelled(true);
                used.setAmount(0);
            }
        }
    }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        ItemStack used = event.getItemInHand();
        Player whoUsed = event.getPlayer();
        if(used.hasItemMeta()){
            if(used.getItemMeta().getPersistentDataContainer().has(pl.pluginKey, PersistentDataType.STRING)){
            if(whoUsed.hasPermission("creative.use")){
                used.setAmount(64);
                return;
            }else{
                whoUsed.sendMessage(Utils.asColor(pl.getConfig().getString("BlockPlacing.no-permission")));
                event.setCancelled(true);
                used.setAmount(0);
                return;
            }
            }
        }
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        String[] message = event.getMessage().split(" ");
        if(pl.blockedCommands.contains(event.getMessage().split("/")[1])){
            Player sender = event.getPlayer();
            for(ItemStack i : sender.getInventory().getContents()){
                if(i==null||!i.hasItemMeta()){
                    return;
                }
                if(i.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
                    event.setCancelled(true);
                    sender.sendMessage(Utils.asColor(pl.getConfig().getString("CommandMessages.blocked-command")));
                    return;
                }
            }
        }
           for(String word : message){
               if(pl.blockedCommands.contains(word)){
                   Player sender = event.getPlayer();
                   for(ItemStack i : sender.getInventory().getContents()){
                       if(i==null||!i.hasItemMeta()){
                           return;
                       }
                       if(i.getItemMeta().getPersistentDataContainer().has(pl.pluginKey,PersistentDataType.STRING)){
                            event.setCancelled(true);
                            sender.sendMessage(Utils.asColor(pl.getConfig().getString("CommandMessages.blocked-command")));
                           return;
                       }
                   }
               }
           }

        }

}
