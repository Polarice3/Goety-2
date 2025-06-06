package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ServantSpawnEggItem extends ForgeSpawnEggItem {

    public ServantSpawnEggItem(final RegistryObject<? extends EntityType<? extends Mob>> entityTypeSupplier, int primaryColorIn, int secondaryColorIn, Properties builder) {
        super(Lazy.of(entityTypeSupplier), primaryColorIn, secondaryColorIn, builder);
    }

    public @NotNull InteractionResult useOn(UseOnContext p_43223_) {
        Level level = p_43223_.getLevel();
        Player player = p_43223_.getPlayer();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = p_43223_.getItemInHand();
            BlockPos blockpos = p_43223_.getClickedPos();
            Direction direction = p_43223_.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(Blocks.SPAWNER)) {
                BlockEntity blockentity = level.getBlockEntity(blockpos);
                if (blockentity instanceof SpawnerBlockEntity spawnerblockentity) {
                    EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                    spawnerblockentity.setEntityId(entitytype1, level.getRandom());
                    blockentity.setChanged();
                    level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    level.gameEvent(p_43223_.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
                    itemstack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            EntityType<?> entitytype = this.getType(itemstack.getTag());
            Entity entity = entitytype.spawn(serverLevel, itemstack, p_43223_.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (entity != null) {
                if (player != null && entity instanceof IOwned owned && !owned.isHostile()){
                    if (player.isCrouching()){
                        owned.setTrueOwner(player);
                        if (owned instanceof Mob mob){
                            ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.SPAWN_EGG, null, itemstack.getTag());
                        }
                    }
                }
                itemstack.shrink(1);
                level.gameEvent(p_43223_.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

            return InteractionResult.CONSUME;
        }
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level p_43225_, Player p_43226_, @NotNull InteractionHand p_43227_) {
        ItemStack itemstack = p_43226_.getItemInHand(p_43227_);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(p_43225_, p_43226_, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else if (!(p_43225_ instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(itemstack);
        } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!(p_43225_.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemstack);
            } else if (p_43225_.mayInteract(p_43226_, blockpos) && p_43226_.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
                EntityType<?> entitytype = this.getType(itemstack.getTag());
                Entity entity = entitytype.spawn(serverLevel, itemstack, p_43226_, blockpos, MobSpawnType.SPAWN_EGG, false, false);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemstack);
                } else {
                    if (entity instanceof IOwned owned && !owned.isHostile()){
                        if (p_43226_.isCrouching()){
                            owned.setTrueOwner(p_43226_);
                            if (owned instanceof Mob mob){
                                ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.SPAWN_EGG, null, itemstack.getTag());
                            }
                        }
                    }
                    if (!p_43226_.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    p_43226_.awardStat(Stats.ITEM_USED.get(this));
                    p_43225_.gameEvent(p_43226_, GameEvent.ENTITY_PLACE, entity.position());
                    return InteractionResultHolder.consume(itemstack);
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }

    public @NotNull Optional<Mob> spawnOffspringFromSpawnEgg(@NotNull Player player, @NotNull Mob targetMob, @NotNull EntityType<? extends Mob> entityType, @NotNull ServerLevel serverLevel, @NotNull Vec3 vec3, ItemStack itemStack) {
        if (!this.spawnsEntity(itemStack.getTag(), entityType)) {
            return Optional.empty();
        } else {
            Mob mob;
            if (targetMob instanceof AgeableMob ageableMob) {
                mob = ageableMob.getBreedOffspring(serverLevel, ageableMob);
            } else if (targetMob instanceof AnimalSummon animalSummon) {
                mob = animalSummon.getBreedOffspring(serverLevel, animalSummon);
            } else if (targetMob instanceof AbstractIllagerServant illager) {
                mob = illager.getBreedOffspring(serverLevel, illager);
            } else {
                mob = entityType.create(serverLevel);
            }

            if (mob == null) {
                return Optional.empty();
            } else {
                mob.setBaby(true);
                if (!mob.isBaby()) {
                    return Optional.empty();
                } else {
                    if (mob instanceof IOwned owned && !owned.isHostile()) {
                        if (targetMob instanceof IOwned owned1 && !owned1.isHostile()) {
                            if (owned1.getTrueOwner() != null){
                                owned.setTrueOwner(owned1.getTrueOwner());
                            }
                        }
                    }

                    mob.moveTo(vec3.x(), vec3.y(), vec3.z(), 0.0F, 0.0F);
                    serverLevel.addFreshEntityWithPassengers(mob);
                    if (itemStack.hasCustomHoverName()) {
                        mob.setCustomName(itemStack.getHoverName());
                    }

                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    return Optional.of(mob);
                }
            }
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        MutableComponent textComponent = Component.translatable("tooltip.goety.servant.spawn_egg")
                .withStyle(ChatFormatting.GOLD);

        tooltip.add(textComponent);
    }
}
