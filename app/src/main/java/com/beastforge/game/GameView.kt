
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

    private var enemyX = 0f
    private var enemyY = 0f

    private var playerHP = 100f
    private var enemyHP = 100f
    private var energy = 100f

    private var moveX = 0f
    private var moveY = 0f

    private var joystickActive = false
    private var joystickX = 0f
    private var joystickY = 0f

    private var initialized = false
    private var lastFrame = 0L

    private var lastPlayerAttack = 0L
    private var lastEnemyAttack = 0L

    private var attackEffectUntil = 0L
    private var specialEffectUntil = 0L

    private var gameOver = false
    private var victory = false

    private fun dp(v: Float) = v * density

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width <= 0 || height <= 0) return

        if (!initialized) resetGame()

        updateGame()

        drawBackground(canvas)
        drawHeader(canvas)
        drawHealthBars(canvas)
        drawCreature(canvas, playerX, playerY, false)
        drawCreature(canvas, enemyX, enemyY, true)
        drawEffects(canvas)
        drawJoystick(canvas)
        drawButtons(canvas)

        if (gameOver) drawGameOver(canvas)

        if (!gameOver) postInvalidateOnAnimation()
    }

    private fun resetGame() {
        playerX = width * 0.5f
        playerY = height * 0.61f

        enemyX = width * 0.5f
        enemyY = height * 0.34f

        playerHP = 100f
        enemyHP = 100f
        energy = 100f

        gameOver = false
        victory = false

        lastFrame = 0L
        lastPlayerAttack = 0L
        lastEnemyAttack = 0L

        initialized = true
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

        if (gameOver) return

        // Player movement

        val playerSpeed = dp(190f)

        playerX += moveX * playerSpeed * delta
        playerY += moveY * playerSpeed * delta

        playerX = playerX.coerceIn(dp(35f), width - dp(35f))
        playerY = playerY.coerceIn(height * 0.23f, height * 0.72f)

        // Enemy AI: follow the player

        val dx = playerX - enemyX
        val dy = playerY - enemyY
        val distance = hypot(dx, dy)

        if (distance > dp(65f)) {
            val enemySpeed = dp(65f)

            enemyX += dx / distance * enemySpeed * delta
            enemyY += dy / distance * enemySpeed * delta
        }

        // Enemy automatic attack

        if (distance < dp(85f) &&
            now - lastEnemyAttack > 1_200_000_000L) {

            playerHP = (playerHP - 8f).coerceAtLeast(0f)
            lastEnemyAttack = now

            if (playerHP <= 0f) {
                gameOver = true
                victory = false
            }
        }

        // Energy regeneration

        energy = (energy + 5f * delta).coerceAtMost(100f)

        enemyX = enemyX.coerceIn(dp(35f), width - dp(35f))
        enemyY = enemyY.coerceIn(height * 0.23f, height * 0.72f)
    }

    private fun drawBackground(canvas: Canvas) {

        paint.shader = LinearGradient(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            Color.rgb(9, 14, 32),
            Color.rgb(42, 18, 48),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            paint
        )

        paint.shader = null

        paint.color = Color.rgb(27, 31, 55)

        canvas.drawRect(
            0f, height * 0.18f,
            width.toFloat(), height * 0.77f,
            paint
        )

        paint.color = Color.argb(35, 120, 145, 255)
        paint.strokeWidth = dp(1f)

        var x = 0f

        while (x < width) {
            canvas.drawLine(
                x, height * 0.18f,
                x, height * 0.77f,
                paint
            )
            x += dp(40f)
        }

        var y = height * 0.18f

        while (y < height * 0.77f) {
            canvas.drawLine(
                0f, y,
                width.toFloat(), y,
                paint
            )
            y += dp(40f)
        }
    }

    private fun drawHeader(canvas: Canvas) {

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = dp(23f)

        canvas.drawText(
            "BEASTFORGE",
            width / 2f,
            dp(39f),
            paint
        )

        paint.color = Color.rgb(150, 175, 230)
        paint.textSize = dp(11f)

        canvas.drawText(
            "CREATURE BATTLE",
            width / 2f,
            dp(59f),
            paint
        )
    }

    private fun drawHealthBars(canvas: Canvas) {

        drawHealthBar(
            canvas,
            dp(16f),
            dp(85f),
            "SKY WOLF",
            playerHP,
            Color.rgb(55, 155, 255)
        )

        drawHealthBar(
            canvas,
            width - dp(151f),
            dp(85f),
            "FIRE BEAST",
            enemyHP,
            Color.rgb(255, 85, 65)
        )
    }

    private fun drawHealthBar(
        canvas: Canvas,
        x: Float,
        y: Float,
        name: String,
        hp: Float,
        color: Int
    ) {

        val barWidth = dp(135f)

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = dp(12f)
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(name, x, y, paint)

        paint.color = Color.DKGRAY

        canvas.drawRoundRect(
            x, y + dp(8f),
            x + barWidth, y + dp(19f),
            dp(5f), dp(5f), paint
        )

        paint.color = color

        canvas.drawRoundRect(
            x, y + dp(8f),
            x + barWidth * (hp / 100f),
            y + dp(19f),
            dp(5f), dp(5f), paint
        )

        paint.color = Color.WHITE
        paint.textSize = dp(9f)

        canvas.drawText(
            "${hp.toInt()}/100",
            x, y + dp(34f), paint
        )
    }

    private fun drawCreature(
        canvas: Canvas,
        x: Float,
        y: Float,
        enemy: Boolean
    ) {

        val mainColor = if (enemy)
            Color.rgb(210, 75, 65)
        else
            Color.rgb(75, 115, 220)

        val earColor = if (enemy)
            Color.rgb(255, 130, 75)
        else
            Color.rgb(130, 165, 255)

        paint.color = Color.argb(45, 100, 130, 255)

        canvas.drawCircle(x, y, dp(51f), paint)

        paint.color = mainColor

        canvas.drawCircle(x, y, dp(33f), paint)

        // Ears

        paint.color = earColor

        val ears = Path().apply {
            moveTo(x - dp(27f), y - dp(12f))
            lineTo(x - dp(25f), y - dp(45f))
            lineTo(x - dp(7f), y - dp(27f))
            close()

            moveTo(x + dp(27f), y - dp(12f))
            lineTo(x + dp(25f), y - dp(45f))
            lineTo(x + dp(7f), y - dp(27f))
            close()
        }

        canvas.drawPath(ears, paint)

        // Face

        paint.color = Color.WHITE

        canvas.drawCircle(
            x - dp(10f), y - dp(4f),
            dp(4f), paint
        )

        canvas.drawCircle(
            x + dp(10f), y - dp(4f),
            dp(4f), paint
        )

        paint.color = Color.BLACK

        canvas.drawCircle(
            x - dp(10f), y - dp(4f),
            dp(2f), paint
        )

        canvas.drawCircle(
            x + dp(10f), y - dp(4f),
            dp(2f), paint
        )

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = dp(11f)
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            if (enemy) "FIRE BEAST" else "SKY WOLF",
            x, y + dp(57f), paint
        )
    }

    private fun drawEffects(canvas: Canvas) {

        val now = System.nanoTime()

        if (now < attackEffectUntil) {
            paint.color = Color.argb(180, 255, 180, 60)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = dp(5f)

            canvas.drawCircle(
                enemyX, enemyY,
                dp(48f), paint
            )

            paint.style = Paint.Style.FILL
        }

        if (now < specialEffectUntil) {
            paint.color = Color.argb(100, 90, 160, 255)

            canvas.drawCircle(
                enemyX, enemyY,
                dp(85f), paint
            )
        }
    }

    private fun drawJoystick(canvas: Canvas) {

        val cx = dp(72f)
        val cy = height - dp(105f)

        paint.color = Color.argb(80, 130, 160, 255)

        canvas.drawCircle(cx, cy, dp(48f), paint)

        val kx = if (joystickActive) joystickX else cx
        val ky = if (joystickActive) joystickY else cy

        paint.color = Color.rgb(95, 135, 245)

        canvas.drawCircle(kx, ky, dp(20f), paint)

        paint.color = Color.WHITE
        paint.textSize = dp(10f)
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(
            "MOVE",
            cx, cy + dp(67f), paint
        )
    }

    private fun drawButtons(canvas: Canvas) {

        val attackX = width - dp(72f)
        val attackY = height - dp(100f)

        val specialX = width - dp(72f)
        val specialY = height - dp(205f)

        paint.color = Color.rgb(190, 65, 65)

        canvas.drawCircle(
            attackX, attackY,
            dp(36f), paint
        )

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = dp(11f)
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "ATTACK",
            attackX, attackY + dp(4f), paint
        )

        paint.color = Color.rgb(90, 80, 210)

        canvas.drawCircle(
            specialX, specialY,
            dp(30f), paint
        )

        paint.color = Color.WHITE

        canvas.drawText(
            "SPECIAL",
            specialX, specialY + dp(4f), paint
        )
    }

    private fun drawGameOver(canvas: Canvas) {

        paint.color = Color.argb(205, 0, 0, 0)

        canvas.drawRect(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            paint
        )

        paint.color = if (victory)
            Color.rgb(90, 220, 140)
        else
            Color.rgb(255, 90, 90)

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = dp(32f)
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            if (victory) "VICTORY!" else "DEFEAT!",
            width / 2f,
            height / 2f,
            paint
        )

        paint.color = Color.WHITE
        paint.textSize = dp(16f)

        canvas.drawText(
            "Tap anywhere to play again",
            width / 2f,
            height / 2f + dp(42f),
            paint
        )
    }

    private fun attack() {

        val now = System.nanoTime()

        if (now - lastPlayerAttack < 600_000_000L) return

        lastPlayerAttack = now

        val distance = hypot(
            playerX - enemyX,
            playerY - enemyY
        )

        if (distance < dp(125f)) {
            enemyHP = (enemyHP - 15f).coerceAtLeast(0f)
            attackEffectUntil = now + 250_000_000L
        }

        checkVictory()
    }

    private fun specialAttack() {

        if (energy < 25f) return

        energy -= 25f

        val distance = hypot(
            playerX - enemyX,
            playerY - enemyY
        )

        if (distance < dp(220f)) {
            enemyHP = (enemyHP - 35f).coerceAtLeast(0f)
            specialEffectUntil = System.nanoTime() +
                450_000_000L
        }

        checkVictory()
    }

    private fun checkVictory() {

        if (enemyHP <= 0f) {
            gameOver = true
            victory = true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val cx = dp(72f)
        val cy = height - dp(105f)

        val attackX = width - dp(72f)
        val attackY = height - dp(100f)

        val specialX = width - dp(72f)
        val specialY = height - dp(205f)

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

                if (gameOver) {
                    resetGame()
                    invalidate()
                    return true
                }

                if (hypot(
                        event.x - attackX,
                        event.y - attackY
                    ) < dp(43f)) {

                    attack()
                    invalidate()
                    return true
                }

                if (hypot(
                        event.x - specialX,
                        event.y - specialY
                    ) < dp(38f)) {

                    specialAttack()
                    invalidate()
                    return true
                }

                if (event.x < width * 0.48f &&
                    event.y > height * 0.58f) {

                    joystickActive = true
                    updateJoystick(event.x, event.y, cx, cy)
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {

                if (joystickActive) {
                    updateJoystick(event.x, event.y, cx, cy)
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

    private fun updateJoystick(
        x: Float,
        y: Float,
        cx: Float,
        cy: Float
    ) {

        val dx = x - cx
        val dy = y - cy

        val distance = hypot(dx, dy).coerceAtLeast(1f)
        val limited = min(distance, dp(48f))

        joystickX = cx + dx / distance * limited
        joystickY = cy + dy / distance * limited

        moveX = dx / distance * limited / dp(48f)
        moveY = dy / distance * limited / dp(48f)
    }
}
