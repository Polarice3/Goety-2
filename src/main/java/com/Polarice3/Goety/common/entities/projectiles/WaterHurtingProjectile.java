package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class WaterHurtingProjectile extends AbstractHurtingProjectile implements ISpellEntity {

    protected WaterHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public WaterHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> entityType, double x, double y, double z, double xPower, double yPower, double zPower, Level level) {
        super(entityType, x, y, z, xPower, yPower, zPower, level);
    }

    public WaterHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> entityType, LivingEntity shooter, double xPower, double yPower, double zPower, Level level) {
        super(entityType, shooter, xPower, yPower, zPower, level);
    }

    public boolean isAffectedByWater(){
        return false;
    }

    public void tick() {
        Entity entity = this.getOwner();
        if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.isLoaded(this.blockPosition())) {
            if (!this.hasBeenShot) {
                this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
                this.hasBeenShot = true;
            }

            if (!this.leftOwner) {
                this.leftOwner = this.checkLeftOwner();
            }
            this.baseTick();
            if (this.shouldBurn()) {
                this.setSecondsOnFire(1);
            }

            this.hitDetection();

            this.checkInsideBlocks();
            this.travel();
            this.trailParticle();
        } else {
            this.discard();
        }
    }

    /**
     * Stole these methods from @Iron:<a href="https://github.com/iron431/irons-spells-n-spellbooks/blob/1.20.1/src/main/java/io/redspace/ironsspellbooks/entity/spells/AbstractMagicProjectile.java">...</a>
     * From here
     */
    public void shoot(Vec3 rotation) {
        this.setDeltaMovement(rotation.scale(this.getInertia()));
    }

    public void hitDetection(){
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
    }

    public void travel(){
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        float f = this.getInertia();
        if (this.isInWater()) {
            if (this.isAffectedByWater()) {
                f = 0.8F;
            }
        }

        this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale(f));
        this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, this.getGravity(), 0.0D));
        this.setPos(d0, d1, d2);
    }
    /**
     * To here
     */

    public void trailParticle(){
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                float f1 = 0.25F;
                this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * f1, d1 - vec3.y * f1, d2 - vec3.z * f1, vec3.x, vec3.y, vec3.z);
            }
        }
        this.level.addParticle(this.getTrailParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
    }

    public float getGravity(){
        return 0.0F;
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for(Entity entity1 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_37272_) -> !p_37272_.isSpectator() && p_37272_.isPickable())) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }
}
