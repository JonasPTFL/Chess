package pieces

import board.Board
import board.Move
import board.MoveDirection
import board.Position


/**
 *
 * @author Jonas Pollpeter
 */

class Queen(
    override val color: PieceColor,
    override var position: Position,
    override var board: Board
) : Piece(PieceType.Queen, color, position, board) {
    override fun getValidMoves(board: Board): List<Move> {
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(getValidMovesInDirection(MoveDirection.Up, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpRight, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.Right, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownRight, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.Down, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownLeft, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.Left, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpLeft, type.defaultMoveCount))
        return validMoves
    }
}