package game

import board.Board
import board.Move
import pieces.PieceColor
import pieces.PieceType

/**
 *
 * @author Jonas Pollpeter
 */

class Game (
    val board: Board = Board(),
    var onPromotionPieceRequiredWhite: ((xPosition: Int) -> PieceType?)? = null,
    var onPromotionPieceRequiredBlack: ((xPosition: Int) -> PieceType?)? = null,
    var onWhiteTurn: (() -> Unit)? = null,
    var onBlackTurn: (() -> Unit)? = null
) {
    private val gameStateListeners = mutableListOf<GameStateListener>()

    private var gameState = GameState.Initial

    init {
        board.initializeBoard()
        board.setOnPromotionRequest(this::onPromotionPieceRequired)
    }

    fun start() {
        if (gameState == GameState.Initial) {
            gameState = GameState.Running
            onWhiteTurn?.invoke()
        }
    }

    private fun onPromotionPieceRequired(xPosition: Int): PieceType? {
        return when (board.turn) {
            PieceColor.White -> onPromotionPieceRequiredWhite?.invoke(xPosition)
            PieceColor.Black -> onPromotionPieceRequiredBlack?.invoke(xPosition)
        }
    }

    private fun checkGameState() {
        val previousGameState = gameState
        if (board.isCheckMate(board.turn.opposite())) {
            gameState = GameState.Checkmate
        } else if (board.isStaleMate(board.turn.opposite())) {
            gameState = GameState.Stalemate
        } else if (board.isDraw()) {
            gameState = GameState.Draw
        }
        if (previousGameState != gameState) {
            gameStateListeners.notify { onGameStateChanged(gameState) }
        }
    }
    fun onMoveExecuted(move: Move) {
        // check game state update
        checkGameState()

        gameStateListeners.notify { onMoveExecuted(move) }

        // notify next turn
        when (board.turn) {
            PieceColor.White -> onWhiteTurn?.invoke() ?: doRandomValidMove()
            PieceColor.Black -> onBlackTurn?.invoke() ?: doRandomValidMove()
        }
    }

    private fun doRandomValidMove() {
        val pieces = board.getAllPieces(board.turn)
        val validMoves = pieces.flatMap { it.getValidMoves() }
        if (validMoves.isEmpty()) return
        val randomMove = validMoves.random()
        randomMove.execute(this)
    }

    fun addGameStateListener(gameStateListener: GameStateListener) {
        gameStateListeners.add(gameStateListener)
    }

    fun removeGameStateListener(gameStateListener: GameStateListener) {
        gameStateListeners.remove(gameStateListener)
    }

    private fun List<GameStateListener>.notify(callback: GameStateListener.() -> Unit){
        forEach { it.callback() }
    }
}