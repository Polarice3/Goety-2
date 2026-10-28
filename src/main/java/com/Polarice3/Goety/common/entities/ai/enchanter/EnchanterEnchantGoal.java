package com.Polarice3.Goety.common.entities.ai.enchanter;

import com.Polarice3.Goety.api.entities.IEnchanter;
import com.Polarice3.Goety.client.particles.MagicSmokeParticleOption;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class EnchanterEnchantGoal<T extends PathfinderMob & IEnchanter> extends EnchanterGoal<T> {
    public static final ColorUtil OURPLE = new ColorUtil(0x9f34cd);
    public int castTime;

    public EnchanterEnchantGoal(T enchanter) {
        super(enchanter);
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.enchanter.hurtTime <= 0 && this.enchanter.getEnchantCool() <= 0) {
            if (this.enchanter.getEnchantList().size() < 2) {
                if (!this.getAllies().isEmpty()) {
                    if (this.enchanter.getTarget() != null) {
                        return true;
                    } else {
                        List<LivingEntity> hurtAllies = this.getAllies().stream().filter(livingEntity -> (livingEntity.getLastHurtByMob() != null && !MobUtil.areAllies(livingEntity.getLastHurtByMob(), this.enchanter)) || (livingEntity instanceof Mob mob && mob.getTarget() != null && mob.getTarget().isAlive())).toList();
                        return !hurtAllies.isEmpty();
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.enchanter.hurtTime <= 0 && this.castTime < 83;
    }

    @Override
    public void start() {
        super.start();
        this.castTime = 0;
        this.enchanter.getNavigation().stop();
        this.enchanter.setAnimationState(IEnchanter.ENCHANT);
    }

    @Override
    public void stop() {
        super.stop();
        this.castTime = 0;
        this.enchanter.setEnchantCool(200);
        this.enchanter.setShowArms(false);
        this.enchanter.setAnimationState(IEnchanter.IDLE);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.castTime;
        this.enchanter.getNavigation().stop();
        this.enchanter.getMoveControl().strafe(0, 0);
        this.enchanter.setShowArms(this.castTime >= 4 && this.castTime < 62);
        if (this.castTime == 3) {
            this.enchanter.playSound(ModSounds.BOOK_THROW.get(), 1.0F, 0.7F);
        }
        if (this.castTime == 9) {
            this.enchanter.playSound(ModSounds.BOOK_BLADDER.get());
        }
        if (this.castTime == 34) {
            List<LivingEntity> list = this.getAllies();
            if (!list.isEmpty()) {
                list.sort(Comparator.comparingDouble(this.enchanter::distanceToSqr));
                int i = 0;
                for (LivingEntity livingEntity : list) {
                    if (i < 2) {
                        this.enchanter.getEnchantList().add(livingEntity.getUUID());
                        ++i;
                    } else {
                        break;
                    }
                }
                if (i > 0) {
                    this.enchanter.playSound(ModSounds.ENCHANTER_BEAM.get(), 1.0F, 0.8F);
                }
            }
        }
        if (this.castTime >= 34 && this.castTime < 49) {
            if (this.enchanter.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new MagicSmokeParticleOption(0xffecff, 0x9f34cd, 10 + this.enchanter.level.getRandom().nextInt(10), 0.2F), this.enchanter.getRandomX(0.5D), this.enchanter.getRandomY(), this.enchanter.getRandomZ(0.5D), 0, 0.0F, 0.0F, 0.0F, 1.0F);
            }
        }
        if (this.castTime == 35) {
            if (this.enchanter.level instanceof ServerLevel serverLevel) {
                ServerParticleUtil.sendGodRay(serverLevel, this.enchanter.getX(), this.enchanter.getY(), this.enchanter.getZ(), OURPLE);
            }
            this.enchanter.playSound(ModSounds.BOOK.get());
            this.enchanter.playSound(ModSounds.ENCHANTER_SPELL.get(), 1.0F, 0.9F);
        }
        if (this.castTime == 49) {
            this.enchanter.playSound(ModSounds.BOOK_CLOSE.get());
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
