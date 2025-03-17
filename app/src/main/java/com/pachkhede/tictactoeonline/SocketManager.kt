package com.pachkhede.tictactoeonline

import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.Locale

object SocketManager {

    private var socket: Socket? = null
    private var isConnected = false
    private var connectionCallback: ((Boolean) -> Unit)? = null

    init {
        try {
            socket = IO.socket("https://tictactoeonline-production.up.railway.app/")
            setupListener()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun setupListener() {
        socket?.on(Socket.EVENT_CONNECT) {
            isConnected = true
            connectionCallback?.invoke(true)
        }
        socket?.on(Socket.EVENT_CONNECT_ERROR) {
            isConnected = false
            connectionCallback?.invoke(false)

        }

        socket?.on(Socket.EVENT_DISCONNECT) {
            isConnected = false
        }
    }

    fun connect(callback: (Boolean) -> Unit) {
        connectionCallback = callback
        socket?.connect()
    }
    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }


    fun disconnect() {
        if (isConnected()) {
            socket?.disconnect()
        }
    }



    fun createUser(name: String, image: String) {
        val json = JSONObject()
        json.put("name", name)
        json.put("image", image)
        socket?.emit("create_user", json)
    }


    fun createRoom() {
        socket?.emit("create_room")
    }

    fun setRoomCreatedListener(onRoomCreated: (String) -> Unit) {
        socket?.on("room_created") { args ->
            val data = args[0] as JSONObject
            val id = data.getString("id")
            onRoomCreated(id)
        }
    }


    fun joinRoom(id: String) {
        val data = JSONObject()
        data.put("id", id)
        socket?.emit("join_room", data)
    }


    fun setRoomJoinedListener(onRoomJoined: (String, String) -> Unit) {
        socket?.on("user_joined") { args ->
            val data = args[0] as JSONObject
            val name = data.getString("name")
            val img = data.getString("image")
            onRoomJoined(name, img)
        }
    }


    fun setErrorListener(onError: (String, String) -> Unit) {
        socket?.on("error") { args ->
            val data = args[0] as JSONObject
            val error = data.getString("error")
            val event = data.getString("event")
            onError(error, event)

        }
    }

    fun setGameStartListener(onGameStart: (JSONObject) -> Unit) {
        socket?.on("start_game") { args ->
            val data = args[0] as JSONObject
            onGameStart(data);
        }
    }


    fun getSocketId(): String = socket?.id() ?: ""

    fun playMove(roomId: String, i: Int, player: String) {
        val json = JSONObject()
        json.put("id", roomId)
        json.put("index", i)
        json.put("player", player.lowercase())

        socket?.emit("play_move", json)

    }

    fun setMovePlayedListener(onMovePlayed: (Int, String) -> Unit) {
        socket?.on("move_played") { args ->
            val data = args[0] as JSONObject

            val index = data.getString("index").toInt()
            val player = data.getString("player").toString().uppercase()

            onMovePlayed(index, player)


        }
    }


    fun setWinListener(onWin: (String, Int, Int) -> Unit) {
        socket?.on("win") { args ->
            val data = args[0] as JSONObject
            val player = data.getString("winner").uppercase()
            val start = data.getInt("start")
            val end = data.getInt("end")

            onWin(player, start, end)

        }
    }

    fun reset(id: String) {
        socket?.emit("reset", id)
    }

    fun setResetListener(onReset: () -> Unit) {
        socket?.on("reset") {
            onReset()
        }
    }


    fun setGameDrawListener(onGameDraw: () -> Unit) {
        socket?.on("draw") {
            onGameDraw()
        }
    }


    fun setUserDisconnectListener(onDisconnect: (JSONObject) -> Unit) {
        socket?.on("user_disconnect") { args ->
            val data = args[0] as JSONObject
            onDisconnect(data)

        }
    }


}