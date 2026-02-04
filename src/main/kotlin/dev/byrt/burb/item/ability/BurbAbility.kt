package dev.byrt.burb.item.ability

import org.bukkit.Material

enum class BurbAbility(val abilityName: String, val abilityLore: String, val abilityId: String, val abilityModel: String, val abilityMaterial: Material, val abilityCooldown: Int) {
    NULL("null", "null", "null", "null", Material.AIR, 0),

    /** Chilli Bean Bomb **/
    PLANTS_SCOUT_ABILITY_1("Chilli Bean Bomb", "A chilli bean with a short temper.","burb.character.plants_scout.ability.1", "red_dye",  Material.RED_DYE, 500),
    /** Pea Gatling **/
    PLANTS_SCOUT_ABILITY_2("Pea Gatling", "Line 'em up and knock 'em down.", "burb.character.plants_scout.ability.2", "orange_dye",  Material.ORANGE_DYE, 700),
    /** Hyper **/
    PLANTS_SCOUT_ABILITY_3("Hyper", "ZOOMIES!", "burb.character.plants_scout.ability.3", "yellow_dye",  Material.YELLOW_DYE, 400),

    /** Goop **/
    PLANTS_HEAVY_ABILITY_1("Goop", "Sticky goop that slows enemies.", "burb.character.plants_heavy.ability.1", "red_dye",  Material.RED_DYE, 160),
    /** Burrow **/
    PLANTS_HEAVY_ABILITY_2("Burrow", "Burrow into the ground and leap out.", "burb.character.plants_heavy.ability.2", "orange_dye",  Material.ORANGE_DYE, 200),
    /** Spikeweed **/
    PLANTS_HEAVY_ABILITY_3("Spikeweed", "Snare the zombies.", "burb.character.plants_heavy.ability.3", "yellow_dye",  Material.YELLOW_DYE, 120),

    /** Heal Beam **/
    PLANTS_HEALER_ABILITY_1("Heal Beam", "Solar powered healing.", "burb.character.plants_healer.ability.1", "red_dye",  Material.RED_DYE, 40),
    /** Sunbeam **/
    PLANTS_HEALER_ABILITY_2("Sunbeam", "The power of the sun, in the palm of my hand.", "burb.character.plants_healer.ability.2", "orange_dye",  Material.ORANGE_DYE, 550),
    /** Heal Flower **/
    PLANTS_HEALER_ABILITY_3("Heal Flower", "", "burb.character.plants_healer.ability.3", "yellow_dye",  Material.YELLOW_DYE, 400),

    /** Potato Mine **/
    PLANTS_RANGED_ABILITY_1("Potato Mine", "So cute, yet so deadly.", "burb.character.plants_ranged.ability.1", "red_dye",  Material.RED_DYE, 200),
    /** Escape **/
    PLANTS_RANGED_ABILITY_2("Escape", "360 NO SCOPE!", "burb.character.plants_ranged.ability.2", "orange_dye",  Material.ORANGE_DYE, 475),
    /** Tallnut Battlement **/
    PLANTS_RANGED_ABILITY_3("Tallnut Battlement", "Create your own cover.", "burb.character.plants_ranged.ability.3", "yellow_dye",  Material.YELLOW_DYE, 300),

    /** Zombie Stink Cloud **/
    ZOMBIES_SCOUT_ABILITY_1("Zombie Stink Cloud", "Whoever smelt it, dealt it.", "burb.character.zombies_scout.ability.1", "footsoldier_ability_stink_cloud",  Material.RED_DYE, 350),
    /** ZPG **/
    ZOMBIES_SCOUT_ABILITY_2("ZPG", "Who gave this zombie a rocket?", "burb.character.zombies_scout.ability.2", "footsoldier_ability_zpg",  Material.ORANGE_DYE, 675),
    /** Rocket Jump **/
    ZOMBIES_SCOUT_ABILITY_3("Rocket Jump", "I have the high ground.", "burb.character.zombies_scout.ability.3", "footsoldier_ability_rocket_jump",  Material.YELLOW_DYE, 450),

    /** Super Ultra Ball **/
    ZOMBIES_HEAVY_ABILITY_1("Super Ultra Ball", "FUS-RO-DAH!", "burb.character.zombies_heavy.ability.1", "red_dye",  Material.RED_DYE, 450),
    /** Turbo Twister **/
    ZOMBIES_HEAVY_ABILITY_2("Turbo Twister", "", "burb.character.zombies_heavy.ability.2", "orange_dye",  Material.ORANGE_DYE, 700),
    /** Heroic Kick **/
    ZOMBIES_HEAVY_ABILITY_3("Heroic Kick", "Strangely powerful toes.", "burb.character.zombies_heavy.ability.3", "yellow_dye",  Material.YELLOW_DYE, 250),

    /** Heal Beam of Science **/
    ZOMBIES_HEALER_ABILITY_1("Heal Beam of Science", "", "burb.character.zombies_healer.ability.1", "red_dye",  Material.RED_DYE, 40),
    /** Warp **/
    ZOMBIES_HEALER_ABILITY_2("Warp", "Transcend time and space, a few blocks forward.", "burb.character.zombies_healer.ability.2", "orange_dye",  Material.ORANGE_DYE, 175),
    /** Science Mine **/
    ZOMBIES_HEALER_ABILITY_3("Science Mine", "And now we wait...", "burb.character.zombies_healer.ability.3", "yellow_dye",  Material.YELLOW_DYE, 275),

    /** Barrel Blast **/
    ZOMBIES_RANGED_ABILITY_1("Barrel Blast", "", "burb.character.zombies_ranged.ability.1", "red_dye",  Material.RED_DYE, 750),
    /** Escape **/
    ZOMBIES_RANGED_ABILITY_2("Escape", "360 NO SCOPE", "burb.character.zombies_ranged.ability.2", "orange_dye",  Material.ORANGE_DYE, 575),
    /** Cannon Rodeo **/
    ZOMBIES_RANGED_ABILITY_3("Cannon Rodeo", "YEE-HAW!", "burb.character.zombies_ranged.ability.3", "yellow_dye",  Material.YELLOW_DYE, 625)
}