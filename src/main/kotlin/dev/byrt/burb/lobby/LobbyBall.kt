package dev.byrt.burb.lobby

import dev.byrt.burb.plugin

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

import org.bukkit.*
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.bukkit.util.Vector

import org.joml.Vector3f

import kotlin.math.PI
import kotlin.math.atan2

object LobbyBall {
    private val ballMap = mutableMapOf<Int, LobbyBallPhysics>()
    val lobbyBallSpawnLocation = Location(Bukkit.getWorlds()[0], 40.0, 10.5, 61.5)
    private val lobbyBallAreaMin = Location(Bukkit.getWorlds()[0], 29.5, -1.0, 41.5)
    private val lobbyBallAreaMax = Location(Bukkit.getWorlds()[0], 50.5, 13.0, 81.5)
    private val lobbyBallNetPlantsMin = Location(Bukkit.getWorlds()[0], 36.0, -1.0, 78.0)
    private val lobbyBallNetPlantsMax = Location(Bukkit.getWorlds()[0], 44.0, 4.0, 80.0)
    private val lobbyBallNetZombiesMin = Location(Bukkit.getWorlds()[0], 35.0, -1.0, 43.0)
    private val lobbyBallNetZombiesMax = Location(Bukkit.getWorlds()[0], 44.0, 4.0, 45.0)

    init {
        val ballDisplayEntity = lobbyBallSpawnLocation.world.spawnEntity(lobbyBallSpawnLocation, EntityType.ITEM_DISPLAY) as ItemDisplay
        val nerdBallItem = ItemStack(Material.ECHO_SHARD, 1)
        val nerdBallItemMeta = nerdBallItem.itemMeta
        nerdBallItemMeta.setCustomModelData(1)
        nerdBallItem.itemMeta = nerdBallItemMeta
        ballDisplayEntity.setItemStack(nerdBallItem)
        ballDisplayEntity.setGravity(false)
        ballDisplayEntity.transformation = Transformation(Vector3f(0.0f, 0.8f, 0.0f), ballDisplayEntity.transformation.leftRotation, Vector3f(1f, 1f, 1f), ballDisplayEntity.transformation.rightRotation)
        ballDisplayEntity.brightness = Display.Brightness(15, 15)
        val ballPhysics = LobbyBallPhysics(ballDisplayEntity)
        ballMap[ballDisplayEntity.entityId] = ballPhysics
        ballPhysics.start()
    }

    fun isInPlantsNet(ball: ItemDisplay): Boolean {
        return ball.location.x in lobbyBallNetPlantsMin.x..lobbyBallNetPlantsMax.x
                && ball.location.y in lobbyBallNetPlantsMin.y..lobbyBallNetPlantsMax.y
                && ball.location.z in lobbyBallNetPlantsMin.z..lobbyBallNetPlantsMax.z
    }

    fun isInZombiesNet(ball: ItemDisplay): Boolean {
        return ball.location.x in lobbyBallNetZombiesMin.x..lobbyBallNetZombiesMax.x
                && ball.location.y in lobbyBallNetZombiesMin.y..lobbyBallNetZombiesMax.y
                && ball.location.z in lobbyBallNetZombiesMin.z..lobbyBallNetZombiesMax.z
    }

    fun isInArea(ball: ItemDisplay): Boolean {
        return ball.location.x in lobbyBallAreaMin.x..lobbyBallAreaMax.x
                && ball.location.y in lobbyBallAreaMin.y..lobbyBallAreaMax.y
                && ball.location.z in lobbyBallAreaMin.z..lobbyBallAreaMax.z
    }

    fun getBallMap(): Map<Int, LobbyBallPhysics> {
        return this.ballMap
    }

    fun cleanup() {
        ballMap.values.forEach { it.cleanup() }
        ballMap.clear()
    }
}

