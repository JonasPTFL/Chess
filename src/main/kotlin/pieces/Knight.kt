package pieces

import board.Board
import board.Move
import board.MoveDirection
import board.Position

class Knight(
    override val color: PieceColor,
    override var position: Position,
    override var board: Board
) : Piece(PieceType.Knight, color, position, board) {
    override fun getValidMoves(board: Board): List<Move> {
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpUpRight, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpRightRight, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownRightRight, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownDownRight, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownDownLeft, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.DownLeftLeft, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpLeftLeft, type.defaultMoveCount))
        validMoves.addAll(getValidMovesInDirection(MoveDirection.UpUpLeft, type.defaultMoveCount))
        return validMoves
    }
}
