package board

import pieces.Piece
import pieces.PieceColor
import pieces.PieceFactory
import pieces.PieceType

/**
 *
 * @author Jonas Pollpeter
 */

class Board(private var onPromotionPieceRequired: ((xPosition: Int) -> PieceType?)? = null) {
    private val defaultPromotionPiece = PieceType.Queen

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

    val board: Array<Array<Piece?>> = Array(8) { Array(8) { null } }
    private val moves = mutableListOf<Move>()

    /**
     * Returns the piece at a given position.
     */
    fun getPiece(position: Position): Piece? {
        return board[position.x][position.y]
    }

    /**
     * Sets a piece at a given position.
     */
    fun setPiece(position: Position, piece: Piece?) {
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

    fun switchPositions(from: Position, to: Position) {
        val pieceFrom = getPiece(from)
        val pieceTo = getPiece(to)
        setPiece(from, pieceTo)
        setPiece(to, pieceFrom)
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

    fun isCheckMate(): Boolean {
        return isCheckMate(turn) || isCheckMate(turn.opposite())
    }

    fun isCheckMate(color: PieceColor): Boolean {
        return isCheck(color) && getAllPieces(color).all { it.getValidMoves().isEmpty() }
    }

    fun isStaleMate(): Boolean {
        return isStaleMate(turn) || isStaleMate(turn.opposite())
    }

    fun isStaleMate(color: PieceColor): Boolean {
        return !isCheck(color) && getAllPieces(color).all { it.getValidMoves().isEmpty() }
    }

    fun isDraw(): Boolean {
        return isStaleMate() || isInSufficientMaterial() || isThreefoldRepetition() || isFiftyMoveRule()
    }

    fun isThreefoldRepetition(): Boolean {
        return false
        // TODO implement threefold repetition correctly, this method causes bugs in detecting threefold repetition
        // return moves.groupBy { it }.any { it.value.size >= 3 }
    }

    fun isFiftyMoveRule(): Boolean {
        return moves.size >= 100 && moves.takeLast(100).all { !it.hasCapturedPiece() && it.piece.type != PieceType.Pawn }
    }

    fun isInSufficientMaterial(): Boolean {
        val whitePiecesWithoutKing = getAllPieces(PieceColor.White).filter { it.type != PieceType.King }
        val blackPiecesWithoutKing = getAllPieces(PieceColor.Black).filter { it.type != PieceType.King }
        return when {
            // only kings left
            whitePiecesWithoutKing.isEmpty() && blackPiecesWithoutKing.isEmpty() -> true
            // white: no material, black: bishop or knight
            whitePiecesWithoutKing.isEmpty() && blackPiecesWithoutKing.size == 1 -> {
                blackPiecesWithoutKing[0].type == PieceType.Knight || blackPiecesWithoutKing[0].type == PieceType.Bishop
            }
            // black: no material, white: bishop or knight
            blackPiecesWithoutKing.isEmpty() && whitePiecesWithoutKing.size == 1 -> {
                whitePiecesWithoutKing[0].type == PieceType.Knight || whitePiecesWithoutKing[0].type == PieceType.Bishop
            }
            // only bishops of same color left
            whitePiecesWithoutKing.size == 1 && blackPiecesWithoutKing.size == 1 -> {
                whitePiecesWithoutKing[0].type == PieceType.Bishop && blackPiecesWithoutKing[0].type == PieceType.Bishop
                        && whitePiecesWithoutKing[0].position.getColor() == blackPiecesWithoutKing[0].position.getColor()
            }

            else -> false
        }
    }

    fun getMoves(): List<Move> {
        return moves
    }

    fun getLastMove(): Move? {
        return moves.lastOrNull()
    }

    fun addMove(move: Move) {
        moves.add(move)
    }

    fun removeLastMove() {
        moves.removeLast()
    }

    fun getPromotionPiece(position: Position): Piece {
        val requestedPromotionPiece = onPromotionPieceRequired?.invoke(position.x)
            ?.takeIf { PieceType.isValidPromotionPiece(it) }
            ?: defaultPromotionPiece
        return PieceFactory.createPiece(requestedPromotionPiece, turn, position, this)
    }

    fun setOnPromotionRequest(onPromotionPieceRequired: ((xPosition: Int) -> PieceType?)?) {
        this.onPromotionPieceRequired = onPromotionPieceRequired
    }

    fun undoLastMove() {
        val lastMove = getLastMove() ?: return
        lastMove.revert(this)
    }

    fun copy(): Board {
        val newBoard = Board()
        newBoard.turn = turn
        newBoard.moves.addAll(moves)
        for (x in 0..7) {
            for (y in 0..7) {
                val piece = getPiece(Position(x, y))
                if (piece != null) {
                    val newPiece = PieceFactory.createPiece(piece.type, piece.color, piece.position, newBoard)
                    newPiece.hasMoved = piece.hasMoved
                    newBoard.setPiece(Position(x, y), newPiece)
                }
            }
        }
        return newBoard
    }

    fun printToConsole() {
        val blackFontColor = "\u001B[30m"
        val resetFontColor = "\u001b[0m" // to reset color to the default
        println("  0 1 2 3 4 5 6 7")
        for (y in 7 downTo 0) {
            print("$y ")
            for (x in 0..7) {
                val piece = getPiece(Position(x, y))
                if (piece?.color == PieceColor.Black) print(blackFontColor)
                print("${piece?.type?.getCharacterIdentifier() ?: " "} ")
                if (piece?.color == PieceColor.Black) print(resetFontColor)
            }
            println()
        }
    }
}