package com.Polarice3.Goety.common.entities.projectiles;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

public class ScytheSlash extends AbstractHurtingProjectile {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(ScytheSlash.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(ScytheSlash.class, EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, Goety.location("textures/entity/projectiles/scythe/scythe_0.png"));
        map.put(1, Goety.location("textures/entity/projectiles/scythe/scythe_1.png"));
        map.put(2, Goety.location("textures/entity/projectiles/scythe/scythe_2.png"));
        map.put(3, Goety.location("textures/entity/projectiles/scythe/scythe_3.png"));
        map.put(4, Goety.location("textures/entity/projectiles/scythe/scythe_4.png"));
        map.put(5, Goety.location("textures/entity/projectiles/scythe/scythe_5.png"));
        map.put(6, Goety.location("textures/entity/projectiles/scythe/scythe_6.png"));
        map.put(7, Goety.location("textures/entity/projectiles/scythe/scythe_7.png"));
    });
    private ItemStack weapon = new ItemStack(ModItems.DEATH_SCYTHE.get());
    private float damage;
    private int lifespan;
    private int totallife;

    public ScytheSlash(EntityType<? extends AbstractHurtingProjectile> p_i50173_1_, Level p_i50173_2_) {
        super(p_i50173_1_, p_i50173_2_);
        this.damage = 7.5F;
        this.lifespan = 0;
        this.totallife = 60;
    }

    public ScytheSlash(ItemStack itemStack, Level world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(ModEntityType.SCYTHE.get(), x, y, z, xSpeed, ySpeed, zSpeed, world);
        this.weapon = itemStack;
    }

    public ScytheSlash(Level world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(ModEntityType.SCYTHE.get(), x, y, z, xSpeed, ySpeed, zSpeed, world);
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getAnimation(), TEXTURE_BY_TYPE.get(0));
    }

    public int getAnimation() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public int getTotallife() {
        return totallife;
    }

    public void setTotallife(int totallife) {
        this.totallife = totallife;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    protected void defineSynchedData() {
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(DATA_TYPE_ID, 0);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setOwnerId(uuid);
            } catch (Throwable ignored) {
            }
        }
        this.setAnimation(compound.getInt("Animation"));

        if (compound.contains("Damage")) {
            this.setLifespan(compound.getInt("Damage"));
        }
        if (compound.contains("Lifespan")) {
            this.setLifespan(compound.getInt("Lifespan"));
        }
        if (compound.contains("TotalLife")) {
            this.setTotallife(compound.getInt("TotalLife"));
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        compound.putInt("Animation", this.getAnimation());
        compound.putFloat("Damage", this.getDamage());
        compound.putInt("Lifespan", this.getLifespan());
        compound.putInt("TotalLife", this.getTotallife());
    }

    public LivingEntity getTrueOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public void tick() {
        super.tick();
        if (this.lifespan < getTotallife()){
            ++this.lifespan;
        } else {
            this.discard();
        }
        if (this.getAnimation() < 7) {
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }
        List<Entity> targets = new ArrayList<>();
        for (Entity entity : this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(0.5F))) {
            if (this.getTrueOwner() != null) {
                if (entity != this.getTrueOwner() && !entity.isAlliedTo(this.getTrueOwner()) && !this.getTrueOwner().isAlliedTo(entity) && entity != this.getTrueOwner().getVehicle()) {
                    targets.add(entity);
                }
            } else {
                targets.add(entity);
            }
        }
        if (MainConfig.ScytheSlashBreaks.get()) {
            AABB aabb = this.getBoundingBox().inflate(0.2D);

            for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                BlockState blockstate = this.level.getBlockState(blockpos);
                if (blockstate.is(BlockTags.MINEABLE_WITH_HOE) || BlockFinder.isScytheBreak(blockstate)) {
                    this.level.destroyBlock(blockpos, true, this);
                }
            }
        }
        if (!targets.isEmpty()){
            for (Entity entity: targets){
                if (MobUtil.validEntity(entity)) {
                    float f = this.getDamage();
                    if (this.getTrueOwner() != null) {
                        if (entity instanceof LivingEntity) {
                            f += EnchantmentHelper.getDamageBonus(this.weapon, ((LivingEntity) entity).getMobType());
                        }
                        if (this.getTrueOwner() instanceof Player player) {
                            boolean attack = entity.hurt(DamageSource.playerAttack(player), f);
                            if (entity instanceof EnderDragon enderDragonEntity){
                                attack = enderDragonEntity.hurt(DamageSource.playerAttack(player), f);
                            }
                            if (attack && entity instanceof LivingEntity) {
                                int enchantment = this.weapon.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get());
                                int soulEater = Mth.clamp(enchantment + 1, 1, 10);
                                SEHelper.increaseSouls(player, MainConfig.DarkScytheSouls.get() * soulEater);
                            }
                        } else {
                            entity.hurt(DamageSource.mobAttack(this.getTrueOwner()), f);
                        }
                    } else {
                        entity.hurt(DamageSource.GENERIC, f);
                    }
                }
            }
        }
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
        return ParticleTypes.CRIT;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
