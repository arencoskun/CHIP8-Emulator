package me.aren.chip8.emu

import me.aren.chip8.emu.Utils.Companion.toInt
import kotlin.random.Random

typealias u8 = UByte
typealias u16 = UShort
typealias u32 = UInt
typealias u64 = ULong

typealias u8a = UByteArray
typealias u16a = UShortArray
typealias u32a = UIntArray
typealias u64a = ULongArray

@OptIn(ExperimentalUnsignedTypes::class)
class CHIP8 {
    var opcode: u16
    var memory: u8a = u8a(4096)
    var graphics: u8a = u8a(64*32)
    var registers: u8a = u8a(16)
    var index: u16
    var programCounter: u16
    var delayTimer: u8
    var soundTimer: u8
    var stack: u16a = u16a(16)
    var sp: u16
    var keys: u8a = u8a(16)

    val FONTSET: u8a = ubyteArrayOf(
        0xF0u, 0x90u, 0x90u, 0x90u, 0xF0u, // 0
        0x20u, 0x60u, 0x20u, 0x20u, 0x70u, // 1
        0xF0u, 0x10u, 0xF0u, 0x80u, 0xF0u, // 2
        0xF0u, 0x10u, 0xF0u, 0x10u, 0xF0u, // 3
        0x90u, 0x90u, 0xF0u, 0x10u, 0x10u, // 4
        0xF0u, 0x80u, 0xF0u, 0x10u, 0xF0u, // 5
        0xF0u, 0x80u, 0xF0u, 0x90u, 0xF0u, // 6
        0xF0u, 0x10u, 0x20u, 0x40u, 0x40u, // 7
        0xF0u, 0x90u, 0xF0u, 0x90u, 0xF0u, // 8
        0xF0u, 0x90u, 0xF0u, 0x10u, 0xF0u, // 9
        0xF0u, 0x90u, 0xF0u, 0x90u, 0x90u, // A
        0xE0u, 0x90u, 0xE0u, 0x90u, 0xE0u, // B
        0xF0u, 0x80u, 0x80u, 0x80u, 0xF0u, // C
        0xE0u, 0x90u, 0x90u, 0x90u, 0xE0u, // D
        0xF0u, 0x80u, 0xF0u, 0x80u, 0xF0u, // E
        0xF0u, 0x80u, 0xF0u, 0x80u, 0x80u  // F
    )

    val keyMap = mapOf(
        '1' to 0x1, '2' to 0x2, '3' to 0x3, 'C' to 0xC,
        '4' to 0x4, '5' to 0x5, '6' to 0x6, 'D' to 0xD,
        '7' to 0x7, '8' to 0x8, '9' to 0x9, 'E' to 0xE,
        'A' to 0xA, '0' to 0x0, 'B' to 0xB, 'F' to 0xF
    )

    init {
        programCounter = 0x200u
        opcode = 0u
        index = 0u
        sp = 0u
        delayTimer = 0u
        soundTimer = 0u

        for (i in graphics.indices) graphics[i] = 0u
        for (i in memory.indices) memory[i] = 0u
        for (i in stack.indices) stack[i] = 0u
        for (i in registers.indices) registers[i] = 0u
        for (i in keys.indices) keys[i] = 0u
        for (i in FONTSET.indices) memory[i] = FONTSET[i]
    }

    private fun incrementProgramCounter() {
        programCounter = (programCounter + 2u.toUShort()).toUShort()
    }

