package pieces

/**
 *
 * @author Jonas Pollpeter
 */

enum class PieceColor {
    White, Black;

    fun opposite(): PieceColor {
        return when (this) {
            White -> Black
            Black -> White
        }
    }

    fun isOpposite(color: PieceColor): Boolean {
        return this == color.opposite()
    }
}