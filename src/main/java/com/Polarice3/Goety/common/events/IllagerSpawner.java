package com.Polarice3.Goety.common.events;

import com.Polarice3.Goety.MainConfig;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.HuntDownPlayerGoal;
import com.Polarice3.Goety.common.entities.hostile.illagers.Conquillager;
import com.Polarice3.Goety.common.entities.hostile.illagers.Envioker;
import com.Polarice3.Goety.common.entities.hostile.illagers.Inquillager;
import com.Polarice3.Goety.common.entities.hostile.illagers.Minister;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

    public int tick(ServerLevel p_64570_) {
        if (!MainConfig.IllagerAssault.get()) {
            return 0;
        } else {
            RandomSource randomsource = p_64570_.random;
            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += MainConfig.IllagerAssaultSpawnFreq.get();
                if (randomsource.nextInt(MainConfig.IllagerAssaultSpawnChance.get()) != 0) {
                    return 0;
                } else {
                    int j = p_64570_.players().size();
                    if (j < 1) {
                        return 0;
                    } else {
                        Player player = p_64570_.players().get(randomsource.nextInt(j));
                        int soulEnergy = Mth.clamp(SEHelper.getSoulAmountInt(player), 0, MainConfig.IllagerAssaultSELimit.get());
                        if (player.isSpectator()) {
                            return 0;
                        } else if (SEHelper.getRestPeriod(player) > 0){
                            return 0;
                        } else if (p_64570_.isCloseToVillage(player.blockPosition(), 2) && soulEnergy < MainConfig.IllagerAssaultSELimit.get()) {
                            return 0;
                        } else if (soulEnergy > MainConfig.IllagerAssaultSEThreshold.get()) {
                            int k = (24 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                            int l = (24 + randomsource.nextInt(24)) * (randomsource.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = player.blockPosition().mutable().move(k, 0, l);
                            if (!p_64570_.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
                                return 0;
                            } else if (!p_64570_.dimensionType().hasRaids()){
                                return 0;
                            } else {
                                Holder<Biome> holder = p_64570_.getBiome(blockpos$mutable);
                                if (holder.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                    return 0;
                                } else {
                                    int i1 = 0;
                                    int e1 = Mth.clamp(soulEnergy / MainConfig.IllagerAssaultSEThreshold.get(), 1, 3) + 1;
                                    int e15 = p_64570_.random.nextInt(e1);
                                    for (int k1 = 0; k1 < e15; ++k1) {
                                        ++i1;
                                        blockpos$mutable.setY(p_64570_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                        if (k1 == 0) {
                                            if (!this.spawnEnvioker(p_64570_, blockpos$mutable, randomsource, soulEnergy, player)) {
                                                break;
                                            }
                                        } else {
                                            this.spawnEnvioker(p_64570_, blockpos$mutable, randomsource, soulEnergy, player);
                                        }

                                        blockpos$mutable.setX(blockpos$mutable.getX() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                        blockpos$mutable.setZ(blockpos$mutable.getZ() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                    }
                                    if (soulEnergy >= MainConfig.IllagerAssaultSEThreshold.get() * 2) {
                                        int j2 = soulEnergy/2;
                                        int e2 = Mth.clamp(j2 / MainConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                                        int e25 = p_64570_.random.nextInt(e2);
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            int random1 = p_64570_.random.nextInt(16);
                                            blockpos$mutable.setY(p_64570_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnRandomIllager(p_64570_, blockpos$mutable, randomsource, random1, soulEnergy, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnRandomIllager(p_64570_, blockpos$mutable, randomsource, random1, soulEnergy, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MainConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                                        int j2 = (int) (soulEnergy/2.5);
                                        int e2 = Mth.clamp(j2 / MainConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                                        int e25 = p_64570_.random.nextInt(e2);
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            blockpos$mutable.setY(p_64570_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnInquillager(p_64570_, blockpos$mutable, randomsource, soulEnergy, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnInquillager(p_64570_, blockpos$mutable, randomsource, soulEnergy, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MainConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                                        int j2 = (int) (soulEnergy/2.5);
                                        int e2 = Mth.clamp(j2 / MainConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                                        int e25 = p_64570_.random.nextInt(e2);
                                        for (int k1 = 0; k1 < e25; ++k1) {
                                            ++i1;
                                            blockpos$mutable.setY(p_64570_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                            if (k1 == 0) {
                                                if (!this.spawnConquillager(p_64570_, blockpos$mutable, randomsource, soulEnergy, player)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnConquillager(p_64570_, blockpos$mutable, randomsource, soulEnergy, player);
                                            }

                                            blockpos$mutable.setX(blockpos$mutable.getX() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                            blockpos$mutable.setZ(blockpos$mutable.getZ() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                        }
                                    }
                                    if (soulEnergy >= MainConfig.IllagerAssaultSEThreshold.get() * 3 && p_64570_.random.nextFloat() <= 0.15F) {
                                        blockpos$mutable.setX(blockpos$mutable.getX() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                        blockpos$mutable.setY(p_64570_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                                        blockpos$mutable.setZ(blockpos$mutable.getZ() + randomsource.nextInt(5) - randomsource.nextInt(5));
                                        this.spawnMinister(p_64570_, blockpos$mutable, randomsource, player);
                                    }
                                    if (soulEnergy >= MainConfig.IllagerAssaultSELimit.get() && MainConfig.SoulEnergyBadOmen.get()) {
                                        if (!player.hasEffect(MobEffects.BAD_OMEN)) {
                                            player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, 0, false, false));
                                        }
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
                illager.setTarget(player);
                this.upgradeIllagers(illager, infamy);
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
                illager.setTarget(player);
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
                illager.setTarget(player);
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
                illager.setTarget(player);
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
                case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 -> illager = EntityType.PILLAGER.create(worldIn);
                case 11, 12, 13 -> illager = EntityType.VINDICATOR.create(worldIn);
                case 14 -> {
                    if (infamy >= MainConfig.IllagerAssaultSEThreshold.get() * 3){
                        if (worldIn.random.nextFloat() < (0.25F + worldIn.getCurrentDifficultyAt(p_222695_2_).getSpecialMultiplier())){
                            illager = ModEntityType.ARMORED_RAVAGER.get().create(worldIn);
                        } else {
                            illager = EntityType.RAVAGER.create(worldIn);
                        }
                    } else {
                        illager = EntityType.RAVAGER.create(worldIn);
                    }
                }
                case 15 -> {
                    if (infamy >= MainConfig.IllagerAssaultSEThreshold.get() * 3){
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
                illager.setTarget(player);
                illager.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(illager));
                this.upgradeIllagers(illager, infamy);
                worldIn.addFreshEntityWithPassengers(illager);
                if (i > 0){
                    Raider rider = null;
                    int riding = random.nextInt(5);
                    switch (riding) {
                        case 0 -> rider = EntityType.PILLAGER.create(worldIn);
                        case 1 -> rider = EntityType.VINDICATOR.create(worldIn);
                        case 2 -> rider = ModEntityType.CONQUILLAGER.get().create(worldIn);
                        case 3 -> rider = ModEntityType.INQUILLAGER.get().create(worldIn);
                        case 4 -> rider = ModEntityType.ENVIOKER.get().create(worldIn);
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
        if (infamy >= MainConfig.IllagerAssaultSEThreshold.get() * 5) {
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

    public void forceSpawn(ServerLevel world, Player player){
        RandomSource random = world.random;
        int j1 = SEHelper.getSoulAmountInt(player);
        if (j1 > MainConfig.IllagerAssaultSEThreshold.get()) {
            int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
            BlockPos.MutableBlockPos blockpos$mutable = player.blockPosition().mutable().move(k, 0, l);
            if (world.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
                int e1 = Mth.clamp(j1 / MainConfig.IllagerAssaultSEThreshold.get(), 1, 3) + 1;
                int e15 = world.random.nextInt(e1);
                for (int k1 = 0; k1 < e15; ++k1) {
                    blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                    if (k1 == 0) {
                        if (!this.spawnEnvioker(world, blockpos$mutable, random, j1, player)) {
                            break;
                        }
                    } else {
                        this.spawnEnvioker(world, blockpos$mutable, random, j1, player);
                    }

                    blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                    blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                }
                if (j1 >= MainConfig.IllagerAssaultSEThreshold.get() * 2) {
                    int j2 = j1/2;
                    int e2 = Mth.clamp(j2 / MainConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                    int e25 = world.random.nextInt(e2);
                    for (int k1 = 0; k1 < e25; ++k1) {
                        int random1 = world.random.nextInt(16);
                        blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                        if (k1 == 0) {
                            if (!this.spawnRandomIllager(world, blockpos$mutable, random, random1, j1, player)) {
                                break;
                            }
                        } else {
                            this.spawnRandomIllager(world, blockpos$mutable, random, random1, j1, player);
                        }

                        blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                        blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                    }
                }
                if (j1 >= MainConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                    int j2 = (int) (j1/2.5);
                    int e2 = Mth.clamp(j2 / MainConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                    int e25 = world.random.nextInt(e2);
                    for (int k1 = 0; k1 < e25; ++k1) {
                        blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                        if (k1 == 0) {
                            if (!this.spawnInquillager(world, blockpos$mutable, random, j1, player)) {
                                break;
                            }
                        } else {
                            this.spawnInquillager(world, blockpos$mutable, random, j1, player);
                        }

                        blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                        blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                    }
                }
                if (j1 >= MainConfig.IllagerAssaultSEThreshold.get() * 2.5) {
                    int j2 = (int) (j1/2.5);
                    int e2 = Mth.clamp(j2 / MainConfig.IllagerAssaultSEThreshold.get(), 1, 5) + 1;
                    int e25 = world.random.nextInt(e2);
                    for (int k1 = 0; k1 < e25; ++k1) {
                        blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                        if (k1 == 0) {
                            if (!this.spawnConquillager(world, blockpos$mutable, random, j1, player)) {
                                break;
                            }
                        } else {
                            this.spawnConquillager(world, blockpos$mutable, random, j1, player);
                        }

                        blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                        blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                    }
                }
                if (j1 >= MainConfig.IllagerAssaultSEThreshold.get() * 3 && world.random.nextFloat() <= 0.15F) {
                    blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
                    blockpos$mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
                    blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                    this.spawnMinister(world, blockpos$mutable, random, player);
                }
                this.nextTick += MainConfig.IllagerAssaultSpawnFreq.get();
            }
        }
    }

}
