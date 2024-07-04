package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.StrayServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractCairnNecromancer;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class SoulJar extends Item {
    public SoulJar(){
        super(new Properties()
                .tab(Goety.TAB)
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1)
        );
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.getCommandSenderWorld();

        if (getNecromancer(stack, level) != null) {
            AbstractNecromancer necromancer = getNecromancer(stack, level);
            if (necromancer != null) {
                boolean flag;
                if (necromancer instanceof AbstractCairnNecromancer){
                    flag = target instanceof StrayServant || target instanceof Stray;
                } else {
                    flag = target instanceof SkeletonServant || target instanceof Skeleton;
                }
                if (flag) {
                    if (necromancer.getTrueOwner() == player) {
                        necromancer.setHealth(necromancer.getMaxHealth());
                        necromancer.setPos(target.getX(), target.getY(), target.getZ());
                        necromancer.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
                        if (level.addFreshEntity(necromancer)) {
                            necromancer.spawnAnim();
                            if (level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 8; ++i) {
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SCULK_SOUL, necromancer);
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.POOF, necromancer);
                                }
                            }
                            necromancer.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                            necromancer.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, 0.5F);
                            target.discard();
                            player.swing(hand);
                            player.getCooldowns().addCooldown(ModItems.SOUL_JAR.get(), MathHelper.secondsToTicks(30));
                            stack.shrink(1);
                        }
                    }
                }
            }
        }

        return super.interactLivingEntity(stack, player, target, hand);
    }

    public static void setNecromancer(AbstractNecromancer necromancer, ItemStack stack) {
        necromancer.stopRiding();
        necromancer.ejectPassengers();

        CompoundTag entityTag = new CompoundTag();
        ResourceLocation typesKey = ForgeRegistries.ENTITY_TYPES.getKey(necromancer.getType());

        if (typesKey != null) {
            entityTag.putString("entity", typesKey.toString());
            if (necromancer.hasCustomName()) {
                entityTag.putString("name", Objects.requireNonNull(necromancer.getCustomName()).getString());
            }
            necromancer.save(entityTag);
            CompoundTag itemNBT = stack.getOrCreateTag();
            itemNBT.put("entity", entityTag);
        }
    }

    public static AbstractNecromancer getNecromancer(ItemStack stack, Level level) {
        CompoundTag itemTag = stack.getTag();

        if (itemTag != null) {
            CompoundTag entityTag = itemTag.getCompound("entity");
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTag.getString("entity")));
            if (entityType != null) {
                Entity entity = entityType.create(level);
                if (level instanceof ServerLevel && entity != null) {
                    entity.load(entityTag);
                }

                if (entity instanceof AbstractNecromancer necromancer){
                    return necromancer;
                }
            }
        }

        return null;
    }

    public static void setOwnerName(@Nullable LivingEntity entity, ItemStack stack) {
        CompoundTag entityTag = stack.getOrCreateTag();
        if (entity != null) {
            entityTag.putString("owner_name", entity.getDisplayName().getString());
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (level != null && getNecromancer(stack, level) != null)  {
            AbstractNecromancer necromancer = getNecromancer(stack, level);

            if (necromancer == null) {
                return;
            }

            if (stack.getTag() != null) {
                if (stack.getTag().contains("owner_name")) {
                    tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + stack.getTag().getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                }
            }

            if (necromancer.getCustomName() != null) {
                tooltip.add(Component.translatable("tooltip.goety.customName").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("").append(necromancer.getCustomName()).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            }
        }
    }
}
