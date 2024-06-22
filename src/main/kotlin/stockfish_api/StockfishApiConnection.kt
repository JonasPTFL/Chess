package stockfish_api

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*


/**
 *
 * @author Jonas Pollpeter
 */

data class ApiResponse(val success: Boolean, val data: String)

class StockfishApiConnection {
    private val client = HttpClient(CIO)
    private val gson = Gson()

    object ApiConstants {
        const val url = "https://stockfish.online/api/stockfish.php"
        const val fen = "fen"
        const val depth = "depth"
        const val mode = "mode"
        const val bestMove = "bestmove"
    }

    suspend fun getBestMoveHTTPResponse(fen: String, depth: Int): ApiResponse {
        val result = client.get(ApiConstants.url) {
            parameter(ApiConstants.fen, fen)
            parameter(ApiConstants.depth, depth)
            parameter(ApiConstants.mode, ApiConstants.bestMove)
        }.body<String>()
        return gson.fromJson(result, ApiResponse::class.java)
    }
}