    fun cycle() {
        opcode = ((memory[programCounter.toInt()].toUInt() shl 8) or memory[(programCounter + 1u).toInt()].toUInt()).toUShort()

        val first = (opcode.toInt() shr 12).toUShort()

        when(first) {
            Utils.hexToU16(0x0) -> {
                // CLS
                if(opcode == Utils.hexToU16(0x00E0)) {
                    for (i in graphics.indices) graphics[i] = 0u
                }
                // RET
                else if(opcode == Utils.hexToU16(0x00EE)) {
                    sp = (sp - 1u.toUShort()).toUShort()
                    programCounter = stack[sp.toInt()]
                }

                incrementProgramCounter()
            }
            // JMP
            Utils.hexToU16(0x1) -> programCounter = opcode and 0x0FFFu
            Utils.hexToU16(0x2) -> {
                stack[sp.toInt()] = programCounter
                sp = (sp + 1u.toUShort()).toUShort()
                programCounter = opcode and 0x0FFFu
            }
            Utils.hexToU16(0x3) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val k = opcode and 0x00FFu

                if(registers[x].toUInt() == k.toUInt()) {
                    incrementProgramCounter()
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0x4) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val k = opcode and 0x00FFu

                if(registers[x].toUInt() != k.toUInt()) {
                    incrementProgramCounter()
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0x5) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val y = (opcode and 0x00F0u).toInt() shr 4

                if(registers[x].toUInt() == registers[y].toUInt()) {
                    incrementProgramCounter()
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0x6) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val k = opcode and 0x00FFu

                registers[x] = k.toUByte()

                incrementProgramCounter()
            }
            Utils.hexToU16(0x7) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val k = opcode and 0x00FFu

                registers[x] = (registers[x] + k.toUByte()).toUByte()

                incrementProgramCounter()
            }
            Utils.hexToU16(0x8) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val y = (opcode and 0x00F0u).toInt() shr 4
                val op = (opcode and 0x000Fu).toInt()

                when(op) {
                    0 -> registers[x] = registers[y]
                    1 -> registers[x] = registers[x] or registers[y]
                    2 -> registers[x] = registers[x] and registers[y]
                    3 -> registers[x] = registers[x] xor registers[y]
                    4 -> {
                        val result = registers[x] + registers[y]
                        registers[x] = result.toUByte()
                        registers[registers.size - 1] = (result > 0xFFu).toInt().toUByte()
                    }
                    5 -> {
                        val noborrow = registers[x] >= registers[y]
                        val result = registers[x] - registers[y]
                        registers[x] = result.toUByte()
                        registers[registers.size - 1] = noborrow.toInt().toUByte()
                    }
                    // TODO: Fails test suite
                    6 -> {
                        val lsb = registers[x] and 0x1u
                        registers[registers.size - 1] = lsb
                        registers[x] = (registers[x].toInt() shr 1).toUByte()
                    }
                    7 -> {
                        val result = registers[y] - registers[x]
                        registers[x] = result.toUByte()
                        registers[registers.size - 1] = (registers[y] >= registers[x]).toInt().toUByte()
                    }
                    // TODO: Fails test suite
                    0xE -> {
                        val msb = (registers[x].toInt() shr 7) and 0x1
                        registers[registers.size - 1] = msb.toUByte()
                        registers[x] = (registers[x].toInt() shl 1).toUByte()
                    }
                }


