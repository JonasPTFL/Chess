package board

import pieces.Piece
import pieces.PieceColor
import pieces.PieceFactory
import pieces.PieceType

/**
 *
 * @author Jonas Pollpeter
 */

class Board(val board: Array<Array<Piece?>> = Array(8) { Array(8) { null } }) {

    private val edgePieces = listOf(
        PieceType.Rook,
        PieceType.Knight,
        PieceType.Bishop,
        PieceType.Queen,
        PieceType.King,
        PieceType.Bishop,
        PieceType.Knight,
        PieceType.Rook
    )

    var turn = PieceColor.White
        private set

    /**
     * Returns the piece at a given position.
     */
    fun getPiece(position: Position): Piece? {
        return board[position.x][position.y]
    }

    /**
     * Sets a piece at a given position.
     */
    public fun setPiece(position: Position, piece: Piece?) {
        board[position.x][position.y] = piece
    }

    /**
     * Moves a piece from one position to another.
     */
    fun movePiece(from: Position, to: Position) {
        val piece = getPiece(from)
        setPiece(from, null)
        setPiece(to, piece)
    }

    fun isOccupied(position: Position) =
        isOccupiedByColor(position, PieceColor.White) || isOccupiedByColor(position, PieceColor.Black)

    fun isOccupiedByColor(position: Position, color: PieceColor) = getPiece(position)?.color == color

    /**
     * Initializes the board with the standard chess setup.
     */
    public fun initializeBoard() {
        // edge pieces
        for (i in 0..7) {
            val positionWhite = Position(i, 0)
            val positionBlack = Position(i, 7)
            setPiece(positionWhite, PieceFactory.createPiece(edgePieces[i], PieceColor.White, positionWhite, this))
            setPiece(positionBlack, PieceFactory.createPiece(edgePieces[i], PieceColor.Black, positionBlack, this))
        }

        // pawns
        for (i in 0..7) {
            val positionWhite = Position(i, 1)
            val positionBlack = Position(i, 6)
            setPiece(positionWhite, PieceFactory.createPiece(PieceType.Pawn, PieceColor.White, positionWhite, this))
            setPiece(positionBlack, PieceFactory.createPiece(PieceType.Pawn, PieceColor.Black, positionBlack, this))
        }
    }

    fun changeTurn() {
        turn = if (turn == PieceColor.White) PieceColor.Black else PieceColor.White
    }

    fun doRandomValidMove() {
        val pieces = getAllPieces(turn)
        val validMoves = pieces.flatMap { it.getValidMoves() }
        val randomMove = validMoves.random()
        randomMove.execute(this)
    }

    fun getAllPieces(): List<Piece> {
        return board.flatten().filterNotNull()
    }

    fun getAllPieces(color: PieceColor): List<Piece> {
        return getAllPieces().filter { it.color == color }
    }

    fun isValidMove(move: Move): Boolean {
        return move.piece.getValidMoves().any { it.to == move.to }
    }

    fun isCheck(color: PieceColor): Boolean {
        return getAllPieces(color).any { it.type == PieceType.King && it.position.isThreatened(color.opposite(), this) }
    }

    fun isCheckMate(color: PieceColor): Boolean {
        return isCheck(color) && getAllPieces(color).any { it.type == PieceType.King && it.getValidMoves().isEmpty() }
    }

    fun isStaleMate(color: PieceColor): Boolean {
        return !isCheck(color) && getAllPieces(color).all { it.getValidMoves().isEmpty() }
    }
}