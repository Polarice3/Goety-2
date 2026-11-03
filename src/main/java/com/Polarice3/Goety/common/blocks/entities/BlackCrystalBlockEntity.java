package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.api.blocks.IEnchantedBlock;
import com.Polarice3.Goety.client.particles.AbsorbTrailParticleOption;
import com.Polarice3.Goety.client.particles.GatherTrailParticleOption;
import com.Polarice3.Goety.client.particles.SpirallingParticleOption;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BlackCrystalBlockEntity extends OwnedBlockEntity implements IEnchantedBlock {
    protected final Object2IntMap<Enchantment> enchantments = new Object2IntOpenHashMap<>();
    public static ColorUtil COLOR = new ColorUtil(0x5038dd);
    public int tickCount;
    public int attackTick;
    public boolean isAttacking;
    @Nullable
    private LivingEntity target;

    public BlackCrystalBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.BLACK_CRYSTAL.get(), p_155229_, p_155230_);
    }

    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        if (tag.contains("IsAttacking")) {
            this.isAttacking = tag.getBoolean("IsAttacking");
        }
        this.loadEnchants(tag);
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        this.saveEnchants(tag, ModBlocks.BLACK_CRYSTAL.get().asItem());
        tag.putBoolean("IsAttacking", this.isAttacking);
        return super.writeNetwork(tag);
    }

    public Object2IntMap<Enchantment> getEnchantments(){
        return this.enchantments;
    }

    public void tick() {
        if (this.level != null) {
            ++this.tickCount;
            if (this.level instanceof ServerLevel serverLevel) {
                int radius = 8 + this.getEnchantments().getOrDefault(ModEnchantments.RADIUS.get(), 0);
                if (this.target == null) {
                    for (LivingEntity livingEntity : serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(this.worldPosition).inflate(radius * 2))) {
                        if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity) && livingEntity.distanceToSqr(this.getBlockPos().getCenter()) <= Mth.square(radius)) {
                            boolean flag = livingEntity.getHealth() >= livingEntity.getMaxHealth() * 0.25F;
                            if (this.getTrueOwner() != null) {
                                flag &= !MobUtil.areAllies(this.getTrueOwner(), livingEntity);
                            } else {
                                flag &= !this.isAllyWith(livingEntity);
                            }
                            if (flag) {
                                this.target = livingEntity;
                            }
                        }
                    }
                } else {
                    if (!this.target.isAlive() || this.target.getHealth() < this.target.getMaxHealth() * 0.25F || this.target.distanceToSqr(this.getBlockPos().getCenter()) > Mth.square(radius)) {
                        this.attackTick = 0;
                        if (this.isAttacking) {
                            this.isAttacking = false;
                            this.level.playSound(null, this.worldPosition, ModSounds.SOUL_LEECH_END.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                        this.target = null;
                    } else {
                        ColorUtil colorUtil1 = COLOR;
                        Vec3 vector3d1 = this.getBlockPos().getCenter();
                        if (!this.isAttacking) {
                            this.isAttacking = true;
                            this.level.playSound(null, this.worldPosition, ModSounds.SOUL_LEECH_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        } else {
                            Vec3 vec3 = new Vec3(this.target.getX(), this.target.getY() + (this.target.getBbHeight() / 2.0F), this.target.getZ());
                            serverLevel.sendParticles(new GatherTrailParticleOption(colorUtil1, vector3d1), vec3.x, vec3.y, vec3.z, 0, 0.0F, 0.0F, 0.0F, 0.5F);
                            for (int i = 0; i < 8; ++i) {
                                Vec3 vec31 = new Vec3(this.target.getRandomX(1.0F), this.target.getRandomY(), this.target.getRandomZ(1.0F));
                                serverLevel.sendParticles(new AbsorbTrailParticleOption(vector3d1, 0xceb0ff, 0x5038dd, 10), vec31.x, vec31.y, vec31.z, 1, 0.0, 0.0, 0.0, 0.0);
                            }
                        }
                        if (this.attackTick % 58 == 0) {
                            this.level.playSound(null, this.worldPosition, ModSounds.SOUL_LEECH_LOOP_1.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
                            this.level.playSound(null, this.worldPosition, ModSounds.SOUL_LEECH_LOOP_2.get(), SoundSource.BLOCKS, 0.75F, 1.0F);
                        }
                        ++this.attackTick;
                        if (this.attackTick % 20 == 0) {
                            DamageSource damageSource = this.getTrueOwner() != null ? ModDamageSource.soulLeech(this.getTrueOwner(), this.getTrueOwner()) : this.level.damageSources().magic();
                            this.target.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 10, 0, false, false));
                            if (this.target.hurt(damageSource, this.target.getMaxHealth() * 0.1F)) {
                                serverLevel.sendParticles(new SpirallingParticleOption(1.0F, colorUtil1.red, colorUtil1.green, colorUtil1.blue, 5), vector3d1.x, vector3d1.y, vector3d1.z, 1, 0.0D, 0.0D, 0.0D, 1.0D);
                                if (this.getTrueOwner() instanceof Player player) {
                                    if (SEHelper.getSoulGiven(this.target) > 0) {
                                        int enchantment = this.getEnchantments().getOrDefault(ModEnchantments.SOUL_EATER.get(), 0);
                                        int soulEater = Mth.clamp(enchantment + 1, 1, 10);
                                        SEHelper.increaseSouls(player, MainConfig.BlackCrystalSouls.get() * soulEater);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