                incrementProgramCounter()
            }
            Utils.hexToU16(0x9) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val y = (opcode and 0x00F0u).toInt() shr 4

                if(registers[x].toUInt() != registers[y].toUInt()) {
                    incrementProgramCounter()
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0xA) -> {
                index = opcode and 0x0FFFu
                incrementProgramCounter()
            }
            Utils.hexToU16(0xB) -> programCounter = ((opcode and 0x0FFFu) + registers[0]).toUShort()
            Utils.hexToU16(0xC) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val kk = (opcode and 0x00FFu).toUByte()
                val randomByte = Random.nextInt(0, 256).toUByte()

                registers[x] = randomByte and kk

                incrementProgramCounter()
            }
            Utils.hexToU16(0xD) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val y = (opcode and 0x00F0u).toInt() shr 4
                val height = (opcode and 0x000Fu).toInt()

                val xCoord = registers[x].toInt() % 64
                val yCoord = registers[y].toInt() % 32

                registers[0xF] = 0u

                for (row in 0 until height) {
                    val spriteByte = memory[(index + row.toUInt()).toInt()]
                    val screenY = (yCoord + row) % 32

                    for (col in 0 until 8) {
                        if ((spriteByte.toInt() and (0x80 shr col)) != 0) {
                            val screenX = (xCoord + col) % 64
                            val pixelIndex = screenY * 64 + screenX

                            if (graphics[pixelIndex].toInt() == 1) {
                                registers[0xF] = 1u
                            }

                            graphics[pixelIndex] = graphics[pixelIndex] xor 1u
                        }
                    }
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0xE) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val ins = (opcode and 0x00FFu).toUByte()

                when(ins) {
                    Utils.hexToU8(0x9E) -> {
                        if(keys[registers[x].toInt()].toUInt() == 1u) {
                            incrementProgramCounter()
                        }

                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0xA1) -> {
                        if(keys[registers[x].toInt()].toUInt() != 1u) {
                            incrementProgramCounter()
                        }

                        incrementProgramCounter()
                    }
                }
            }
            Utils.hexToU16(0xF) -> {
                val x = (opcode and 0x0F00u).toInt() shr 8
                val ins = (opcode and 0x00FFu).toUByte()

                when(ins) {
                    Utils.hexToU8(0x07) -> {
                        registers[x] = delayTimer
                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0x0A) -> {
                        val pressedKey = keys.indexOfFirst { it.toUInt() == 1u }

                        if (pressedKey != -1) {
                            registers[x] = pressedKey.toUByte()
                            incrementProgramCounter()
                        }

                        // dont increment pc to pause execution
                    }
                    Utils.hexToU8(0x15) -> {
                        delayTimer = registers[x]
                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0x18) -> {
                        soundTimer = registers[x]
                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0x1E) -> {
                        index = (index + registers[x]).toUShort()
                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0x29) -> {
                        index = (registers[x] * 5u).toUShort()
                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0x33) -> {
                        val value = registers[x].toInt()

                        // hundreds
                        memory[index.toInt()] = (value / 100).toUByte()

                        // tens
                        memory[(index + 1u).toInt()] = ((value % 100) / 10).toUByte()

                        // ones
                        memory[(index + 2u).toInt()] = (value % 10).toUByte()

                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0x55) -> {
                        for (i in 0..x) {
                            memory[(index + i.toUInt()).toInt()] = registers[i]
                        }

                        index = (index + x.toUInt() + 1u).toUShort()
                        incrementProgramCounter()
                    }
                    Utils.hexToU8(0x65) -> {
                        for (i in 0..x) {
                            registers[i] = memory[(index + i.toUInt()).toInt()]
                        }

                        index = (index + x.toUInt() + 1u).toUShort()
                        incrementProgramCounter()
                    }
                }
            }
        }
    }

    fun reset() {
        programCounter = 0x200u
        opcode = 0u
        index = 0u
        sp = 0u
        delayTimer = 0u
        soundTimer = 0u

        for (i in graphics.indices) graphics[i] = 0u
        for (i in memory.indices) memory[i] = 0u
        for (i in stack.indices) stack[i] = 0u
        for (i in registers.indices) registers[i] = 0u
        for (i in keys.indices) keys[i] = 0u
        for (i in FONTSET.indices) memory[i] = FONTSET[i]
    }

    fun updateKeyState(keyValue: Int, isPressed: Boolean) {
        keys[keyValue] = if (isPressed) 1u else 0u
    }

    fun loadRom(romData: ByteArray) {
        val startAddress = 0x200

        for (i in romData.indices) {
            memory[startAddress + i] = romData[i].toUByte()
        }
    }

    fun decrementTimers() {
        if (delayTimer > 0u) {
            delayTimer = (delayTimer - 1u).toUByte()
        }

        if (soundTimer > 0u) {
            soundTimer = (soundTimer - 1u).toUByte()
        }
    }

}