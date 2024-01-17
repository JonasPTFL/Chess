package board

import pieces.Piece

data class Move(val piece: Piece, val from: Position, val to: Position) {
    fun execute(board: Board) {
        board.movePiece(from, to)
        piece.move(to)
        board.changeTurn()
    }
}
