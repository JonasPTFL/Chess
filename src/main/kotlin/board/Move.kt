package board

import pieces.Piece
import pieces.PieceColor

data class Move(val piece: Piece, val from: Position, val to: Position, val moveType: MoveType = MoveType.Normal) {
    private var capturedPiece: Piece? = null
    private var pieceHasMoved = piece.hasMoved

    fun execute(board: Board) {
        capturedPiece = board.getPiece(to)
        pieceHasMoved = piece.hasMoved
        when {
            moveType.isCastling() -> applyCastling(board)
            moveType == MoveType.EnPassant -> applyEnPassant(board)
            moveType == MoveType.Promotion -> {
                board.movePiece(from, to)
                piece.move(to)
                val promotionPiece = board.getPromotionPiece(to)
                promotionPiece.move(to)
                board.setPiece(to, promotionPiece)
            }
            else -> {
                board.movePiece(from, to)
                piece.move(to)
            }
        }
        board.changeTurn()
        board.addMove(this)
    }

    fun revert(board: Board) {
        board.movePiece(to, from)
        piece.move(from)
        board.setPiece(to, capturedPiece)
        piece.hasMoved = pieceHasMoved
        board.changeTurn()
        board.removeLastMove()
    }

    fun blocksCheck(board: Board, color: PieceColor): Boolean {
        execute(board)
        val blocksCheck = !board.isCheck(color)
        revert(board)
        return blocksCheck
    }

    private fun applyCastling(board: Board) {
        val rookPosition = if (moveType == MoveType.CastlingQueenSide) Position(3, to.y) else Position(5, to.y)
        val kingPosition = if (moveType == MoveType.CastlingKingSide) Position(6, to.y) else Position(2, to.y)
        board.movePiece(from, kingPosition)
        board.movePiece(to, rookPosition)
        piece.move(kingPosition)
        board.getPiece(rookPosition)?.move(rookPosition)
    }

    private fun applyEnPassant(board: Board) {
        val enPassantPosition = Position(to.x, from.y)
        board.movePiece(from, to)
        piece.move(to)
        board.setPiece(enPassantPosition, null)
    }
}
