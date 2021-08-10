package com.github.xchopstickz.VirtuaPets.ashley.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.utils.ObjectMap
import com.github.xchopstickz.VirtuaPets.ashley.component.*
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.collections.getOrPut
import java.util.*

class StateSystem(
    private val messageManager: MessageManager,
    private val messageTypes: Set<Int> = setOf()
) : IteratingSystem(allOf(StateComponent::class).exclude(RemoveComponent::class).get()), EntityListener {
    private val stateAnimationStringCache = ObjectMap<EntityState, String>()

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        with(entity.stateCmp) {
            stateMachine.owner = entity

            for (messageType in messageTypes) {
                messageManager.addListener(stateMachine, messageType)
            }
        }
    }

    override fun entityRemoved(entity: Entity) {
        with(entity.stateCmp) {
            for (messageType in messageTypes) {
                messageManager.removeListener(stateMachine, messageType)
            }
        }
    }

    override fun update(deltaTime: Float) {
        GdxAI.getTimepiece().update(deltaTime)
        messageManager.update()
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        with(entity.stateCmp) {
            when {
                EntityState.EMPTY_STATE != state -> {
                    // switch to new state
                    stateTime = 0f
                    stateMachine.changeState(state)
                    entity[AnimationComponent.mapper]?.let {
                        it.stateKey = stateAnimationStringCache.getOrPut(state) { state.toString().lowercase(Locale.getDefault())}
                    }
                    state = EntityState.EMPTY_STATE
                }
                else -> {
                    // update current state
                    stateTime += deltaTime
                    stateMachine.update()
                }
            }
        }
    }
}