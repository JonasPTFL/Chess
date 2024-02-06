package pieces

import board.*
import kotlin.math.abs

class Pawn(
    override val color: PieceColor,
    initialPosition: Position,
    override var board: Board
) : Piece(PieceType.Pawn, color, initialPosition, board) {
    override fun getPossibleMoves(): List<Move> {
        val validMoves = mutableListOf<Move>()
        validMoves.addValidMovesInDirection(MoveDirection.Up, MoveDirection.Down, MoveType.NoHit)
        validMoves.addValidMovesInDirection(MoveDirection.UpRight, MoveDirection.DownRight, MoveType.OnlyHit)
        validMoves.addValidMovesInDirection(MoveDirection.UpLeft, MoveDirection.DownLeft, MoveType.OnlyHit)

        // check for en passant
        val lastMove = board.history.getLastMove()
        lastMove?.piece?.let { lastMovePiece ->
            if (lastMovePiece.type == PieceType.Pawn && lastMovePiece.isOppositeColor(this)) {
                val lastMoveFrom = lastMove.from
                val lastMoveTo = lastMove.to
                val pawnMoveDirection = if (color == PieceColor.White) MoveDirection.Up else MoveDirection.Down
                // check if last move was a double move and if it was next to this pawn
                if (lastMoveFrom.distanceTo(lastMoveTo) == 2
                    && lastMoveFrom.x == lastMoveTo.x
                    && lastMoveTo.y == position.y
                    && abs(lastMoveTo.x - position.x) == 1
                    ) {
                    validMoves.add(
                        Move(
                            this,
                            position,
                            Position(lastMoveTo.x, position.y + pawnMoveDirection.yDirection),
                            MoveType.EnPassant
                        )
                    )
                }
            }
        }

        if (!hasMoved) {
            validMoves.addValidMovesInDirection(
                MoveDirection.Up,
                MoveDirection.Down,
                MoveType.NoHit,
                type.defaultMoveCount + 1
            )
        }

        // check for promotion and map to promotion move
        return validMoves.map {
            if (it.to.y == 0 || it.to.y == 7) {
                Move(it.piece, it.from, it.to, MoveType.Promotion)
            } else {
                it
            }
        }
    }

    override fun getThreatenedPositions(): List<Position> {
        val threatenedMoves = mutableListOf<Move>()
        threatenedMoves.addValidMovesInDirection(MoveDirection.UpRight, MoveDirection.DownRight, MoveType.ThreatensKing)
        threatenedMoves.addValidMovesInDirection(MoveDirection.UpLeft, MoveDirection.DownLeft, MoveType.ThreatensKing)
        return threatenedMoves.map { it.to }
    }

    private fun MutableList<Move>.addValidMovesInDirection(
        directionWhite: MoveDirection,
        directionBlack: MoveDirection,
        moveType: MoveType,
        moveCount: Int = type.defaultMoveCount
    ) {
        addAll(
            getValidMovesInDirection(
                if (color == PieceColor.White) directionWhite else directionBlack,
                moveCount,
                moveType
            )
        )
    }
}