class LobbyBallPhysics(val ball: ItemDisplay) {
    private var velocity = Vector(0, 0, 0)
    private var rotation = Vector(0, 0, 0)
    private var rotationDampening = 0.95
    private var friction = 0.98
    fun start() {
        object : BukkitRunnable() {
            override fun run() {
                if(!ball.isValid) {
                    cancel()
                    return
                }
                val currentLocation = ball.location
                val nextLocation = currentLocation.clone().add(velocity)
                val collisionX = currentLocation.world.getBlockAt(nextLocation.blockX, currentLocation.blockY, currentLocation.blockZ).type != Material.AIR
                val collisionY = currentLocation.world.getBlockAt(currentLocation.blockX, nextLocation.blockY, currentLocation.blockZ).type != Material.AIR
                val collisionZ = currentLocation.world.getBlockAt(currentLocation.blockX, currentLocation.blockY, nextLocation.blockZ).type != Material.AIR

                if(nextLocation.block.type == Material.SLIME_BLOCK) velocity.multiply(1.2)
                if(nextLocation.block.type == Material.HONEY_BLOCK) velocity.multiply(0.3)
                if(nextLocation.block.type == Material.COBWEB) velocity.multiply(-0.4)

                if(collisionX) velocity.x = -velocity.x * 0.6
                if(collisionY) velocity.y = -velocity.y * 0.6
                if(collisionZ) velocity.z = -velocity.z * 0.6

                if(!collisionX && !collisionY && !collisionZ) {
                    ball.teleport(nextLocation)
                } else {
                    if(collisionX) nextLocation.x = currentLocation.x
                    if(collisionY) nextLocation.y = currentLocation.y
                    if(collisionZ) nextLocation.z = currentLocation.z
                    ball.teleport(nextLocation)
                }
                // Friction
                if(collisionX || collisionZ) {
                    velocity.x *= 0.4
                    velocity.z *= 0.4
                }
                ball.setRotation((atan2(velocity.x, velocity.z) * (180/ PI)).toFloat(), ball.pitch)
                velocity.multiply(friction)
                velocity.subtract(Vector(0.0, 0.04, 0.0)) // Bounce

                if(LobbyBall.isInPlantsNet(ball)) {
                    val f: Firework = ball.world.spawn(Location(ball.location.world, ball.location.x, ball.location.y + 1.0, ball.location.z), Firework::class.java)
                    val fm = f.fireworkMeta
                    fm.addEffect(
                        FireworkEffect.builder()
                            .flicker(false)
                            .trail(false)
                            .with(FireworkEffect.Type.BALL)
                            .withColor(Color.LIME)
                            .build()
                    )
                    fm.power = 0
                    f.fireworkMeta = fm
                    f.ticksToDetonate = 1
                    velocity.zero()
                    ball.teleport(LobbyBall.lobbyBallSpawnLocation)
                    ball.location.world.playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.VOICE, 1.0f, 1.0f))
                }
                if(LobbyBall.isInZombiesNet(ball)) {
                    val f: Firework = ball.world.spawn(Location(ball.location.world, ball.location.x, ball.location.y + 1.0, ball.location.z), Firework::class.java)
                    val fm = f.fireworkMeta
                    fm.addEffect(
                        FireworkEffect.builder()
                            .flicker(false)
                            .trail(false)
                            .with(FireworkEffect.Type.BALL)
                            .withColor(Color.PURPLE)
                            .build()
                    )
                    fm.power = 0
                    f.fireworkMeta = fm
                    f.ticksToDetonate = 1
                    velocity.zero()
                    ball.teleport(LobbyBall.lobbyBallSpawnLocation)
                    ball.location.world.playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.VOICE, 1.0f, 1.0f))
                }
                if(!LobbyBall.isInArea(ball)) {
                    velocity.zero()
                    ball.teleport(LobbyBall.lobbyBallSpawnLocation)
                    ball.location.world.playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.VOICE, 1.0f, 1.0f))
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun applyForce(force: Vector) {
        velocity.add(force)
    }

    fun cleanup() {
        ball.remove()
    }
}