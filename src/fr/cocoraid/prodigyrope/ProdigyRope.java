package fr.cocoraid.prodigyrope;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ProdigyRope extends JavaPlugin {


    @Override
    public void onEnable() {

    }


    @Override
    public void onDisable() {

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            CustomLeash l = new CustomLeash(p.getLocation());
            l.spawn();

            Slime s = p.getWorld().spawn(p.getLocation().add(0,10,0),Slime.class);
            s.setAI(false);
            s.setSize(0);
            l.attach(s);

            //LeashHitch hitch = p.getWorld().spawn(p.getLocation(),LeashHitch.class);
            //b.setLeashHolder(hitch);


        }
        return true;
    }
}
