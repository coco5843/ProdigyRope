package fr.cocoraid.prodigyrope;

import net.minecraft.server.v1_15_R1.EntitySlime;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSlime;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProdigyRope extends JavaPlugin implements Listener {


    private Class<?> insentient = Reflection.getMinecraftClass("EntityInsentient");
    private Reflection.MethodInvoker getHandleMethod = Reflection.getMethod(Reflection.getCraftBukkitClass("entity.CraftSlime"),"getHandle");
    private Reflection.MethodInvoker setInvisibleMethod = Reflection.getMethod(Reflection.getMinecraftClass("EntityLiving"),"setInvisible",boolean.class);
    private Reflection.MethodInvoker setLeashHolderMethod = Reflection.getMethod(insentient,"setLeashHolder",Reflection.getMinecraftClass("Entity"),boolean.class);

    private ProdigyRope instance;
    @Override
    public void onEnable() {

        instance = this;
        Bukkit.getPluginManager().registerEvents(this,this);
    }


    @Override
    public void onDisable() {

    }

    private class TempRope {

        private BukkitTask task;
        private Slime a;
        private Slime b;
        private boolean next;
        private Player p;


        private TempRope(Player p) {
            this.p = p;
            a = p.getWorld().spawn(p.getLocation(),Slime.class);
            b = p.getWorld().spawn(p.getLocation().add(0,2,0),Slime.class);

            a.setSize(1);
            a.setAI(false);
            b.setSize(1);
            b.setAI(false);
            a.setRemoveWhenFarAway(false);
            b.setRemoveWhenFarAway(false);
            a.setInvulnerable(true);
            b.setInvulnerable(true);

            Object nms1 = getHandleMethod.invoke(a);
            Object nms2 = getHandleMethod.invoke(b);
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    setInvisibleMethod.invoke(nms1,true);
                    setInvisibleMethod.invoke(nms2,true);

                }
            }.runTaskLater(instance,2);
            setLeashHolderMethod.invoke(nms1,nms2,true);
        }

        public void updatePlacing(Location loc) {
            Location l = loc.add(loc.getDirection().multiply(2));
            if(next) {
                b.teleport(l);
            } else {
                a.teleport(l.clone().subtract(0,0.7,0));
                b.teleport(l.clone().add(0,6,0));
            }
        }

        public boolean next() {
            if(next) {
                return false;
            }
            this.next = true;
            return true;
        }

        public void reset() {
            if(a != null) a.remove();
            if(b != null) b.remove();
        }

        public void end() {
            if(task != null)
                task.cancel();
        }

        public void setTask(BukkitTask task) {
            this.task = task;
        }
        public BukkitTask getTask() {
            return task;
        }

    }


    private Map<UUID, TempRope> placers = new HashMap<>();


    private void startPlacing(Player player) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.isOnline() || !placers.containsKey(player.getUniqueId())) {
                    this.cancel();
                    //remove slimes
                    placers.get(player.getUniqueId()).reset();
                    placers.remove(player.getUniqueId());
                    return;
                }
                placers.get(player.getUniqueId()).updatePlacing(player.getEyeLocation());
            }
        }.runTaskTimer(this,0,0);
        placers.get(player.getUniqueId()).setTask(task);
    }


    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Slime) {
            if (placers.containsKey(e.getDamager().getUniqueId()))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e) {
        if(placers.containsKey(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }


    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            if(placers.containsKey(e.getPlayer().getUniqueId())) {
                if(placers.get(e.getPlayer().getUniqueId()).getTask() != null) {
                    if(!placers.get(e.getPlayer().getUniqueId()).next()) {
                        //end
                        placers.get(e.getPlayer().getUniqueId()).end();
                        placers.remove(e.getPlayer().getUniqueId());
                        e.setCancelled(true);
                    }
                    return;
                }
            }
        }
    }


    @EventHandler
    public void hang(EntityUnleashEvent e) {
        //pretty rare to get 2 slimes attached lol
        if(e.getEntity() instanceof Slime && ((Slime) e.getEntity()).getLeashHolder() instanceof Slime) {
            if(!e.getEntity().isDead())
                e.getEntity().remove();
            if(!((Slime) e.getEntity()).getLeashHolder().isDead()) {
                ((Slime) e.getEntity()).getLeashHolder().remove();
            }
        }

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("prodigyrope.use")) {
                if (placers.containsKey(p.getUniqueId())) {
                    p.sendMessage("§cYou are already placing ropes !");
                    return false;
                }

                placers.put(p.getUniqueId(), new TempRope(p));
                startPlacing(p);
            }
        }
        return true;
    }
}
