package fr.cocoraid.prodigyrope;


import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class NMS {

    public void setLeashHolder(LivingEntity entity, Entity holder, boolean flag) {


        net.minecraft.server.v1_15_R1.EntityLiving living = ((CraftLivingEntity)entity).getHandle();
        net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity)entity).getHandle();



    }

}
