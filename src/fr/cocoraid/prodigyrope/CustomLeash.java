package fr.cocoraid.prodigyrope;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class CustomLeash extends EntityLeash {


    private Location loc;
    public CustomLeash(Location loc) {
        super(EntityTypes.LEASH_KNOT,((CraftWorld)loc.getWorld()).getHandle());
        this.loc = loc;
        setPosition(loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D);
        a(new AxisAlignedBB(locX() - 0.1875D, locY() - 0.25D + 0.125D, locZ() - 0.1875D, locX() + 0.1875D, locY() + 0.25D + 0.125D, locZ() + 0.1875D));
    }

    public  void spawn() {
        setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld)loc.getWorld()).getHandle().addEntity(this);
    }

    public void attach(LivingEntity entity) {
        EntityInsentient nmsEntity = (EntityInsentient)((CraftLivingEntity)entity).getHandle();
        nmsEntity.setLeashHolder(this, true);

    }

    @Override
    public void tick() {

    }
}
