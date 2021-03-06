package com.github.xchopstickz.VirtuaPets.ashley.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.xchopstickz.VirtuaPets.ashley.component.RenderComponent
import com.github.xchopstickz.VirtuaPets.ashley.component.TransformComponent
import com.github.xchopstickz.VirtuaPets.ashley.component.render
import com.github.xchopstickz.VirtuaPets.ashley.component.transform
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger

class RenderSystem(
    private val batch: Batch,
    private val viewport: Viewport
) : SortedIteratingSystem(
    allOf(TransformComponent::class, RenderComponent::class).get(),
    compareBy { it[TransformComponent.mapper] }
) {
    override fun update(deltaTime: Float) {
        forceSort()

        viewport.apply()
        batch.use(viewport.camera) {
            super.update(deltaTime)
        }
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform
        val render = entity.render

        if(render.sprite.texture == null) {
            LOG.error { "Entity '$entity' does not have a texture" }
            return
        }

        render.sprite.run {
            setBounds(
                transform.position.x,
                transform.position.y,
                transform.size.x,
                transform.size.y
            )
            draw(batch)
        }
    }

    companion object {
        private val LOG = logger<RenderSystem>()

    }
}
