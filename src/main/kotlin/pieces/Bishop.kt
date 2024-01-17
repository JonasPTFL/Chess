package pieces

import board.Board
import board.Move
import board.MoveDirection
import board.Position

class Bishop(
    override val color: PieceColor,
    override var position: Position,
    override var board: Board
) : Piece(PieceType.Bishop, color, position, board) {
    override fun getValidMoves(board: Board): List<Move> {
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpRight, type.defaultMoveCount,))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownRight, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownLeft, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpLeft, type.defaultMoveCount))
        return validMoves
    }
}
