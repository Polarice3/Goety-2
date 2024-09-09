package com.Polarice3.Goety.compat.curios;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.compat.ICompatable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CuriosIntegration implements ICompatable {

    private static final Map<Item, String> TYPES = ImmutableMap.<Item, String>builder()
            .put(ModItems.RING_OF_WANT.get(), "ring")
            .put(ModItems.RING_OF_FORCE.get(), "ring")
            .put(ModItems.RING_OF_THE_FORGE.get(), "ring")
            .put(ModItems.RING_OF_THE_DRAGON.get(), "ring")
            .put(ModItems.DARK_HAT.get(), "head")
            .put(ModItems.GRAND_TURBAN.get(), "head")
            .put(ModItems.FROST_CROWN.get(), "head")
            .put(ModItems.WILD_CROWN.get(), "head")
            .put(ModItems.NETHER_CROWN.get(), "head")
            .put(ModItems.WITCH_HAT.get(), "head")
            .put(ModItems.WITCH_HAT_HEDGE.get(), "head")
            .put(ModItems.CRONE_HAT.get(), "head")
            .put(ModItems.NECRO_CROWN.get(), "head")
            .put(ModItems.NAMELESS_CROWN.get(), "head")
            .put(ModItems.TARGETING_MONOCLE.get(), "head")
            .put(ModItems.AMETHYST_NECKLACE.get(), "necklace")
            .put(ModItems.PENDANT_OF_HUNGER.get(), "necklace")
            .put(ModItems.STAR_AMULET.get(), "necklace")
            .put(ModItems.SEA_AMULET.get(), "necklace")
            .put(ModItems.FELINE_AMULET.get(), "necklace")
            .put(ModItems.DARK_ROBE.get(), "body")
            .put(ModItems.GRAND_ROBE.get(), "body")
            .put(ModItems.FROST_ROBE.get(), "body")
            .put(ModItems.FROST_ROBE_CRYO.get(), "body")
            .put(ModItems.WIND_ROBE.get(), "body")
            .put(ModItems.STORM_ROBE.get(), "body")
            .put(ModItems.WILD_ROBE.get(), "body")
            .put(ModItems.ILLUSION_ROBE.get(), "body")
            .put(ModItems.ILLUSION_ROBE_MIRROR.get(), "body")
            .put(ModItems.WITCH_ROBE.get(), "body")
            .put(ModItems.WITCH_ROBE_HEDGE.get(), "body")
            .put(ModItems.WARLOCK_ROBE.get(), "body")
            .put(ModItems.WARLOCK_ROBE_DARK.get(), "body")
            .put(ModItems.NETHER_ROBE.get(), "body")
            .put(ModItems.NETHER_ROBE_WARPED.get(), "body")
            .put(ModItems.NECRO_CAPE.get(), "back")
            .put(ModItems.NAMELESS_CAPE.get(), "back")
            .put(ModItems.GRAVE_GLOVE.get(), "hands")
            .put(ModItems.THRASH_GLOVE.get(), "hands")
            .put(ModItems.TOTEM_OF_ROOTS.get(), "charm")
            .put(ModItems.TOTEM_OF_SOULS.get(), "charm")
            .put(ModItems.ALARMING_CHARM.get(), "charm")
            .put(ModItems.FOCUS_BAG.get(), "belt")
            .put(ModItems.FOCUS_PACK.get(), "belt")
            .put(ModItems.BREW_BAG.get(), "belt")
            .put(ModItems.WARLOCK_SASH.get(), "belt")
            .put(ModItems.WAYFARERS_BELT.get(), "belt")
            .put(ModItems.SPITEFUL_BELT.get(), "belt")
            .build();

    public void setup(FMLCommonSetupEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::sendImc);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onCapabilitiesAttach);
    }

    private void sendImc(InterModEnqueueEvent event) {
        TYPES.values().stream().distinct().forEach(t -> InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(t).build()));
    }

    private void onCapabilitiesAttach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (TYPES.containsKey(stack.getItem())) {
            if (stack.getItem() instanceof ICurioItem iCurioItem) {
                ItemizedCurioCapability capability = new ItemizedCurioCapability(iCurioItem, stack);
                event.addCapability(new ResourceLocation(Goety.MOD_ID, "curios"), CurioItemCapability.createProvider(capability));
            }
        }
    }

    public static class ItemizedCurioCapability implements ICurio {
        private final ItemStack stack;
        private final ICurioItem curioItem;

        public ItemizedCurioCapability(ICurioItem curio, ItemStack stack) {
            this.curioItem = curio;
            this.stack = stack;
        }

        @Override
        public ItemStack getStack() {
            return this.stack;
        }

        @Override
        public void curioTick(SlotContext slotContext) {
            this.curioItem.curioTick(slotContext, this.getStack());
        }

        @Override
        public boolean canEquip(SlotContext slotContext) {
            return this.curioItem.canEquip(slotContext, this.getStack());
        }

        @Override
        public boolean canUnequip(SlotContext slotContext) {
            return this.curioItem.canUnequip(slotContext, this.getStack());
        }

        @Override
        public List<Component> getSlotsTooltip(List<Component> tooltips) {
            return this.curioItem.getSlotsTooltip(tooltips, this.getStack());
        }

        @Override
        public void curioBreak(SlotContext slotContext) {
            this.curioItem.curioBreak(slotContext, this.getStack());
        }

        @Override
        public boolean canSync(SlotContext slotContext) {
            return this.curioItem.canSync(slotContext, this.getStack());
        }

        @Nonnull
        @Override
        public CompoundTag writeSyncData(SlotContext slotContext) {
            return this.curioItem.writeSyncData(slotContext, this.getStack());
        }

        @Override
        public void readSyncData(SlotContext slotContext, CompoundTag compound) {
            this.curioItem.readSyncData(slotContext, compound, this.getStack());
        }

        @Nonnull
        @Override
        public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel,
                                    boolean recentlyHit) {
            return this.curioItem
                    .getDropRule(slotContext, source, lootingLevel, recentlyHit, this.getStack());
        }

        @Override
        public List<Component> getAttributesTooltip(List<Component> tooltips) {
            return this.curioItem.getAttributesTooltip(tooltips, this.getStack());
        }

        @Override
        public int getFortuneLevel(SlotContext slotContext, LootContext lootContext) {
            return this.curioItem.getFortuneLevel(slotContext, lootContext, this.getStack());
        }

        @Override
        public int getLootingLevel(SlotContext slotContext, DamageSource source, LivingEntity target,
                                   int baseLooting) {
            return this.curioItem
                    .getLootingLevel(slotContext, source, target, baseLooting, this.getStack());
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                            UUID uuid) {
            return this.curioItem.getAttributeModifiers(slotContext, uuid, this.getStack());
        }

        @Override
        public void onEquipFromUse(SlotContext slotContext) {
            this.curioItem.onEquipFromUse(slotContext, this.getStack());
        }

        @Override
        public boolean canEquipFromUse(SlotContext slotContext) {
            return this.curioItem.canEquipFromUse(slotContext, this.getStack());
        }

        @Override
        public void onEquip(SlotContext slotContext, ItemStack prevStack) {
            this.curioItem.onEquip(slotContext, prevStack, this.getStack());
        }

        @Override
        public void onUnequip(SlotContext slotContext, ItemStack newStack) {
            this.curioItem.onUnequip(slotContext, newStack, this.getStack());
        }

        @Nonnull
        @Override
        public SoundInfo getEquipSound(SlotContext slotContext) {
            return this.curioItem.getEquipSound(slotContext, this.getStack());
        }

        @Override
        public boolean makesPiglinsNeutral(SlotContext slotContext) {
            return this.curioItem.makesPiglinsNeutral(slotContext, this.getStack());
        }

        @Override
        public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan) {
            return this.curioItem.isEnderMask(slotContext, enderMan, this.getStack());
        }
    }

    public static class CurioItemCapability {

        public static ICapabilityProvider createProvider(final ICurio curio) {
            return new Provider(curio);
        }

        public static class Provider implements ICapabilityProvider {

            final LazyOptional<ICurio> capability;

            Provider(ICurio curio) {
                this.capability = LazyOptional.of(() -> curio);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, this.capability);
            }
        }
    }

}
