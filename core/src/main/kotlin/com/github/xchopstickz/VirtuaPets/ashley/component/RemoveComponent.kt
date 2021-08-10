package com.github.xchopstickz.VirtuaPets.ashley.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor


class RemoveComponent : Component, Pool.Poolable {
    var delay = 0f

    override fun reset() {
        delay = 0f
    }

    companion object {
        val mapper = mapperFor<RemoveComponent>()
    }
}


val Entity.removeCmp: RemoveComponent
    get() = this[RemoveComponent.mapper]
        ?: throw GdxRuntimeException("RemoveComponent for entity '$this' is null")


fun Entity.removeFromEngine(engine: Engine, delay: Float = 0f) {
    this.add(engine.createComponent(RemoveComponent::class.java).apply { this.delay = delay })
}

/**
 * Checks if an [Entity] gets removed by the engine or has a [RemoveComponent].
 * Use this instead of [Entity.isRemoving] to check if an entity really gets removed.
 */
val Entity.isRemoved: Boolean
    get() {
        return this.isRemoving || this[RemoveComponent.mapper] != null
    }
