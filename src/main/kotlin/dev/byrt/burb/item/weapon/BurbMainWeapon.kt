package dev.byrt.burb.item.weapon

import org.bukkit.Material

/**
 * @param weaponName Display name
 * @param weaponLore Item lore
 * @param weaponDamage Damage dealt by weapon in half hearts
 * @param fireRate Delay of rate of fire in ticks
 * @param reloadSpeed Reload delay in ticks
 * @param maxAmmo Max amount of ammunition held by the weapon
 * @param material Item material
 */
enum class BurbMainWeapon(val weaponName: String, val weaponLore: String, val weaponType: BurbMainWeaponType, val weaponDamage: Double, val fireRate: Int, val reloadSpeed: Int, val maxAmmo: Int, val velocity: Double, val material: Material, val sound: String, val model: String) {
    NULL("null", "null", BurbMainWeaponType.NULL, 0.0, 0, 0,0, 0.0, Material.AIR, "null", "null"),
    PLANTS_SCOUT_MAIN("Pea Cannon", "Shoots heavy hitting peas.", BurbMainWeaponType.RIFLE,6.25, 12, 50, 12, 3.25, Material.POPPED_CHORUS_FRUIT, "burb.weapon.peashooter.fire","pea_cannon"),
    PLANTS_HEAVY_MAIN("Chomp", "Sharp chomper fangs.", BurbMainWeaponType.MELEE,4.0, 0, 0, 0, 0.0, Material.WOODEN_SWORD, "burb.weapon.chomper.fire","chomper_fangs"),
    PLANTS_HEALER_MAIN("Sun Pulse", "Shoots bolts of light.", BurbMainWeaponType.RIFLE,2.25, 4, 65, 30, 2.75, Material.POPPED_CHORUS_FRUIT, "burb.weapon.sunflower.fire","sunflower_weapon"),
    PLANTS_RANGED_MAIN("Spike Shot", "Shoots accurate cactus pines.", BurbMainWeaponType.RIFLE,8.0, 15, 60, 6, 4.5, Material.POPPED_CHORUS_FRUIT, "burb.weapon.cactus.fire","spike_shot"),
    ZOMBIES_SCOUT_MAIN("Z-1 Assault Blaster", "Shoots Z1 pellets.", BurbMainWeaponType.RIFLE,1.5, 3, 55, 25, 2.5, Material.POPPED_CHORUS_FRUIT, "burb.weapon.foot_soldier.fire","blaster"),
    ZOMBIES_HEAVY_MAIN("Heroic Fists", "Super Brainz' powerful fists.", BurbMainWeaponType.MELEE,4.0, 0, 0, 0, 0.0, Material.WOODEN_SWORD, "entity.player.attack.knockback","melee_gloves_l"),
    ZOMBIES_HEALER_MAIN("Goo Blaster", "Shoots yucky clumps of goo.", BurbMainWeaponType.SHOTGUN,1.0, 20, 65, 8, 1.75, Material.POPPED_CHORUS_FRUIT, "burb.weapon.scientist.fire","goo_blaster"),
    ZOMBIES_RANGED_MAIN("Spyglass Shot", "Shoots accurate glass shards.", BurbMainWeaponType.RIFLE,8.75, 25, 75, 5, 4.85, Material.POPPED_CHORUS_FRUIT, "block.glass.break","spyglass_shot")
}