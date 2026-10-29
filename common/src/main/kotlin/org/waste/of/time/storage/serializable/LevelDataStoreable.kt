package org.waste.of.time.storage.serializable

import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.*
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.ProblemReporter
import net.minecraft.util.Util
import net.minecraft.world.level.LevelSettings
import net.minecraft.world.level.WorldDataConfiguration
import net.minecraft.world.level.storage.LevelData
import net.minecraft.world.level.storage.LevelResource
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess
import net.minecraft.world.level.storage.PrimaryLevelData
import net.minecraft.world.level.storage.TagValueOutput
import org.waste.of.time.WorldTools.DAT_EXTENSION
import org.waste.of.time.WorldTools.LOG
import org.waste.of.time.WorldTools.config
import org.waste.of.time.WorldTools.mc
import org.waste.of.time.config.WorldToolsConfig.World.WorldGenerator.GeneratorType
import org.waste.of.time.manager.CaptureManager
import org.waste.of.time.manager.CaptureManager.currentLevelName
import org.waste.of.time.manager.MessageManager
import org.waste.of.time.manager.MessageManager.translateHighlight
import org.waste.of.time.storage.CustomRegionBasedStorage
import org.waste.of.time.storage.Storeable
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class LevelDataStoreable : Storeable() {
    override fun shouldStore() = config.general.capture.levelData

    override val verboseInfo: MutableComponent
        get() = translateHighlight(
            "worldtools.capture.saved.levelData",
            currentLevelName,
            "level${DAT_EXTENSION}"
        )

    override val anonymizedInfo: MutableComponent
        get() = verboseInfo

    /**
     * Since 26.1 most of the world state that used to live in level.dat is stored as separate saved data files,
     * see [net.minecraft.util.filefix.fixes.LevelDatToSavedDataFileFix].
     */
    override fun store(
        session: LevelStorageAccess,
        cachedStorages: MutableMap<String, CustomRegionBasedStorage>
    ) {
        val resultingFile = session.getLevelPath(LevelResource.ROOT).toFile()
        val dataNbt = serializeLevelData()
        // if we save an empty level.dat, clients will crash when opening the SP worlds screen
        if (dataNbt.isEmpty) throw RuntimeException("Failed to serialize level data")
        val levelNbt = CompoundTag().apply {
            put("Data", dataNbt)
        }

        try {
            val newFile = File.createTempFile("level", DAT_EXTENSION, resultingFile).toPath()
            NbtIo.writeCompressed(levelNbt, newFile)
            val backup = session.getLevelPath(LevelResource.OLD_LEVEL_DATA_FILE)
            val current = session.getLevelPath(LevelResource.LEVEL_DATA_FILE)
            Util.safeReplaceFile(current, newFile, backup)

            val dataDirectory = session.getLevelPath(LevelResource.DATA)
            dataDirectory.writeSavedData("world_gen_settings", generatorMockNbt())
            weatherNbt()?.let { dataDirectory.writeSavedData("weather", it) }
            savePlayerData(session)
            LOG.info("Saved level data.")
        } catch (exception: IOException) {
            MessageManager.sendError(
                "worldtools.log.error.failed_to_save_level",
                resultingFile.path,
                exception.localizedMessage
            )
        }
    }

    /**
     * See [net.minecraft.world.level.storage.PrimaryLevelData.setTagData]
     */
    private fun serializeLevelData() = CompoundTag().apply {
        val player = CaptureManager.lastPlayer ?: mc.player ?: return@apply
        val level = player.level()

        put("ServerBrands", ListTag().apply {
            mc.connection?.serverBrand()?.let {
                add(StringTag.valueOf(it))
            }
        })

        putBoolean("WasModded", false)

        // skip removed features

        PrimaryLevelData.writeVersionTag(this)
        NbtUtils.addCurrentDataVersion(this)

        // modern client world APIs no longer expose all server properties; keep sane defaults
        putInt("GameType", 0)
        store(
            "spawn",
            LevelData.RespawnData.CODEC,
            LevelData.RespawnData(
                GlobalPos.of(level.dimension(), player.blockPosition()),
                net.minecraft.util.Mth.wrapDegrees(player.yRot),
                0f
            )
        )
        putLong("Time", level.levelData.gameTime)
        PrimaryLevelData.writeLastPlayed(this)
        putString("LevelName", currentLevelName)
        putInt("version", 19133)
        putBoolean("allowCommands", true) // not sure
        putBoolean("initialized", true) // not sure
        store(
            "difficulty_settings",
            LevelSettings.DifficultySettings.CODEC,
            LevelSettings.DifficultySettings(level.levelData.difficulty, false, false)
        )
        store("singleplayer_uuid", UUIDUtil.CODEC, player.uuid)
        store(WorldDataConfiguration.MAP_CODEC, WorldDataConfiguration.DEFAULT)
    }

    /**
     * The singleplayer owner loads the player data of `singleplayer_uuid` from the player data directory.
     */
    private fun savePlayerData(session: LevelStorageAccess) {
        val player = CaptureManager.lastPlayer ?: mc.player ?: return
        val playerDataDir = session.getLevelPath(LevelResource.PLAYER_DATA_DIR)
        Files.createDirectories(playerDataDir)

        val output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess())
        player.saveWithoutId(output)
        val playerNbt = output.buildResult().apply {
            NbtUtils.addCurrentDataVersion(this)
            remove("LastDeathLocation") // can contain sensitive information
            putString("Dimension", player.level().dimension().identifier().toString())
        }

        val newFile = File.createTempFile(player.stringUUID + "-", DAT_EXTENSION, playerDataDir.toFile()).toPath()
        NbtIo.writeCompressed(playerNbt, newFile)
        Util.safeReplaceFile(
            playerDataDir.resolve(player.stringUUID + DAT_EXTENSION),
            newFile,
            playerDataDir.resolve(player.stringUUID + DAT_EXTENSION + "_old")
        )
    }

    /**
     * See [net.minecraft.world.level.storage.SavedDataStorage]
     */
    private fun Path.writeSavedData(name: String, data: CompoundTag) {
        val file = resolve("minecraft").resolve("$name$DAT_EXTENSION")
        Files.createDirectories(file.parent)
        NbtIo.writeCompressed(CompoundTag().apply {
            put("data", data)
            NbtUtils.addCurrentDataVersion(this)
        }, file)
    }

    private fun weatherNbt(): CompoundTag? {
        val level = (CaptureManager.lastPlayer ?: mc.player)?.level() ?: return null
        return CompoundTag().apply {
            putInt("clear_weather_time", 0) // not sure
            putInt("rain_time", 0) // not sure
            putInt("thunder_time", 0) // not sure
            putBoolean("raining", level.isRaining)
            putBoolean("thundering", level.isThundering)
        }
    }

    /**
     * See [net.minecraft.world.level.levelgen.WorldGenSettings.CODEC]
     */
    private fun generatorMockNbt() = CompoundTag().apply {
        putBoolean("bonus_chest", config.world.worldGenerator.bonusChest)
        putLong("seed", config.world.worldGenerator.seed)
        putBoolean("generate_structures", config.world.worldGenerator.generateFeatures)

        put("dimensions", CompoundTag().apply {
            CaptureManager.lastWorldKeys.forEach { key ->
                val path = key.identifier().path
                put("minecraft:$path", CompoundTag().apply {
                    put("generator", generateGenerator(path))

                    when (path) {
                        "the_nether" -> {
                            putString("type", "minecraft:the_nether")
                        }
                        "the_end" -> {
                            putString("type", "minecraft:the_end")
                        }
                        else -> {
                            putString("type", "minecraft:overworld")
                        }
                    }
                })
            }
        })
    }

    private fun generateGenerator(path: String) = CompoundTag().apply {
        when (config.world.worldGenerator.type) {
            GeneratorType.VOID -> voidGenerator()
            GeneratorType.DEFAULT -> defaultGenerator(path)
            GeneratorType.FLAT -> flatGenerator()
        }
    }

    private fun CompoundTag.voidGenerator() {
        put("settings", CompoundTag().apply {
            putByte("features", 1)
            putString("biome", "minecraft:the_void")
            put("layers", ListTag().apply {
                add(CompoundTag().apply {
                    putString("block", "minecraft:air")
                    putInt("height", 1)
                })
            })
            put("structure_overrides", ListTag())
            putByte("lakes", 0)
        })
        putString("type", "minecraft:flat")
    }

    private fun CompoundTag.defaultGenerator(path: String) {
        when (path) {
            "the_nether" -> {
                put("biome_source", CompoundTag().apply {
                    putString("preset", "minecraft:nether")
                    putString("type", "minecraft:multi_noise")
                })
                putString("settings", "minecraft:nether")
                putString("type", "minecraft:noise")
            }
            "the_end" -> {
                put("biome_source", CompoundTag().apply {
                    putString("type", "minecraft:the_end")
                })
                putString("settings", "minecraft:end")
                putString("type", "minecraft:noise")
            }
            else -> {
                put("biome_source", CompoundTag().apply {
                    putString("preset", "minecraft:overworld")
                    putString("type", "minecraft:multi_noise")
                })
                putString("settings", "minecraft:overworld")
                putString("type", "minecraft:noise")
            }
        }
    }

    private fun CompoundTag.flatGenerator() {
        put("settings", CompoundTag().apply {
            putString("biome", "minecraft:plains")
            putByte("features", 0)
            putByte("lakes", 0)
            put("layers", ListTag().apply {
                add(CompoundTag().apply {
                    putString("block", "minecraft:bedrock")
                    putInt("height", 1)
                })
                add(CompoundTag().apply {
                    putString("block", "minecraft:dirt")
                    putInt("height", 2)
                })
                add(CompoundTag().apply {
                    putString("block", "minecraft:grass_block")
                    putInt("height", 1)
                })
            })
            put("structure_overrides", ListTag().apply {
                add(StringTag.valueOf("minecraft:strongholds"))
                add(StringTag.valueOf("minecraft:villages"))
            })
        })
        putString("type", "minecraft:flat")
    }
}
