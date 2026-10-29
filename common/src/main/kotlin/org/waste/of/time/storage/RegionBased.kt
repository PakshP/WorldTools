package org.waste.of.time.storage

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.LevelStorageSource
import org.waste.of.time.WorldTools.LOG

abstract class RegionBased(
    val chunkPos: ChunkPos,
    val level: Level,
    private val suffix: String
) : Storeable() {
    val dimension: String = level.dimension().identifier().path

    abstract fun compound(): CompoundTag

    abstract fun incrementStats()

    // can be overridden but super should be called after
    open fun writeToStorage(
        session: LevelStorageSource.LevelStorageAccess,
        storage: CustomRegionBasedStorage,
        cachedStorages: MutableMap<String, CustomRegionBasedStorage>
    ) {
        try {
            storage.write(
                chunkPos,
                compound()
            )
            incrementStats()
        } catch (e: Exception) {
            LOG.error("Failed to store $this", e)
        }
    }

    override fun store(
        session: LevelStorageSource.LevelStorageAccess,
        cachedStorages: MutableMap<String, CustomRegionBasedStorage>
    ) {
        if (!shouldStore()) return
        val storage = generateStorage(session, cachedStorages)
        writeToStorage(session, storage, cachedStorages)
    }

    fun generateStorage(
        session: LevelStorageSource.LevelStorageAccess,
        cachedStorages: MutableMap<String, CustomRegionBasedStorage>
    ): CustomRegionBasedStorage {
        // since 26.1 every dimension, including the overworld, is stored in dimensions/<namespace>/<path>/
        val path = session.getDimensionPath(level.dimension())
            .resolve(suffix)
        return cachedStorages.getOrPut(path.toString()) {
            CustomRegionBasedStorage(path, false)
        }
    }
}
