package engine

import board.Board
import board.Move
import board.MoveType
import pieces.PieceColor
import pieces.PieceType
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min


/**
 *
 * @author Jonas Pollpeter
 */

class Engine(private val parameters: EngineParameters = EngineParameters()) {
    private var evaluationStartTime = 0L

    private var nodeCounter = 0

    init {
        println("Engine initialized")
        parameters.printToConsole()
    }

    private fun getCurrentDepth(board: Board): Int {
        return if (board.isEndgame()) parameters.maxDepthEndgame else parameters.maxDepth
    }

    private fun Board.isEndgame(): Boolean {
        return getAllPieces(PieceColor.White).sumOf { it.type.value } <= parameters.endgameMaxPieceCount
                && getAllPieces(PieceColor.Black).sumOf { it.type.value } <= parameters.endgameMaxPieceCount
    }

    private fun PieceType.getPieceSquareTable(isEndgame: Boolean) = when(this) {
        PieceType.Pawn -> PieceSquareTables.pawnTable
        PieceType.Knight -> PieceSquareTables.knightTable
        PieceType.Bishop -> PieceSquareTables.bishopTable
        PieceType.Rook -> PieceSquareTables.rookTable
        PieceType.Queen -> PieceSquareTables.queenTable
        PieceType.King -> if (isEndgame) PieceSquareTables.kingTableEndgame else PieceSquareTables.kingTable
    }

    private fun continuousNPSCalculation(terminationCondition: Boolean) {
        while (!terminationCondition) {
            println("Nodes per second: $nodeCounter")
            nodeCounter = 0
            Thread.sleep(1000)
        }
    }

    fun getBestMove(board: Board): Move {
        val boardToEvaluate = board.copy()
        evaluationStartTime = System.currentTimeMillis()
        var moveFound = false

        thread {
            continuousNPSCalculation(moveFound)
        }

        val bestMove = if (boardToEvaluate.turn == PieceColor.White) {
            successorFunction(boardToEvaluate).maxBy { move ->
                move.execute(boardToEvaluate)
                val eval = alphaBetaPruningMinimax(boardToEvaluate, false, getCurrentDepth(boardToEvaluate))
                move.revert(boardToEvaluate)
                eval
            }
        } else {
            successorFunction(boardToEvaluate).minBy { move ->
                move.execute(boardToEvaluate)
                val eval = alphaBetaPruningMinimax(boardToEvaluate, true, getCurrentDepth(boardToEvaluate))
                move.revert(boardToEvaluate)
                eval
            }
        }
        moveFound = true
        return bestMove
    }

    private fun alphaBetaPruningMinimax(
        board: Board,
        maximizingPlayer: Boolean,
        depth: Int,
        initialAlpha: Float = Float.NEGATIVE_INFINITY,
        initialBeta: Float = Float.POSITIVE_INFINITY
    ): Float {
        nodeCounter++
        if (terminalTest(board, depth)) {
            return utility(board)
        }
        var alpha = initialAlpha
        var beta = initialBeta

        return if (maximizingPlayer) {
            var value = Float.NEGATIVE_INFINITY
            successorFunction(board).forEach { move ->
                move.execute(board)
                value = max(value, alphaBetaPruningMinimax(board, false, depth - 1, alpha, beta))
                move.revert(board)
                alpha = max(alpha, value)
                if (value >= beta) return@alphaBetaPruningMinimax value
            }
            value
        } else {
            var value = Float.POSITIVE_INFINITY
            successorFunction(board).forEach { move ->
                move.execute(board)
                value = min(value, alphaBetaPruningMinimax(board, true, depth - 1, alpha, beta))
                move.revert(board)
                beta = min(beta, value)
                if (value < alpha) return@alphaBetaPruningMinimax value
            }
            value
        }
    }

    fun utility(board: Board): Float {
        var score = parameters.pieceValueWeight * board.getAllPieces().sumOf { piece ->
            val yPos = if (piece.color == PieceColor.White) 7 - piece.position.y else piece.position.y
            val xPos = piece.position.x

            val pieceSquareTable = piece.type.getPieceSquareTable(board.isEndgame())
            val pieceTypeValue = if (piece.color == PieceColor.White) piece.type.value else piece.type.value.unaryMinus()

            pieceTypeValue + pieceSquareTable[yPos][xPos]
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

    private fun successorFunction(board: Board): List<Move> {
        return board.getAllPieces(board.turn).sortedByDescending {
            // sort pieces by value to improve alpha beta pruning
            it.type.value
        }.flatMap { piece ->
            piece.getValidMoves().sortedByDescending { move ->
                // sort moves with captures first to improve alpha beta pruning
                var sortScore = 0
                if (move.moveType == MoveType.Promotion) sortScore+=3
                if (move.isCapturingPiece(board)) sortScore+=2
                if (move.moveType.isCastling()) sortScore+=1

                sortScore
            }
        }
    }
}