package board

import game.Game
import pieces.Piece
import pieces.PieceColor

data class Move(val piece: Piece, val from: Position, val to: Position, val moveType: MoveType = MoveType.Normal) {
    private var capturedPiece: Piece? = null
    private var pieceHasMoved = piece.hasMoved

    fun execute(game: Game) {
        internalExecute(game.board)
        game.onMoveExecuted(this)
    }

    private fun internalExecute(board: Board){
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
        if (moveType == MoveType.Promotion) {
            board.setPiece(to, piece)
        }
        board.movePiece(to, from)
        piece.move(from)
        val capturedPiecePosition = if (moveType == MoveType.EnPassant) getEnpassantCapturePosition() else to
        // revert captured piece, use en passant position if move was en passant
        board.setPiece(capturedPiecePosition, capturedPiece)
        piece.hasMoved = pieceHasMoved
        board.changeTurn()
        board.removeLastMove()
    }

    fun blocksCheck(board: Board, color: PieceColor): Boolean {
        internalExecute(board)
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
        val enPassantPosition = getEnpassantCapturePosition()
        board.movePiece(from, to)
        piece.move(to)
        capturedPiece = board.getPiece(enPassantPosition)
        board.setPiece(enPassantPosition, null)
    }

    private fun getEnpassantCapturePosition(): Position {
        return Position(to.x, from.y)
    }
}
