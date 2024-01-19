package game

import board.Board
import board.Move
import pieces.PieceColor
import pieces.PieceType

/**
 *
 * @author Jonas Pollpeter
 */

class Game(
    val board: Board = Board(),
    var onPromotionPieceRequiredWhite: ((xPosition: Int) -> PieceType?)? = null,
    var onPromotionPieceRequiredBlack: ((xPosition: Int) -> PieceType?)? = null,
    var onWhiteTurn: (() -> Unit)? = null,
    var onBlackTurn: (() -> Unit)? = null
) {
    private val gameStateListeners = mutableListOf<GameStateListener>()

    var state = GameState.Initial
        private set

    init {
        board.initializeBoard()
        board.setOnPromotionRequest(this::onPromotionPieceRequired)
    }

    fun start() {
        if (state == GameState.Initial) {
            state = GameState.Running
            onWhiteTurn?.invoke() ?: doRandomValidMove()
        }
    }

    private fun onPromotionPieceRequired(xPosition: Int): PieceType? {
        return when (board.turn) {
            PieceColor.White -> onPromotionPieceRequiredWhite?.invoke(xPosition)
            PieceColor.Black -> onPromotionPieceRequiredBlack?.invoke(xPosition)
        }
    }

    private fun updateGameState() {
        state = when {
            board.isCheckMate(board.turn) || board.isCheckMate(board.turn.opposite()) -> GameState.Checkmate
            board.isStaleMate(board.turn) || board.isStaleMate(board.turn.opposite()) -> GameState.Stalemate
            board.isInSufficientMaterial() -> GameState.InSufficientMaterialDraw
            board.isThreefoldRepetition() -> GameState.ThreefoldRepetitionDraw
            board.isFiftyMoveRule() -> GameState.FiftyMoveRuleDraw
            else -> return
        }
        gameStateListeners.notify { onGameStateChanged(state) }
    }

    fun onMoveExecuted(move: Move) {
        gameStateListeners.notify { onMoveExecuted(move) }
        // check game state update
        updateGameState()

        if (state == GameState.Running) {
            // notify next turn
            when (board.turn) {
                PieceColor.White -> onWhiteTurn?.invoke() ?: doRandomValidMove()
                PieceColor.Black -> onBlackTurn?.invoke() ?: doRandomValidMove()
            }
        }
    }

    private fun doRandomValidMove() {
        val pieces = board.getAllPieces(board.turn)
        val validMoves = pieces.flatMap { it.getValidMoves() }
        val randomMove = validMoves.random()
        randomMove.execute(this)
    }

    fun addGameStateListener(gameStateListener: GameStateListener) {
        gameStateListeners.add(gameStateListener)
    }

    fun removeGameStateListener(gameStateListener: GameStateListener) {
        gameStateListeners.remove(gameStateListener)
    }

    private fun List<GameStateListener>.notify(callback: GameStateListener.() -> Unit) {
        forEach { it.callback() }
    }

    fun isRunning(): Boolean {
        return state == GameState.Running
    }
}