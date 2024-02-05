package game

import board.Board
import board.Move
import engine.Engine
import pieces.PieceColor
import pieces.PieceType
import kotlin.concurrent.thread

/**
 *
 * @author Jonas Pollpeter
 */

class Game(
    val board: Board = Board(),
    var onPromotionPieceRequiredWhite: ((xPosition: Int) -> PieceType?)? = null,
    var onPromotionPieceRequiredBlack: ((xPosition: Int) -> PieceType?)? = null,
    var onWhiteTurn: (() -> Unit)? = null,
    var onBlackTurn: (() -> Unit)? = null,
    private val randomMoveDelay: Long = 0
) {
    private val gameStateListeners = mutableListOf<GameStateListener>()

    var state = GameState.Initial
        private set
    private val computerEngine = Engine()

    init {
        board.initializeBoard()
        board.setOnPromotionRequest(this::onPromotionPieceRequired)
    }

    fun start() {
        if (state == GameState.Initial) {
            state = GameState.Running
            onWhiteTurn?.invoke() ?: doEngineMove()
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
            board.isCheckMate() -> GameState.Checkmate
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

        computerEngine.utility(board)

        if (state == GameState.Running) {
            // execute next turn on separate thread, to not block the visualizer and code execution of other players
            thread {
                when (board.turn) {
                    PieceColor.White -> onWhiteTurn?.invoke() ?: doEngineMove()
                    PieceColor.Black -> onBlackTurn?.invoke() ?: doEngineMove()
                }
            }
        }
    }

    fun doEngineMove() {
        val computerMove = computerEngine.getBestMove(board)
        computerMove.execute(this)
    }

    fun doRandomValidMove() {
        Thread.sleep(randomMoveDelay)
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