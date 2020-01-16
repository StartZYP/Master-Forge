//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.mcykzg.master;

import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    public Commands() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("st")) {
            Player p = (Player)sender;
            int len = args.length;
            Master instance = Master.getInstance();
            if (len == 0) {
                p.sendMessage("§c§l打开师傅列表: §a/st masters");
                p.sendMessage("§c§l申请成为师傅: §a/st request");
                p.sendMessage("§c§l退出师傅列表: §a/st quit");
                p.sendMessage("§c§l断绝师徒关系: §a/st end");
                p.sendMessage("§c§l查看我的徒弟或师傅: §a/st me");
                p.sendMessage("§c§l领取徒弟的出师奖励: §a/st gift");
                p.sendMessage("§7退出师傅列表后不会与现有徒弟脱离关系");
            } else if (len == 1) {
                String master;
                if (args[0].equalsIgnoreCase("me")) {
                    master = instance.getMyMaster(p.getName());
                    String apprentice = instance.getMyApprentice(p.getName());
                    p.sendMessage("§c§l我的师父: §a" + master);
                    p.sendMessage("§c§l我的徒弟: §a" + apprentice);
                } else if (args[0].equalsIgnoreCase("masters")) {
                    p.closeInventory();
                    p.openInventory(Inventorys.listMasters(1));
                } else if (args[0].equalsIgnoreCase("request")) {
                    if (instance.isInMasterList(p.getName())) {
                        p.sendMessage("§c你已经进入了师傅列表了哦");
                        return false;
                    }

                    if (instance.hasApprentice(p.getName())) {
                        p.sendMessage("§c你已经有徒弟了");
                        return false;
                    }

                    if (p.getLevel() < instance.getConfig().getInt("MasterLevel")) {
                        p.sendMessage("§c你的等级不够哦");
                        return false;
                    }

                    instance.joinMasterList(p.getName());
                    if (instance.isInMasterList(p.getName())) {
                        instance.setMyLevel(p.getName(), p.getLevel());
                    }

                    p.sendMessage("§a你已进入了师父列表,将会有人作为你的徒弟!");
                } else if (args[0].equalsIgnoreCase("quit")) {
                    if (!instance.isInMasterList(p.getName())) {
                        p.sendMessage("§c你还未进入了师傅列表了哦");
                        return false;
                    }

                    instance.quitMasterList(p.getName());
                    p.sendMessage("§a退出成功");
                } else if (args[0].equalsIgnoreCase("end")) {
                    if (instance.hasMaster(p.getName())) {
                        if (p.getLevel() >= instance.getConfig().getInt("Level")) {
                            p.sendMessage("§c等级大于60级不能脱离关系哦");
                            return false;
                        }

                        master = instance.getMyMaster(p.getName());
                        p.sendMessage(instance.getConfig().getString("EndMess1").replace("<player>", master));
                        Player masterP = Bukkit.getPlayerExact(master);
                        if (masterP != null && masterP.isOnline()) {
                            masterP.sendMessage(instance.getConfig().getString("EndMess2"));
                        }

                        instance.setMyMaster(p.getName(), (String)null);
                    } else if (instance.hasApprentice(p.getName())) {
                        master = instance.getMyApprentice(p.getName());
                        instance.setMyMaster(master, (String)null);
                        p.sendMessage("§a成功脱离关系");
                    } else {
                        p.sendMessage("§c你还没有徒弟或者师父");
                    }
                } else if (args[0].equalsIgnoreCase("gift")) {
                    int count = instance.getMyGiftCount(p.getName());
                    if (count > 0) {
                        List<String> commands = instance.getConfig().getStringList("Commands");
                        Iterator var11 = commands.iterator();

                        while(var11.hasNext()) {
                            String command = (String)var11.next();
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", p.getName()));
                        }

                        instance.setMyGiftCount(p.getName(), count - 1);
                    } else {
                        p.sendMessage("§c没有奖励可以领取");
                    }
                }
            }
        }

        return false;
    }
}
