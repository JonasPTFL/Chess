package gui

import board.Move
import board.MoveType
import board.Position
import game.Game
import game.GameState
import game.GameStateListener
import pieces.Piece
import pieces.PieceColor
import pieces.PieceType
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel

/**
 *
 * @author Jonas Pollpeter
 */

class Visualizer(
    private val game: Game,
    actionAllowedForColor: Set<PieceColor> = setOf(PieceColor.White)
) : JFrame(), GameStateListener, KeyListener {
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
    private val colorLastMoveIndicatorBlackSquare = Color.decode("#786f2c")
    private val colorLastMoveIndicatorWhiteSquare = Color.decode("#939352")
    private val colorPromotionCircleBackground = Color.decode("#b0b0b0")
    private val colorPromotionCircleHighlightedBackground = Color.decode("#cb6a31")

    private val board = game.board
    private val boardPanel: JPanel
    private var selectedPiece: Piece? = null
    private var boardFlipped = false
    private var showCoordinates = false
    private var allowedToMove = false

    private var promotionMove: Move? = null
    private var selectedPromotionPiece: PieceType? = null
    private var promotionXPosition = 0
    val promotionPieceTypes = arrayOf(
        PieceType.Queen,
        PieceType.Knight,
        PieceType.Rook,
        PieceType.Bishop
    )
    private var lastMousePosition: Point? = null

    private val boardFlipKey = KeyEvent.VK_F
    private val showCoordinatesKey = KeyEvent.VK_H
    private val navigateBackwardKey = KeyEvent.VK_LEFT
    private val navigateForwardMoveKey = KeyEvent.VK_RIGHT
    private val undoLastMove = KeyEvent.VK_COMMA
    private val redoLastMove = KeyEvent.VK_PERIOD

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
            .getSubimage(0, 0, chessPiecesTexture.width / texturePieceCount, chessPiecesTexture.height / 2)
            .getScaledInstance(32, 32, Image.SCALE_SMOOTH)


        // register for callbacks on game
        if (actionAllowedForColor.contains(PieceColor.White)) {
            game.onPromotionPieceRequiredWhite = { selectedPromotionPiece }
            game.onWhiteTurn = {
                allowedToMove = true
            }
        }
        if (actionAllowedForColor.contains(PieceColor.Black)) {
            game.onPromotionPieceRequiredBlack = { selectedPromotionPiece }
            game.onBlackTurn = { allowedToMove = true }
        }
        // register for callbacks on game state
        game.addGameStateListener(this)

        // draw board
        boardPanel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val validMovePositions = selectedPiece?.getValidMoves()?.map { it.to } ?: emptyList()
                for (x in 0..7) {
                    for (y in 0..7) {
                        val xPanelCoordinate = x.toPanelXPosition()
                        val yPanelCoordinate = y.toPanelYPosition()
                        val position = Position(x, y)
                        val piece = board.history.currentlyNavigatedBoard.getPiece(position)
                        drawSquareBackground(g, xPanelCoordinate, yPanelCoordinate, position)

                        if (piece != null) drawPieceTexture(g, xPanelCoordinate, yPanelCoordinate, piece)

                        if (validMovePositions.contains(position)) {
                            drawValidMoveMarker(g, xPanelCoordinate, yPanelCoordinate, position)
                        }
                        if (showCoordinates) {
                            g.color = Color.WHITE
                            g.drawString(
                                "${position.x},${position.y}",
                                (xPanelCoordinate+1) * squareSize - 18,
                                (yPanelCoordinate+1) * squareSize - 4
                            )
                        }
                    }
                }
                if (promotionMove != null) drawPromotionSelection(g)

                drawGameState(g)
            }
        }
        val mouseListener = object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!game.isRunning() || !allowedToMove || board.history.isNavigatedAway()) return
                if (promotionMove != null) {
                    promotionPieceTypes.forEachIndexed { index, pieceType ->
                        if (isMouseHoveringOverPromotionPiece(index)) {
                            selectedPromotionPiece = pieceType
                            promotionMove?.let { game.executeMove(it) }
                            promotionMove = null
                            return
                        }
                    }
                }
                val clickedPosition = Position((e.x / squareSize).toPanelXPosition(), (e.y / squareSize).toPanelYPosition())
                val clickedPiece = board.getPiece(clickedPosition)

                val clickedMove = selectedPiece?.getValidMoves()?.find { move -> move.to == clickedPosition }
                selectedPiece = if (clickedMove == null && clickedPiece?.color == board.turn) {
                    // change selected piece, if clicked piece is of same color
                    clickedPiece
                } else {
                    if (clickedMove?.moveType == MoveType.Promotion) {
                        promotionMove = clickedMove
                        promotionXPosition = clickedMove.to.x
                        selectedPromotionPiece = null
                    } else {
                        // move selected piece, if clicked position is valid
                        clickedMove?.let { game.executeMove(it) }
                    }
                    // deselect piece, if clicked position is not valid or move was executed
                    null
                }
                update()
            }

            override fun mouseMoved(e: MouseEvent?) {
                if (promotionMove != null) {
                    lastMousePosition = e?.point
                    update()
                }
            }
        }
        boardPanel.addMouseListener(mouseListener)
        boardPanel.addMouseMotionListener(mouseListener)
        addKeyListener(this)

        add(boardPanel)
    }

    override fun keyTyped(e: KeyEvent?) {
    }

    override fun keyPressed(e: KeyEvent?) {
        when (e?.keyCode) {
            showCoordinatesKey -> {
                showCoordinates = !showCoordinates
                update()
            }
            boardFlipKey -> {
                boardFlipped = !boardFlipped
                update()
            }
            navigateBackwardKey -> {
                board.history.navigateBackward()
                update()
            }
            navigateForwardMoveKey -> {
                board.history.navigateForward()
                update()
            }
            undoLastMove -> {
                board.history.undoLastMove()
                update()
            }
            redoLastMove -> {
                board.history.redoLastMove()
                update()
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {
    }

    private fun Int.toPanelXPosition(): Int {
        return if (boardFlipped) 7 - this else this
    }

    private fun Int.toPanelYPosition(): Int {
        return if (boardFlipped) this else 7 - this
    }

    private fun Position.isWhiteOnPanel(): Boolean {
        return getColor() != PieceColor.White
    }

    private fun drawSquareBackground(g: Graphics, x: Int, y: Int, position: Position) {
        val isSelected = selectedPiece?.position == Position(position.x, position.y)
        val isLastMovePosition = board.history.currentlyNavigatedBoard.history.getLastMove()?.to == position || board.history.currentlyNavigatedBoard.history.getLastMove()?.from == position
        val isWhiteSquare = position.isWhiteOnPanel()

        // determine background color of square
        g.color = when {
            // selected piece color for white square
            isSelected && isWhiteSquare -> colorSelectedWhiteSquare
            // selected piece color for black square
            isSelected && !isWhiteSquare -> colorSelectedBlackSquare
            // last move indicator for white square
            isLastMovePosition && isWhiteSquare -> colorLastMoveIndicatorWhiteSquare
            // last move indicator for black square
            isLastMovePosition && !isWhiteSquare -> colorLastMoveIndicatorBlackSquare
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
        ).getScaledInstance(
            squareSize,
            squareSize,
            Image.SCALE_SMOOTH
        )

        g.drawImage(img, x * squareSize, y * squareSize, null)
    }

    private fun drawValidMoveMarker(g: Graphics, x: Int, y: Int, position: Position) {
        g.color = if (position.isWhiteOnPanel()) colorSelectedWhiteSquare else colorSelectedBlackSquare
        val centerOffset = (squareSize - moveIndicatorSize) / 2
        g.fillOval(
            x * squareSize + centerOffset,
            y * squareSize + centerOffset,
            moveIndicatorSize,
            moveIndicatorSize
        )
    }

    private fun drawGameState(g: Graphics) {
        val text = when (game.state) {
            GameState.Checkmate -> "Checkmate! ${board.turn.opposite()} wins!"
            GameState.Stalemate -> "Stalemate! Draw!"
            GameState.FiftyMoveRuleDraw -> "Draw by fifty move rule!"
            GameState.ThreefoldRepetitionDraw -> "Draw by threefold repetition!"
            GameState.InSufficientMaterialDraw -> "Draw by insufficient material!"
            else -> return
        }

        drawOverlayBackground(g)

        g.font = Font("Calibri", Font.BOLD, 40)
        g.color = Color.WHITE
        // draw string centered
        val fontMetrics = g.getFontMetrics(g.font)
        val textWidth = fontMetrics.stringWidth(text)
        val textHeight = fontMetrics.height
        g.drawString(text, (componentWidth - textWidth) / 2, (componentHeight - textHeight) / 2)
    }

    private fun drawPromotionSelection(g: Graphics) {
        drawOverlayBackground(g)
        promotionPieceTypes.forEachIndexed { index, pieceType ->
            val img = chessPiecesTexture.getSubimage(
                texturePieceOrder.indexOf(pieceType) * chessPiecesTexture.width / texturePieceCount,
                if (board.turn == PieceColor.White) 0 else chessPiecesTexture.height / 2,
                chessPiecesTexture.width / texturePieceCount,
                chessPiecesTexture.height / 2
            ).getScaledInstance(
                squareSize,
                squareSize,
                Image.SCALE_SMOOTH
            )

            // draw circle background for promotion piece, highlight on mouse hover
            g.color = if (isMouseHoveringOverPromotionPiece(index)) colorPromotionCircleHighlightedBackground
            else colorPromotionCircleBackground

            val xPos =
                if (boardFlipped) componentWidth - (promotionXPosition + 1) * squareSize else promotionXPosition * squareSize
            val yPos = index * squareSize
            g.fillOval(xPos, yPos, squareSize, squareSize)

            g.drawImage(img, xPos, yPos, null)
        }
    }

    private fun drawOverlayBackground(g: Graphics) {
        g.color = Color(0, 0, 0, 128)
        g.fillRect(0, 0, componentWidth, componentHeight)
    }

    private fun isMouseHoveringOverPromotionPiece(index: Int): Boolean {
        lastMousePosition?.let {
            val xPosStart =
                if (boardFlipped) componentWidth - (promotionXPosition + 1) * squareSize else promotionXPosition * squareSize
            val xPosEnd =
                if (boardFlipped) componentWidth - (promotionXPosition) * squareSize else (promotionXPosition + 1) * squareSize
            return it.x in xPosStart until xPosEnd
                    && it.y in index * squareSize until (index + 1) * squareSize
        } ?: return false
    }

    fun update() {
        boardPanel.repaint()
    }

    fun close() {
        dispose()
    }

    override fun onGameStateChanged(gameState: GameState) {
        update()
    }

    override fun onMoveExecuted(move: Move) {
        if (game.isRunning()) {
            allowedToMove = false
            update()
        }
    }
}