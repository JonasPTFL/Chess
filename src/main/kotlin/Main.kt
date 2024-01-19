import board.Board
import gui.Visualizer
import javax.swing.SwingUtilities

fun main() {
    println("Chess")

    val board = Board()
    board.initializeBoard()

    SwingUtilities.invokeLater {
        val visualizer = Visualizer(board)
        visualizer.isVisible = true
    }

//    thread {
//        while (true) {
//            board.doRandomValidMove()
//            visualizer?.update()
//            Thread.sleep(1000)
//        }
//    }
}