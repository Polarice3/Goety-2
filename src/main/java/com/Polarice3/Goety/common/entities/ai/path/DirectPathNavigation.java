package com.Polarice3.Goety.common.entities.ai.path;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;

//Stolen from @alexthe666:https://github.com/AlexModGuy/AlexsMobs/blob/1.20/src/main/java/com/github/alexthe666/alexsmobs/entity/ai/DirectPathNavigator.java
public class DirectPathNavigation extends GroundPathNavigation {
    private final Mob mob;
    private float yMobOffset = 0;

    public DirectPathNavigation(Mob mob, Level world) {
        this(mob, world, 0);
    }

    public DirectPathNavigation(Mob mob, Level world, float yMobOffset) {
        super(mob, world);
        this.mob = mob;
        this.yMobOffset = yMobOffset;
    }

    public void tick() {
        ++this.tick;
    }

    public boolean moveTo(double x, double y, double z, double speedIn) {
        this.mob.getMoveControl().setWantedPosition(x, y, z, speedIn);
        return true;
    }

    public boolean moveTo(Entity entityIn, double speedIn) {
        this.mob.getMoveControl().setWantedPosition(entityIn.getX(), entityIn.getY() + yMobOffset, entityIn.getZ(), speedIn);
        return true;
    }
}
