package com.Polarice3.Goety.api.blocks;

import com.Polarice3.Goety.common.entities.vehicle.SeatEntity;
import com.Polarice3.Goety.common.items.magic.CommandFocus;
import com.Polarice3.Goety.common.items.magic.OrderFocus;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;
import java.util.Optional;
/**
 * Based on @Creators-of-Create codes: <a href="https://github.com/Creators-of-Create/Create/blob/mc1.20.1/dev/src/main/java/com/simibubi/create/content/contraptions/actors/seat/SeatBlock.java">...</a>;
 */
public interface ISeat {

    default InteractionResult sitDown(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || player instanceof FakePlayer) {
            return InteractionResult.PASS;
        }
        if (WandUtil.findFocus(player).getItem() instanceof CommandFocus || WandUtil.findFocus(player).getItem() instanceof OrderFocus) {
            return InteractionResult.PASS;
        }

        List<SeatEntity> seats = world.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
        if (!seats.isEmpty()) {
            SeatEntity seatEntity = seats.get(0);
            List<Entity> passengers = seatEntity.getPassengers();
            if (!passengers.isEmpty() && passengers.get(0) instanceof Player) {
                return InteractionResult.PASS;
            }
            if (!world.isClientSide) {
                seatEntity.ejectPassengers();
                player.startRiding(seatEntity);
            }
            return InteractionResult.SUCCESS;
        }

        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        this.placeSeat(world, pos, getLeashed(world, player).orElse(player));
        return InteractionResult.SUCCESS;
    }

    static boolean isSeatOccupied(Level world, BlockPos pos) {
        return !world.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).isEmpty();
    }

    static Optional<Entity> getLeashed(Level level, Player player) {
        List<Entity> entities = player.level.getEntities((Entity) null, player.getBoundingBox().inflate(10), e -> true);
        for (Entity e : entities) {
            if (e instanceof Mob mob && mob.getLeashHolder() == player && ISeat.canBePickedUp(e)) {
                return Optional.of(mob);
            }
        }
        return Optional.empty();
    }

    static boolean canBePickedUp(Entity passenger) {
        if (passenger instanceof Shulker) {
            return false;
        }
        if (passenger instanceof Player) {
            return false;
        }
        if (passenger.getType().is(ModTags.EntityTypes.NO_SEATING)) {
            return false;
        }
        return passenger instanceof LivingEntity;
    }

    default Vec3 seatOffset(BlockPos pos) {
        return Vec3.atBottomCenterOf(pos);
    }

    default boolean hasLookAngle() {
        return false;
    }

    default float seatLookAngle(Level world, BlockPos pos) {
        return 0.0F;
    }

    default void placeSeat(Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide) {
            return;
        }
        SeatEntity seat = new SeatEntity(world, this.seatOffset(pos));
        if (this.hasLookAngle()) {
            seat.setCustomLook(this.seatLookAngle(world, pos));
        }
        world.addFreshEntity(seat);
        entity.startRiding(seat, true);
        if (entity instanceof TamableAnimal ta) {
            ta.setInSittingPose(true);
        }
    }
}
