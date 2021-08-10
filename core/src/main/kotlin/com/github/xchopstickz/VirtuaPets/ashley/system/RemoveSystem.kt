package com.github.xchopstickz.VirtuaPets.ashley.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.xchopstickz.VirtuaPets.ashley.component.RemoveComponent
import com.github.xchopstickz.VirtuaPets.ashley.component.removeCmp
import ktx.ashley.allOf

class RemoveSystem : IteratingSystem(allOf(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val removeCmp = entity.removeCmp

        removeCmp.delay -= deltaTime
        if (removeCmp.delay <= 0f) {
            engine.removeEntity(entity)
        }
    }
}
