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

    public static final RegistryObject<SoundEvent> SKULL_LORD_AMBIENT = create("skull_lord_ambient");
    public static final RegistryObject<SoundEvent> SKULL_LORD_HURT = create("skull_lord_hurt");
    public static final RegistryObject<SoundEvent> SKULL_LORD_CHARGE = create("skull_lord_charge");
    public static final RegistryObject<SoundEvent> SKULL_LORD_SHOOT = create("skull_lord_shoot");
    public static final RegistryObject<SoundEvent> SKULL_LORD_LASER_BEGIN = create("skull_lord_laser_begin");
    public static final RegistryObject<SoundEvent> SKULL_LORD_LASER_START = create("skull_lord_laser_start");
    public static final RegistryObject<SoundEvent> SKULL_LORD_FLY = create("skull_lord_fly");
    public static final RegistryObject<SoundEvent> SKULL_LORD_DEATH = create("skull_lord_death");

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

    public static final RegistryObject<SoundEvent> PIKER_AMBIENT = create("piker_ambient");
    public static final RegistryObject<SoundEvent> PIKER_HURT = create("piker_hurt");
    public static final RegistryObject<SoundEvent> PIKER_SWING = create("piker_swing");
    public static final RegistryObject<SoundEvent> PIKER_PIKE = create("piker_pike");
    public static final RegistryObject<SoundEvent> PIKER_CELEBRATE = create("piker_celebrate");
    public static final RegistryObject<SoundEvent> PIKER_STEP = create("piker_step");
    public static final RegistryObject<SoundEvent> PIKER_DEATH = create("piker_death");

    public static final RegistryObject<SoundEvent> PREACHER_AMBIENT = create("preacher_ambient");
    public static final RegistryObject<SoundEvent> PREACHER_HURT = create("preacher_hurt");
    public static final RegistryObject<SoundEvent> PREACHER_CAST = create("preacher_cast");
    public static final RegistryObject<SoundEvent> PREACHER_DEATH = create("preacher_death");

    public static final RegistryObject<SoundEvent> WRAITH_AMBIENT = create("wraith_ambient");
    public static final RegistryObject<SoundEvent> WRAITH_HURT = create("wraith_hurt");
    public static final RegistryObject<SoundEvent> WRAITH_FLY = create("wraith_fly");
    public static final RegistryObject<SoundEvent> WRAITH_ATTACK = create("wraith_attack");
    public static final RegistryObject<SoundEvent> WRAITH_PUKE = create("wraith_puke");
    public static final RegistryObject<SoundEvent> WRAITH_FIRE = create("wraith_fire");
    public static final RegistryObject<SoundEvent> WRAITH_TELEPORT = create("wraith_teleport");
    public static final RegistryObject<SoundEvent> WRAITH_DEATH = create("wraith_death");

    public static final RegistryObject<SoundEvent> NECROMANCER_AMBIENT = create("necromancer_ambient");
    public static final RegistryObject<SoundEvent> NECROMANCER_HURT = create("necromancer_hurt");
    public static final RegistryObject<SoundEvent> NECROMANCER_LAUGH = create("necromancer_laugh");
    public static final RegistryObject<SoundEvent> NECROMANCER_STEP = create("necromancer_step");
    public static final RegistryObject<SoundEvent> NECROMANCER_DEATH = create("necromancer_death");

    public static final RegistryObject<SoundEvent> VANGUARD_AMBIENT = create("vanguard_ambient");
    public static final RegistryObject<SoundEvent> VANGUARD_HURT = create("vanguard_hurt");
    public static final RegistryObject<SoundEvent> VANGUARD_SPEAR = create("vanguard_spear");
    public static final RegistryObject<SoundEvent> VANGUARD_STEP = create("vanguard_step");
    public static final RegistryObject<SoundEvent> VANGUARD_DEATH = create("vanguard_death");

    public static final RegistryObject<SoundEvent> HAUNTED_ARMOR_AMBIENT = create("haunted_armor_ambient");
    public static final RegistryObject<SoundEvent> HAUNTED_ARMOR_HURT = create("haunted_armor_hurt");
    public static final RegistryObject<SoundEvent> HAUNTED_ARMOR_STEP = create("haunted_armor_step");
    public static final RegistryObject<SoundEvent> HAUNTED_ARMOR_DEATH = create("haunted_armor_death");

    public static final RegistryObject<SoundEvent> ZOMBIE_RAVAGER_AMBIENT = create("zombie_ravager_ambient");
    public static final RegistryObject<SoundEvent> ZOMBIE_RAVAGER_HURT = create("zombie_ravager_hurt");
    public static final RegistryObject<SoundEvent> ZOMBIE_RAVAGER_BITE = create("zombie_ravager_bite");
    public static final RegistryObject<SoundEvent> ZOMBIE_RAVAGER_STUN = create("zombie_ravager_stun");
    public static final RegistryObject<SoundEvent> ZOMBIE_RAVAGER_ROAR = create("zombie_ravager_roar");
    public static final RegistryObject<SoundEvent> ZOMBIE_RAVAGER_STEP = create("zombie_ravager_step");
    public static final RegistryObject<SoundEvent> ZOMBIE_RAVAGER_DEATH = create("zombie_ravager_death");

    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_AMBIENT = create("redstone_golem_ambient");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_HURT = create("redstone_golem_hurt");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_SUMMON = create("redstone_golem_summon");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_PRE_ATTACK = create("redstone_golem_pre_attack");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_ATTACK = create("redstone_golem_attack");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_CHEST = create("redstone_golem_chest");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_GROWL = create("redstone_golem_growl");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_MINE_SPAWN = create("redstone_golem_mine_spawn");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_STEP = create("redstone_golem_step");
    public static final RegistryObject<SoundEvent> REDSTONE_GOLEM_DEATH = create("redstone_golem_death");

    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_AMBIENT = create("grave_golem_ambient");
    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_HURT = create("grave_golem_hurt");
    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_AWAKEN = create("grave_golem_awaken");
    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_ARM = create("grave_golem_arm");
    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_BLAST = create("grave_golem_blast");
    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_GROWL = create("grave_golem_growl");
    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_STEP = create("grave_golem_step");
    public static final RegistryObject<SoundEvent> GRAVE_GOLEM_DEATH = create("grave_golem_death");

    public static final RegistryObject<SoundEvent> HAUNT_AMBIENT = create("haunt_ambient");
    public static final RegistryObject<SoundEvent> HAUNT_HURT = create("haunt_hurt");
    public static final RegistryObject<SoundEvent> HAUNT_FLY = create("haunt_fly");

    public static final RegistryObject<SoundEvent> MINISTER_AMBIENT = create("minister_ambient");
    public static final RegistryObject<SoundEvent> MINISTER_HURT = create("minister_hurt");
    public static final RegistryObject<SoundEvent> MINISTER_LAUGH = create("minister_laugh");
    public static final RegistryObject<SoundEvent> MINISTER_COMMAND = create("minister_command");
    public static final RegistryObject<SoundEvent> MINISTER_CAST = create("minister_cast");
    public static final RegistryObject<SoundEvent> MINISTER_SPEECH = create("minister_speech");
    public static final RegistryObject<SoundEvent> MINISTER_CELEBRATE = create("minister_celebrate");
    public static final RegistryObject<SoundEvent> MINISTER_DEATH = create("minister_death");

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

    public static final RegistryObject<SoundEvent> WALL_SPAWN = create("wall_spawn");
    public static final RegistryObject<SoundEvent> WALL_HIT = create("wall_hit");
    public static final RegistryObject<SoundEvent> WALL_ERUPT = create("wall_erupt");
    public static final RegistryObject<SoundEvent> WALL_DISAPPEAR = create("wall_disappear");

    public static final RegistryObject<SoundEvent> BOMB_SPAWN = create("bomb_spawn");
    public static final RegistryObject<SoundEvent> BOMB_PULSE = create("bomb_pulse");
    public static final RegistryObject<SoundEvent> BOMB_FUSE = create("bomb_fuse");
    public static final RegistryObject<SoundEvent> BOMB_LOAD = create("bomb_load");
    public static final RegistryObject<SoundEvent> BOMB_SPARKLE = create("bomb_sparkle");

    public static final RegistryObject<SoundEvent> SPIDER_BITE = create("spider_bite");

    public static final RegistryObject<SoundEvent> ROAR_SPELL = create("roar_spell");
    public static final RegistryObject<SoundEvent> FIRE_BREATH = create("fire_breath");
    public static final RegistryObject<SoundEvent> SOUL_ARMOR = create("soul_armor");
    public static final RegistryObject<SoundEvent> PREPARE_SPELL = create("prepare_spell");
    public static final RegistryObject<SoundEvent> PREPARE_SUMMON = create("prepare_summon");
    public static final RegistryObject<SoundEvent> CAST_SPELL = create("cast_spell");
    public static final RegistryObject<SoundEvent> NECRO_CAST = create("necro_cast");
    public static final RegistryObject<SoundEvent> SUMMON_SPELL = create("summon_spell");
    public static final RegistryObject<SoundEvent> VANGUARD_SPELL = create("vanguard_spell");
    public static final RegistryObject<SoundEvent> VANGUARD_SUMMON = create("vanguard_summon");
    public static final RegistryObject<SoundEvent> COMMAND = create("command");
    public static final RegistryObject<SoundEvent> BOLT_IMPACT = create("bolt_impact");
    public static final RegistryObject<SoundEvent> SOUL_EXPLODE = create("soul_explode");
    public static final RegistryObject<SoundEvent> HEAL_SPELL = create("heal_spell");
    public static final RegistryObject<SoundEvent> WIND = create("wind");
    public static final RegistryObject<SoundEvent> UPDRAFT_BLAST = create("updraft_blast");
    public static final RegistryObject<SoundEvent> WIND_BLAST = create("wind_blast");
    public static final RegistryObject<SoundEvent> ZAP = create("zap");
    public static final RegistryObject<SoundEvent> RUMBLE = create("rumble");
    public static final RegistryObject<SoundEvent> TOOTH_SPAWN = create("tooth_spawn");
    public static final RegistryObject<SoundEvent> IMPALE = create("impale");
    public static final RegistryObject<SoundEvent> ICE_SPIKE_CAST = create("ice_spike_cast");
    public static final RegistryObject<SoundEvent> ICE_SPIKE_HIT = create("ice_spike_hit");
    public static final RegistryObject<SoundEvent> SPELL_FAIL = create("spell_fail");
    public static final RegistryObject<SoundEvent> SOUL_EAT = create("soul_eat");

    public static final RegistryObject<SoundEvent> SCYTHE_SWING = create("scythe_swing");
    public static final RegistryObject<SoundEvent> SCYTHE_HIT = create("scythe_hit");
    public static final RegistryObject<SoundEvent> SCYTHE_HIT_MEATY = create("scythe_hit_meaty");

    public static final RegistryObject<SoundEvent> FLAME_CAPTURE_CATCH = create("flame_capture_catch");
    public static final RegistryObject<SoundEvent> FLAME_CAPTURE_RELEASE = create("flame_capture_release");

    public static final RegistryObject<SoundEvent> REDSTONE_EXPLODE = create("redstone_explode");

    public static final RegistryObject<SoundEvent> BLAST_FUNGUS_THROW = create("blast_fungus_throw");
    public static final RegistryObject<SoundEvent> BLAST_FUNGUS_EXPLODE = create("blast_fungus_explode");

    public static final RegistryObject<SoundEvent> FOCUS_PICK = create("focus_pick");

    public static final RegistryObject<SoundEvent> CORRUPT_BEAM_START = create("corrupt_beam_start");
    public static final RegistryObject<SoundEvent> CORRUPT_BEAM_LOOP = create("corrupt_beam_loop");
    public static final RegistryObject<SoundEvent> CORRUPT_BEAM_SOUL = create("corrupt_beam_soul");

    public static final RegistryObject<SoundEvent> BREW_GAS = create("brew_gas");

    public static final RegistryObject<SoundEvent> ALTAR_START = create("altar_start");
    public static final RegistryObject<SoundEvent> ALTAR_LOOP = create("altar_loop");
    public static final RegistryObject<SoundEvent> ALTAR_FINISH = create("altar_finish");

    public static final RegistryObject<SoundEvent> CAULDRON_BUBBLES = create("cauldron_bubbles");
    public static final RegistryObject<SoundEvent> CAULDRON_CHIMES = create("cauldron_chimes");

    public static final RegistryObject<SoundEvent> FIRE_TORNADO_AMBIENT = create("fire_tornado_ambient");

    public static final RegistryObject<SoundEvent> APOSTLE_THEME = create("apostle_theme");
    public static final RegistryObject<SoundEvent> APOSTLE_THEME_POST = create("apostle_theme_post");
    public static final RegistryObject<SoundEvent> VIZIER_THEME = create("vizier_theme");

    public static final RegistryObject<SoundEvent> MUSIC_DISC_APOSTLE = create("apostle_theme_disc");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_VIZIER = create("vizier_theme_disc");

    public static final RegistryObject<SoundEvent> BOSS_POST = create("boss_post");

    static RegistryObject<SoundEvent> create(String name) {
        SoundEvent event = SoundEvent.createVariableRangeEvent(Goety.location(name));
        return SOUNDS.register(name, () -> event);
    }
}
