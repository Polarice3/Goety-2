package com.Polarice3.Goety.common.entities.util;

import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;

public class FireRainTrap extends AbstractTrap {

    public FireRainTrap(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.setParticle(ParticleTypes.ASH);
    }

    public FireRainTrap(Level worldIn, double x, double y, double z) {
        this(ModEntityType.FIRE_RAIN_TRAP.get(), worldIn);
        this.setPos(x, y, z);
    }

    public void tick() {
        super.tick();
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(this.getX(), this.getY(), this.getZ());

        while(blockpos$mutable.getY() < this.getY() + 32.0D && !this.level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
            blockpos$mutable.move(Direction.UP);
        }
        if (this.getOwner() != null) {
            SmallFireball fireballEntity = new SmallFireball(level, this.getOwner(), 0, -900D, 0);
            fireballEntity.setPos(this.getX() + this.random.nextInt(5), blockpos$mutable.getY(), this.getZ() + this.random.nextInt(5));
            level.addFreshEntity(fireballEntity);
        } else {
            this.discard();
        }
        if (this.tickCount >= this.getDuration()) {
            this.discard();
        }
    }

}
