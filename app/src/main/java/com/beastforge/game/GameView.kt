
package com.beastforge.game

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class GameView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val density = resources.displayMetrics.density

    private var playerX = 0f
    private var playerY = 0f

    private var initialized = false
    private var lastFrame = 0L

    private var joystickX = 0f
    private var joystickY = 0f
    private var joystickActive = false

    private var moveX = 0f
    private var moveY = 0f

    private var attackUntil = 0L
    private var energy = 100f

    private var creaturePulse = 0f

    private val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private fun dp(value: Float) = value * density

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) return

        if (!initialized) {
            playerX = w * 0.5f
            playerY = h * 0.43f
            initialized = true
        }

        updateGame()

        drawBackground(canvas, w, h)
        drawArena(canvas, w, h)
        drawHeader(canvas)
        drawCreature(canvas)
        drawJoystick(canvas)
        drawControls(canvas)
        drawStatus(canvas)

        postInvalidateOnAnimation()
    }

    private fun updateGame() {

        val now = System.nanoTime()

        if (lastFrame == 0L) {
            lastFrame = now
            return
        }

        val delta = ((now - lastFrame) / 1_000_000_000f)
            .coerceIn(0f, 0.04f)

        lastFrame = now

        val speed = dp(190f)

        playerX += moveX * speed * delta
        playerY += moveY * speed * delta

        playerX = playerX.coerceIn(dp(35f), width - dp(35f))
        playerY = playerY.coerceIn(height * 0.20f, height * 0.70f)

        creaturePulse += delta * 3f
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.shader = LinearGradient(
            0f, 0f, w, h,
            Color.rgb(10, 13, 31),
            Color.rgb(35, 19, 54),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null
    }

    private fun drawArena(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.color = Color.rgb(25, 28, 52)

        canvas.drawRect(
            0f, h * 0.17f,
            w, h * 0.78f,
            paint
        )

        // Arena grid

        paint.color = Color.argb(35, 100, 150, 255)
        paint.strokeWidth = dp(1f)

        val gap = dp(40f)

        var x = 0f

        while (x < w) {
            canvas.drawLine(
                x, h * 0.17f,
                x, h * 0.78f,
                paint
            )
            x += gap
        }

        var y = h * 0.17f

        while (y < h * 0.78f) {
            canvas.drawLine(
                0f, y, w, y, paint
            )
            y += gap
        }

        // Arena boundary

        paint.color = Color.rgb(75, 88, 145)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dp(2f)

        canvas.drawRoundRect(
            dp(8f),
            h * 0.18f,
            w - dp(8f),
            h * 0.77f,
            dp(18f),
            dp(18f),
            paint
        )

        paint.style = Paint.Style.FILL
    }

    private fun drawHeader(canvas: Canvas) {

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = dp(25f)

        canvas.drawText(
            "BEASTFORGE",
            width / 2f,
            dp(43f),
            paint
        )

        paint.color = Color.rgb(145, 170, 240)
        paint.textSize = dp(11f)

        canvas.drawText(
            "CREATURE FUSION ARENA",
            width / 2f,
            dp(64f),
            paint
        )
    }

    private fun drawCreature(canvas: Canvas) {

        val size = dp(1f)
        val pulse = sin(creaturePulse) * dp(3f)

        // Magical glow

        paint.color = Color.argb(40, 65, 125, 255)

        canvas.drawCircle(
            playerX, playerY,
            dp(57f) + pulse,
            paint
        )

        // Shadow

        paint.color = Color.argb(100, 0, 0, 0)

        canvas.drawOval(
            playerX - dp(36f),
            playerY + dp(25f),
            playerX + dp(36f),
            playerY + dp(43f),
            paint
        )

        // Legs

        paint.color = Color.rgb(50, 76, 165)

        canvas.drawRoundRect(
            playerX - dp(23f),
            playerY + dp(13f),
            playerX - dp(9f),
            playerY + dp(40f),
            dp(6f), dp(6f), paint
        )

        canvas.drawRoundRect(
            playerX + dp(9f),
            playerY + dp(13f),
            playerX + dp(23f),
            playerY + dp(40f),
            dp(6f), dp(6f), paint
        )

        // Ears

        paint.color = Color.rgb(105, 145, 245)

        val leftEar = Path().apply {
            moveTo(playerX - dp(29f), playerY - dp(17f))
            lineTo(playerX - dp(26f), playerY - dp(48f))
            lineTo(playerX - dp(7f), playerY - dp(29f))
            close()
        }

        val rightEar = Path().apply {
            moveTo(playerX + dp(29f), playerY - dp(17f))
            lineTo(playerX + dp(26f), playerY - dp(48f))
            lineTo(playerX + dp(7f), playerY - dp(29f))
            close()
        }

        canvas.drawPath(leftEar, paint)
        canvas.drawPath(rightEar, paint)

        // Body

        paint.color = Color.rgb(75, 105, 205)

        canvas.drawCircle(
            playerX, playerY,
            dp(34f), paint
        )

        // Face

        paint.color = Color.rgb(125, 155, 245)

        canvas.drawCircle(
            playerX, playerY + dp(3f),
            dp(23f), paint
        )

        // Eyes

        paint.color = Color.WHITE

        canvas.drawCircle(
            playerX - dp(10f),
            playerY - dp(4f),
            dp(4f), paint
        )

        canvas.drawCircle(
            playerX + dp(10f),
            playerY - dp(4f),
            dp(4f), paint
        )

        paint.color = Color.BLACK

        canvas.drawCircle(
            playerX - dp(10f),
            playerY - dp(4f),
            dp(2f), paint
        )

        canvas.drawCircle(
            playerX + dp(10f),
            playerY - dp(4f),
            dp(2f), paint
        )

        // Name

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = dp(13f)

        canvas.drawText(
            "SKY WOLF",
            playerX,
            playerY + dp(65f),
            paint
        )
    }

    private fun drawJoystick(canvas: Canvas) {

        val cx = dp(75f)
        val cy = height - dp(105f)

        paint.color = Color.argb(65, 180, 195, 255)

        canvas.drawCircle(
            cx, cy, dp(49f), paint
        )

        paint.color = Color.argb(110, 100, 135, 230)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dp(2f)

        canvas.drawCircle(
            cx, cy, dp(49f), paint
        )

        paint.style = Paint.Style.FILL

        val knobX = if (joystickActive) joystickX else cx
        val knobY = if (joystickActive) joystickY else cy

        paint.color = Color.rgb(105, 145, 255)

        canvas.drawCircle(
            knobX, knobY, dp(21f), paint
        )

        paint.color = Color.WHITE
        paint.textSize = dp(10f)
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(
            "MOVE",
            cx,
            cy + dp(70f),
            paint
        )
    }

    private fun drawControls(canvas: Canvas) {

        val attackX = width - dp(75f)
        val attackY = height - dp(112f)

        val specialX = width - dp(75f)
        val specialY = height - dp(220f)

        // Attack button

        paint.color = if (System.nanoTime() < attackUntil)
            Color.rgb(255, 145, 65)
        else
            Color.rgb(185, 65, 75)

        canvas.drawCircle(
            attackX, attackY, dp(35f), paint
        )

        paint.color = Color.WHITE
        paint.textSize = dp(12f)
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "ATTACK",
            attackX,
            attackY + dp(4f),
            paint
        )

        // Special button

        paint.color = Color.rgb(90, 80, 205)

        canvas.drawCircle(
            specialX, specialY,
            dp(30f), paint
        )

        paint.color = Color.WHITE

        canvas.drawText(
            "SPECIAL",
            specialX,
            specialY + dp(4f),
            paint
        )
    }

    private fun drawStatus(canvas: Canvas) {

        val left = dp(20f)
        val top = dp(91f)
        val barWidth = dp(145f)

        paint.color = Color.WHITE
        paint.textSize = dp(11f)
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "SKY WOLF  LV.1",
            left, top, paint
        )

        // Energy background

        paint.color = Color.DKGRAY

        canvas.drawRoundRect(
            left, top + dp(9f),
            left + barWidth, top + dp(17f),
            dp(5f), dp(5f), paint
        )

        // Energy amount

        paint.color = Color.rgb(80, 170, 255)

        canvas.drawRoundRect(
            left, top + dp(9f),
            left + barWidth * (energy / 100f),
            top + dp(17f),
            dp(5f), dp(5f), paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val cx = dp(75f)
        val cy = height - dp(105f)

        val attackX = width - dp(75f)
        val attackY = height - dp(112f)

        val specialX = width - dp(75f)
        val specialY = height - dp(220f)

        when (event.action) {

            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {

                val dx = event.x - cx
                val dy = event.y - cy

                if (event.x < width * 0.48f &&
                    event.y > height * 0.58f) {

                    joystickActive = true

                    val distance = hypot(dx, dy)
                        .coerceAtLeast(1f)

                    val limited = min(distance, dp(49f))

                    joystickX = cx + dx / distance * limited
                    joystickY = cy + dy / distance * limited

                    moveX = dx / distance *
                        (limited / dp(49f))

                    moveY = dy / distance *
                        (limited / dp(49f))

                } else {
                    joystickActive = false
                    moveX = 0f
                    moveY = 0f
                }

                if (event.action == MotionEvent.ACTION_DOWN) {

                    if (hypot(
                            event.x - attackX,
                            event.y - attackY
                        ) < dp(42f)) {

                        attackUntil = System.nanoTime() +
                            350_000_000L
                    }

                    if (hypot(
                            event.x - specialX,
                            event.y - specialY
                        ) < dp(38f) && energy >= 25f) {

                        energy -= 25f
                    }
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {

                joystickActive = false
                moveX = 0f
                moveY = 0f

                invalidate()
                return true
            }
        }

        return true
    }
}
