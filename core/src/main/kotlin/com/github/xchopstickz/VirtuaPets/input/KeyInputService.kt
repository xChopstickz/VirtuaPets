package com.github.xchopstickz.VirtuaPets.input

class KeyInputService : InputService {
    override fun keyDown(keycode: Int): Boolean {
        println("Down")
        return true
    }
}