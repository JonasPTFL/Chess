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

class Engine(
    maxDepth: Int = 3,
    private val pieceValue: Float = 1f,
    private val piecePossibleMoveSize: Float = 1f,
) {
    val firstLayerMoves = mutableListOf<Pair<Move, Int>>()

    var maxDepth = maxDepth
        private set

    fun setMaxDepth(maxDepth: Int) {
        this.maxDepth = maxDepth
    }

    fun getBestMove(board: Board): Move {
        val bestMove = minimaxDecision(board)
        return bestMove.copy(piece = board.getPiece(bestMove.from) ?: throw IllegalStateException("no piece found"))
    }

    private fun utility(board: Board, color: PieceColor): Int {
        var score = pieceValue * board.getAllPieces().sumOf { piece ->
            applyColorFactor(piece.type.value, color, piece.color)
        }
        score += piecePossibleMoveSize * board.getAllPieces().sumOf { piece ->
            applyColorFactor(piece.getValidMoves().size, color, piece.color)
        }
        return score.toInt()
    }

    private fun terminalTest(board: Board, depth: Int): Boolean {
        return depth >= maxDepth || board.isCheckMate() || board.isDraw()
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

    private fun minimaxDecision(board: Board): Move {
        firstLayerMoves.clear()
        val bestBoard = successorFunction(board).also { it.forEach {
//            it.printToConsole()
            println("move: ${it.getLastMove()} moves: ${it.getMoves().size}")
        }
        }.maxByOrNull {
            val result = minValue(it, Int.MIN_VALUE, Int.MAX_VALUE, 0)
            val lastMove = it.getLastMove()
            lastMove?.let { move ->
                firstLayerMoves.add(move to result)
            }
            result
        }
//        firstLayerMoves.sortedByDescending { it.second }.joinToString("\n") {
//            "${it.second}: ${it.first} at ${bestBoard?.getMoves()?.size}"
//        }.also { println(it) }
        bestBoard?.printToConsole()
        return bestBoard?.getLastMove() ?: throw IllegalStateException("no successors found")
    }

    private fun maxValue(board: Board, initialAlpha: Int, initialBeta: Int, depth: Int): Int {
        var alpha = initialAlpha
        if (terminalTest(board, depth)) return utility(board, board.turn)

        var v = Int.MIN_VALUE
        for (successor in successorFunction(board)) {
            v = max(v, minValue(successor, alpha, initialBeta, depth + 1))
            if (v >= initialBeta) return v
            alpha = max(alpha, v)
        }
        return v
    }

    private fun minValue(board: Board, initialAlpha: Int, initialBeta: Int, depth: Int): Int {
        var beta = initialBeta
        if (terminalTest(board, depth)) return utility(board, board.turn)

        var v = Int.MAX_VALUE
        for (successor in successorFunction(board)) {
            v = min(v, maxValue(successor, initialAlpha, beta, depth + 1))
            if (v <= initialAlpha) return v
            beta = min(beta, v)
        }
        return v
    }

    private fun applyColorFactor(value: Int, evaluationColor: PieceColor, currentPieceColor: PieceColor): Int {
        return if (evaluationColor == currentPieceColor) value else value.unaryMinus()
    }
}