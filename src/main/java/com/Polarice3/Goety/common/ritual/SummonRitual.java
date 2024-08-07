package com.Polarice3.Goety.common.ritual;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SummonRitual extends Ritual {
    private final boolean tame;

    public SummonRitual(RitualRecipe recipe, boolean tame) {
        super(recipe);
        this.tame = tame;
    }

    public void spawnEntity(Player castingPlayer, Entity entity, Level world) {
        if (castingPlayer instanceof ServerPlayer serverPlayer){
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, entity);
        }
        world.addFreshEntity(entity);
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);

        activationItem.shrink(1);

        ((ServerLevel) world).sendParticles(ParticleTypes.LARGE_SMOKE, blockPos.getX() + 0.5,
                blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 1, 0, 0, 0, 0);

        EntityType<?> entityType = this.recipe.getEntityToSummon();
        if (entityType != null) {
            Entity entity = this.createSummonedEntity(entityType, world, blockPos, tileEntity, castingPlayer);
            if (entity instanceof IOwned owned){
                EntityType<?> entityType1 = owned.getVariant(world, blockPos);
                if (entityType1 != null) {
                    entity = this.createSummonedEntity(entityType1, world, blockPos, tileEntity, castingPlayer);
                }
            }
            if (entity instanceof LivingEntity living) {
                this.prepareLivingEntityForSpawn(living, world, blockPos, tileEntity, castingPlayer, this.tame);

                this.initSummoned(living, world, blockPos, tileEntity, castingPlayer);

                this.spawnEntity(castingPlayer, living, world);

            } else {
                entity.absMoveTo(blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F,
                        world.random.nextInt(360), 0);
                this.spawnEntity(castingPlayer, entity, world);
            }
        }
    }

    public Entity createSummonedEntity(EntityType<?> entityType, Level world, BlockPos goldenBowlPosition, DarkAltarBlockEntity tileEntity,
                                       Player castingPlayer) {
        return entityType.create(world);
    }

    public void initSummoned(LivingEntity living, Level world, BlockPos goldenBowlPosition, DarkAltarBlockEntity tileEntity,
                             Player castingPlayer) {
    }
}
/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */