package com.Polarice3.Goety.init;

import com.Polarice3.Goety.Goety;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Goety.MOD_ID);

    public static void init(){
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<SoundEvent> APOSTLE_AMBIENT = create("apostle_ambient");
    public static final RegistryObject<SoundEvent> APOSTLE_HURT = create("apostle_hurt");
    public static final RegistryObject<SoundEvent> APOSTLE_PREPARE_SPELL = create("apostle_prepare_spell");
    public static final RegistryObject<SoundEvent> APOSTLE_PREPARE_SUMMON = create("apostle_prepare_summon");
    public static final RegistryObject<SoundEvent> APOSTLE_CAST_SPELL = create("apostle_cast_spell");
    public static final RegistryObject<SoundEvent> APOSTLE_TELEPORT = create("apostle_teleport");
    public static final RegistryObject<SoundEvent> APOSTLE_PREDEATH = create("apostle_predeath");
    public static final RegistryObject<SoundEvent> APOSTLE_DEATH = create("apostle_death");

    public static final RegistryObject<SoundEvent> WARLOCK_AMBIENT = create("warlock_ambient");
    public static final RegistryObject<SoundEvent> WARLOCK_HURT = create("warlock_hurt");
    public static final RegistryObject<SoundEvent> WARLOCK_CELEBRATE = create("warlock_celebrate");
    public static final RegistryObject<SoundEvent> WARLOCK_DEATH = create("warlock_death");

    public static final RegistryObject<SoundEvent> CRONE_AMBIENT = create("crone_ambient");
    public static final RegistryObject<SoundEvent> CRONE_LAUGH = create("crone_laugh");
    public static final RegistryObject<SoundEvent> CRONE_DEATH = create("crone_death");

    public static final RegistryObject<SoundEvent> TORMENTOR_AMBIENT = create("tormentor_ambient");
    public static final RegistryObject<SoundEvent> TORMENTOR_HURT = create("tormentor_hurt");
    public static final RegistryObject<SoundEvent> TORMENTOR_CHARGE = create("tormentor_charge");
    public static final RegistryObject<SoundEvent> TORMENTOR_CELEBRATE = create("tormentor_celebrate");
    public static final RegistryObject<SoundEvent> TORMENTOR_DEATH = create("tormentor_death");

    public static final RegistryObject<SoundEvent> INQUILLAGER_AMBIENT = create("inquillager_ambient");
    public static final RegistryObject<SoundEvent> INQUILLAGER_HURT = create("inquillager_hurt");
    public static final RegistryObject<SoundEvent> INQUILLAGER_CELEBRATE = create("inquillager_celebrate");
    public static final RegistryObject<SoundEvent> INQUILLAGER_DEATH = create("inquillager_death");

    public static final RegistryObject<SoundEvent> CONQUILLAGER_AMBIENT = create("conquillager_ambient");
    public static final RegistryObject<SoundEvent> CONQUILLAGER_HURT = create("conquillager_hurt");
    public static final RegistryObject<SoundEvent> CONQUILLAGER_CELEBRATE = create("conquillager_celebrate");
    public static final RegistryObject<SoundEvent> CONQUILLAGER_DEATH = create("conquillager_death");

    public static final RegistryObject<SoundEvent> WRAITH_AMBIENT = create("wraith_ambient");
    public static final RegistryObject<SoundEvent> WRAITH_HURT = create("wraith_hurt");
    public static final RegistryObject<SoundEvent> WRAITH_FLY = create("wraith_fly");
    public static final RegistryObject<SoundEvent> WRAITH_ATTACK = create("wraith_attack");
    public static final RegistryObject<SoundEvent> WRAITH_FIRE = create("wraith_fire");
    public static final RegistryObject<SoundEvent> WRAITH_TELEPORT = create("wraith_teleport");
    public static final RegistryObject<SoundEvent> WRAITH_DEATH = create("wraith_death");

    public static final RegistryObject<SoundEvent> VIZIER_AMBIENT = create("vizier_ambient");
    public static final RegistryObject<SoundEvent> VIZIER_HURT = create("vizier_hurt");
    public static final RegistryObject<SoundEvent> VIZIER_CONFUSE = create("vizier_confuse");
    public static final RegistryObject<SoundEvent> VIZIER_RAGE = create("vizier_rage");
    public static final RegistryObject<SoundEvent> VIZIER_CELEBRATE = create("vizier_celebrate");
    public static final RegistryObject<SoundEvent> VIZIER_SCREAM = create("vizier_scream");
    public static final RegistryObject<SoundEvent> VIZIER_DEATH = create("vizier_death");

    public static final RegistryObject<SoundEvent> ICE_CHUNK_IDLE = create("ice_chunk_idle");
    public static final RegistryObject<SoundEvent> ICE_CHUNK_SUMMON = create("ice_chunk_summon");
    public static final RegistryObject<SoundEvent> ICE_CHUNK_HIT = create("ice_chunk_hit");

    public static final RegistryObject<SoundEvent> ROAR_SPELL = create("roar_spell");
    public static final RegistryObject<SoundEvent> FIRE_BREATH = create("fire_breath");
    public static final RegistryObject<SoundEvent> PREPARE_SPELL = create("prepare_spell");
    public static final RegistryObject<SoundEvent> CAST_SPELL = create("cast_spell");
    public static final RegistryObject<SoundEvent> SUMMON_SPELL = create("summon_spell");
    public static final RegistryObject<SoundEvent> BOLT_IMPACT = create("bolt_impact");
    public static final RegistryObject<SoundEvent> SOUL_EXPLODE = create("soul_explode");
    public static final RegistryObject<SoundEvent> WIND = create("wind");
    public static final RegistryObject<SoundEvent> UPDRAFT_BLAST = create("updraft_blast");
    public static final RegistryObject<SoundEvent> WIND_BLAST = create("wind_blast");
    public static final RegistryObject<SoundEvent> ZAP = create("zap");
    public static final RegistryObject<SoundEvent> RUMBLE = create("rumble");
    public static final RegistryObject<SoundEvent> SPELL_FAIL = create("spell_fail");
    public static final RegistryObject<SoundEvent> SOUL_EAT = create("soul_eat");

    public static final RegistryObject<SoundEvent> SCYTHE_SWING = create("scythe_swing");
    public static final RegistryObject<SoundEvent> SCYTHE_HIT = create("scythe_hit");
    public static final RegistryObject<SoundEvent> SCYTHE_HIT_MEATY = create("scythe_hit_meaty");

    public static final RegistryObject<SoundEvent> FLAME_CAPTURE_CATCH = create("flame_capture_catch");
    public static final RegistryObject<SoundEvent> FLAME_CAPTURE_RELEASE = create("flame_capture_release");

    public static final RegistryObject<SoundEvent> BLAST_FUNGUS_THROW = create("blast_fungus_throw");
    public static final RegistryObject<SoundEvent> BLAST_FUNGUS_EXPLODE = create("blast_fungus_explode");

    public static final RegistryObject<SoundEvent> BREW_GAS = create("brew_gas");

    public static final RegistryObject<SoundEvent> ALTAR_START = create("altar_start");
    public static final RegistryObject<SoundEvent> ALTAR_LOOP = create("altar_loop");
    public static final RegistryObject<SoundEvent> ALTAR_FINISH = create("altar_finish");

    public static final RegistryObject<SoundEvent> FIRE_TORNADO_AMBIENT = create("fire_tornado_ambient");

    public static final RegistryObject<SoundEvent> APOSTLE_THEME = create("apostle_theme");
    public static final RegistryObject<SoundEvent> APOSTLE_THEME_POST = create("apostle_theme_post");
    public static final RegistryObject<SoundEvent> VIZIER_THEME = create("vizier_theme");

    public static final RegistryObject<SoundEvent> BOSS_POST = create("boss_post");

    static RegistryObject<SoundEvent> create(String name) {
        SoundEvent event = new SoundEvent(Goety.location(name));
        return SOUNDS.register(name, () -> event);
    }
}
