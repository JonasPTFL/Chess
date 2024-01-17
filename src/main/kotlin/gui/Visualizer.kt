package gui

import board.Board
import board.Position
import pieces.PieceType
import java.awt.Color
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel


/**
 *
 * @author Jonas Pollpeter
 */

class Visualizer(private val board: Board) : JFrame() {
    private val chessPiecesTextureFileName = "chess_pieces_texture.png"
    private val texturePieceCount = 6
    private val texturePieceOrder =
        arrayOf(PieceType.King, PieceType.Queen, PieceType.Bishop, PieceType.Knight, PieceType.Rook, PieceType.Pawn)
    private val squareSize = 100
    private val chessPiecesTexture: java.awt.image.BufferedImage

    private val colorWhite = Color.decode("#90826d")
    private val colorBlack = Color.decode("#6d523b")

    private val boardPanel: JPanel

    init {
        title = "Chess"
        setSize(squareSize * 8, squareSize * 8)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        chessPiecesTexture = try {
            // Load the PNG file
            ImageIO.read(File(chessPiecesTextureFileName))
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Could not load image")
        }


        // draw board
        boardPanel = object : JPanel() {
            override fun paintComponent(g: java.awt.Graphics) {
                super.paintComponent(g)

                for (x in 0..7) {
                    for (y in 0..7) {
                        g.color = if ((x + y) % 2 == 0) colorWhite else colorBlack
                        g.fillRect(x * squareSize, y * squareSize, squareSize, squareSize)

                        if (!board.isOccupied(Position(x, y))) continue

                        val piece = board.getPiece(Position(x, y))!!

                        g.drawImage(
                            chessPiecesTexture.getSubimage(
                                texturePieceOrder.indexOf(piece.type) * chessPiecesTexture.width / texturePieceCount,
                                if (piece.color == pieces.PieceColor.White) 0 else chessPiecesTexture.height / 2,
                                chessPiecesTexture.width / texturePieceCount,
                                chessPiecesTexture.height / 2
                            ).getScaledInstance(
                                squareSize,
                                squareSize,
                                java.awt.Image.SCALE_SMOOTH
                            ), x * squareSize, y * squareSize, this
                        )
                    }
                }
            }
        }

        add(boardPanel)
    }

    fun update() {
        boardPanel.repaint()
    }
}