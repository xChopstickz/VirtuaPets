package com.github.xchopstickz.VirtuaPets.ashley.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.collections.gdxArrayOf

class AnimationComponent: Component,Pool.Poolable {
    var atlasFilePath = ""
        set(value) {
            dirty = value != field
            field = value
        }

    var regionKey = ""
    set(value) {
        dirty = value != field
        field = value
    }

    var stateKey = ""
    set(value) {
        dirty = value != field
        field = value
    }

    var playMode = Animation.PlayMode.LOOP
    var stateTime = 0f
    var animationSpeed = 1f
    internal var gdxAnimation: Animation<TextureRegion> = EMPTY_ANIMATION
    set(value) {
        dirty = false
        stateTime = 0f
        field = value
    }

    internal var dirty = true
        private set

    override fun reset() {
        atlasFilePath = ""
        regionKey = ""
        stateKey = ""
        playMode = Animation.PlayMode.LOOP
        animationSpeed = 1f
        stateTime = 0f
        dirty = true
    }

    fun isAnimationFinished() = gdxAnimation.isAnimationFinished(stateTime)

    companion object {
        val mapper = mapperFor<AnimationComponent>()
        val EMPTY_ANIMATION = Animation<TextureRegion>(0f, gdxArrayOf())
    }
}

val Entity.animationCmp: AnimationComponent
    get() = this[AnimationComponent.mapper]
        ?: throw GdxRuntimeException("Transformcomponent for entity '$this' is null")

fun Entity.playAnimation(stateKey: String, animationSpeed: Float = 1f) {
    this[AnimationComponent.mapper]?.let { animationCmp ->
        animationCmp.playMode = Animation.PlayMode.NORMAL
        if (animationCmp.stateKey == stateKey) {
            animationCmp.stateTime = 0f
    } else {
        animationCmp.stateKey = stateKey
    }
        animationCmp.animationSpeed = animationSpeed
    }
}