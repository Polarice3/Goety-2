package com.Polarice3.Goety.common.items;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.entities.ally.BlackWolf;
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

public class HowlingSoul extends Item {
    public HowlingSoul(){
        super(new Properties()
                .tab(Goety.TAB)
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1)
        );
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.getCommandSenderWorld();

        if (getBlackBeast(stack, level) != null) {
            BlackBeast blackBeast = getBlackBeast(stack, level);
            if (blackBeast != null) {
                boolean flag = target instanceof BlackWolf;
                if (flag) {
                    if (blackBeast.getTrueOwner() == player) {
                        blackBeast.setHealth(blackBeast.getMaxHealth());
                        blackBeast.setPos(target.getX(), target.getY(), target.getZ());
                        blackBeast.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
                        if (level.addFreshEntity(blackBeast)) {
                            blackBeast.spawnAnim();
                            if (level instanceof ServerLevel serverLevel) {
                                for (int i = 0; i < 8; ++i) {
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SCULK_SOUL, blackBeast);
                                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.POOF, blackBeast);
                                }
                            }
                            blackBeast.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                            blackBeast.playSound(ModSounds.BLACK_BEAST_ROAR.get(), 2.0F, 0.5F);
                            target.discard();
                            player.swing(hand);
                            player.getCooldowns().addCooldown(ModItems.HOWLING_SOUL.get(), MathHelper.secondsToTicks(30));
                            stack.shrink(1);
                        }
                    }
                }
            }
        }

        return super.interactLivingEntity(stack, player, target, hand);
    }

    public static void setBlackBeast(BlackBeast blackBeast, ItemStack stack) {
        blackBeast.stopRiding();
        blackBeast.ejectPassengers();

        CompoundTag entityTag = new CompoundTag();
        ResourceLocation typesKey = ForgeRegistries.ENTITY_TYPES.getKey(blackBeast.getType());

        if (typesKey != null) {
            entityTag.putString("entity", typesKey.toString());
            if (blackBeast.hasCustomName()) {
                entityTag.putString("name", Objects.requireNonNull(blackBeast.getCustomName()).getString());
            }
            blackBeast.save(entityTag);
            CompoundTag itemNBT = stack.getOrCreateTag();
            itemNBT.put("entity", entityTag);
        }
    }

    public static BlackBeast getBlackBeast(ItemStack stack, Level level) {
        CompoundTag itemTag = stack.getTag();

        if (itemTag != null) {
            CompoundTag entityTag = itemTag.getCompound("entity");
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTag.getString("entity")));
            if (entityType != null) {
                Entity entity = entityType.create(level);
                if (level instanceof ServerLevel && entity != null) {
                    entity.load(entityTag);
                }

                if (entity instanceof BlackBeast blackBeast){
                    return blackBeast;
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
        if (level != null && getBlackBeast(stack, level) != null)  {
            BlackBeast blackBeast = getBlackBeast(stack, level);

            if (blackBeast == null) {
                return;
            }

            if (stack.getTag() != null) {
                if (stack.getTag().contains("owner_name")) {
                    tooltip.add(Component.translatable("tooltip.goety.arcaPlayer").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("" + stack.getTag().getString("owner_name")).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
                }
            }

            if (blackBeast.getCustomName() != null) {
                tooltip.add(Component.translatable("tooltip.goety.customName").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))).append(Component.literal("").append(blackBeast.getCustomName()).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY)))));
            }
        }
    }
}
