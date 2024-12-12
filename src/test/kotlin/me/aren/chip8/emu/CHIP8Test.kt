package me.aren.chip8.emu

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class CHIP8Test {
 private val chip8: CHIP8 = CHIP8()

 @Test
 fun test00E0() {
  chip8.reset()
  chip8.memory[0x200] = 0x00u
  chip8.memory[0x201] = 0xE0u

  chip8.cycle()

  for (i in chip8.graphics) assertEquals(0u.toUByte(), i)

 }

 @Test
 fun test00EE() {
  chip8.reset()
  chip8.memory[0x200] = 0x22u
  chip8.memory[0x201] = 0x50u
  chip8.memory[0x250] = 0x00u
  chip8.memory[0x251] = 0xEEu

  chip8.cycle()

  assertEquals(Utils.hexToU16(0x250), chip8.programCounter)
  assertEquals(1u.toUShort(), chip8.sp)

  chip8.cycle()

  assertEquals(Utils.hexToU16(0x202), chip8.programCounter)
  assertEquals(0u.toUShort(), chip8.sp)
 }

 @Test
 fun test1nnn() {
  chip8.reset()
  chip8.memory[0x200] = 0x12u
  chip8.memory[0x201] = 0x50u

  chip8.cycle()

  assertEquals(Utils.hexToU16(0x250), chip8.programCounter)
 }


 @Test
 fun test2nnn() {
  chip8.reset()
  chip8.memory[0x200] = 0x22u
  chip8.memory[0x201] = 0x50u
  chip8.memory[0x250] = 0x00u
  chip8.memory[0x251] = 0xEEu

  chip8.cycle()

  assertEquals(Utils.hexToU16(0x250), chip8.programCounter)
  assertEquals(1u.toUShort(), chip8.sp)
 }
}