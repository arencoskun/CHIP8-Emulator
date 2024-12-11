@file:OptIn(ExperimentalUnsignedTypes::class)

package me.aren.chip8

import me.aren.chip8.emu.CHIP8

fun main() {
    println("Hello World!")
    val chip8: CHIP8 = CHIP8()

    // Program to trigger infinite recursion
    // Memory addr. 200 sends to addr. 250, 250 sends back to 200
    chip8.memory[0x200] = 0x12u  // high
    chip8.memory[0x201] = 0x50u  // low

    chip8.memory[0x250] = 0x12u  // high
    chip8.memory[0x251] = 0x00u  // low (useless as all bits are already 0)

    while(true) {
        chip8.cycle()
    }
}