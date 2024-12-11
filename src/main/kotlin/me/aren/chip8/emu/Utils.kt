@file:OptIn(ExperimentalUnsignedTypes::class)

package me.aren.chip8.emu

typealias u8 = UByte
typealias u16 = UShort
typealias u32 = UInt
typealias u64 = ULong

typealias u8a = UByteArray
typealias u16a = UShortArray
typealias u32a = UIntArray
typealias u64a = ULongArray

class Utils {
    companion object {
        fun hexToU8(hex: Int): u8 {
            require(hex in 0..0xFF) { "Value out of range for UByte (0x00 to 0xFF)" }
            return hex.toUByte()
        }

        fun hexToU16(hex: Int): u16 {
            require(hex in 0..0xFFFF) { "Value out of range for UShort (0x0000 to 0xFFFF)" }
            return hex.toUShort()
        }

        fun hexToU32(hex: Long): u32 {
            require(hex in 0..0xFFFFFFFF) { "Value out of range for UInt (0x00000000 to 0xFFFFFFFF)" }
            return hex.toUInt()
        }

        fun hexToU64(hex: Long): u64 {
            require(hex >= 0) { "Value must be non-negative for ULong" }
            return hex.toULong()
        }
    }
}
