package dev.byrt.burb.item.ability.combo

import dev.byrt.burb.item.ability.BurbAbilities
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.BurbCharacter
import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.util.Cooldowns

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable

object BurbAbilityComboManager {
    private val abilityComboMap = mutableMapOf<BurbPlayer, BurbAbilityCombo>()
    fun abilityCombo(burbPlayer: BurbPlayer, burbAbilityComboClick: BurbAbilityComboClick) {
        if(burbPlayer.isDead || burbPlayer.bukkitPlayer().vehicle != null) return
        // Check cooldown
        if(Cooldowns.attemptAbilityCombo(burbPlayer.bukkitPlayer())) {
            // Continue combo if already active
            if(abilityComboMap.containsKey(burbPlayer)) {
                abilityComboMap[burbPlayer]?.addClick(burbAbilityComboClick)
            } else {
                // Create new combo if not active
                abilityComboMap[burbPlayer] = BurbAbilityCombo()
                abilityComboMap[burbPlayer]?.addClick(burbAbilityComboClick)
            }

            val comboString = mutableListOf<String>()
            abilityComboMap[burbPlayer]?.combo?.forEach { click -> comboString.add("${burbPlayer.playerTeam.teamColourTag}${click.comboAbbreviation}") }
            burbPlayer.bukkitPlayer().sendActionBar(Formatting.allTags.deserialize(comboString.joinToString(" <gray>- ").trim()))

            when(burbAbilityComboClick) {
                BurbAbilityComboClick.LEFT -> burbPlayer.bukkitPlayer().playSound(Sounds.Weapon.ABILITY_COMBO_LEFT)
                BurbAbilityComboClick.RIGHT -> burbPlayer.bukkitPlayer().playSound(Sounds.Weapon.ABILITY_COMBO_RIGHT)
            }
            // Initiate timeout
            abilityTimeoutCheck(burbPlayer, abilityComboMap[burbPlayer]?.combo?.toList()!!)
            // Always check combo
            checkCombo(burbPlayer)
        }
    }

    fun abilityTimeoutCheck(burbPlayer: BurbPlayer, combo: List<BurbAbilityComboClick>) {
        val abilityTimeout = 15L
        object : BukkitRunnable() {
            var ticks = 0
            override fun run() {
                // Reset timeout if combo has changed
                if(abilityComboMap[burbPlayer]?.combo != combo) {
                    cancel()
                }
                // Cancel combo if timeout is exceeded
                if(ticks > abilityTimeout) {
                    burbPlayer.bukkitPlayer().playSound(Sounds.Misc.INTERFACE_ERROR)
                    burbPlayer.bukkitPlayer().sendActionBar(Formatting.allTags.deserialize("<red>Your ability combo timed out."))
                    resetCombo(burbPlayer)
                    cancel()
                }
                ticks++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun checkCombo(burbPlayer: BurbPlayer) {
        // Return if player has no combo active
        if(!abilityComboMap.containsKey(burbPlayer)) return
        val abilitySet = burbPlayer.playerCharacter.characterAbilities.abilitySet
        for(combo in BurbAbilityComboClicks.entries) {
            if(combo.comboClicks == abilityComboMap[burbPlayer]?.combo) {
                // Melee characters
                if(burbPlayer.playerCharacter in listOf(BurbCharacter.PLANTS_HEAVY, BurbCharacter.ZOMBIES_HEAVY)) {
                    for(ability in abilitySet) {
                        if(combo.name.removePrefix("MELEE_").contains(ability.name.removePrefix("${burbPlayer.playerCharacter.name}_"))) {
                            BurbAbilities.useAbility(burbPlayer.bukkitPlayer(), ability, ItemStack(ability.abilityMaterial).apply { itemMeta = itemMeta.apply {
                                this.persistentDataContainer.set(NamespacedKey(plugin, "burb.ability.id"), PersistentDataType.STRING, ability.abilityId)
                                this.persistentDataContainer.set(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER, ability.abilityCooldown) }
                            })
                            resetCombo(burbPlayer)
                            return
                        }
                    }
                }
                // Ranged characters
                if(burbPlayer.playerCharacter in listOf(BurbCharacter.PLANTS_SCOUT, BurbCharacter.PLANTS_RANGED, BurbCharacter.PLANTS_HEALER, BurbCharacter.ZOMBIES_SCOUT, BurbCharacter.ZOMBIES_RANGED, BurbCharacter.ZOMBIES_HEALER)) {
                    val abilitySet = burbPlayer.playerCharacter.characterAbilities.abilitySet
                    for(ability in abilitySet) {
                        if(combo.name.removePrefix("RANGED_").contains(ability.name.removePrefix("${burbPlayer.playerCharacter.name}_"))) {
                            BurbAbilities.useAbility(burbPlayer.bukkitPlayer(), ability, ItemStack(ability.abilityMaterial).apply { itemMeta = itemMeta.apply {
                                this.persistentDataContainer.set(NamespacedKey(plugin, "burb.ability.id"), PersistentDataType.STRING, ability.abilityId)
                                this.persistentDataContainer.set(NamespacedKey(plugin, "burb.ability.cooldown"), PersistentDataType.INTEGER, ability.abilityCooldown) }
                            })
                            resetCombo(burbPlayer)
                            return
                        }
                    }
                }
            }
        }
        abilityComboMap[burbPlayer]?.combo?.size?.let {
            if(it >= 3) {
                burbPlayer.bukkitPlayer().playSound(Sounds.Misc.INTERFACE_ERROR)
                burbPlayer.bukkitPlayer().sendActionBar(Formatting.allTags.deserialize("<red>Ability combo does not exist."))
                resetCombo(burbPlayer)
            }
        }
    }

    fun resetCombo(burbPlayer: BurbPlayer) {
        for(entry in abilityComboMap.entries.filter { it.key == burbPlayer }) {
            abilityComboMap[burbPlayer]?.cancelCombo()
            abilityComboMap.remove(entry.key)
        }
    }

    fun hasCombo(burbPlayer: BurbPlayer): Boolean {
        return abilityComboMap.containsKey(burbPlayer)
    }
}