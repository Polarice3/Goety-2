package com.Polarice3.Goety.common.blocks.entities;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.api.blocks.entities.IBarrack;
import com.Polarice3.Goety.api.entities.ITrainable;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.entities.ally.illager.ILooter;
import com.Polarice3.Goety.common.blocks.OminousDecreeBlock;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class OminousDecreeBlockEntity extends OwnedBlockEntity {
    private static final Map<EntityType<?>, Boolean> SPELL_CASTER_CACHE = new ConcurrentHashMap<>();
    public GlobalPos decreePos = null;
    public DecreeMode mode = DecreeMode.ALL;
    public CompoundTag entityType = new CompoundTag();
    public int glowTick = 0;
    public double range = 4.0D;
    public boolean showBlock;

    public OminousDecreeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.OMINOUS_DECREE.get(), blockPos, blockState);
    }

    @Nullable
    public GlobalPos getDecreePos(){
        return this.decreePos;
    }

    public void setDecreePos(GlobalPos globalPos) {
        this.decreePos = globalPos;
    }

    public DecreeMode getMode() {
        return this.mode;
    }

    public void setMode(DecreeMode mode) {
        this.mode = mode;
        this.markUpdated();
    }

    public void setMode(String string) {
        for (DecreeMode decreeMode : DecreeMode.values()) {
            if (decreeMode.getName().equals(string)) {
                this.setMode(decreeMode);
                break;
            }
        }
    }

    public void cycleMode() {
        if (this.getMode() == DecreeMode.ALL) {
            this.setMode(DecreeMode.BABY_ONLY);
        } else if (this.getMode() == DecreeMode.BABY_ONLY) {
            this.setMode(DecreeMode.ADULT_ONLY);
        } else {
            this.setMode(DecreeMode.ALL);
        }
        if (this.level != null) {
            this.level.playSound(null, this.worldPosition, ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 0.25F, 2.0F);
        }
    }

    private static boolean producesSpellCaster(EntityType<? extends Mob> entityType, Level level) {
        return SPELL_CASTER_CACHE.computeIfAbsent(entityType, entityType1 -> entityType1.create(level) instanceof SpellcasterIllagerServant);
    }

    public void tick() {
        if (this.level != null) {
            if (!this.level.isClientSide) {
                if (this.level.getGameTime() % 20 == 0 && this.glowTick <= 0) {
                    if (this.getDecreePos() != null) {
                        if (this.getDecreePos().dimension() == this.level.dimension()) {
                            BlockPos decreePos = this.getDecreePos().pos();
                            AABB aabb = new AABB(this.worldPosition).inflate(this.getRange());
                            List<Mob> list = this.level.getEntitiesOfClass(Mob.class, aabb, mob -> mob instanceof IServant servant && servant.getTrueOwner() == this.getTrueOwner() && !servant.isCommanded() && !servant.isPatrolling());
                            list.removeIf(mob -> {
                               if (this.getTrueOwner() instanceof Player player) {
                                   if (SEHelper.isGrounded(player, mob)) {
                                       return true;
                                   }
                               }

                               if (this.getEntityTypeTag() != null && !this.getEntityTypeTag().isEmpty()) {
                                   if (mob.getType() != this.getEntityType()) {
                                       return true;
                                   }
                               }

                               if (mob instanceof ITrainable trainable && trainable.isTraining()) {
                                   return true;
                               }

                               if (mob instanceof RaiderServant raiderServant && raiderServant.getLeader() != null) {
                                   return true;
                               }

                               if (this.getMode() == DecreeMode.BABY_ONLY) {
                                   return !mob.isBaby();
                               } else if (this.getMode() == DecreeMode.ADULT_ONLY) {
                                   return mob.isBaby();
                               }

                               return false;
                            });

                            if (this.level.getBlockEntity(decreePos) instanceof IBarrack barrack) {
                                Predicate<Mob> requirements = barrack.trainingRequirements(this.level, decreePos);
                                EntityType<? extends Mob> trainedType = barrack.getTrainedMob(this.level, decreePos);
                                boolean trainsMagic = trainedType != null && producesSpellCaster(trainedType, this.level);

                                list.removeIf(mob -> {
                                    if (!(mob instanceof ITrainable)) {
                                        return false;
                                    }
                                    boolean reject = !requirements.test(mob);
                                    if (mob instanceof Neollager neollager) {
                                        reject |= neollager.isMagic() != trainsMagic;
                                    }
                                    return reject;
                                });
                            }
                            int i = 0;
                            BlockEntity blockEntity = this.level.getBlockEntity(decreePos);
                            for (Mob mob : list) {
                                if (mob instanceof IServant servant) {
                                    boolean command = true;
                                    boolean alert = true;
                                    if (servant instanceof RaiderServant raiderServant) {
                                        if (raiderServant.getLeader() == null) {
                                            if (raiderServant instanceof ILooter looter && blockEntity instanceof ChestBlockEntity chestBlock) {
                                                if (chestBlock.getBlockState().is(ModTags.Blocks.RAIDING_CHESTS) && looter.getDumpChestPos() == null) {
                                                    looter.setDumpChestPos(decreePos);
                                                    looter.setDumpChestDim(this.level.dimension());
                                                } else if (looter.getChestPos() == null) {
                                                    looter.setChestPos(decreePos);
                                                    looter.setChestDim(this.level.dimension());
                                                } else {
                                                    alert = false;
                                                }
                                                command = false;
                                            } else if (blockEntity instanceof OminousIdolBlockEntity idolBlock && idolBlock.getTrueOwner() == raiderServant.getTrueOwner() && raiderServant.canLinkToIdol()) {
                                                if (raiderServant.getIdol() != idolBlock && idolBlock.hasSpace()) {
                                                    idolBlock.addIllager(raiderServant);
                                                    raiderServant.setRevivePos(idolBlock.getBlockPos());
                                                    if (idolBlock.getLevel() != null) {
                                                        raiderServant.setReviveDim(idolBlock.getLevel().dimension());
                                                    }
                                                } else {
                                                    alert = false;
                                                }
                                                command = false;
                                            }
                                        } else {
                                            alert = false;
                                            command = false;
                                        }
                                    }
                                    if (command) {
                                        servant.setCommandPos(decreePos);
                                        servant.setBoundPos(decreePos);
                                    }
                                    if (alert) {
                                        ++i;
                                    }
                                }
                            }
                            if (i > 0) {
                                this.glowTick = 20;
                                this.level.playSound(null, this.worldPosition, ModSounds.DECREE.get(), SoundSource.BLOCKS, 0.5F, 0.75F);
                            }
                        }
                    }
                }
                if (this.glowTick > 0) {
                    --this.glowTick;
                }
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(OminousDecreeBlock.POWERED, this.glowTick > 0), 3);
            }
        }
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
        this.markUpdated();
    }

    public boolean isShowBlock(){
        return this.showBlock;
    }

    public void setShowBlock(boolean showBlock){
        this.showBlock = showBlock;
        this.markUpdated();
    }

    public void setEntityType(EntityType<?> entityType) {
        if (entityType == null) {
            this.entityType = new CompoundTag();
        } else {
            ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            this.entityType.putString("id", location != null ? location.toString() : "minecraft:pig");
        }
    }

    public void setEntityType(CompoundTag tag){
        if (tag == null) {
            this.entityType = new CompoundTag();
        } else {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(tag.getString("id"));
            this.entityType.putString("id", resourcelocation != null ? resourcelocation.toString() : "minecraft:pig");
        }
    }

    public CompoundTag getEntityTypeTag() {
        return this.entityType;
    }

    @Nullable
    public EntityType<?> getEntityType() {
        if (EntityType.by(this.getEntityTypeTag()).isPresent()) {
            return EntityType.by(this.getEntityTypeTag()).get();
        }
        return null;
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void readNetwork(CompoundTag tag) {
        super.readNetwork(tag);
        if (tag.contains("showBlock")) {
            this.showBlock = tag.getBoolean("showBlock");
        }
        if (tag.contains("DecreePosition")) {
            Optional<GlobalPos> optional = GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("DecreePosition")).resultOrPartial(Goety.LOGGER::error);
            optional.ifPresent(this::setDecreePos);
        }
        if (tag.contains("EntityType")) {
            this.entityType = tag.getCompound("EntityType");
        }
        if (tag.contains("Range")) {
            this.setRange(tag.getDouble("Range"));
        }
        if (tag.contains("Mode")) {
            this.setMode(tag.getString("Mode"));
        }
    }

    public CompoundTag writeNetwork(CompoundTag tag) {
        CompoundTag tag1 = super.writeNetwork(tag);
        tag1.putBoolean("showBlock", this.showBlock);
        if (this.getDecreePos() != null) {
            Optional<GlobalPos> globalPos = Optional.of(this.getDecreePos());
            globalPos.flatMap((p_219745_) -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, p_219745_).resultOrPartial(Goety.LOGGER::error)).ifPresent((p_219756_) -> {
                tag.put("DecreePosition", p_219756_);
            });
        }
        tag1.put("EntityType", this.entityType);
        tag1.putDouble("Range", this.getRange());
        tag1.putString("Mode", this.getMode().getName());
        return tag1;
    }

    public enum DecreeMode {
        BABY_ONLY("Baby"),
        ADULT_ONLY("Adult"),
        ALL("All");
        final String name;

        DecreeMode(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
