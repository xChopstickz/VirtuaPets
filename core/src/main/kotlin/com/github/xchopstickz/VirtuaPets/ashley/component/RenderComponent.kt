package com.github.xchopstickz.VirtuaPets.ashley.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor

class RenderComponent : Component, Pool.Poolable {
    val sprite = Sprite()
    val offset = Vector2()

    override fun reset() {
        sprite.texture = null
        sprite.setColor(1f, 1f, 1f, 1f)
        offset.set(0f, 0f)
    }

    companion object {
        val mapper = mapperFor<RenderComponent>()
    }
}


val Entity.renderCmp: RenderComponent
    get() = this[RenderComponent.mapper]
        ?: throw GdxRuntimeException("Transformcomponent for entity '$this' is null")
