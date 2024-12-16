@file:OptIn(ExperimentalUnsignedTypes::class)

package me.aren.chip8

import me.aren.chip8.emu.CHIP8
import me.aren.chip8.emu.Emulator
import java.io.File
import javax.swing.SwingUtilities

fun main() {
    /*println("Hello World!")
    val chip8: CHIP8 = CHIP8()

    /*
    // Program to trigger infinite recursion
    // Memory addr. 200 sends to addr. 250, 250 sends back to 200
    chip8.memory[0x200] = 0x12u  // high
    chip8.memory[0x201] = 0x50u  // low

    chip8.memory[0x250] = 0x12u  // high
    chip8.memory[0x251] = 0x00u  // low (useless as all bits are already 0)
     */

    /*
    // set register 1 to 0x50
    chip8.memory[0x200] = 0x61u
    chip8.memory[0x201] = 0x50u

    // should increment pc by 2
    chip8.memory[0x202] = 0x31u
    chip8.memory[0x203] = 0x49u
     */

    /*chip8.memory[0x200] = 0x22u  // high
    chip8.memory[0x201] = 0x50u  // low
    chip8.memory[0x250] = 0x00u
    chip8.memory[0x251] = 0xEEu
    */

    chip8.memory[0x200] = 0x63u
    chip8.memory[0x201] = 0x01u
    chip8.memory[0x202] = 0x64u
    chip8.memory[0x203] = 0x01u
    chip8.memory[0x204] = 0x83u
    chip8.memory[0x205] = 0x41u

    while(true) {
        chip8.cycle()
    }*/

    // TODO: Fix instructions that fails in the test suite

    SwingUtilities.invokeLater {
        val emu = Emulator()
        val resourceUrl = Emulator::class.java.getResource("/roms/6-keypad.ch8")

        if (resourceUrl != null) {
            val romFile = File(resourceUrl.toURI())
            emu.loadRomFromFile(romFile)
        } else {
            println("ROM file not found")
        }
    }
}