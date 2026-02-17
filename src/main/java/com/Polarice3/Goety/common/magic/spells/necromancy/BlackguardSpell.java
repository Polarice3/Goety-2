package com.Polarice3.Goety.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BlackguardSpell extends SummonSpell {

    public int defaultSoulCost() {
        return SpellConfig.BlackguardCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.BlackguardDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.BlackguardSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return null;
    }

    @Override
    public SoundEvent loopSound(LivingEntity caster) {
        return ModSounds.VANGUARD_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.BlackguardCoolDown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof BlackguardServant;
    }

    @Override
    public int summonLimit() {
        return SpellConfig.BlackguardLimit.get();
    }

    @Override
    public void commonResultHit(ServerLevel worldIn, LivingEntity caster) {
        for (int i = 0; i < caster.level.random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ModParticleTypes.LICH.get(), caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0F, 0.0F, 0.0F, 0);
        }
        this.playSound(worldIn, caster, ModSounds.VANGUARD_SUMMON.get());
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)){
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!isShifting(caster)) {
            if (staff.is(ModItems.NAMELESS_STAFF.get())){
                Vec3 vec3 = caster.position();
                Direction direction = caster.getDirection();
                double stepX = direction.getStepX();
                double stepZ = direction.getStepZ();
                for (int i1 = -3; i1 <= 3; ++i1) {
                    BlackguardServant summonedentity = new BlackguardServant(ModEntityType.BLACKGUARD_SERVANT.get(), worldIn);
                    summonedentity.setTrueOwner(caster);
                    Vec3 vec32 = new Vec3(((2 * stepX) + (i1 * stepZ)) + vec3.x(), vec3.y(), ((2 * stepZ) + (i1 * stepX)) + vec3.z());
                    if (!worldIn.noCollision(summonedentity, summonedentity.getBoundingBox().move(vec32))){
                        vec32 = Vec3.atCenterOf(BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, caster.level));
                    }
                    summonedentity.setPos(vec32);
                    MobUtil.moveDownToGround(summonedentity);
                    summonedentity.setPersistenceRequired();
                    summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                    this.buffSummon(caster, summonedentity, potency);
                    summonedentity.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    summonedentity.setYHeadRot(caster.getYHeadRot());
                    summonedentity.setYRot(caster.getYRot());
                    this.SummonSap(caster, summonedentity);
                    this.setTarget(caster, summonedentity);
                    if (worldIn.addFreshEntity(summonedentity)) {
                        worldIn.sendParticles(ModParticleTypes.LICH.get(), summonedentity.getX(), summonedentity.getY(), summonedentity.getZ(), 1, 0, 0, 0, 0.0F);
                        ServerParticleUtil.summonPowerfulUndeadParticles(worldIn, summonedentity);
                        this.playSound(worldIn, summonedentity, ModSounds.SOUL_EXPLODE.get(), 0.25F + (worldIn.random.nextFloat() / 2.0F), 1.0F);
                        this.playSound(worldIn, summonedentity, SoundEvents.ENDERMAN_TELEPORT, 0.25F + (worldIn.random.nextFloat() / 2.0F), 1.0F);
                    }
                    this.summonAdvancement(caster, summonedentity);
                }
            } else {
                int i = 1;
                if (rightStaff(staff)){
                    i = 2 + caster.level.random.nextInt(6);
                }
                for (int i1 = 0; i1 < i; ++i1) {
                    BlackguardServant summonedentity = new BlackguardServant(ModEntityType.BLACKGUARD_SERVANT.get(), worldIn);
                    summonedentity.setTrueOwner(caster);
                    summonedentity.moveTo(BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn), 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summonedentity);
                    summonedentity.setPersistenceRequired();
                    summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                    this.buffSummon(caster, summonedentity, potency);
                    summonedentity.finalizeSpawn(worldIn, caster.level.getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    this.SummonSap(caster, summonedentity);
                    this.setTarget(caster, summonedentity);
                    if (worldIn.addFreshEntity(summonedentity)) {
                        this.uponSummon(worldIn, caster, staff, summonedentity);
                    }
                    this.summonAdvancement(caster, summonedentity);
                }
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.VANGUARD_SUMMON.get());
        }
    }
}
