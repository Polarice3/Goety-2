package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class MonsoonCloud extends AbstractSpellCloud{
    public boolean staff = false;

    public MonsoonCloud(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setRainParticle(ParticleTypes.RAIN);
    }

    public MonsoonCloud(Level pLevel, LivingEntity pOwner, LivingEntity pTarget){
        this(ModEntityType.MONSOON_CLOUD.get(), pLevel);
        if (pOwner != null){
            this.setOwner(pOwner);
        }
        if (pTarget != null){
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(pTarget.getX(), pTarget.getY(), pTarget.getZ());

            while(blockpos$mutable.getY() < pTarget.getY() + 4.0D && !this.level.getBlockState(blockpos$mutable).blocksMotion()) {
                blockpos$mutable.move(Direction.UP);
            }
            this.setPos(pTarget.getX(), blockpos$mutable.getY(), pTarget.getZ());
        }
        this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5F, 1.25F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {
        super.readAdditionalSaveData(p_20052_);
        if (p_20052_.contains("staff")) {
            this.staff = p_20052_.getBoolean("staff");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {
        super.addAdditionalSaveData(p_20139_);
        p_20139_.putBoolean("staff", this.staff);
    }

    public void setStaff(boolean staff){
        this.staff = staff;
    }

    public int getColor(){
        return 0x434343;
    }

    public void hurtEntities(LivingEntity livingEntity){
        if (this.level instanceof ServerLevel serverLevel) {
            if (livingEntity != null && !livingEntity.isDeadOrDying() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                if (livingEntity.isSensitiveToWater()) {
                    livingEntity.hurt(livingEntity.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                }
                if (this.random.nextFloat() <= 0.05F) {
                    Vec3 vec3 = this.position();
                    float damage = SpellConfig.ThunderboltDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get();
                    damage += this.extraDamage;
                    BlockHitResult rayTraceResult = this.blockResult(serverLevel, this, 16);
                    Optional<BlockPos> lightningRod = BlockFinder.findLightningRod(serverLevel, BlockPos.containing(rayTraceResult.getLocation()), 16);
                    if (lightningRod.isPresent() && !this.staff) {
                        BlockPos blockPos = lightningRod.get();
                        ModNetwork.sendToALL(new SThunderBoltPacket(vec3, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 10));
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.THUNDERBOLT.get(), this.getSoundSource(), 1.0F, 1.0F);
                    } else {
                        Vec3 vec31 = new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() / 2, livingEntity.getZ());
                        ModNetwork.sendToALL(new SThunderBoltPacket(vec3, vec31, 10));
                        if (livingEntity.hurt(ModDamageSource.indirectShock(this, this.getOwner()), damage)) {
                            float chance = 0.05F;
                            if (serverLevel.random.nextFloat() <= (chance + 0.10F)) {
                                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.TRIPPING.get(), MathHelper.secondsToTicks(5)));
                            }
                        }
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.THUNDERBOLT.get(), this.getSoundSource(), 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    public BlockHitResult blockResult(Level worldIn, Entity entity, double range) {
        float f = entity.getXRot();
        float f1 = entity.getYRot();
        Vec3 vector3d = entity.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return worldIn.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
    }
}
