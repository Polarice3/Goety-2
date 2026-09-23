package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.IPosHolder;
import com.Polarice3.Goety.api.items.IShowOutlines;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.OminousDecreeBlockEntity;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class OminousDecreeItem extends BlockItemBase implements IShowOutlines, IPosHolder {
    public static String TAG_MODE = "Mode";
    public static String TAG_RANGE = "Range";
    public static String TAG_ENTITY_TYPE = "EntityType";

    public OminousDecreeItem() {
        super(ModBlocks.OMINOUS_DECREE.get());
    }

    public static String getMode(ItemStack itemStack) {
        String mode = "All";
        if (itemStack.getTag() != null) {
            if (itemStack.getTag().contains(TAG_MODE)) {
                return itemStack.getTag().getString(TAG_MODE);
            }
        }
        return mode;
    }

    public static void setMode(ItemStack itemStack, String mode) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putString(TAG_MODE, mode);
        itemStack.setTag(nbt);
    }

    public static void cycleMode(Player player, ItemStack itemStack, Level level, BlockPos blockPos) {
        if (getMode(itemStack).equals(OminousDecreeBlockEntity.DecreeMode.ALL.getName())) {
            setMode(itemStack, OminousDecreeBlockEntity.DecreeMode.BABY_ONLY.getName());
        } else if (getMode(itemStack).equals(OminousDecreeBlockEntity.DecreeMode.BABY_ONLY.getName())) {
            setMode(itemStack, OminousDecreeBlockEntity.DecreeMode.ADULT_ONLY.getName());
        } else {
            setMode(itemStack, OminousDecreeBlockEntity.DecreeMode.ALL.getName());
        }
        level.playSound(player, blockPos, ModSounds.CAST_SPELL.get(), SoundSource.BLOCKS, 0.25F, 2.0F);
    }

    public static double getRange(ItemStack itemStack) {
        double range = 4.0D;
        if (itemStack.getTag() != null) {
            if (itemStack.getTag().contains(TAG_RANGE)) {
                return itemStack.getTag().getDouble(TAG_RANGE);
            }
        }
        return range;
    }

    public static void setRange(ItemStack itemStack, double range) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putDouble(TAG_RANGE, range);
        itemStack.setTag(nbt);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!entity.level.isClientSide) {
            if (entity instanceof IServant servant && servant.getTrueOwner() == player) {
                addEntityTypeTags(stack, entity.getType());
                ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F));
            }
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        BlockPos blockpos = context.getClickedPos();
        if (player != null && player.isShiftKeyDown()) {
            ItemStack held = player.getItemInHand(hand);
            if (!held.isEmpty() && held.getItem() instanceof IPosHolder) {
                player.swing(hand);
                this.addBlockEntityTags(held, player, player.level().dimension(), blockpos, null);
                player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
                player.level.playLocalSound(blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1.0F, 0.45F, false);
                return InteractionResult.sidedSuccess(player.level.isClientSide);
            }
        }
        return super.useOn(context);
    }

    public static void addEntityTypeTags(ItemStack itemStack, EntityType<?> entityType) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        if (entityType != null) {
            CompoundTag compoundTag = new CompoundTag();
            ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            compoundTag.putString("id", location != null ? location.toString() : "minecraft:pig");
            nbt.put(TAG_ENTITY_TYPE, compoundTag);
        }
        itemStack.setTag(nbt);
    }

    @Nullable
    public static EntityType<?> getEntityType(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        if (nbt.contains(TAG_ENTITY_TYPE)) {
            CompoundTag compoundTag = nbt.getCompound(TAG_ENTITY_TYPE);
            if (EntityType.by(compoundTag).isPresent()) {
                return EntityType.by(compoundTag).get();
            }
        }
        return null;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() || player.isCrouching()){
            if (itemstack.getItem() instanceof IPosHolder){
                if (IPosHolder.hasBlock(itemstack) && itemstack.getTag() != null){
                    itemstack.setTag(new CompoundTag());
                }
            }
        } else {
            cycleMode(player, itemstack, level, player.blockPosition());
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getTag() != null) {
            GlobalPos globalPos = IPosHolder.getPosition(stack.getTag());
            if (globalPos != null) {
                tooltip.add(Component.translatable("info.goety.focus.Pos2").append(" ").append(Component.translatable("info.goety.focus.PosNum", globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ())));
                tooltip.add(Component.translatable("info.goety.focus.PosDim", globalPos.dimension().location().toString()));
            }
            if (getEntityType(stack) != null) {
                EntityType<?> entityType = getEntityType(stack);
                if (entityType != null) {
                    tooltip.add(Component.translatable("tooltip.goety.decree.type").withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.translatable(entityType.getDescriptionId())));
                }
            }
            tooltip.add(Component.translatable("tooltip.goety.blockRange", getRange(stack)).withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(Component.translatable("tooltip.goety.decree.mode").withStyle(ChatFormatting.AQUA).append(Component.translatable("tooltip.goety.decree." + getMode(stack).toLowerCase(Locale.ROOT))));
        }
    }
}
