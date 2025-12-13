package com.Polarice3.Goety.common.magic.spells.nether;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MeteorShowerSpell extends EverChargeSpell {

    @Override
    public int defaultSoulCost() {
        return SpellConfig.MeteorShowerCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.MeteorShowerChargeUp.get();
    }

    @Override
    public int Cooldown() {
        return SpellConfig.MeteorShowerDuration.get();
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        int i = 0;
        if (WandUtil.enchantedFocus(caster)) {
            i += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        return SpellConfig.MeteorShowerShots.get() + (i * 2);
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.MeteorShowerCoolDown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound(LivingEntity caster) {
        if (CuriosFinder.hasUnholySet(caster)){
            return ModSounds.APOSTLE_PREPARE_SPELL.get();
        }
        return SoundEvents.LIGHTNING_BOLT_THUNDER;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.BURNING.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int burning = spellStat.getBurning();
        int range = spellStat.getRange();
        float radius = (float) spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
            range += WandUtil.getRangeLevel(caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
        }
        HitResult rayTrace = this.rayTrace(worldIn, caster, range, 3.0D);
        Vec3 location = rayTrace.getLocation();
        LivingEntity target = this.getTarget(caster, range);
        if (target != null){
            location = target.position();
        }
        BlockPos mutableBlockPos = new BlockPos.MutableBlockPos(location.x, location.y + 1.5D, location.z);
        int maxHeight = 15;
        int i = 0;
        while (mutableBlockPos.getY() < worldIn.getMaxBuildHeight() && worldIn.isEmptyBlock(mutableBlockPos) && i < maxHeight){
            mutableBlockPos = mutableBlockPos.above();
            i++;
        }
        if (rightStaff(staff)){
            potency += 2;
        }
        for (int j = 0; j < potency + 1; ++j){
            float xZ = 24.0F;
            float y = 4.0F;
            Vec3 vec3 = mutableBlockPos.getCenter()
                    .add(worldIn.random.nextFloat() * xZ - (xZ / 2.0F),
                            worldIn.random.nextFloat() * y - (y / 2.0F),
                            worldIn.random.nextFloat() * xZ - (xZ / 2.0F));
            int clearTries = 0;
            while (clearTries < 6 && !worldIn.isEmptyBlock(BlockPos.containing(vec3)) && worldIn.getFluidState(BlockPos.containing(vec3)).isEmpty()){
                clearTries++;
                vec3 = mutableBlockPos.getCenter()
                        .add(worldIn.random.nextFloat() * xZ - (xZ / 2.0F),
                                worldIn.random.nextFloat() * y - (y / 2.0F),
                                worldIn.random.nextFloat() * xZ - (xZ / 2.0F));
            }
            if (!worldIn.isEmptyBlock(BlockPos.containing(vec3)) && worldIn.getFluidState(BlockPos.containing(vec3)).isEmpty()){
                vec3 = mutableBlockPos.getCenter();
            }
            Vec3 vec30 = vec3.offsetRandom(worldIn.getRandom(), 1.5F);
            worldIn.sendParticles(new FoggyCloudParticleOption(new ColorUtil(0x1a090d), 3.0F, 1), vec30.x(), vec30.y(), vec30.z(), 1, 0, 0, 0, 0);
            worldIn.sendParticles(new FoggyCloudParticleOption(new ColorUtil(0x240d12), 3.0F, 1), vec3.x(), vec3.y(), vec3.z(), 1, 0, 0, 0, 0);
            Vec3 vec31 = location.subtract(vec3);
            AbstractHurtingProjectile fireball = new Lavaball(worldIn,
                    vec3.x,
                    vec3.y,
                    vec3.z,
                    vec31.x,
                    vec31.y,
                    vec31.z);
            if (CuriosFinder.hasUnholySet(caster)){
                fireball = new HellBlast(vec3.x,
                        vec3.y,
                        vec3.z,
                        vec31.x,
                        vec31.y,
                        vec31.z, worldIn);
            }
            fireball.setOwner(caster);
            fireball.setPos(vec3);
            if (fireball instanceof Lavaball lavaball) {
                lavaball.setUpgraded(true);
                if (isShifting(caster)) {
                    lavaball.setDangerous(false);
                }
                lavaball.setExtraDamage(potency);
                lavaball.setFiery(burning);
                lavaball.setExplosionPower(lavaball.getExplosionPower() + radius);
            } else if (fireball instanceof HellBlast hellBlast){
                hellBlast.setExtraDamage(potency);
                hellBlast.setRadius(hellBlast.getRadius() + radius);
                hellBlast.setFiery(burning);
            }
            if (worldIn.addFreshEntity(fireball)){
                SoundEvent soundEvent = SoundEvents.GHAST_SHOOT;
                if (fireball instanceof HellBlast){
                    soundEvent = ModSounds.HELL_BLAST_SHOOT.get();
                }
                this.playSound(worldIn, fireball, soundEvent, 2.0F, this.projPitch(worldIn.getRandom()));
            }
        }
    }
}
