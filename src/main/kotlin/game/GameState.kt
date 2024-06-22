package game


/**
 *
 * @author Jonas Pollpeter
 */

enum class GameState {
    Initial,
    Running,
    Checkmate,
    Stalemate,
    FiftyMoveRuleDraw,
    ThreefoldRepetitionDraw,
    InSufficientMaterialDraw
}