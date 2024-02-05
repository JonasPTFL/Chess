package engine


/**
 *
 * @author Jonas Pollpeter
 */
object PieceSquareTables {
    val pawnTable = arrayOf(
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf( 5,  5,  5,  5,  5,  5,  5,  5),
        intArrayOf( 1,  1,  2,  3,  3,  2,  1,  1),
        intArrayOf( 0,  0,  1,  2,  2,  1,  0,  0),
        intArrayOf( 0,  0,  0,  2,  2,  0,  0,  0),
        intArrayOf( 0,  0, -1,  0,  0, -1,  0,  0),
        intArrayOf( 0,  1,  1, -2, -2,  1,  1,  0),
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0)
    )

    val knightTable = arrayOf(
        intArrayOf(-5, -4, -3, -3, -3, -3, -4, -5),
        intArrayOf(-4, -2,  0,  0,  0,  0, -2, -4),
        intArrayOf(-3,  0,  1,  2,  2,  1,  0, -3),
        intArrayOf(-3,  1,  2,  2,  2,  2,  1, -3),
        intArrayOf(-3,  0,  2,  2,  2,  2,  0, -3),
        intArrayOf(-3,  1,  1,  2,  2,  1,  1, -3),
        intArrayOf(-4, -2,  0,  1,  1,  0, -2, -4),
        intArrayOf(-5, -4, -3, -3, -3, -3, -4, -5)
    )

    val bishopTable = arrayOf(
        intArrayOf(-2, -1, -1, -1, -1, -1, -1, -2),
        intArrayOf(-1,  0,  0,  0,  0,  0,  0, -1),
        intArrayOf(-1,  0,  0,  1,  1,  0,  0, -1),
        intArrayOf(-1,  1,  1,  1,  1,  1,  1, -1),
        intArrayOf(-1,  0,  1,  1,  1,  1,  0, -1),
        intArrayOf(-1,  1,  1,  1,  1,  1,  1, -1),
        intArrayOf(-1,  1,  0,  0,  0,  0,  1, -1),
        intArrayOf(-2, -1, -1, -1, -1, -1, -1, -2)
    )

    val rookTable = arrayOf(
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf( 5, 10, 10, 10, 10, 10, 10,  5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf( 0,  0,  0,  5,  5,  0,  0,  0)
    )

    val queenTable = arrayOf(
        intArrayOf(-2, -1, -1, -1, -1, -1, -1, -2),
        intArrayOf(-1,  0,  0,  0,  0,  0,  0, -1),
        intArrayOf(-1,  0,  1,  1,  1,  1,  0, -1),
        intArrayOf(-1,  0,  1,  1,  1,  1,  0, -1),
        intArrayOf( 0,  0,  1,  1,  1,  1,  0, -1),
        intArrayOf(-1,  1,  1,  1,  1,  1,  0, -1),
        intArrayOf(-1,  0,  1,  0,  0,  0,  0, -1),
        intArrayOf(-2, -1, -1, -1, -1, -1, -1, -2)
    )

    val kingTable = arrayOf(
        intArrayOf(-3, -4, -4, -5, -5, -4, -4, -3),
        intArrayOf(-3, -4, -4, -5, -5, -4, -4, -3),
        intArrayOf(-3, -4, -4, -5, -5, -4, -4, -3),
        intArrayOf(-3, -4, -4, -5, -5, -4, -4, -3),
        intArrayOf(-2, -3, -3, -4, -4, -3, -3, -2),
        intArrayOf(-1, -2, -2, -2, -2, -2, -2, -1),
        intArrayOf( 2,  2,  0,  0,  2,  2,  2,  2),
        intArrayOf( 2,  3,  1,  0,  0,  1,  3,  2)
    )

    val kingTableEndgame = arrayOf(
        intArrayOf(-5, -4, -3, -2, -2, -3, -4, -5),
        intArrayOf(-3, -2, -1,  0,  0, -1, -2, -3),
        intArrayOf(-3, -1,  2,  3,  3,  2, -1, -3),
        intArrayOf(-3, -1,  3,  4,  4,  3, -1, -3),
        intArrayOf(-3, -1,  3,  4,  4,  3, -1, -3),
        intArrayOf(-3, -1,  2,  3,  3,  2, -1, -3),
        intArrayOf(-3, -3,  0,  0,  0,  0, -3, -3),
        intArrayOf(-5, -3, -3, -3, -3, -3, -3, -5)
    )
}