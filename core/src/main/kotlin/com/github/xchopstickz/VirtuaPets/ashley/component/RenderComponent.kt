package com.github.xchopstickz.VirtuaPets.ashley.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor

class RenderComponent : Component, Pool.Poolable {
    val sprite = Sprite()

    override fun reset() {
        sprite.texture = null
    }

    companion object {
        val mapper = mapperFor<RenderComponent>()
    }
}


val Entity.render: RenderComponent
    get() = this[RenderComponent.mapper]
        ?: throw GdxRuntimeException("Transformcomponent for entity '$this' is null")
