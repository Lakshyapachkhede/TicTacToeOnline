package com.pachkhede.tictactoeonline

import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketManager {

    private var socket: Socket? = null
    private var isConnected = false
    private var connectionCallback : ((Boolean) -> Unit)? = null

    init {
        try {
            socket = IO.socket("http://192.168.99.37:3000/")
            setupListener()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun setupListener(){
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

    fun connect(callback : (Boolean)->Unit) {
        connectionCallback = callback
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
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


}