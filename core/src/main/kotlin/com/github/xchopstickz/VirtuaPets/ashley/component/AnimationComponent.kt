package com.github.xchopstickz.VirtuaPets.ashley.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import com.github.xchopstickz.VirtuaPets.assets.TextureAtlasAssets
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.collections.gdxArrayOf

enum class AnimationType(
    val atlasAssets: TextureAtlasAssets,
    val altasKey: String,
    val speed: Float = 1f,
    val playMode: Animation.PlayMode = Animation.PlayMode.LOOP
) {
    NONE(TextureAtlasAssets.PINGII,"error"),
    PINGII_EGG(TextureAtlasAssets.PINGII,"pingii-egg",1.95f),
    PINGII_CRACK(TextureAtlasAssets.PINGII,"pingii-crack",7.5f),
    PINGII_IDLE(TextureAtlasAssets.PINGII,"pingii-idle",7.5f),
    PINGII_SLEEP(TextureAtlasAssets.PINGII,"pingii-sleep"),
    PINGII_JUMP(TextureAtlasAssets.PINGII,"pingii-jump",2.75f)
}

class AnimationComponent: Component,Pool.Poolable {
    var dirty = true
        private set
    var stateTime = 0f
    var type = AnimationType.NONE
        set(value) {
            dirty = value != field
            field = value
    }
     var gdxAnimation: Animation<TextureRegion> = EMPTY_ANIMATION
    set(value) {
        dirty = false
        stateTime = 0f
        field = value
    }

    fun dirty() = dirty

    override fun reset() {
        dirty = true
        stateTime = 0f
        type = AnimationType.NONE
    }
    companion object {
        val mapper = mapperFor<AnimationComponent>()
        val EMPTY_ANIMATION = Animation<TextureRegion>(0f, gdxArrayOf())
    }
}

val Entity.animation: AnimationComponent
    get() = this[AnimationComponent.mapper]
        ?: throw GdxRuntimeException("Transformcomponent for entity '$this' is null")
