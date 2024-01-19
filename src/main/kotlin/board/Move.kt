package board

import pieces.Piece
import pieces.PieceColor

data class Move(val piece: Piece, val from: Position, val to: Position, val moveType: MoveType = MoveType.Normal) {
    private var capturedPiece: Piece? = null
    private var pieceHasMoved = piece.hasMoved

    fun execute(board: Board) {
        capturedPiece = board.getPiece(to)
        pieceHasMoved = piece.hasMoved
        board.movePiece(from, to)
        piece.move(to)
        board.changeTurn()
    }

    fun revert(board: Board) {
        board.movePiece(to, from)
        piece.move(from)
        board.setPiece(to, capturedPiece)
        piece.hasMoved = pieceHasMoved
        board.changeTurn()
    }

    fun blocksCheck(board: Board, color: PieceColor): Boolean {
        execute(board)
        val blocksCheck = !board.isCheck(color)
        revert(board)
        return blocksCheck
    }
}
