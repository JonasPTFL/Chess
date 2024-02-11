package board

import pieces.Piece
import pieces.PieceColor
import pieces.PieceType

data class Move(val piece: Piece, val from: Position, val to: Position, val moveType: MoveType = MoveType.Normal) {
    private var capturedPiece: Piece? = null
    private var pieceHasMoved = piece.hasMoved
    private var promotionPiece: Piece? = null
    // TODO improve move structure with inheritance for castling, promotion, etc. moves
    private var castlingRookPosition: Position? = null
    private var castlingKingPosition: Position? = null

    fun execute(board: Board) {
        internalExecute(board)
    }

    private fun internalExecute(board: Board){
        capturedPiece = board.getPiece(to)
        pieceHasMoved = piece.hasMoved
        when {
            moveType.isCastling() -> applyCastling(board)
            moveType == MoveType.EnPassant -> applyEnPassant(board)
            moveType == MoveType.Promotion -> {
                board.movePiece(from, to)
                promotionPiece = board.getPromotionPiece(to)
                promotionPiece!!.move(to)
                board.setPiece(to, promotionPiece)
            }
            else -> {
                board.movePiece(from, to)
            }
        }
        board.changeTurn()
        board.history.add(this)
    }

    fun revert(board: Board) {
        if (moveType == MoveType.Promotion) {
            // TODO promoted piece is not reverted right, on rever, the default queen piece will be set
            board.setPiece(to, piece)
        }
        if (moveType.isCastling()) {
            board.movePiece(castlingKingPosition!!, from)
            board.movePiece(castlingRookPosition!!, to)
        } else {
            board.movePiece(to, from)
            val capturedPiecePosition = if (moveType == MoveType.EnPassant) getEnpassantCapturePosition() else to
            // revert captured piece, use en passant position if move was en passant
            board.setPiece(capturedPiecePosition, capturedPiece)
        }
        piece.hasMoved = pieceHasMoved
        board.changeTurn()
        board.history.removeLastMove()
    }

    fun isCheckSafe(board: Board, color: PieceColor): Boolean {
        internalExecute(board)
        val checkSafe = !board.isCheck(color)
        revert(board)
        return checkSafe
    }

    private fun applyCastling(board: Board) {
        castlingKingPosition = if (moveType == MoveType.CastlingKingSide) Position(6, to.y) else Position(2, to.y)
        castlingRookPosition = if (moveType == MoveType.CastlingQueenSide) Position(3, to.y) else Position(5, to.y)
        board.movePiece(from, castlingKingPosition!!)
        board.movePiece(to, castlingRookPosition!!)
    }

    private fun applyEnPassant(board: Board) {
        val enPassantPosition = getEnpassantCapturePosition()
        board.movePiece(from, to)
        capturedPiece = board.getPiece(enPassantPosition)
        board.setPiece(enPassantPosition, null)
    }

    private fun getEnpassantCapturePosition(): Position {
        return Position(to.x, from.y)
    }

    fun hasCapturedPiece(): Boolean {
        return capturedPiece != null
    }

    override fun toString(): String {
        return "${piece.color} ${piece.type} from ${from.x},${from.y} to ${to.x},${to.y}"
    }

    fun toShortAlgebraicNotation(blackPiecesUppercase: Boolean = false): String {
        val pieceNotation = if (blackPiecesUppercase) piece.type.fenNotationCharacter.uppercase() else piece.type.getFENNotation(piece.color)
        val destinationNotation = to.toAlgebraicNotation()

        val moveTypeNotation = when(moveType) {
            MoveType.CastlingKingSide -> "O-O"
            MoveType.CastlingQueenSide -> "O-O-O"
            MoveType.Promotion -> {
                if (promotionPiece == null) throw IllegalStateException("promotion piece not set")
                destinationNotation + "=" + promotionPiece!!.type.fenNotationCharacter.uppercase()
            }
            else -> destinationNotation
        }
        // TODO check for check and checkmate correctly, in relation to the board state when move was executed
        // board at this point is needed to check for check and checkmate, or?
        val checkNotation = when {
            // isMoveCausingCheckmate() -> "#"
            // isMoveCausingCheck -> "+"
            else -> ""
        }

        // build move notation
        val moveNotation = StringBuilder()
        // no piece notation for pawns
        if (piece.type != PieceType.Pawn) moveNotation.append(pieceNotation)
        // check for disambiguation
        // TODO disambiguation notation
        // add capture notation
        if (hasCapturedPiece()){
            moveNotation.append("x")
        }
        // add destination notation
        moveNotation.append(moveTypeNotation)
        // add check notation
        moveNotation.append(checkNotation)

        return moveNotation.toString()
    }

    fun toAlgebraic(): String {
        return from.toAlgebraicNotation() + to.toAlgebraicNotation()
    }

    fun isCapturingPiece(board: Board): Boolean {
        return board.getPiece(to) != null
    }

    companion object {
        fun fromAlgebraic(moveString: String, board: Board): Move {
            val from = Position.fromAlgebraicNotation(moveString.substring(0, 2))
            val to = Position.fromAlgebraicNotation(moveString.substring(2, 4))
            val piece = board.getPiece(from) ?: throw IllegalArgumentException("no piece at position $from")
            // determine move type based on piece type and move distance
            val moveType = when (piece.type) {
                PieceType.King -> {
                    if (from.distanceTo(to) == 2) {
                        if (to.x == 2) MoveType.CastlingQueenSide else MoveType.CastlingKingSide
                    } else {
                        MoveType.Normal
                    }
                }
                PieceType.Pawn -> {
                    if (to.y == 0 || to.y == 7) {
                        // TODO get desired promotion piece from move string
                        MoveType.Promotion
                    }
                    else if (to.x != from.x) MoveType.EnPassant
                    else MoveType.Normal
                }
                else -> MoveType.Normal
            }
            return Move(piece, from, to, moveType)
        }
    }
}
