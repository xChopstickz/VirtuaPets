package com.github.xchopstickz.VirtuaPets.ashley.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.xchopstickz.VirtuaPets.VirtuaPets
import com.github.xchopstickz.VirtuaPets.ashley.component.*
import com.github.xchopstickz.VirtuaPets.assets.TextureAtlasAssets
import ktx.ashley.allOf
import ktx.assets.async.AssetStorage
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import java.util.*

class AnimationSystem(
    private val assetStorage: AssetStorage
): IteratingSystem(allOf(AnimationComponent::class, RenderComponent::class).get()) {
    private val animationCache =
        EnumMap<TextureAtlasAssets, EnumMap<AnimationType, Animation<TextureRegion>>>(TextureAtlasAssets::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animation = entity.animation

        if(animation.dirty){
            //update animation
            animation.gdxAnimation = gdxAnimation(animation.type)
        } else {
            animation.stateTime += deltaTime
        }

        val keyFrame = animation.gdxAnimation.getKeyFrame(animation.stateTime)
        entity.render.sprite.run {
            setRegion(keyFrame)
            setSize(keyFrame.regionWidth * VirtuaPets.UNIT_SCALE, keyFrame.regionWidth * VirtuaPets.UNIT_SCALE)
            setOrigin(width*0.5f,height*0.5f)
        }
    }

    private fun gdxAnimation(animationType: AnimationType): Animation<TextureRegion>{
        val atlasAnimations = animationCache.computeIfAbsent(animationType.atlasAssets) {
            EnumMap<AnimationType, Animation<TextureRegion>>(AnimationType::class.java)
        }

        return atlasAnimations.computeIfAbsent(animationType) {
            newGdxAnimation(animationType)
        }
    }

    private fun newGdxAnimation(animationType: AnimationType) : Animation<TextureRegion> {
        val textureAtlas = assetStorage.get(animationType.atlasAssets.descriptor)
        val regions = textureAtlas.findRegions(animationType.altasKey)

        if (regions.isEmpty) {
            LOG.error { "Invalid Animation: (atlasAsset=${animationType.atlasAssets}, atlasKey=${animationType.altasKey})" }
            return AnimationComponent.EMPTY_ANIMATION
        }

        LOG.debug { "New Animation: (atlasAsset=${animationType.atlasAssets}, atlasKey=${animationType.altasKey})" }
        return  Animation(DEFAULT_FPS * animationType.speed, regions, animationType.playMode)
    }

    companion object {
        private val LOG = logger<AnimationType>()
        private val DEFAULT_FPS = 1/16f
    }
}