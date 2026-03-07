package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.MagicSmokeParticle;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ScytheSlash extends AbstractHurtingProjectile {
    private ItemStack weapon = new ItemStack(ModItems.DEATH_SCYTHE.get());
    private float damage;
    private int lifespan;
    private int totalLife;

    public TrailEffect leftTrail = new TrailEffect(0.15F, 6.0F);
    public TrailEffect rightTrail = new TrailEffect(0.15F, 6.0F);

    public ScytheSlash(EntityType<? extends AbstractHurtingProjectile> p_i50173_1_, Level p_i50173_2_) {
        super(p_i50173_1_, p_i50173_2_);
        this.damage = 7.5F;
        this.lifespan = 0;
        this.totalLife = 60;
    }

    public ScytheSlash(ItemStack itemStack, Level world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(ModEntityType.SCYTHE.get(), x, y, z, xSpeed, ySpeed, zSpeed, world);
        this.weapon = itemStack;
    }

    public ScytheSlash(Level world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(ModEntityType.SCYTHE.get(), x, y, z, xSpeed, ySpeed, zSpeed, world);
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public int getTotalLife() {
        return totalLife;
    }

    public void setTotalLife(int totalLife) {
        this.totalLife = totalLife;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Damage")) {
            this.setLifespan(compound.getInt("Damage"));
        }
        if (compound.contains("Lifespan")) {
            this.setLifespan(compound.getInt("Lifespan"));
        }
        if (compound.contains("TotalLife")) {
            this.setTotalLife(compound.getInt("TotalLife"));
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", this.getDamage());
        compound.putInt("Lifespan", this.getLifespan());
        compound.putInt("TotalLife", this.getTotalLife());
    }

    public void tick() {
        super.tick();
        ProjectileUtil.rotateTowardsMovement(this, 1.0F);
        if (this.lifespan < getTotalLife()){
            ++this.lifespan;
        } else {
            this.discard();
        }
        if (ItemConfig.ScytheSlashBreaks.get()) {
            AABB aabb = this.getBoundingBox().inflate(0.2D);

            for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                BlockState blockstate = this.level.getBlockState(blockpos);
                if (blockstate.is(BlockTags.MINEABLE_WITH_HOE) || BlockFinder.isScytheBreak(blockstate)) {
                    ItemStack itemStack = this.weapon;
                    if (this.weapon == null || this.weapon.isEmpty()){
                        itemStack = new ItemStack(ModItems.DEATH_SCYTHE.get());
                    }
                    BlockFinder.breakBlock(this.level, blockpos, itemStack, this);
                }
            }
        }
        if (!this.level.isClientSide) {
            List<Entity> targets = new ArrayList<>();
            for (Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.5F))) {
                if (this.getOwner() != null) {
                    if (entity != this.getOwner() && !MobUtil.areAllies(entity, this.getOwner()) && entity != this.getOwner().getVehicle()) {
                        targets.add(entity);
                    }
                } else {
                    targets.add(entity);
                }
            }
            if (!targets.isEmpty()) {
                for (Entity entity : targets) {
                    if (MobUtil.validEntity(entity)) {
                        float f = this.getDamage();
                        if (this.getOwner() != null) {
                            if (entity instanceof LivingEntity) {
                                f += EnchantmentHelper.getDamageBonus(this.weapon, ((LivingEntity) entity).getMobType());
                            }
                            if (this.getOwner() instanceof Player player) {
                                boolean attack = entity.hurt(entity.damageSources().playerAttack(player), f);
                                if (entity instanceof EnderDragon enderDragonEntity) {
                                    attack = enderDragonEntity.hurt(entity.damageSources().playerAttack(player), f);
                                }
                                if (attack && entity instanceof LivingEntity) {
                                    int enchantment = this.weapon.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get());
                                    int soulEater = Mth.clamp(enchantment + 1, 1, 10);
                                    SEHelper.increaseSouls(player, ItemConfig.DarkScytheSouls.get() * soulEater);
                                }
                            } else {
                                DamageSource damageSource = this.getOwner() instanceof LivingEntity livingEntity ? entity.damageSources().mobAttack(livingEntity) : entity.damageSources().thrown(this, this);
                                entity.hurt(damageSource, f);
                            }
                        } else {
                            entity.hurt(entity.damageSources().thrown(this, this), f);
                        }
                    }
                }
            }
        } else if (tickCount > 5) {
            Vec3 oldPos = new Vec3(xOld, yOld, zOld);
            Matrix4f transform = new Matrix4f();
            transform.rotate(Axis.YP.rotationDegrees(-this.getYRot() + 180));
            transform.rotate(Axis.XP.rotationDegrees(this.getXRot()));
            Vector4f left = transform.transform(new Vector4f(1.0F, Mth.sin(tickCount * 0.4F) * 0.2F, 0.0F, 1.0F));
            Vector4f right = transform.transform(new Vector4f(-1.0F, Mth.cos(tickCount * 0.4F) * 0.2F, 0.0F, 1.0F));
            leftTrail.update(oldPos.add(left.x(), left.y(), left.z()));
            rightTrail.update(oldPos.add(right.x(), right.y(), right.z()));
        }
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(20);
    }

    protected void onHitBlock(BlockHitResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
        this.discard();
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    protected ParticleOptions getTrailParticle() {
        return new MagicSmokeParticle.Option(0x6ea8f5, 0x95f5ff, 10, 0.35F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
