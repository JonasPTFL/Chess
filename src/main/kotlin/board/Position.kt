package board


/**
 *
 * @author Jonas Pollpeter
 */

data class Position(val x: Int, val y: Int) {
    fun isOnBoard(): Boolean {
        return x in 0..7 && y in 0..7
    }

    fun plus(direction: MoveDirection): Position {
        return Position(x + direction.xDirection, y + direction.yDirection)
    }
}
