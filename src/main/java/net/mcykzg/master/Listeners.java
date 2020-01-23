//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.mcykzg.master;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {
    private Master instance = null;

    public Listeners(Master instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (this.instance.isInMasterList(p.getName())) {
            this.instance.setMyLevel(p.getName(), p.getLevel());
        }

    }

    @EventHandler
    public void onJoinWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (p.getWorld().getName().equals(this.instance.getConfig().getString("World")) && p.getLevel() < 60 && !this.instance.hasMaster(p.getName())) {
            p.closeInventory();
            p.openInventory(Inventorys.listMasters(1));
        }

    }

    @EventHandler
    public void onLevelUp(PlayerLevelChangeEvent e) {
        Player p = e.getPlayer();
        int level = e.getNewLevel();
        if (level >= this.instance.getConfig().getInt("GraduteLevel") && this.instance.hasMaster(p.getName())) {
            p.sendMessage(this.instance.getConfig().getString("OverMess1"));
            String master = this.instance.getMyMaster(p.getName());
            Player masterP = Bukkit.getPlayerExact(master);
            if (masterP != null && masterP.isOnline()) {
                masterP.sendMessage(this.instance.getConfig().getString("OverMess2"));
            }

            this.instance.setOver(p.getName(), true);
            this.instance.setMyGiftCount(master, this.instance.getMyGiftCount(master) + 1);
            this.instance.setMyMaster(p.getName(), (String)null);
            this.instance.setMyApprenticeCount(master, this.instance.getMyApprenticeCount(master) + 1);
        }

        if (this.instance.isInMasterList(p.getName())) {
            this.instance.setMyLevel(p.getName(), level);
        }

    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        Inventory inv = e.getInventory();
        int slot = e.getSlot();
        if (slot <= 53 && slot >= 0) {
            String title = inv.getTitle();
            if (title.startsWith("§a师傅列表: ")) {
                e.setCancelled(true);
                int page = Integer.parseInt(title.split("§a师傅列表: §r第")[1].replace("页", ""));
                if (slot == 53) {
                    p.openInventory(Inventorys.listMasters(page + 1));
                } else if (slot == 45) {
                    p.closeInventory();
                    if (page - 1 > 0) {
                        p.openInventory(Inventorys.listMasters(page - 1));
                    }
                }

                ItemStack item = e.getCurrentItem();
                if (item == null || item.getType() == Material.AIR) {
                    return;
                }

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    String dis = item.getItemMeta().getDisplayName();
                    if (dis.startsWith("§a玩家:§r ")) {
                        String master = dis.split("§a玩家:§r ")[1];
                        Player masterP = Bukkit.getPlayerExact(master);
                        if (masterP != null && masterP.isOnline() && p.getAddress().getAddress().getHostAddress().equals(masterP.getAddress().getAddress().getHostAddress())) {
                            p.sendMessage("§c为防止刷奖励,同一IP下的两个玩家无法作为师徒");
                            return;
                        }

                        if (this.instance.hasApprentice(master)) {
                            p.sendMessage("§c他已经有徒弟了");
                            return;
                        }

                        if (this.instance.hasMaster(p.getName())) {
                            p.sendMessage("§c你已经有师父了");
                            return;
                        }

                        if (p.getLevel() >= instance.getConfig().getInt("GraduateLevel") || this.instance.isOver(p.getName())) {
                            p.sendMessage("§c你已经出师了");
                            return;
                        }

                        this.instance.setMyMaster(p.getName(), master);
                        p.sendMessage(this.instance.getConfig().getString("RequestMess1").replace("<player>", master));
                        Player tar = Bukkit.getPlayerExact(master);
                        if (tar != null && tar.isOnline()) {
                            tar.sendMessage(this.instance.getConfig().getString("RequestMess2").replace("<player>", p.getName()));
                        }

                        p.closeInventory();
                    }
                }
            }

        }
    }
}
