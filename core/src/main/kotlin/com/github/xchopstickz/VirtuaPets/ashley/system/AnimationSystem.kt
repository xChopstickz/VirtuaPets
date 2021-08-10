package com.github.xchopstickz.VirtuaPets.ashley.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.github.xchopstickz.VirtuaPets.VirtuaPets
import com.github.xchopstickz.VirtuaPets.ashley.component.*
import com.github.xchopstickz.VirtuaPets.assets.TextureAtlasAssets
import kotlinx.coroutines.launch
import ktx.ashley.allOf
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.collections.getOrPut
import ktx.log.debug
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import java.util.*

class AnimationSystem(
    private val assetStorage: AssetStorage,
    private val unitScale: Float,
    private val defaultFrameDuration: Float = 1 / 10f,
    private val maxCacheSize: Int = 100
): IteratingSystem(allOf(AnimationComponent::class, RenderComponent::class).get()) {
    private val animationCache = ObjectMap<String, ObjectMap<String, Animation<TextureRegion>>>(maxCacheSize)
    private val stateKeyStringCache = ObjectMap<String, ObjectMap<String, ObjectMap<String, String>>>(maxCacheSize)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        with(entity.animationCmp) {
            // set or update animation
            if (dirty) {
                // some animation related properties changed -> get new animation
                gdxAnimation = cachedAnimation(atlasFilePath, regionKey, stateKey)
            } else {
                stateTime += (deltaTime * animationSpeed)
            }

            // get current region (=keyFrame) of animation
            val keyFrame = if (gdxAnimation == AnimationComponent.EMPTY_ANIMATION) {
                // something went wrong when trying to set animation -> check log errors
                // we will render an error texture to visualize it in game
                errorRegion()
            } else {
                gdxAnimation.playMode = playMode
                gdxAnimation.getKeyFrame(stateTime)
            }

            // update the sprite's texture according to the current animation frame
            entity.renderCmp.sprite.run {
                val flipX = isFlipX
                val flipY = isFlipY
                setRegion(keyFrame)
                setSize(keyFrame.regionWidth * unitScale, keyFrame.regionHeight * unitScale)
                setOrigin(width * 0.5f, height * 0.5f)
                setFlip(flipX, flipY)
            }
        }
    }

    private fun validateCacheSize(cache: ObjectMap<String, *>) {
        if (cache.size >= maxCacheSize) {
            LOG.info { "Maximum cache size reached. Cache will be cleared now" }
            cache.clear()
        }
    }

    private fun cachedAnimation(
        atlasFilePath: String,
        regionKey: String,
        stateKey: String
    ): Animation<TextureRegion> {
        validateCacheSize(animationCache)
        val atlasAnimations = animationCache.getOrPut(atlasFilePath) {
            ObjectMap<String, Animation<TextureRegion>>(maxCacheSize)
        }

        validateCacheSize(atlasAnimations)
        val aniRegionKey = if (stateKey.isBlank()) {
            regionKey
        } else {
            stateRegionKey(atlasFilePath, regionKey, stateKey)
        }
        return atlasAnimations.getOrPut(aniRegionKey) {
            newGdxAnimation(atlasFilePath, aniRegionKey)
        }
    }

    private fun stateRegionKey(atlasFilePath: String, regionKey: String, stateKey: String): String {
        validateCacheSize(stateKeyStringCache)
        return stateKeyStringCache.getOrPut(atlasFilePath) { ObjectMap() }
            .getOrPut(regionKey) { ObjectMap() }
            .getOrPut(stateKey) {
                val result = "${regionKey}/${stateKey}"
                LOG.debug { "Caching state animation region key string '$result'" }
                result
            }
    }

    private fun newGdxAnimation(atlasFilePath: String, regionKey: String) : Animation<TextureRegion> {
        val regions = atlasRegions(atlasFilePath, regionKey)

        if (regions.isEmpty) {
            LOG.error { "No regions available for animation: (atlasFilePath=${atlasFilePath}, regionKey=${regionKey})" }
            return AnimationComponent.EMPTY_ANIMATION
        }
        LOG.debug { "New animation: (atlasFilePath=${atlasFilePath}, regionKey=${regionKey})" }
        return Animation(defaultFrameDuration, regions)
    }

    private fun atlasRegions(atlasFilePath: String, regionKey: String): Array<TextureAtlas.AtlasRegion> {
        return if (!assetStorage.isLoaded<TextureAtlas>(atlasFilePath)) {
            if (assetStorage.fileResolver.resolve(atlasFilePath).exists()) {
                LOG.error { "Atlas '${atlasFilePath}' not loaded yet! Will load it now lazily" }
                assetStorage.loadSync<TextureAtlas>(atlasFilePath).findRegions(regionKey)
            } else {
                LOG.error { "Invalid atlas '${atlasFilePath}'" }
                gdxArrayOf()
            }
        } else {
            assetStorage.get<TextureAtlas>(atlasFilePath).findRegions(regionKey)
        }
    }

    private fun errorRegion(): TextureRegion {
        val errorRegionKey = "ErrorRegion"
        if (!assetStorage.isLoaded<TextureRegion>(errorRegionKey)) {
            LOG.debug { "Creating error TextureRegion" }
            KtxAsync.launch {
                val pixmap = Pixmap((1 / unitScale).toInt(), (1 / unitScale).toInt(), Pixmap.Format.RGB888).apply {
                    setColor(1f, 0f, 0f, 1f)
                    fill()
                }
                val texture = Texture(pixmap)

                assetStorage.add("${errorRegionKey}Pixmap", pixmap)
                assetStorage.add("${errorRegionKey}Texture", texture)
                assetStorage.add(errorRegionKey, TextureRegion(texture))
            }
        }

        return assetStorage[errorRegionKey]
    }

    companion object {
        private val LOG = logger<AnimationSystem>()
    }
}