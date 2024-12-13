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

 @Test
 fun test3xkk() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x35u
  chip8.memory[0x202] = 0x33u
  chip8.memory[0x203] = 0x35u

  chip8.cycle()
  chip8.cycle()

  assertEquals(518u.toUShort(), chip8.programCounter)
 }

 @Test
 fun test3xkk_2() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x35u
  chip8.memory[0x202] = 0x33u
  chip8.memory[0x203] = 0x34u

  chip8.cycle()
  chip8.cycle()

  assertEquals(516u.toUShort(), chip8.programCounter)
 }

 @Test
 fun test4xkk() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x35u
  chip8.memory[0x202] = 0x43u
  chip8.memory[0x203] = 0x35u

  chip8.cycle()
  chip8.cycle()

  assertEquals(516u.toUShort(), chip8.programCounter)
 }

 @Test
 fun test4xkk_2() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x35u
  chip8.memory[0x202] = 0x43u
  chip8.memory[0x203] = 0x34u

  chip8.cycle()
  chip8.cycle()

  assertEquals(518u.toUShort(), chip8.programCounter)
 }

 @Test
 fun test6xkk() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x35u

  chip8.cycle()

  assertEquals(0x35u.toUByte(), chip8.registers[3])
 }

 @Test
 fun test7xkk() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x35u
  chip8.memory[0x202] = 0x73u
  chip8.memory[0x203] = 0x10u

  chip8.cycle()
  chip8.cycle()

  assertEquals(0x45u.toUByte(), chip8.registers[3])
 }

 @Test
 fun test8xy0() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x35u
  chip8.memory[0x202] = 0x85u
  chip8.memory[0x203] = 0x30u

  chip8.cycle()
  chip8.cycle()

  assertEquals(0x35u.toUByte(), chip8.registers[5])
 }

 @Test
 fun test8xy1() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x01u
  chip8.memory[0x202] = 0x83u
  chip8.memory[0x203] = 0x21u

  chip8.cycle()
  chip8.cycle()

  assertEquals(0x1.toUByte(), chip8.registers[3])
 }

 @Test
 fun test8xy1_2() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x00u
  chip8.memory[0x202] = 0x83u
  chip8.memory[0x203] = 0x21u

  chip8.cycle()
  chip8.cycle()

  assertEquals(0x0.toUByte(), chip8.registers[3])
 }

 @Test
 fun test8xy1_3() {
  chip8.reset()
  chip8.memory[0x200] = 0x63u
  chip8.memory[0x201] = 0x01u
  chip8.memory[0x202] = 0x64u
  chip8.memory[0x203] = 0x01u
  chip8.memory[0x204] = 0x83u
  chip8.memory[0x205] = 0x41u

  chip8.cycle()
  chip8.cycle()
  chip8.cycle()

  assertEquals(0x1.toUByte(), chip8.registers[3])
 }


}