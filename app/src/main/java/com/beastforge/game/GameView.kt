
package com.beastforge.game

import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import kotlin.math.min

class GameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val density = resources.displayMetrics.density

    private var playerX = 0f
    private var playerY = 0f

    private var initialized = false

    private fun dp(value: Float): Float {
        return value * density
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) return

        if (!initialized) {
            playerX = w * 0.5f
            playerY = h * 0.53f
            initialized = true
        }

        drawBackground(canvas, w, h)

        drawHeader(canvas)

        drawCreature(canvas)

        drawInstructions(canvas)
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.shader = LinearGradient(
            0f, 0f,
            w, h,
            Color.rgb(12, 17, 38),
            Color.rgb(34, 20, 54),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, 0f, w, h, paint)

        paint.shader = null

        paint.color = Color.rgb(26, 32, 60)

        canvas.drawRect(
            0f,
            h * 0.76f,
            w,
            h,
            paint
        )
    }

    private fun drawHeader(canvas: Canvas) {

        paint.color = Color.WHITE
        paint.textSize = dp(27f)
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD

        canvas.drawText(
            "BEASTFORGE",
            width / 2f,
            dp(55f),
            paint
        )

        paint.textSize = dp(12f)
        paint.color = Color.rgb(160, 175, 220)

        canvas.drawText(
            "CREATURE FUSION ARENA",
            width / 2f,
            dp(78f),
            paint
        )
    }

    private fun drawCreature(canvas: Canvas) {

        val size = min(width, height).toFloat()

        // Outer magical glow
        paint.color = Color.argb(45, 80, 130, 255)

        canvas.drawCircle(
            playerX,
            playerY,
            size * 0.18f,
            paint
        )

        // Creature body
        paint.color = Color.rgb(95, 120, 210)

        canvas.drawCircle(
            playerX,
            playerY,
            size * 0.095f,
            paint
        )

        // Creature ears
        paint.color = Color.rgb(135, 160, 240)

        canvas.drawCircle(
            playerX - size * 0.065f,
            playerY - size * 0.07f,
            size * 0.035f,
            paint
        )

        canvas.drawCircle(
            playerX + size * 0.065f,
            playerY - size * 0.07f,
            size * 0.035f,
            paint
        )

        // Eyes
        paint.color = Color.WHITE

        canvas.drawCircle(
            playerX - size * 0.03f,
            playerY - size * 0.01f,
            size * 0.012f,
            paint
        )

        canvas.drawCircle(
            playerX + size * 0.03f,
            playerY - size * 0.01f,
            size * 0.012f,
            paint
        )

        // Creature name
        paint.textSize = dp(17f)
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.WHITE

        canvas.drawText(
            "UNKNOWN BEAST",
            playerX,
            playerY + size * 0.15f,
            paint
        )
    }

    private fun drawInstructions(canvas: Canvas) {

        paint.textSize = dp(14f)
        paint.color = Color.LTGRAY
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(
            "Touch anywhere to move your creature",
            width / 2f,
            height * 0.88f,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {

                playerX = event.x
                playerY = event.y

                invalidate()

                return true
            }
        }

        return true
    }
}
