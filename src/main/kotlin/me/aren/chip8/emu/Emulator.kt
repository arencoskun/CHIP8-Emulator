package me.aren.chip8.emu

import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

@ExperimentalUnsignedTypes
class Emulator : JFrame() {
    private val chip8 = CHIP8()
    private val SCALE = 10
    private val DISPLAY_WIDTH = 64 * SCALE
    private val DISPLAY_HEIGHT = 32 * SCALE

    private val displayPanel = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            for (y in 0 until 32) {
                for (x in 0 until 64) {
                    val pixelIndex = y * 64 + x
                    if (chip8.graphics[pixelIndex].toInt() == 1) {
                        g.color = Color.WHITE
                    } else {
                        g.color = Color.BLACK
                    }
                    g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE)
                }
            }
        }
    }

    init {
        title = "CHIP-8 Emulator"
        setSize(DISPLAY_WIDTH + 14, DISPLAY_HEIGHT + 32)
        defaultCloseOperation = EXIT_ON_CLOSE

        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {}

            override fun keyPressed(e: KeyEvent?) {
                if (e != null) {
                    println(e.keyChar)
                    val keyValue = chip8.keyMap[e.keyChar.uppercaseChar()]
                    if (keyValue != null) {
                        println(keyValue)
                        chip8.updateKeyState(keyValue, true)
                    }
                }
            }

            override fun keyReleased(e: KeyEvent?) {
                if (e != null) {
                    val keyValue = chip8.keyMap[e.keyChar.uppercaseChar()]
                    if (keyValue != null) {
                        chip8.updateKeyState(keyValue, false)
                    }
                }
            }

        })

        add(displayPanel)

        Timer(16) { // ~60 FPS
            chip8.cycle()
            displayPanel.repaint()
        }.start()

        isVisible = true
    }

    fun loadRomFromFile(file: File): Boolean {
        return try {
            val romData = file.readBytes()
            chip8.loadRom(romData)
            true
        } catch (e: Exception) {
            println("Error loading ROM: ${e.message}")
            false
        }
    }
}