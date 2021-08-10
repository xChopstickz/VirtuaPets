package com.github.xchopstickz.VirtuaPets.ashley.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor

const val Z_DEFAULT = 0

class TransformComponent : Component, Pool.Poolable, Comparable<TransformComponent>{
    val position = Vector3()
    val size = Vector2(1f,1f)
    val boundingBoxOffset = Vector2(0f, 0f)
    val width: Float
        get() = size.x + boundingBoxOffset.x
    val height: Float
        get() = size.y + boundingBoxOffset.y

    override fun reset() {
        position.set(0f,0f,0f)
        size.set(1f,1f)
        boundingBoxOffset.set(0f, 0f)
    }

    override fun compareTo(other: TransformComponent): Int {
        val zDiff = other.position.z.compareTo(position.z)
        return if(zDiff == 0) other.position.y.compareTo(position.y) else zDiff
    }

    companion object {
        val mapper = mapperFor<TransformComponent>()
        val TMP_RECT_1 = Rectangle()
        val TMP_RECT_2 = Rectangle()
    }
}

val Entity.transform: TransformComponent
    get() = this[TransformComponent.mapper]
        ?: throw GdxRuntimeException("Transformcomponent for entity '$this' is null")

/**
 * Returns true if and only if the bounding rectangle of this entity overlaps the bounding rectangle of [entity].
 * The bounding rectangles are created by the position and size of the [TransformComponent].
 * If one of the entities does not have a [TransformComponent] then false is returned.
 */
fun Entity.withinRange(entity: Entity): Boolean {
    val transformA = this[TransformComponent.mapper]
    val transformB = entity[TransformComponent.mapper]

    if (transformA == null || transformB == null) {
        return false
    }

    TransformComponent.TMP_RECT_1.set(transformA.position.x, transformA.position.y, transformA.width, transformA.height)
    TransformComponent.TMP_RECT_2.set(transformB.position.x, transformB.position.y, transformB.width, transformB.height)

    return TransformComponent.TMP_RECT_1.overlaps(TransformComponent.TMP_RECT_2)
}
