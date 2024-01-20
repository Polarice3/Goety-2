package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.MobsConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.HuntDownPlayerGoal;
import com.Polarice3.Goety.common.entities.hostile.illagers.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.Maps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;

public class IllagerSpawner {
    private int nextTick;

    public int tick(ServerLevel world) {
        if (!MobsConfig.IllagerAssault.get()) {
            return 0;
        } else {
            RandomSource random = world.random;
            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += MobsConfig.IllagerAssaultSpawnFreq.get();
                if (random.nextInt(MobsConfig.IllagerAssaultSpawnChance.get()) != 0) {
                    return 0;
                } else {
                    int j = world.players().size();
                    if (j < 1) {
                        return 0;
                    } else {
                        Player player = world.players().get(random.nextInt(j));
                        int soulEnergy = Mth.clamp(SEHelper.getSoulAmountInt(player), 0, MobsConfig.IllagerAssaultSELimit.get());
                        if (player.isSpectator() || player.isCreative()) {
                            return 0;
                        } else if (SEHelper.getRestPeriod(player) > 0){
                            return 0;
                        } else if (world.isCloseToVillage(player.blockPosition(), 2) && soulEnergy < MobsConfig.IllagerAssaultSELimit.get()) {
                            return 0;
                        } else if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get()) {
                            int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                            int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = player.blockPosition().mutable().move(k, 0, l);
                            if (!world.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
                                return 0;
                            } else if (!world.dimensionType().hasRaids()){
                                return 0;
                            } else {
                                Holder<Biome> holder = world.getBiome(blockpos$mutable);
                                if (holder.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                    return 0;
                                } else if (player.blockPosition().getY() < world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY() - 32 && !world.canSeeSky(player.blockPosition())){
                                    return 0;
                                } else {
                                    int i1 = 0;
                                    int j1 = soulEnergy / 2;
                                    int e1 = Mth.clamp(j1 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 3) + 1;
                                    int e15 = world.random.nextInt(e1);
                                    for (int k1 = 0; k1 < e15; ++k1) {
                                        ++i1;
                                        blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                        if (k1 == 0) {
                                            if (!this.spawnEnvioker(world, blockpos$mutable, random, soulEnergy, player)) {
                                                break;
                                            }
                                        } else {
                                            this.spawnEnvioker(world, blockpos$mutable, random, soulEnergy, player);
                                        }

                                        blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                        blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2) {
                                        int j2 = soulEnergy/2;
                                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                                        int e25 = world.random.nextInt(e2) + 3;
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            int random1 = world.random.nextInt(15);
                                            blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnRandomIllager(world, blockpos$mutable, random, random1, soulEnergy, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnRandomIllager(world, blockpos$mutable, random, random1, soulEnergy, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2) {
                                        int j2 = soulEnergy/2;
                                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 3) + 1;
                                        int e25 = world.random.nextInt(e2);
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            int random1 = world.random.nextInt(5);
                                            blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnRavager(world, blockpos$mutable, random, random1, soulEnergy, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnRavager(world, blockpos$mutable, random, random1, soulEnergy, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                                        int j2 = (int) (soulEnergy/2.5);
                                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                                        int e25 = world.random.nextInt(e2);
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnPreacher(world, blockpos$mutable, random, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnPreacher(world, blockpos$mutable, random, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                                        int j2 = (int) (soulEnergy/2.5);
                                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                                        int e25 = world.random.nextInt(e2);
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnConquillager(world, blockpos$mutable, random, soulEnergy, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnConquillager(world, blockpos$mutable, random, soulEnergy, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 3) {
                                        int j2 = (int) (soulEnergy/3);
                                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                                        int e25 = world.random.nextInt(e2);
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnInquillager(world, blockpos$mutable, random, soulEnergy, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnInquillager(world, blockpos$mutable, random, soulEnergy, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 5 && world.random.nextFloat() <= 0.15F) {
                                        blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                        blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                        blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                        this.spawnMinister(world, blockpos$mutable, random, player);
                                    }
                                    if (soulEnergy >= MobsConfig.IllagerAssaultSELimit.get() && MobsConfig.SoulEnergyBadOmen.get()) {
                                        if (!player.hasEffect(MobEffects.BAD_OMEN)) {
                                            player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, 0, false, false));
                                        }
                                    }
                                    if (CuriosFinder.hasCurio(player, ModItems.ALARMING_CHARM.get())){
                                        ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.RAID_HORN, 64.0F, 1.0F));
                                    }
                                    return i1;
                                }
                            }
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
    }

    public boolean spawnEnvioker(ServerLevel worldIn, BlockPos p_222695_2_, RandomSource random, int infamy, Player player) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), ModEntityType.ENVIOKER.get())) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(ModEntityType.ENVIOKER.get(), worldIn, MobSpawnType.PATROL, p_222695_2_, random)) {
            return false;
        } else {
            Envioker illager = ModEntityType.ENVIOKER.get().create(worldIn);
            if (illager != null) {
                illager.setPos((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
                if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(illager, worldIn, p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
                illager.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                if (random.nextInt(4) == 0){
                    illager.setRider(true);
                }
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                this.upgradeIllagers(illager, infamy);
                worldIn.addFreshEntityWithPassengers(illager);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean spawnPreacher(ServerLevel worldIn, BlockPos p_222695_2_, RandomSource random, Player player) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), ModEntityType.PREACHER.get())) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(ModEntityType.PREACHER.get(), worldIn, MobSpawnType.PATROL, p_222695_2_, random)) {
            return false;
        } else {
            Preacher illager = ModEntityType.PREACHER.get().create(worldIn);
            if (illager != null) {
                illager.setPos((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
                if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(illager, worldIn, p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
                illager.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                worldIn.addFreshEntityWithPassengers(illager);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean spawnInquillager(ServerLevel worldIn, BlockPos p_222695_2_, RandomSource random, int infamy, Player player) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), ModEntityType.INQUILLAGER.get())) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(ModEntityType.INQUILLAGER.get(), worldIn, MobSpawnType.PATROL, p_222695_2_, random)) {
            return false;
        } else {
            Inquillager illager = ModEntityType.INQUILLAGER.get().create(worldIn);
            if (illager != null) {
                illager.setPos((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
                if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(illager, worldIn, p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
                illager.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                if (random.nextInt(4) == 0){
                    illager.setRider(true);
                }
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                this.upgradeIllagers(illager, infamy);
                worldIn.addFreshEntityWithPassengers(illager);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean spawnConquillager(ServerLevel worldIn, BlockPos p_222695_2_, RandomSource random, int infamy, Player player) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), ModEntityType.CONQUILLAGER.get())) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(ModEntityType.CONQUILLAGER.get(), worldIn, MobSpawnType.PATROL, p_222695_2_, random)) {
            return false;
        } else {
            Conquillager illager = ModEntityType.CONQUILLAGER.get().create(worldIn);
            if (illager != null) {
                illager.setPos((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
                if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(illager, worldIn, p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
                illager.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                if (random.nextInt(4) == 0){
                    illager.setRider(true);
                }
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                this.upgradeIllagers(illager, infamy);
                worldIn.addFreshEntityWithPassengers(illager);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean spawnMinister(ServerLevel worldIn, BlockPos p_222695_2_, RandomSource random, Player player) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), ModEntityType.MINISTER.get())) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(ModEntityType.MINISTER.get(), worldIn, MobSpawnType.PATROL, p_222695_2_, random)) {
            return false;
        } else {
            Minister illager = ModEntityType.MINISTER.get().create(worldIn);
            if (illager != null) {
                illager.setPos((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
                if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(illager, worldIn, p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
                illager.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                if (random.nextInt(4) == 0){
                    illager.setRider(true);
                }
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                worldIn.addFreshEntityWithPassengers(illager);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean spawnRandomIllager(ServerLevel worldIn, BlockPos p_222695_2_, RandomSource random, int r, int infamy, Player player) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), EntityType.PILLAGER)) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, worldIn, MobSpawnType.PATROL, p_222695_2_, random)) {
            return false;
        } else {
            Raider illager = null;
            int i = 0;
            switch (r) {
                case 0, 1, 2, 3, 4, 5 -> illager = EntityType.PILLAGER.create(worldIn);
                case 6, 7, 8, 9, 10, 11 -> illager = ModEntityType.PIKER.get().create(worldIn);
                case 12, 13, 14 -> illager = EntityType.VINDICATOR.create(worldIn);
            }
            if (illager != null) {
                illager.setPos(p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ());
                if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(illager, worldIn, p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
                illager.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                this.upgradeIllagers(illager, infamy);
                worldIn.addFreshEntityWithPassengers(illager);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean spawnRavager(ServerLevel worldIn, BlockPos p_222695_2_, RandomSource random, int r, int infamy, Player player) {
        BlockState blockstate = worldIn.getBlockState(p_222695_2_);
        if (!NaturalSpawner.isValidEmptySpawnBlock(worldIn, p_222695_2_, blockstate, blockstate.getFluidState(), EntityType.PILLAGER)) {
            return false;
        } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, worldIn, MobSpawnType.PATROL, p_222695_2_, random)) {
            return false;
        } else {
            Raider illager = null;
            int i = 0;
            switch (r) {
                case 0, 1, 2 -> {
                    if (infamy >= MobsConfig.IllagerAssaultSEThreshold.get() * 3){
                        if (worldIn.random.nextFloat() < (0.25F + worldIn.getCurrentDifficultyAt(p_222695_2_).getSpecialMultiplier())){
                            illager = ModEntityType.ARMORED_RAVAGER.get().create(worldIn);
                        } else {
                            illager = EntityType.RAVAGER.create(worldIn);
                        }
                    } else {
                        illager = EntityType.RAVAGER.create(worldIn);
                    }
                }
                case 3, 4 -> {
                    if (infamy >= MobsConfig.IllagerAssaultSEThreshold.get() * 3){
                        if (worldIn.random.nextFloat() < (0.25F + worldIn.getCurrentDifficultyAt(p_222695_2_).getSpecialMultiplier())){
                            illager = ModEntityType.ARMORED_RAVAGER.get().create(worldIn);
                        } else {
                            illager = EntityType.RAVAGER.create(worldIn);
                        }
                    } else {
                        illager = EntityType.RAVAGER.create(worldIn);
                    }
                    ++i;
                }
            }
            if (illager != null) {
                illager.setPos(p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ());
                if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(illager, worldIn, p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
                illager.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player)) {
                    illager.setTarget(player);
                }
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                this.upgradeIllagers(illager, infamy);
                worldIn.addFreshEntityWithPassengers(illager);
                if (i > 0){
                    Raider rider = null;
                    int riding = random.nextInt(6);
                    switch (riding) {
                        case 0 -> rider = EntityType.PILLAGER.create(worldIn);
                        case 1 -> rider = ModEntityType.PIKER.get().create(worldIn);
                        case 2 -> rider = EntityType.VINDICATOR.create(worldIn);
                        case 3 -> rider = ModEntityType.CONQUILLAGER.get().create(worldIn);
                        case 4 -> rider = ModEntityType.INQUILLAGER.get().create(worldIn);
                        case 5 -> rider = ModEntityType.ENVIOKER.get().create(worldIn);
                    }
                    if (rider != null){
                        rider.setPos(p_222695_2_.getX(), p_222695_2_.getY(), p_222695_2_.getZ());
                        rider.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(p_222695_2_), MobSpawnType.PATROL, null, null);
                        rider.setTarget(player);
                        this.upgradeIllagers(rider, infamy);
                        rider.startRiding(illager);
                        worldIn.addFreshEntity(rider);
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public void upgradeIllagers(Raider raider, int infamy){
        Level world = raider.level;
        if (infamy >= MobsConfig.IllagerAssaultSEThreshold.get() * 5) {
            if (raider instanceof Pillager){
                ItemStack itemstack = new ItemStack(Items.CROSSBOW);
                Map<Enchantment, Integer> map = Maps.newHashMap();
                if (world.getDifficulty() == Difficulty.HARD) {
                    map.put(Enchantments.QUICK_CHARGE, 2);
                } else {
                    map.put(Enchantments.QUICK_CHARGE, 1);
                }

                map.put(Enchantments.MULTISHOT, 1);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
            if (raider instanceof Vindicator){
                ItemStack itemstack = new ItemStack(Items.IRON_AXE);
                Map<Enchantment, Integer> map = Maps.newHashMap();
                int i = 1;
                if (world.getDifficulty() == Difficulty.HARD) {
                    i = 2;
                }
                map.put(Enchantments.SHARPNESS, i);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
            if (raider instanceof Conquillager){
                ItemStack itemstack = new ItemStack(Items.CROSSBOW);
                Map<Enchantment, Integer> map = Maps.newHashMap();
                if (world.getDifficulty() == Difficulty.HARD) {
                    map.put(Enchantments.QUICK_CHARGE, 3);
                } else {
                    map.put(Enchantments.QUICK_CHARGE, 2);
                }

                map.put(Enchantments.MULTISHOT, 1);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
            if (raider instanceof Inquillager){
                ItemStack itemstack = new ItemStack(Items.IRON_SWORD);
                if (world.random.nextFloat() <= 0.25F){
                    itemstack = new ItemStack(Items.DIAMOND_SWORD);
                }
                Map<Enchantment, Integer> map = Maps.newHashMap();
                int i = 2;
                if (world.getDifficulty() == Difficulty.HARD) {
                    i = 4;
                }
                map.put(Enchantments.SHARPNESS, i);
                map.put(Enchantments.FIRE_ASPECT, 2);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
            if (raider instanceof Envioker){
                ItemStack itemstack = new ItemStack(Items.IRON_SWORD);
                if (world.random.nextFloat() <= 0.25F){
                    itemstack = new ItemStack(Items.DIAMOND_SWORD);
                }
                Map<Enchantment, Integer> map = Maps.newHashMap();
                int i = 3;
                if (world.getDifficulty() == Difficulty.HARD) {
                    i = 5;
                }
                map.put(Enchantments.SHARPNESS, i);
                map.put(Enchantments.KNOCKBACK, 2);
                EnchantmentHelper.setEnchantments(map, itemstack);
                raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
            }
        }
    }

    public void forceSpawn(ServerLevel pLevel, Player pPlayer, CommandSourceStack pSource){
        RandomSource random = pLevel.random;
        int soulEnergy = SEHelper.getSoulAmountInt(pPlayer);
        if (soulEnergy > MobsConfig.IllagerAssaultSEThreshold.get()) {
            int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            BlockPos.MutableBlockPos blockpos$mutable = pPlayer.blockPosition().mutable().move(k, 0, l);
            if (pLevel.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
                if (pPlayer.blockPosition().getY() < pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY() - 32 && !pLevel.canSeeSky(pPlayer.blockPosition())){
                    ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F));
                    pSource.sendFailure(Component.translatable("commands.goety.illager.spawn.failure_location", pPlayer.getDisplayName()));
                } else {
                    int j0 = soulEnergy/2;
                    int e1 = Mth.clamp(j0 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 3) + 1;
                    int e15 = pLevel.random.nextInt(e1);
                    for (int k1 = 0; k1 < e15; ++k1) {
                        blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                        if (k1 == 0) {
                            if (!this.spawnEnvioker(pLevel, blockpos$mutable, random, soulEnergy, pPlayer)) {
                                break;
                            }
                        } else {
                            this.spawnEnvioker(pLevel, blockpos$mutable, random, soulEnergy, pPlayer);
                        }

                        blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                        blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                    }
                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2) {
                        int j2 = soulEnergy/2;
                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                        int e25 = pLevel.random.nextInt(e2) + 3;
                        for (int k1 = 0; k1 < e25; ++k1) {
                            int random1 = pLevel.random.nextInt(15);
                            blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                            if (k1 == 0) {
                                if (!this.spawnRandomIllager(pLevel, blockpos$mutable, random, random1, soulEnergy, pPlayer)) {
                                    break;
                                }
                            } else {
                                this.spawnRandomIllager(pLevel, blockpos$mutable, random, random1, soulEnergy, pPlayer);
                            }

                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                        }
                    }
                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2) {
                        int j2 = soulEnergy/2;
                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 3) + 1;
                        int e25 = Mth.clamp(pLevel.random.nextInt(e2) - 1, 0, 5);
                        for (int k1 = 0; k1 < e25; ++k1) {
                            int random1 = pLevel.random.nextInt(5);
                            blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                            if (k1 == 0) {
                                if (!this.spawnRavager(pLevel, blockpos$mutable, random, random1, soulEnergy, pPlayer)) {
                                    break;
                                }
                            } else {
                                this.spawnRavager(pLevel, blockpos$mutable, random, random1, soulEnergy, pPlayer);
                            }

                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                        }
                    }
                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                        int j2 = (int) (soulEnergy/2.5);
                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                        int e25 = pLevel.random.nextInt(e2);
                        for (int k1 = 0; k1 < e25; ++k1) {
                            blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                            if (k1 == 0) {
                                if (!this.spawnPreacher(pLevel, blockpos$mutable, random, pPlayer)) {
                                    break;
                                }
                            } else {
                                this.spawnPreacher(pLevel, blockpos$mutable, random, pPlayer);
                            }

                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                        }
                    }
                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                        int j2 = (int) (soulEnergy/2.5);
                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                        int e25 = pLevel.random.nextInt(e2);
                        for (int k1 = 0; k1 < e25; ++k1) {
                            blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                            if (k1 == 0) {
                                if (!this.spawnConquillager(pLevel, blockpos$mutable, random, soulEnergy, pPlayer)) {
                                    break;
                                }
                            } else {
                                this.spawnConquillager(pLevel, blockpos$mutable, random, soulEnergy, pPlayer);
                            }

                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                        }
                    }
                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 3) {
                        int j2 = (int) (soulEnergy/3);
                        int e2 = Mth.clamp(j2 / MobsConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                        int e25 = pLevel.random.nextInt(e2);
                        for (int k1 = 0; k1 < e25; ++k1) {
                            blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                            if (k1 == 0) {
                                if (!this.spawnInquillager(pLevel, blockpos$mutable, random, soulEnergy, pPlayer)) {
                                    break;
                                }
                            } else {
                                this.spawnInquillager(pLevel, blockpos$mutable, random, soulEnergy, pPlayer);
                            }

                            blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                        }
                    }
                    if (soulEnergy >= MobsConfig.IllagerAssaultSEThreshold.get() * 5 && pLevel.random.nextFloat() <= 0.15F) {
                        blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                        blockpos$mutable.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                        blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                        this.spawnMinister(pLevel, blockpos$mutable, random, pPlayer);
                    }
                    if (CuriosFinder.hasCurio(pPlayer, ModItems.ALARMING_CHARM.get())){
                        ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.RAID_HORN, 64.0F, 1.0F));
                    }
                    this.nextTick += MobsConfig.IllagerAssaultSpawnFreq.get();
                    pSource.sendSuccess(Component.translatable("commands.goety.illager.spawn.success", pPlayer.getDisplayName()), false);
                }
            } else {
                ModNetwork.sendToALL(new SPlayPlayerSoundPacket(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F));
                pSource.sendFailure(Component.translatable("commands.goety.illager.spawn.failure_location", pPlayer.getDisplayName()));
            }
        }
    }

}
