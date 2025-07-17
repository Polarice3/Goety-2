package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class MiniElectroOrb extends SpellHurtingProjectile{
    public boolean staff = false;

    public MiniElectroOrb(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public MiniElectroOrb(double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
        super(ModEntityType.MINI_ELECTRO_ORB.get(), p_36818_, p_36819_, p_36820_, p_36821_, p_36822_, p_36823_, p_36824_);
    }

    public MiniElectroOrb(LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
        super(ModEntityType.MINI_ELECTRO_ORB.get(), p_36827_, p_36828_, p_36829_, p_36830_, p_36831_);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("staff")) {
            this.staff = compound.getBoolean("staff");
        }
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("staff", this.isStaff());
    }

    public void setStaff(boolean staff){
        this.staff = staff;
    }

    public boolean isStaff(){
        return this.staff;
    }

    protected void onHit(HitResult hitResult) {
        if (!this.level.isClientSide) {
            DamageSource damageSource = ModDamageSource.getDamageSource(this.level, ModDamageSource.SHOCK);
            float damage = SpellConfig.ElectroOrbDamage.get().floatValue() * SpellConfig.SpellDamageMultiplier.get().floatValue();
            if (this.getOwner() != null) {
                damageSource = ModDamageSource.indirectShock(this, this.getOwner());
            }
            damage += this.getExtraDamage();
            if (hitResult instanceof EntityHitResult result) {
                Entity entity = result.getEntity();
                entity.hurt(damageSource, damage);
                float chance = this.isStaff() ? 0.25F : 0.05F;
                if (this.level.isThundering() && this.level.isRainingAt(entity.blockPosition())) {
                    chance += 0.25F;
                }
                if (entity instanceof LivingEntity livingEntity && this.level.random.nextFloat() <= chance) {
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)));
                }
            }
            this.playSound(ModSounds.THUNDERBOLT.get(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.discard();
        }
    }

    public boolean isOnFire() {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
