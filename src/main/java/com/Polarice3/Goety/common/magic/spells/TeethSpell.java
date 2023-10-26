package com.Polarice3.Goety.common.magic.spells;

import com.Polarice3.Goety.SpellConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.ViciousTooth;
import com.Polarice3.Goety.common.magic.Spells;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.*;

public class TeethSpell extends Spells {

    public int SoulCost() {
        return SpellConfig.TeethCost.get();
    }

    public int CastDuration() {
        return SpellConfig.TeethDuration.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    public void RegularResult(ServerLevel worldIn, LivingEntity entityLiving){
        Player playerEntity = (Player) entityLiving;
        int range = 16;
        double radius = 2.0D;
        float damage = 0.0F;
        if (WandUtil.enchantedFocus(entityLiving)){
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, playerEntity, range, radius);
        BlockPos blockPos = entityLiving.blockPosition();
        if (this.isShifting(entityLiving)){
            blockPos = entityLiving.blockPosition();
        } else if (rayTraceResult instanceof EntityHitResult){
            Entity target = ((EntityHitResult) rayTraceResult).getEntity();
            blockPos = target.blockPosition();
        } else if (rayTraceResult instanceof BlockHitResult) {
            blockPos = ((BlockHitResult) rayTraceResult).getBlockPos().above();
        }
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        while(blockpos$mutable.getY() < blockPos.getY() + 8.0D && !worldIn.getBlockState(blockpos$mutable).blocksMotion()) {
            blockpos$mutable.move(Direction.UP);
        }
        if (worldIn.noCollision(new AABB(blockpos$mutable))){
            ViciousTooth viciousTooth = new ViciousTooth(ModEntityType.VICIOUS_TOOTH.get(), worldIn);
            viciousTooth.setPos(Vec3.atCenterOf(blockpos$mutable));
            viciousTooth.setOwner(entityLiving);
            viciousTooth.setExtraDamage(damage);
            if (worldIn.addFreshEntity(viciousTooth)) {
                viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
    }

    public void StaffResult(ServerLevel worldIn, LivingEntity entityLiving){
        Player playerEntity = (Player) entityLiving;
        int range = 16;
        double radius = 2.0D;
        float damage = 0.0F;
        if (WandUtil.enchantedFocus(entityLiving)){
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), entityLiving);
            damage += WandUtil.getLevels(ModEnchantments.POTENCY.get(), entityLiving);
        }
        HitResult rayTraceResult = this.rayTrace(worldIn, playerEntity, range, radius);
        BlockPos blockPos = entityLiving.blockPosition();
        if (this.isShifting(entityLiving)){
            this.surroundTeeth(entityLiving, blockPos, damage, true);
        } else {
            if (rayTraceResult instanceof EntityHitResult){
                Entity target = ((EntityHitResult) rayTraceResult).getEntity();
                blockPos = target.blockPosition();
            } else if (rayTraceResult instanceof BlockHitResult) {
                blockPos = ((BlockHitResult) rayTraceResult).getBlockPos().above();
            }
            for (int length = 0; length < 16; length++) {
                blockPos = blockPos.offset(-2 + entityLiving.getRandom().nextInt(4), 0, -2 + entityLiving.getRandom().nextInt(4));
                BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                while(blockpos$mutable.getY() < blockPos.getY() + 8.0D && !worldIn.getBlockState(blockpos$mutable).blocksMotion()) {
                    blockpos$mutable.move(Direction.UP);
                }

                if (worldIn.noCollision(new AABB(blockpos$mutable))){
                    ViciousTooth viciousTooth = new ViciousTooth(ModEntityType.VICIOUS_TOOTH.get(), worldIn);
                    viciousTooth.setPos(Vec3.atCenterOf(blockpos$mutable));
                    viciousTooth.setOwner(entityLiving);
                    viciousTooth.setExtraDamage(damage);
                    if (worldIn.addFreshEntity(viciousTooth)) {
                        viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                    }
                }
            }
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.EVOKER_CAST_SPELL, this.getSoundSource(), 1.0F, 1.0F);
    }

    public void surroundTeeth(LivingEntity livingEntity, BlockPos blockPos, float damage, boolean isStaff){
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        while (blockpos$mutable.getY() < blockPos.getY() + 8.0D && !livingEntity.level.getBlockState(blockpos$mutable).blocksMotion()) {
            blockpos$mutable.move(Direction.UP);
        }

        if (isStaff) {
            for (int i = 0; i < 5; ++i) {
                float f1 = (float) i * (float) Math.PI * 0.4F;
                ViciousTooth viciousTooth = new ViciousTooth(ModEntityType.VICIOUS_TOOTH.get(), livingEntity.level);
                viciousTooth.setPos(blockPos.getX() + (double) Mth.cos(f1) * 1.5D, blockpos$mutable.getY(), blockPos.getZ() + (double) Mth.cos(f1) * 1.5D);
                viciousTooth.setOwner(livingEntity);
                if (livingEntity.level.addFreshEntity(viciousTooth)) {
                    viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                }
            }
            for (int k = 0; k < 8; ++k) {
                float f2 = (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                ViciousTooth viciousTooth = new ViciousTooth(ModEntityType.VICIOUS_TOOTH.get(), livingEntity.level);
                viciousTooth.setPos(blockPos.getX() + (double) Mth.cos(f2) * 2.5D, blockpos$mutable.getY(), blockPos.getZ() + (double) Mth.sin(f2) * 2.5D);
                viciousTooth.setOwner(livingEntity);
                viciousTooth.setExtraDamage(damage);
                if (livingEntity.level.addFreshEntity(viciousTooth)) {
                    viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                }
            }
        }
    }

}
