package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IHeretic;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.util.FireBlastTrap;
import com.Polarice3.Goety.common.items.magic.InfernalTome;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

/**
 * Based on SonarWave code from Upgrade Aquatic:<a href="https://github.com/team-abnormals/upgrade-aquatic/blob/1.19.x/src/main/java/com/teamabnormals/upgrade_aquatic/common/entity/projectile/SonarWave.java">...</a>;
 */
public class HellChant extends SpellEntity{
    private static final EntityDataAccessor<Float> Y_ROT_VISUAL = SynchedEntityData.defineId(HellChant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> X_ROT_VISUAL = SynchedEntityData.defineId(HellChant.class, EntityDataSerializers.FLOAT);
    private float growProgress = 0;
    private float prevGrowProgress = 0;
    private int burning = 0;

    public HellChant(EntityType<? extends HellChant> type, Level worldIn) {
        super(type, worldIn);
        this.blocksBuilding = true;
    }

    public HellChant(Level worldIn, double x, double y, double z) {
        this(ModEntityType.HELL_CHANT.get(), worldIn);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public HellChant(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(ModEntityType.HELL_CHANT.get(), world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Y_ROT_VISUAL, 0.0F);
        this.entityData.define(X_ROT_VISUAL, 0.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("VisualYRot")) {
            this.setYRotVisual(compound.getFloat("VisualYRot"));
            this.setXRotVisual(compound.getFloat("VisualXRot"));
            this.setYRot(compound.getFloat("VisualYRot"));
            this.setXRot(compound.getFloat("VisualXRot"));
        }
        this.growProgress = compound.getFloat("GrowProgress");
        this.burning = compound.getInt("Burning");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("VisualYRot", this.getYRotVisual());
        compound.putFloat("VisualXRot", this.getXRotVisual());
        compound.putFloat("GrowProgress", this.growProgress);
        compound.putInt("Burning", this.burning);
    }

    public void setBurning(int burning){
        this.burning = burning;
    }

    public int getBurning(){
        return this.burning;
    }

    public float getYRotVisual() {
        return this.entityData.get(Y_ROT_VISUAL);
    }

    public void setYRotVisual(float yRot) {
        this.entityData.set(Y_ROT_VISUAL, yRot);
    }

    public float getXRotVisual() {
        return this.entityData.get(X_ROT_VISUAL);
    }

    public void setXRotVisual(float xRot) {
        this.entityData.set(X_ROT_VISUAL, xRot);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        LivingEntity owner = this.getOwner();

        this.move(MoverType.SELF, this.getDeltaMovement());

        for (Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(this.growProgress), entity -> entity != this && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity))) {
            if (owner instanceof Mob mob && mob instanceof IHeretic heretic) {
                if (!MobUtil.areAllies(mob, entity)) {
                    if (entity == mob.getTarget() || MobUtil.getOwner(entity) == mob.getTarget() || entity instanceof Mob mob1 && mob1.getTarget() == mob) {
                        heretic.setChantTimes(heretic.getChantTimes() + 1);
                        if (heretic.getChantTimes() == 3) {
                            Vec3 vec3 = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                            FireBlastTrap fireBlastTrap = new FireBlastTrap(owner.level, vec3.x, vec3.y + 0.25D, vec3.z);
                            MobUtil.moveDownToGround(fireBlastTrap);
                            fireBlastTrap.setOwner(owner);
                            fireBlastTrap.setAreaOfEffect(2.0F);
                            fireBlastTrap.setExtraDamage(this.getExtraDamage());
                            fireBlastTrap.setBurning(this.getBurning());
                            this.level.addFreshEntity(fireBlastTrap);
                        }
                    }
                }
            } else if (owner != null && owner.getUseItem().getItem() instanceof InfernalTome){
                if (!MobUtil.areAllies(owner, entity) && entity.isAttackable()) {
                    ItemStack itemStack = owner.getUseItem();
                    CompoundTag compoundTag = itemStack.getTag();
                    if (compoundTag != null){
                        if (compoundTag.contains(InfernalTome.CHANT_TIMES)){
                            InfernalTome.increaseChantTimes(itemStack);
                            if (InfernalTome.getChantTimes(itemStack) == 3){
                                Vec3 vec3 = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                                FireBlastTrap fireBlastTrap = new FireBlastTrap(owner.level, vec3.x, vec3.y + 0.25D, vec3.z);
                                MobUtil.moveDownToGround(fireBlastTrap);
                                fireBlastTrap.setOwner(owner);
                                fireBlastTrap.setAreaOfEffect(2.0F);
                                fireBlastTrap.setExtraDamage(this.getExtraDamage());
                                fireBlastTrap.setBurning(this.getBurning());
                                this.level.addFreshEntity(fireBlastTrap);
                            }
                        }
                    }
                }
            }
        }

        Vec3 motion = this.getDeltaMovement();
        float horizontalMotionMagnitude = Mth.sqrt((float) motion.horizontalDistanceSqr());
        double motionX = motion.x();
        double motionY = motion.y();
        double motionZ = motion.z();

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            this.setYRot((float) (Mth.atan2(motionX, motionZ) * (double) (180F / (float) Math.PI)));
            this.setXRot((float) (Mth.atan2(motionY, horizontalMotionMagnitude) * (double) (180F / (float) Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        this.setYRot((float) (Mth.atan2(motionX, motionZ) * (double) (180F / (float) Math.PI)));

        for (this.setXRot((float) (Mth.atan2(motionY, horizontalMotionMagnitude) * (double) (180F / (float) Math.PI))); this.getXRot() - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
        }

        while (this.getXRot() - this.xRotO >= 180.0F) {
            this.xRotO += 360.0F;
        }

        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }

        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }

        float yRot = Mth.lerp(0.2F, this.yRotO, this.getYRot());
        float xRot = Mth.lerp(0.2F, this.xRotO, this.getXRot());
        this.setYRot(yRot);
        this.setXRot(xRot);
        if (!this.level.isClientSide) {
            this.setYRotVisual(this.getYRot());
            this.setXRotVisual(this.getXRot());
        }
        this.prevGrowProgress = this.growProgress;

        if (this.growProgress < 0.1F) {
            this.growProgress += 0.025F;
        } else {
            this.growProgress += 0.1F;
        }

        if (this.tickCount > 40) {
            this.discard();
        }
    }

    @Override
    protected void moveTowardsClosestSpace(double x, double y, double z) {
    }

    public void chant(LivingEntity owner) {
        float xMotion = -Mth.sin(owner.getYRot() * ((float) Math.PI / 180F)) * Mth.cos(owner.getXRot() * ((float) Math.PI / 180F));
        float yMotion = -Mth.sin(owner.getXRot() * ((float) Math.PI / 180F));
        float zMotion = Mth.cos(owner.getYRot() * ((float) Math.PI / 180F)) * Mth.cos(owner.getXRot() * ((float) Math.PI / 180F));

        Vec3 motion = new Vec3(xMotion, yMotion, zMotion).normalize().scale(0.75D);

        this.setDeltaMovement(motion.x, motion.y, motion.z);
        this.setOwner(owner);
        this.setPos(owner.getX() + xMotion, owner.getEyeY() - 0.5F, owner.getZ() + zMotion);

        float motionSqrt = Mth.sqrt((float) motion.horizontalDistanceSqr());
        this.setYRot((float) (Mth.atan2(motion.x, motion.z) * (180F / Math.PI)));
        this.setYRot((float) (Mth.atan2(motion.y, motionSqrt) * (180F / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.setYRotVisual(this.getYRot());
        this.setXRotVisual(this.getXRot());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public float getGrowProgress(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevGrowProgress, this.growProgress);
    }
}
