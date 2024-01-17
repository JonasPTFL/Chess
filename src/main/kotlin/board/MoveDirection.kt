package board


/**
 *
 * @author Jonas Pollpeter
 */

enum class MoveDirection(val xDirection: Int, val yDirection: Int) {
    Up(0,1),
    UpRight(1,1),
    Right(1,0),
    DownRight(1,-1),
    Down(0,-1),
    DownLeft(-1,-1),
    Left(-1,0),
    UpLeft(-1,1),
    UpUpRight(1,2),
    UpRightRight(2,1),
    DownRightRight(2,-1),
    DownDownRight(1,-2),
    DownDownLeft(-1,-2),
    DownLeftLeft(-2,-1),
    UpLeftLeft(-2,1),
    UpUpLeft(-1,2);

    companion object {
        fun getAllNormalDirections(): Set<MoveDirection> {
            return setOf(Up, UpRight, Right, DownRight, Down, DownLeft, Left, UpLeft)
        }
    }
}