package engine

import board.Board
import board.Move
import pieces.PieceColor
import kotlin.math.max
import kotlin.math.min


/**
 *
 * @author Jonas Pollpeter
 */

class Engine(private val parameters: EngineParameters = EngineParameters()) {
    private var evaluationStartTime = 0L

    init {
        println("Engine initialized")
        println("maxDepth: ${parameters.maxDepth}")
        println("maxTime: ${parameters.maxTime}")
    }

    private fun getCurrentDepth(board: Board): Int {
        return if (board.isEndgame()) parameters.maxDepthEndgame else parameters.maxDepth
    }

    private fun Board.isEndgame(): Boolean {
        return getAllPieces(PieceColor.White).sumOf { it.type.value } <= parameters.endgameMaxPieceCount
                && getAllPieces(PieceColor.Black).sumOf { it.type.value } <= parameters.endgameMaxPieceCount
    }

    fun getBestMove(board: Board): Move {
        evaluationStartTime = System.currentTimeMillis()

        val bestBoard = if (board.turn == PieceColor.White) {
            successorFunction(board).maxBy { successor ->
                alphaBetaPruningMinimax(successor, false, getCurrentDepth(successor))
            }
        } else {
            successorFunction(board).minBy { successor ->
                alphaBetaPruningMinimax(successor, true, getCurrentDepth(successor))
            }
        }
        val bestMove = bestBoard.getLastMove() ?: throw IllegalStateException("no move found")
        return bestMove.copy(piece = board.getPiece(bestMove.from) ?: throw IllegalStateException("no piece found"))
    }

    private fun alphaBetaPruningMinimax(
        board: Board,
        maximizingPlayer: Boolean,
        depth: Int,
        initialAlpha: Float = Float.NEGATIVE_INFINITY,
        initialBeta: Float = Float.POSITIVE_INFINITY
    ): Float {
        if (terminalTest(board, depth)) {
            return utility(board)
        }
        var alpha = initialAlpha
        var beta = initialBeta

        return if (maximizingPlayer) {
            var value = Float.NEGATIVE_INFINITY
            successorFunction(board).forEach { successor ->
                value = max(value, alphaBetaPruningMinimax(successor, false, depth - 1, alpha, beta))
                alpha = max(alpha, value)
                if (value >= beta) return@alphaBetaPruningMinimax value
            }
            value
        } else {
            var value = Float.POSITIVE_INFINITY
            successorFunction(board).forEach { successor ->
                value = min(value, alphaBetaPruningMinimax(successor, true, depth - 1, alpha, beta))
                beta = min(beta, value)
                if (value < alpha) return@alphaBetaPruningMinimax value
            }
            value
        }
    }

    fun utility(board: Board): Float {
        var score = parameters.pieceValueWeight * board.getAllPieces().sumOf { piece ->
            if (piece.color == PieceColor.White) piece.type.value else piece.type.value.unaryMinus()
        }

        if (board.isCheckMate(PieceColor.Black)) {
            score += parameters.checkmateWeight
        } else if (board.isCheckMate(PieceColor.White)) {
            score -= parameters.checkmateWeight
        }

        if (board.isCheck(PieceColor.White)) {
            score += parameters.checkWeight
        } else if (board.isCheck(PieceColor.Black)) {
            score -= parameters.checkWeight
        }

        return score
    }

    private fun terminalTest(board: Board, depth: Int): Boolean {
        return depth <= 0 || board.isCheckMate() || board.isDraw() || System.currentTimeMillis() - evaluationStartTime >= parameters.maxTime
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