
package com.beastforge.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View

class CreatureDetailView(
    context: Context,
    creature: Creature,
    private val onBack: () -> Unit
) : View(context) {

    private var currentCreature =
        TrainingSystem.getCreature(creature)

    private var xp = TrainingSystem.getXP(currentCreature)

    private var message = "Train your creature to earn XP"

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val density = resources.displayMetrics.density

    private fun dp(value: Float) = value * density

    private fun drawText(
        canvas: Canvas,
        value: String,
        x: Float,
        y: Float,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) {
        paint.color = color
        paint.textSize = dp(size)
        paint.typeface =
            if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        canvas.drawText(value, x, y, paint)
    }

    private fun drawBar(
        canvas: Canvas,
        label: String,
        value: Int,
        max: Int,
        y: Float,
        color: Int
    ) {
        val left = dp(30f)
        val right = width - dp(30f)
        val barWidth = right - left

        drawText(
            canvas, "$label  $value",
            left, y, 14f, Color.WHITE, true
        )

        paint.color = Color.rgb(45, 50, 70)

        canvas.drawRoundRect(
            left, y + dp(12f),
            right, y + dp(22f),
            dp(6f), dp(6f), paint
        )

        val progress = (value.toFloat() / max)
            .coerceIn(0f, 1f)

        paint.color = color

        canvas.drawRoundRect(
            left, y + dp(12f),
            left + barWidth * progress, y + dp(22f),
            dp(6f), dp(6f), paint
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.rgb(12, 15, 30))

        drawText(
            canvas, "‹ BACK",
            dp(25f), dp(45f), 18f, Color.CYAN, true
        )

        drawText(
            canvas, "CREATURE PROFILE",
            dp(25f), dp(85f), 15f, Color.LTGRAY, true
        )

        CreatureRenderer.draw(
            canvas,
            currentCreature,
            width / 2f,
            dp(195f),
            dp(65f)
        )

        drawText(
            canvas,
            currentCreature.name.uppercase(),
            dp(25f), dp(300f),
            25f, Color.rgb(255, 195, 90), true
        )

        drawText(
            canvas,
            "${currentCreature.element.name} ELEMENT",
            dp(25f), dp(330f),
            13f, Color.CYAN, true
        )

        drawText(
            canvas,
            "LEVEL ${currentCreature.level}/20",
            width - dp(125f), dp(330f),
            13f, Color.WHITE, true
        )

        drawBar(
            canvas, "HP", currentCreature.hp, 200,
            dp(390f), Color.rgb(70, 210, 110)
        )

        drawBar(
            canvas, "ATTACK", currentCreature.attack, 150,
            dp(445f), Color.rgb(255, 100, 80)
        )

        drawBar(
            canvas, "DEFENCE", currentCreature.defence, 150,
            dp(500f), Color.rgb(80, 160, 255)
        )

        drawBar(
            canvas, "SPEED", currentCreature.speed, 150,
            dp(555f), Color.rgb(255, 210, 70)
        )

        drawText(
            canvas,
            "EXPERIENCE: $xp / 100",
            dp(30f), dp(610f),
            14f, Color.WHITE, true
        )

        paint.color = Color.rgb(45, 50, 70)

        canvas.drawRoundRect(
            dp(30f), dp(620f),
            width - dp(30f), dp(632f),
            dp(6f), dp(6f), paint
        )

        paint.color = Color.rgb(180, 110, 255)

        val xpWidth = (width - dp(60f)) * (xp / 100f)

        canvas.drawRoundRect(
            dp(30f), dp(620f),
            dp(30f) + xpWidth, dp(632f),
            dp(6f), dp(6f), paint
        )

        drawText(
            canvas,
            "SPECIAL ABILITY",
            dp(30f), dp(675f),
            14f, Color.rgb(190, 140, 255), true
        )

        drawText(
            canvas,
            getAbility(),
            dp(30f), dp(700f),
            13f, Color.WHITE
        )

        val buttonTop = height - dp(85f)

        paint.color = Color.rgb(110, 65, 190)

        canvas.drawRoundRect(
            dp(25f), buttonTop,
            width - dp(25f), height - dp(25f),
            dp(14f), dp(14f), paint
        )

        drawText(
            canvas,
            if (currentCreature.level >= 20)
                "MAX LEVEL REACHED"
            else
                "TRAIN CREATURE  (+25 XP)",
            dp(45f), buttonTop + dp(36f),
            15f, Color.WHITE, true
        )

        drawText(
            canvas,
            message,
            dp(25f), buttonTop - dp(15f),
            11f, Color.rgb(255, 210, 120)
        )
    }

    private fun getAbility(): String {
        return when (currentCreature.element) {
            Element.FIRE -> "Flame Strike"
            Element.WATER -> "Aqua Wave"
            Element.EARTH -> "Stone Shield"
            Element.AIR -> "Wind Dash"
            Element.SHADOW -> "Shadow Strike"
            Element.LIGHTNING -> "Thunder Blast"
            Element.NATURE -> "Nature Heal"
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_UP) {

            if (event.y < dp(65f)) {
                onBack()
                return true
            }

            val buttonTop = height - dp(85f)

            if (event.y >= buttonTop) {

                val result = TrainingSystem.train(currentCreature)

                currentCreature = result.creature
                xp = result.xp
                message = result.message

                invalidate()
                return true
            }
        }

        return true
    }
}
