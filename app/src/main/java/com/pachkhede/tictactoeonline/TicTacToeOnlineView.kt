package com.pachkhede.tictactoeonline

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt

class TicTacToeOnlineView(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {

    var cellTouched: ((Int) -> Unit)? = null

    val O = 0
    val X = 1
    val Empty = -1

    var turn = "X"
    var mplayer = "X"

    private val path = Path()
    private val paint = Paint().apply {
        strokeWidth = 8f
        color = Color.BLACK
        style = Paint.Style.STROKE

    }

    private val paintX = Paint().apply {
        strokeWidth = 20f
        color = "#E91E63".toColorInt()
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val paintO = Paint().apply {
        strokeWidth = 20f
        color = "#00BCD4".toColorInt()
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val borderPaint = Paint(paint).apply { strokeWidth = 20f }
    private val padding = 15;
    private var rw = 0f

    private val points = mutableListOf<Pair<PointF, PointF>>()
    var board = IntArray(9) { Empty }

    var isWon = false
    var winner = ""
    var startWin = -1
    var endWin = -1
    var isDraw = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event?.action != MotionEvent.ACTION_DOWN) {
            return true
        }

        val x = event.x
        val y = event.y

        points.forEachIndexed { i, point ->
            if (x in point.first.x..point.second.x && y in point.first.y..point.second.y) {

                if (board[i] == Empty && turn == mplayer) {
                    if (!isWon && !isDraw) {
                        turn = if (turn == "X") "O" else "X"
                        cellTouched?.invoke(i)
                    }
                }

            }
        }
        return true
    }


    fun setAt(idx: Int, isX: Boolean): Boolean {
        if (board[idx] == Empty) {
            board[idx] = if (isX) X else O
            invalidate()
            return true
        }



        return false


    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rw = (w - (padding * 4)) / 3f
        points.clear()

        for (i in 0..2) {

            for (j in 0..2) {
                val x1 = (padding + (padding + rw) * j)
                val y1 = (padding + (padding + rw) * i)
                val x2 = x1 + rw
                val y2 = y1 + rw
                points
                    .add(Pair(PointF(x1, y1), PointF(x2, y2)))


            }
        }

    }


    private fun drawBoard(canvas: Canvas) {


        for (i in board.indices) {
            path.reset()

            if (board[i] == Empty) {
                continue
            } else if (board[i] == X) {
                val point = points[i]
                path.moveTo(point.first.x, point.first.y)
                canvas.drawLine(
                    point.first.x + padding * 2,
                    point.first.y + padding * 2,
                    point.second.x - padding * 2,
                    point.second.y - padding * 2,
                    paintX
                )

                path.moveTo(point.second.x, point.first.y)
                canvas.drawLine(
                    point.second.x - padding * 2,
                    point.first.y + padding * 2,
                    point.first.x + padding * 2,
                    point.second.y - padding * 2,
                    paintX
                )


            } else if (board[i] == O) {
                val point = points[i]

                canvas.drawCircle(
                    (point.first.x + point.second.x) / 2,
                    (point.first.y + point.second.y) / 2,
                    ((point.second.x - point.first.x) - padding * 4) / 2,
                    paintO
                )
            }
        }


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0..2) {

            for (j in 0..2) {

                val point = points[i * 3 + j]
                drawRectangleRoundCorners(
                    point.first.x,
                    point.first.y,
                    point.second.x,
                    point.second.y
                )

                canvas.drawPath(path, paint)

            }
        }

        drawBoard(canvas)

        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            borderPaint
        )

        if (isWon) {
            drawWinLine(startWin, endWin, if(winner == "X") paintX else paintO, canvas)
        }


    }


    private fun drawWinLine(from: Int, to: Int, paint: Paint, canvas: Canvas) {
        val startPoint = points[from]
        val endPoint = points[to]
        val scx = (startPoint.first.x + startPoint.second.x) / 2
        val scy = (startPoint.first.y + startPoint.second.y) / 2
        val ecx = (endPoint.first.x + endPoint.second.x) / 2
        val ecy = (endPoint.first.y + endPoint.second.y) / 2
        canvas.drawLine(scx, scy, ecx, ecy, paint)
    }


    private fun drawRectangleRoundCorners(x1: Float, y1: Float, x2: Float, y2: Float) {
        path.reset()
        val r = 20f

        val rx = if (x2 < x1) -r else r
        val ry = if (y2 < y1) -r else r

        path.moveTo(x1 + rx, y1)

        path.lineTo(x2 - rx, y1)
        path.quadTo(x2, y1, x2, y1 + ry)

        path.lineTo(x2, y2 - ry)
        path.quadTo(x2, y2, x2 - rx, y2)

        path.lineTo(x1 + rx, y2)
        path.quadTo(x1, y2, x1, y2 - ry)

        path.lineTo(x1, y1 + ry)
        path.quadTo(x1, y1, x1 + rx, y1)

        path.close()
    }


    fun reset() {
        board = IntArray(9) { Empty }
        winner = ""
        isWon = false
        isDraw = false
        startWin = -1
        endWin = -1
        turn = "X"
        invalidate()
    }

    private fun getScreenWidth() : Int{
        val displayMetrics = DisplayMetrics();
        (context as Activity).windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        val width = displayMetrics.widthPixels;
        return width;
    }

}




