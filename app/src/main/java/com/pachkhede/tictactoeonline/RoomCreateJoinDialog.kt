package com.pachkhede.tictactoeonline

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView


class RoomCreateJoinDialog : DialogFragment() {

    private var view: View? = null
    private var roomId = ""
    private var joinRoomId = ""

    private var isCreateLayoutOpen = true
    private var startGameData: JSONObject? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            view = inflater.inflate(R.layout.create_join_room_dialog, null)


            val sharedPref = requireActivity().getSharedPreferences(
                getString(R.string.shared_pref_main),
                Context.MODE_PRIVATE
            )
            val name = sharedPref.getString(getString(R.string.profileName), "Player1")!!
            val img = sharedPref.getString(getString(R.string.profileImg), "a")!!

            setPlayer1Info(name, img)
            setLoadingIconOnPlayer2()


            SocketManager.connect { success ->

                if (success) {


                    SocketManager.createUser(name, img)


                    SocketManager.setRoomCreatedListener { id ->
                        activity?.runOnUiThread {
                            roomId = id
                            view?.findViewById<TextView>(R.id.roomCode)?.text = ("Room Id: " + id)

                        }
                    }

                    SocketManager.createRoom()

                    SocketManager.setRoomJoinedListener { name, img ->
                        activity?.runOnUiThread {
                            if (isCreateLayoutOpen) {
                                setPlayer2Info(name, img)
                            }

                            object : CountDownTimer(3000, 1000) {
                                override fun onTick(p0: Long) {
                                    activity?.runOnUiThread {
                                        view?.findViewById<Button>(R.id.buttonOk)?.text =
                                            "Starts in ${p0 / 1000}"
                                    }
                                }

                                override fun onFinish() {
                                    activity?.runOnUiThread {
                                        view?.findViewById<Button>(R.id.buttonOk)?.text = "Done"
                                    }
                                    val intent = Intent(activity, GameOnlineActivity::class.java)
                                    intent.putExtra("id", if (joinRoomId == "") roomId else joinRoomId)
                                    intent.putExtra("data", startGameData?.toString())
                                    startActivity(intent)
                                    dialog?.dismiss()

                                }

                            }.start()
                        }
                    }
                    SocketManager.setGameStartListener { data ->
                        startGameData = data

                    }
                    SocketManager.setErrorListener { error, event ->
                        if (event == "join_room") {
                            activity?.runOnUiThread {
                                view?.findViewById<EditText>(R.id.inputRoomIdEditText)?.error =
                                    error
                            }
                        }


                    }

                } else {
                    activity?.runOnUiThread {
                        view?.findViewById<TextView>(R.id.roomCode)?.text = ("Error Connecting")
                    }
                }
            }


            view?.findViewById<Button>(R.id.createLayoutButton)?.setOnClickListener {
                showCreateLayout()
            }


            view?.findViewById<Button>(R.id.joinLayoutButton)?.setOnClickListener {
                showJoinLayout()
            }

            view?.findViewById<Button>(R.id.buttonOk)?.setOnClickListener {
                if (!isCreateLayoutOpen) {
                    val id = view?.findViewById<EditText>(R.id.inputRoomIdEditText)?.text.toString()
                        .trim()

                    if (id == roomId) {
                        view?.findViewById<EditText>(R.id.inputRoomIdEditText)?.error =
                            "cannot join a room created by self"
                    } else {
                        joinRoomId = id
                        SocketManager.joinRoom(id)
                    }
                }
            }


            val cancelButton = view?.findViewById<Button>(R.id.buttonCancel)
            cancelButton?.setOnClickListener {
                dialog?.dismiss()
                SocketManager.disconnect()
            }

            view?.findViewById<ImageView>(R.id.shareButton)?.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)

                val text =
                    "Lets play TicTacToe. Download from https://play.google.com. Start game and go to play online and join room using this code ${roomId}"

                intent.type = "text/plain"

                intent.putExtra(Intent.EXTRA_SUBJECT, "Play TicTacToe")
                intent.putExtra(Intent.EXTRA_TEXT, text)

                startActivity(Intent.createChooser(intent, "Share Using"))
            }


            builder.setView(view)

            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")


    }

    private fun showCreateLayout() {
        isCreateLayoutOpen = true
        view?.findViewById<LinearLayout>(R.id.createLayout)?.visibility = View.VISIBLE
        view?.findViewById<LinearLayout>(R.id.joinLayout)?.visibility = View.GONE
    }

    private fun showJoinLayout() {
        isCreateLayoutOpen = false
        view?.findViewById<LinearLayout>(R.id.createLayout)?.visibility = View.GONE
        view?.findViewById<LinearLayout>(R.id.joinLayout)?.visibility = View.VISIBLE
    }


    private fun setLoadingIconOnPlayer2() {
        view?.findViewById<TextView>(R.id.playerName2)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.playerImage2)?.visibility = View.GONE
        view?.findViewById<GifImageView>(R.id.loadingGif)?.visibility = View.VISIBLE

    }

    private fun setPlayer2Info(name: String, img: String) {
        view?.findViewById<TextView>(R.id.playerName2)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.playerImage2)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.playerName2)?.text = name
        val imgId = resources.getIdentifier(img, "drawable", activity?.packageName)
        view?.findViewById<ImageView>(R.id.playerImage2)?.setImageResource(imgId)
        view?.findViewById<GifImageView>(R.id.loadingGif)?.visibility = View.GONE

    }

    private fun setPlayer1Info(name: String, img: String) {
        view?.findViewById<TextView>(R.id.playerName1)?.text = name
        val imgId = resources.getIdentifier(img, "drawable", activity?.packageName)
        view?.findViewById<ImageView>(R.id.playerImage1)?.setImageResource(imgId)

    }



}