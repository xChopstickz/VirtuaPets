package com.github.xchopstickz.VirtuaPets.ashley.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor

interface EntityState : State<Entity> {
    override fun enter(entity: Entity?) = Unit

    override fun update(entity: Entity?) = Unit

    override fun exit(entity: Entity?) = Unit

    override fun onMessage(entity: Entity?, telegram: Telegram?) = false

    companion object {
        val EMPTY_STATE = object : EntityState {}
    }
}

class StateComponent: Component, Pool.Poolable {

    var stateTime = 0f
    var state = EntityState.EMPTY_STATE
    // Keep stateMachine internal to avoid calling changeState at any time during a frame.
    // That way we can guarantee that AI is always processed within the StateSystem.
    internal val stateMachine = DefaultStateMachine<Entity, State<Entity>>()

    fun dispatchMessage(
        messageDispatcher: MessageDispatcher,
        msg: Int,
        extraInfo: Any? = null,
        sender: Telegraph? = null
    ) {
        messageDispatcher.dispatchMessage(sender, stateMachine, msg, extraInfo)
    }

    override fun reset() {
        stateTime = 0f
        state = EntityState.EMPTY_STATE
        stateMachine.globalState = null
        stateMachine.owner = null
    }
    companion object {
        val mapper = mapperFor<StateComponent>()
    }
}

val Entity.stateCmp: StateComponent
    get() = this[StateComponent.mapper]
        ?: throw GdxRuntimeException("StateComponent for entity '$this' is null")

