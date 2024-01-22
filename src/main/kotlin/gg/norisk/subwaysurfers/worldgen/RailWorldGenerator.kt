package gg.norisk.subwaysurfers.worldgen

import gg.norisk.subwaysurfers.registry.BlockRegistry
import gg.norisk.subwaysurfers.registry.EntityRegistry
import net.minecraft.block.Blocks
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import kotlin.math.nextUp
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class RailWorldGenerator(
    val player: PlayerEntity,
    val direction: Direction = player.horizontalFacing,
    val startPos: BlockPos = player.blockPos,
    val maxGenerationDistance: Int = 50
) {
    var latestDistance: Int = 0

    //TODO das muss alles besser gemacht werden lol
    val coins: MutableList<MutableList<CoinSpawnInformation>> = mutableListOf(
        mutableListOf(), mutableListOf(), mutableListOf()
    )
    val items: MutableList<MutableList<CoinSpawnInformation>> = mutableListOf(
        mutableListOf(), mutableListOf(), mutableListOf()
    )
    val trains: MutableList<MutableList<TrainSpawnInformation>> = mutableListOf(
        mutableListOf(), mutableListOf(), mutableListOf()
    )

    fun tick(player: PlayerEntity, world: ServerWorld) {
        generateRailsForPlayer(player, world)
    }

    private fun BlockPos.toAxisPos(direction: Direction): BlockPos {
        return BlockPos(
            x * direction.offsetX, y * direction.offsetY, z * direction.offsetZ
        )
    }

    private fun fillTrainList(maxDistance: Int) {
        repeat(3) { row ->
            val rowTrains = trains[row]
            val randomFlag = Random.nextBoolean()
            repeat(Random.nextInt(0, 5)) { distance ->
                rowTrains.add(TrainSpawnInformation(randomFlag))
                if (randomFlag) {
                    //Abstand
                    repeat(1) {
                        rowTrains.add((TrainSpawnInformation(false)))
                    }
                }
            }
        }
    }

    private fun fillCoinList(maxDistance: Int) {
        repeat(3) { row ->
            val rowCoins = coins[row]
            val randomFlag = Random.nextBoolean()
            repeat(Random.nextInt(0, maxDistance)) { distance ->
                rowCoins.add(CoinSpawnInformation(randomFlag))
            }
        }
    }

    private fun generateRailsForPlayer(player: PlayerEntity, world: ServerWorld) {
        val playerPos = player.blockPos.toAxisPos(direction)
        val startAxisPos = startPos.toAxisPos(direction)

        val distanceToOrigin = sqrt(playerPos.getSquaredDistance(startAxisPos)).toInt()

        if (distanceToOrigin > latestDistance) {
            latestDistance = distanceToOrigin

            if (coins.all { it.isEmpty() }) {
                fillCoinList(100)
            }
            if (trains.all { it.isEmpty() }) {
                fillTrainList(100)
            }

            val currentPos = startPos.offset(direction, distanceToOrigin)
            val scale = 2

            generateRailWay(maxGenerationDistance, scale, currentPos, world)
            generateWalls(maxGenerationDistance, scale, currentPos, world)
        }
    }

    private fun handleNewRailPlacement(row: Int, blockPos: BlockPos, world: ServerWorld) {
        handleTrainPlacement(row, blockPos, world)
        handleCoinPlacement(row, blockPos, world)
    }

    private fun handleTrainPlacement(row: Int, blockPos: BlockPos, world: ServerWorld) {
        val rowTrains = trains.getOrNull(row) ?: return
        val trainSpawnInformation = rowTrains.removeFirstOrNull() ?: return
        if (trainSpawnInformation.shouldSpawn) {
            val train = EntityRegistry.TRAIN.create(world) ?: return
            train.shouldDrive = true
            train.owner = player.uuid
            train.yaw = getCorrectYaw()
            train.bodyYaw = train.yaw
            train.headYaw = train.bodyYaw
            train.setPosition(blockPos.toCenterPos().x, blockPos.toCenterPos().y, blockPos.toCenterPos().z)
            world.spawnEntity(train)
        }
    }

    private fun getCorrectYaw(): Float {
        return when (direction.opposite) {
            Direction.DOWN -> TODO()
            Direction.UP -> TODO()
            Direction.NORTH -> 180f
            Direction.SOUTH -> 0f
            Direction.WEST -> 90f
            Direction.EAST -> -90f
        }
    }

    private fun handleCoinPlacement(row: Int, blockPos: BlockPos, world: ServerWorld) {
        val rowCoins = coins.getOrNull(row) ?: return
        val coinSpawnInformation = rowCoins.removeFirstOrNull() ?: return
        if (coinSpawnInformation.shouldSpawn) {
            val coin = EntityRegistry.COIN.spawn(world, blockPos, SpawnReason.TRIGGERED) ?: return
            coin.owner = player.uuid
        }
    }

    private fun generateWalls(maxDistance: Int, scale: Int, currentPos: BlockPos, world: ServerWorld) {
        repeat(maxDistance) { distance ->
            for (i in listOf(-2, 2)) {
                val offset = currentPos.offset(direction, distance).offset(direction.rotateYClockwise(), i * scale)
                world.setBlockState(offset, Blocks.DIAMOND_BLOCK.defaultState)
            }
        }
    }

    private fun generateRailWay(maxDistance: Int, scale: Int, currentPos: BlockPos, world: ServerWorld) {
        repeat(maxDistance) { distance ->
            for (i in listOf(-1, 0, 1)) {
                val offset = currentPos.offset(direction, distance).offset(direction.rotateYClockwise(), i * scale)
                if (placeRailIfNotExists(world, offset)) {
                    handleNewRailPlacement(
                        when (i) {
                            -1 -> 0
                            0 -> 1
                            1 -> 2
                            else -> TODO()
                        }, offset, world
                    )
                }
            }
        }
    }

    private fun placeRailIfNotExists(world: ServerWorld, blockPos: BlockPos): Boolean {
        val blockState = world.getBlockState(blockPos)
        if (blockState.isAir && !world.getBlockState(blockPos.down()).isSolid) {
            return false
        }
        if (!blockState.isOf(BlockRegistry.SUBWAY_RAIL)) {
            world.setBlockState(blockPos, BlockRegistry.SUBWAY_RAIL.defaultState)
            return true
        }
        return false
    }
}
