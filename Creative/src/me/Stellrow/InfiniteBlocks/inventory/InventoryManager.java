package me.Stellrow.InfiniteBlocks.inventory;

import me.Stellrow.InfiniteBlocks.InfiniteBlocks;
import me.Stellrow.InfiniteBlocks.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.xml.stream.events.Namespace;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager implements Listener {
    private final InfiniteBlocks pl;
    private HashMap<String, Map<Integer, List<Material>>> menu = new HashMap<>();
    private Inventory mainPage = Bukkit.createInventory(null,54, Utils.asColor("&eCreative-Menu"));
    private HashMap<String,Map<Integer,Inventory>> translatedMenu = new HashMap<>();
    private ItemStack mainMenuButton;
    private ItemStack previousPage;
    private ItemStack nextPage;

    //Keys
    private NamespacedKey menuItem;
    private NamespacedKey inventoryButton;
    private NamespacedKey currentPageNumber;
    private NamespacedKey previousButton;
    private NamespacedKey nextButton;

    public InventoryManager(InfiniteBlocks pl) {
        this.pl = pl;
        menuItem = new NamespacedKey(pl,"mainpageitem");
        inventoryButton = new NamespacedKey(pl,"inventorybutton");
        currentPageNumber = new NamespacedKey(pl,"currentpagenumber");
        //
        previousButton = new NamespacedKey(pl,"previous");
        nextButton = new NamespacedKey(pl,"next");
    }
    public void setup(HashMap<String, Map<Integer, List<Material>>> menu){
        this.menu = menu;
        setupInventory();
        setupPages();
        buildButtons();
    }
    private void setupInventory(){
        for(String category : menu.keySet()){
            ItemStack menuitem = buildMainPage(category);
            Integer slot = pl.getConfig().getInt("CategoryConfig."+category+".slot");
            mainPage.setItem(slot,menuitem);
        }
    }
    public void openInventory(Player toOpen){
        toOpen.openInventory(mainPage);
    }
    private ItemStack buildPageItem(Material material){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.asColor(pl.getConfig().getString("ItemConfig.name").replaceAll("%type",material.toString())));
        itemMeta.setLore(Utils.getTranslatedLore(pl.getConfig().getStringList("ItemConfig.lore")));
        item.setItemMeta(itemMeta);
        return item;
    }
    private ItemStack buildMainPage(String configPath){
        ItemStack item = new ItemStack(Material.valueOf(pl.getConfig().getString("CategoryConfig."+configPath+".type")));
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(Utils.asColor(pl.getConfig().getString("CategoryConfig."+configPath+".name")));
        im.setLore(Utils.getTranslatedLore(pl.getConfig().getStringList("CategoryConfig."+configPath+".lore")));
        im.getPersistentDataContainer().set(menuItem, PersistentDataType.STRING,pl.getConfig().getString("CategoryConfig."+configPath+".category-to-open"));
        item.setItemMeta(im);
        return item;
    }

    private void setupPages(){
        for(String category : menu.keySet()){
            //Translate from
            Map<Integer,List<Material>> categoryPage = menu.get(category);
            //Store to
            Map<Integer,Inventory> translatedCategoryPage = new HashMap<>();
            for(Integer pageKey : categoryPage.keySet()){
                List<Material> valuesInsidePage = categoryPage.get(pageKey);
                Inventory pageInventory = Bukkit.createInventory(null,54,Utils.asColor("&eCreativeInventory"));
                for(Material value : valuesInsidePage){
                    pageInventory.addItem(buildPageItem(value));
                }
                translatedCategoryPage.put(pageKey,pageInventory);
            }
            translatedMenu.put(category,translatedCategoryPage);
        }

    }
    private void openPage(ItemStack clicked,Player whoClicked){
        String redirectTo = clicked.getItemMeta().getPersistentDataContainer().get(menuItem,PersistentDataType.STRING);
        Map<Integer,Inventory> categoryPages = translatedMenu.get(redirectTo);
        whoClicked.openInventory(adjustInventory(categoryPages.get(0),redirectTo,0,categoryPages));
    }

    private void retrieveItem(Player toGive,ItemStack selected){
        ItemStack item = selected.clone();
        item.setAmount(64);
        ItemMeta im = selected.getItemMeta();
        List<String> lore = im.getLore();
        lore.add(Utils.asColor(pl.getConfig().getString("ItemConfig.who-took").replaceAll("%playerName",toGive.getName())));
        im.setLore(lore);
        im.getPersistentDataContainer().set(pl.pluginKey,PersistentDataType.STRING,"infiniteblock");
        item.setItemMeta(im);
        toGive.getInventory().addItem(item);
    }

    private Inventory adjustInventory(Inventory toModify,String category,Integer pageChosen,Map<Integer,Inventory> selectedCategory){
        Inventory inv = toModify;
        ItemStack air = new ItemStack(Material.AIR);
        inv.setItem(45,air);
        inv.setItem(46,air);
        inv.setItem(47,air);
        inv.setItem(51,air);
        inv.setItem(52,air);
        inv.setItem(53,air);
        if(selectedCategory.get(pageChosen-1)==null){
            //48 left arrow
            inv.setItem(48,air);
        }else{
            ItemStack prev = previousPage;
            ItemMeta prevm = prev.getItemMeta();
            prevm.getPersistentDataContainer().set(previousButton,PersistentDataType.STRING,"prev");
            prevm.getPersistentDataContainer().set(inventoryButton,PersistentDataType.STRING,category);
            prevm.getPersistentDataContainer().set(currentPageNumber,PersistentDataType.INTEGER,pageChosen);
            prev.setItemMeta(prevm);
            inv.setItem(48,prev);
        }
        //
        if(selectedCategory.get(pageChosen+1)==null){
            //50 right arrow
            inv.setItem(50,air);
        }else{
            ItemStack next = nextPage;
            ItemMeta nextm = next.getItemMeta();
            nextm.getPersistentDataContainer().set(nextButton,PersistentDataType.STRING,"next");
            nextm.getPersistentDataContainer().set(inventoryButton,PersistentDataType.STRING,category);
            nextm.getPersistentDataContainer().set(currentPageNumber,PersistentDataType.INTEGER,pageChosen);
            next.setItemMeta(nextm);
            inv.setItem(50,next);
        }
        //49 main
        inv.setItem(49,mainMenuButton);
        //



        return inv;
    }
    //Build buttons
    private void buildButtons(){

        ///Main button
        mainMenuButton = new ItemStack(Material.valueOf(pl.getConfig().getString("CategoryButtons.mainPage.type")));
        ItemMeta im = mainMenuButton.getItemMeta();
        im.setDisplayName(Utils.asColor(pl.getConfig().getString("CategoryButtons.mainPage.name")));
        im.setLore(Utils.getTranslatedLore(pl.getConfig().getStringList("CategoryButtons.mainPage.lore")));
        im.getPersistentDataContainer().set(inventoryButton,PersistentDataType.STRING,"mainPage");
        mainMenuButton.setItemMeta(im);
        //


        ///Previous page
        previousPage = new ItemStack(Material.valueOf(pl.getConfig().getString("CategoryButtons.lastPage.type")));
        ItemMeta previousPageMeta = previousPage.getItemMeta();
        previousPageMeta.setDisplayName(Utils.asColor(pl.getConfig().getString("CategoryButtons.lastPage.name")));
        previousPageMeta.setLore(Utils.getTranslatedLore(pl.getConfig().getStringList("CategoryButtons.lastPage.lore")));
        previousPage.setItemMeta(previousPageMeta);
        //

        ///Next page
        nextPage = new ItemStack(Material.valueOf(pl.getConfig().getString("CategoryButtons.nextPage.type")));
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName(Utils.asColor(pl.getConfig().getString("CategoryButtons.nextPage.name")));
        nextPageMeta.setLore(Utils.getTranslatedLore(pl.getConfig().getStringList("CategoryButtons.nextPage.lore")));
        nextPage.setItemMeta(nextPageMeta);
        //

    }


    ///Events for the inventory
    //Cancel taking out of inventory
    @EventHandler
    public void cancelClick(InventoryClickEvent event){
        if(event.getInventory().equals(mainPage)){
            event.setCancelled(true);
            if(event.getCurrentItem()==null){
               return;
            }
            openPage(event.getCurrentItem(), (Player) event.getWhoClicked());

        }
        if(event.getView().getTitle().equalsIgnoreCase(Utils.asColor("&eCreativeInventory"))){
            event.setCancelled(true);
            if(event.getCurrentItem()==null){
                return;
            }
            if(event.getCurrentItem().equals(mainMenuButton)){
                openInventory((Player) event.getWhoClicked());
                return;
            }
            if(event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(previousButton,PersistentDataType.STRING)){
                String category = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(inventoryButton,PersistentDataType.STRING);
                Integer currentPage = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(currentPageNumber,PersistentDataType.INTEGER);
                Map<Integer,Inventory> selected = translatedMenu.get(category);
                event.getWhoClicked().openInventory(adjustInventory(selected.get(currentPage-1),category,currentPage-1,selected));
                return;
            }
            if(event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(nextButton,PersistentDataType.STRING)){
                String category = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(inventoryButton,PersistentDataType.STRING);
                Integer currentPage = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(currentPageNumber,PersistentDataType.INTEGER);
                Map<Integer,Inventory> selected = translatedMenu.get(category);
                event.getWhoClicked().openInventory(adjustInventory(selected.get(currentPage+1),category,currentPage+1,selected));
                return;

            }

            retrieveItem((Player) event.getWhoClicked(),event.getCurrentItem());
        }
    }


}
