package com.Polarice3.Goety.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public interface PlayerDetector {
	PlayerDetector NO_CREATIVE_PLAYERS = (serverLevel, entitySelector, blockPos, d, bl) -> entitySelector.getPlayers(
			serverLevel, player -> player.blockPosition().closerThan(blockPos, d) && !player.isCreative() && !player.isSpectator()
		)
		.stream()
		.filter(player -> !bl || inLineOfSight(serverLevel, blockPos.getCenter(), player.getEyePosition()))
		.map(Entity::getUUID)
		.toList();
	PlayerDetector INCLUDING_CREATIVE_PLAYERS = (serverLevel, entitySelector, blockPos, d, bl) -> entitySelector.getPlayers(
			serverLevel, player -> player.blockPosition().closerThan(blockPos, d) && !player.isSpectator()
		)
		.stream()
		.filter(player -> !bl || inLineOfSight(serverLevel, blockPos.getCenter(), player.getEyePosition()))
		.map(Entity::getUUID)
		.toList();
	PlayerDetector SHEEP = (serverLevel, entitySelector, blockPos, d, bl) -> {
		AABB aABB = new AABB(blockPos).inflate(d);
		return entitySelector.getEntities(serverLevel, EntityType.SHEEP, aABB, LivingEntity::isAlive)
			.stream()
			.filter(sheep -> !bl || inLineOfSight(serverLevel, blockPos.getCenter(), sheep.getEyePosition()))
			.map(Entity::getUUID)
			.toList();
	};

	List<UUID> detect(ServerLevel serverLevel, EntitySelector entitySelector, BlockPos blockPos, double d, boolean bl);

	private static boolean inLineOfSight(Level level, Vec3 vec3, Vec3 vec32) {
		BlockHitResult blockHitResult = level.clip(new ClipContext(vec32, vec3, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
		return blockHitResult.getBlockPos().equals(BlockPos.containing(vec3)) || blockHitResult.getType() == HitResult.Type.MISS;
	}

	interface EntitySelector {
		EntitySelector SELECT_FROM_LEVEL = new EntitySelector() {
			@Override
			public List<ServerPlayer> getPlayers(ServerLevel serverLevel, Predicate<? super Player> predicate) {
				return serverLevel.getPlayers(predicate);
			}

			@Override
			public <T extends Entity> List<T> getEntities(ServerLevel serverLevel, EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate) {
				return serverLevel.getEntities(entityTypeTest, aABB, predicate);
			}
		};

		List<? extends Player> getPlayers(ServerLevel serverLevel, Predicate<? super Player> predicate);

		<T extends Entity> List<T> getEntities(ServerLevel serverLevel, EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate);

		static EntitySelector onlySelectPlayer(Player player) {
			return onlySelectPlayers(List.of(player));
		}

		static EntitySelector onlySelectPlayers(List<Player> list) {
			return new EntitySelector() {
				@Override
				public List<Player> getPlayers(ServerLevel serverLevel, Predicate<? super Player> predicate) {
					return list.stream().filter(predicate).toList();
				}

				@Override
				public <T extends Entity> List<T> getEntities(ServerLevel serverLevel, EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate) {
					return list.stream().map(entityTypeTest::tryCast).filter(Objects::nonNull).filter(predicate).toList();
				}
			};
		}
	}
}
