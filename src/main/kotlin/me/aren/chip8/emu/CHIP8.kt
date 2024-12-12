package me.aren.chip8.emu

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

    init {
        programCounter = Utils.hexToU16(0x200)
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

    @OptIn(ExperimentalStdlibApi::class)
    fun cycle() {
        // I hate this, thanks kotlin
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
            Utils.hexToU16(0x1) -> {
                println("JMP to ${(opcode and Utils.hexToU16(0x0FFF)).toHexString()}")
                programCounter = opcode and Utils.hexToU16(0x0FFF)
            }
            Utils.hexToU16(0x2) -> {
                stack[sp.toInt()] = programCounter
                sp = (sp + 1u.toUShort()).toUShort()
                programCounter = opcode and Utils.hexToU16(0x0FFF)
            }
            Utils.hexToU16(0x3) -> {
                val x = (opcode and Utils.hexToU16(0x0F00)).toInt() shr 8
                val k = opcode and Utils.hexToU16(0x00FF)

                if(registers[x].toUInt() == k.toUInt()) {
                    incrementProgramCounter()
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0x4) -> {
                val x = (opcode and Utils.hexToU16(0x0F00)).toInt() shr 8
                val k = opcode and Utils.hexToU16(0x00FF)

                if(registers[x].toUInt() != k.toUInt()) {
                    incrementProgramCounter()
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0x5) -> {
                val x = (opcode and Utils.hexToU16(0x0F00)).toInt() shr 8
                val y = opcode and Utils.hexToU16(0x00F0)

                if(registers[x].toUInt() == registers[y.toInt()].toUInt()) {
                    incrementProgramCounter()
                }

                incrementProgramCounter()
            }
            Utils.hexToU16(0x6) -> {
                val x = (opcode and Utils.hexToU16(0x0F00)).toInt() shr 8
                val k = opcode and Utils.hexToU16(0x00FF)

                registers[x] = k.toUByte()

                incrementProgramCounter()
            }
        }
    }

    fun reset() {
        programCounter = Utils.hexToU16(0x200)
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

}