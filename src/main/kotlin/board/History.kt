package board


/**
 *
 * @author Jonas Pollpeter
 */

class History(val board: Board) : ArrayList<Move>() {
    private val undoneMoves = ArrayDeque<Move>()
    var currentlyNavigatedBoard: Board = board
        private set

    fun moveCount(): Int = size

    fun halfMoveCount(): Int = size / 2

    fun getLastMove(): Move? = lastOrNull()

    fun getLastMoves(count: Int): List<Move> = takeLast(count)

    fun removeLastMove() {
        removeLast()
    }

    fun navigateForward() {
        val diffToCurrentBoard = board.history.size - currentlyNavigatedBoard.history.size
        if (diffToCurrentBoard > 0 && currentlyNavigatedBoard.history.undoneMoves.isEmpty()) {
            currentlyNavigatedBoard.history.undoneMoves.addAll(board.history.takeLast(diffToCurrentBoard))
        } else if (diffToCurrentBoard == 0) {
            currentlyNavigatedBoard = board
        } else {
            if (currentlyNavigatedBoard == board) {
                currentlyNavigatedBoard = board.copy()
            }
            currentlyNavigatedBoard.redoLastMove()
        }
    }

    fun navigateBackward() {
        if (currentlyNavigatedBoard == board) {
            currentlyNavigatedBoard = board.copy()
        }
        currentlyNavigatedBoard.undoLastMove()
    }

    fun undoLastMove() {
        board.undoLastMove()
    }

    fun redoLastMove() {
        board.redoLastMove()
    }

    private fun Board.undoLastMove() {
        val lastMove = this.history.getLastMove() ?: return
        lastMove.revert(this)
        this.history.undoneMoves.addLast(lastMove)
    }

    private fun Board.redoLastMove() {
        val lastUndoneMove = this.history.undoneMoves.removeLastOrNull() ?: return
        lastUndoneMove.execute(this)
    }

    fun isNavigatedAway(): Boolean {
        return currentlyNavigatedBoard.history.size != board.history.size
    }
}