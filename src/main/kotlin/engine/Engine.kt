package engine

import board.Board
import board.Move
import pieces.PieceColor


/**
 *
 * @author Jonas Pollpeter
 */

class Engine(
    maxDepth: Int = 3,
    maxTime: Long = 2000,
    private val pieceValueWeight: Float = 1f,
    private val piecePossibleMoveSizeWeight: Float = 1f,
    private val checkmateWeight: Int = 1000,
) {
    val firstLayerMoves = mutableListOf<Pair<Move, Int>>()

    val currentBest = 0f
    var currentlyMaximizing = true

    var maxDepth = maxDepth
        private set
    var maxTime = maxTime
        private set

    var evaluationStartTime = 0L
        private set

    fun setMaxDepth(maxDepth: Int) {
        this.maxDepth = maxDepth
    }

    fun setMaxTime(maxTime: Long) {
        this.maxTime = maxTime
    }

    init {
        println("Engine initialized")
        println("maxDepth: $maxDepth")
        println("maxTime: $maxTime")
    }

    fun getBestMove(board: Board): Move {
        evaluationStartTime = System.currentTimeMillis()
        currentlyMaximizing = board.turn == PieceColor.White

        val bestBoard = if (board.turn == PieceColor.White) {
            successorFunction(board).maxBy { successor ->
                minimax(successor, false)
            }
        } else {
            successorFunction(board).minBy { successor ->
                minimax(successor, true)
            }
        }
        val bestMove = bestBoard.getLastMove() ?: throw IllegalStateException("no move found")
        return bestMove.copy(piece = board.getPiece(bestMove.from) ?: throw IllegalStateException("no piece found"))
    }

    private fun minimax(board: Board, maximizingPlayer: Boolean, depth: Int = maxDepth): Float {
        if (terminalTest(board, depth)) {
            return utility(board)
        }

        return if (maximizingPlayer) {
            successorFunction(board).maxOf { successor ->
                minimax(successor, false, depth - 1)
            }
        } else {
            successorFunction(board).minOf { successor ->
                minimax(successor, true, depth - 1)
            }
        }
    }

    fun utility(board: Board): Float {
        var score = pieceValueWeight * board.getAllPieces().sumOf { piece ->
            if (piece.color == PieceColor.White) piece.type.value else piece.type.value.unaryMinus()
        }

        return score
    }

    private fun terminalTest(board: Board, depth: Int): Boolean {
        return depth <= 0 || board.isCheckMate() || board.isDraw() //|| System.currentTimeMillis() - evaluationStartTime >= maxTime
    }

    private fun successorFunction(board: Board): List<Board> {
        return board.getAllPieces(board.turn).flatMap { piece ->
            piece.getValidMoves().map { move ->
                val copiedBoard = board.copy()
                val copiedMove = move.copy(piece = copiedBoard.getPiece(move.from)!!)
                copiedMove.executeOnNewBoard(copiedBoard)
                copiedBoard
            }
        }
    }
}