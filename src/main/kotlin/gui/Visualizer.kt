package gui

import board.Board
import board.Position
import pieces.Piece
import pieces.PieceColor
import pieces.PieceType
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
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
    private val moveIndicatorSize = 30
    private val chessPiecesTexture: java.awt.image.BufferedImage
    private val componentWidth = squareSize * 8
    private val componentHeight = squareSize * 8

    private val colorWhite = Color.decode("#90826d")
    private val colorBlack = Color.decode("#6d523b")
    private val colorSelectedBlackSquare = Color.decode("#525332")
    private val colorSelectedWhiteSquare = Color.decode("#6b7455")

    private val boardPanel: JPanel
    private var selectedPiece: Piece? = null
    private var boardFlipped = true

    init {
        title = "Chess"
        setSize((squareSize * 8.15).toInt(), (squareSize * 8.39).toInt())
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        chessPiecesTexture = try {
            // Load the PNG file
            ImageIO.read(File(chessPiecesTextureFileName))
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Could not load image")
        }
        iconImage = chessPiecesTexture
            .getSubimage(0,0,chessPiecesTexture.width / texturePieceCount, chessPiecesTexture.height / 2)
            .getScaledInstance(32, 32, Image.SCALE_SMOOTH)


        // draw board
        boardPanel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2d = g as Graphics2D
                val old = g2d.transform
                // rotate board
                g2d.rotate(Math.toRadians(if (boardFlipped) 180.0 else 0.0), width.toDouble() / 2, height.toDouble() / 2)

                val validMovePositions = selectedPiece?.getValidMoves()?.map { it.to } ?: emptyList()
                for (x in 0..7) {
                    for (y in 0..7) {
                        val isWhiteSquare = (x + y) % 2 != 0
                        drawSquareBackground(g, x, y, isWhiteSquare)

                        board.getPiece(Position(x, y))?.let {
                            drawPieceTexture(g, x, y, it)
                        }

                        if (validMovePositions.contains(Position(x, y))) {
                            drawValidMoveMarker(g, x, y, isWhiteSquare)
                        }
                    }
                }

                g2d.transform = old
            }
        }
        boardPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val clickXPos = if (boardFlipped) componentWidth - e.x else e.x
                val clickYPos = if (boardFlipped) componentHeight - e.y else e.y
                val clickedPosition = Position(clickXPos / squareSize, clickYPos / squareSize)
                val clickedPiece = board.getPiece(clickedPosition)

                if (clickedPiece?.color == board.turn) {
                    // change selected piece, if clicked piece is of same color
                    selectedPiece = clickedPiece
                } else {
                    // move selected piece, if clicked position is valid
                    selectedPiece?.let {
                        val clickedMove = it.getValidMoves().find { move -> move.to == clickedPosition }
                        clickedMove?.execute(board)
                        // deselect piece, if clicked position is not valid or move was executed
                        selectedPiece = null
                    }
                }
                update()
            }
        })

        add(boardPanel)
    }

    private fun drawSquareBackground(g: Graphics, x: Int, y: Int, isWhiteSquare: Boolean) {
        val isSelected = selectedPiece?.position == Position(x, y)

        // determine background color of square
        g.color = when {
            // selected piece color for white square
            isSelected && isWhiteSquare -> colorSelectedWhiteSquare
            // selected piece color for black square
            isSelected && !isWhiteSquare -> colorSelectedBlackSquare
            // white square
            isWhiteSquare -> colorWhite
            // black square
            else -> colorBlack
        }
        g.fillRect(x * squareSize, y * squareSize, squareSize, squareSize)
    }

    private fun drawPieceTexture(g: Graphics, x: Int, y: Int, piece: Piece) {
        val img = chessPiecesTexture.getSubimage(
            texturePieceOrder.indexOf(piece.type) * chessPiecesTexture.width / texturePieceCount,
            if (piece.color == PieceColor.White) 0 else chessPiecesTexture.height / 2,
            chessPiecesTexture.width / texturePieceCount,
            chessPiecesTexture.height / 2
        )

        val rotationRequired = Math.toRadians(if (boardFlipped) 180.0 else 0.0)
        val locationX: Double = img.width / 2.0
        val locationY: Double = img.height / 2.0
        val tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY)
        val op = AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR)

        val scaledImg = op.filter(img, null).getScaledInstance(
            squareSize,
            squareSize,
            Image.SCALE_SMOOTH
        )

        g.drawImage(scaledImg, x * squareSize, y * squareSize, null)
    }

    private fun drawValidMoveMarker(g: Graphics, x: Int, y: Int, isWhiteSquare: Boolean) {
        g.color = if (isWhiteSquare) colorSelectedWhiteSquare else colorSelectedBlackSquare
        val centerOffset = (squareSize - moveIndicatorSize) / 2
        g.fillOval(
            x * squareSize + centerOffset,
            y * squareSize + centerOffset,
            moveIndicatorSize,
            moveIndicatorSize
        )
    }

    fun update() {
        boardPanel.repaint()
    }
}