package dev.byrt.burb.item.ability.combo

import dev.byrt.burb.item.ItemManager
import dev.byrt.burb.item.ability.BurbAbilities
import dev.byrt.burb.library.Sounds
import dev.byrt.burb.player.BurbPlayer
import dev.byrt.burb.plugin
import dev.byrt.burb.text.Formatting
import dev.byrt.burb.util.Cooldowns
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.scheduler.BukkitRunnable
import javax.naming.Name

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

            val teamColour = burbPlayer.playerTeam?.textColour ?: NamedTextColor.WHITE
            Component.join(
                JoinConfiguration.separator(Component.text(" - ", NamedTextColor.GRAY)),
                abilityComboMap[burbPlayer]?.combo?.map { click -> Component.text(click.comboAbbreviation, teamColour) } ?: emptyList()
            ).let(burbPlayer.bukkitPlayer()::sendActionBar)

            when(burbAbilityComboClick) {
                BurbAbilityComboClick.LEFT -> burbPlayer.bukkitPlayer().playSound(Sounds.Weapon.ABILITY_COMBO_LEFT)
                BurbAbilityComboClick.RIGHT -> burbPlayer.bukkitPlayer().playSound(Sounds.Weapon.ABILITY_COMBO_RIGHT)
            }
            // Initiate timeout
            abilityTimeoutCheck(burbPlayer, abilityComboMap[burbPlayer]?.combo?.toList()!!)
            // Check combo
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
        for(combo in BurbAbilityComboClicks.entries) {
            if(combo.comboClicks == abilityComboMap[burbPlayer]?.combo) {
                for(ability in burbPlayer.playerCharacter.characterAbilities.abilitySet) {
                    if(combo.name.removePrefix("${burbPlayer.playerCharacter.characterType}_").contains(ability.name.removePrefix("${burbPlayer.playerCharacter.name}_"))) {
                        BurbAbilities.useAbility(burbPlayer.bukkitPlayer(), ability, ItemManager.getAbilityItem(ability, burbPlayer.playerCharacter))
                        resetCombo(burbPlayer)
                        return
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