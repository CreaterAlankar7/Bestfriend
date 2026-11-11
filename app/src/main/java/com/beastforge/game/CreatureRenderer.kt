
package com.beastforge.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

object CreatureRenderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private fun elementColor(element: Element): Int {
        return when (element) {
            Element.FIRE -> Color.rgb(255, 90, 45)
            Element.WATER -> Color.rgb(50, 160, 255)
            Element.EARTH -> Color.rgb(150, 110, 65)
            Element.AIR -> Color.rgb(130, 220, 255)
            Element.SHADOW -> Color.rgb(130, 90, 200)
            Element.LIGHTNING -> Color.rgb(255, 220, 50)
            Element.NATURE -> Color.rgb(80, 210, 100)
        }
    }

    fun draw(
        canvas: Canvas,
        creature: Creature,
        x: Float,
        y: Float,
        radius: Float
    ) {
        val color = elementColor(creature.element)

        // Elemental aura
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(
            65,
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
        canvas.drawCircle(x, y, radius * 1.4f, paint)

        // Creature body
        paint.color = color
        canvas.drawOval(
            x - radius,
            y - radius * 0.65f,
            x + radius,
            y + radius * 0.8f,
            paint
        )

        // Head
        canvas.drawCircle(
            x,
            y - radius * 0.45f,
            radius * 0.65f,
            paint
        )

        // Left ear / horn
        val left = Path().apply {
            moveTo(x - radius * 0.45f, y - radius * 0.75f)
            lineTo(x - radius * 0.8f, y - radius * 1.35f)
            lineTo(x - radius * 0.1f, y - radius * 0.85f)
            close()
        }
        canvas.drawPath(left, paint)

        // Right ear / horn
        val right = Path().apply {
            moveTo(x + radius * 0.45f, y - radius * 0.75f)
            lineTo(x + radius * 0.8f, y - radius * 1.35f)
            lineTo(x + radius * 0.1f, y - radius * 0.85f)
            close()
        }
        canvas.drawPath(right, paint)

        // Eyes
        paint.color = Color.WHITE
        canvas.drawCircle(
            x - radius * 0.25f,
            y - radius * 0.45f,
            radius * 0.12f,
            paint
        )
        canvas.drawCircle(
            x + radius * 0.25f,
            y - radius * 0.45f,
            radius * 0.12f,
            paint
        )

        // Eye pupils
        paint.color = Color.BLACK
        canvas.drawCircle(
            x - radius * 0.25f,
            y - radius * 0.45f,
            radius * 0.055f,
            paint
        )
        canvas.drawCircle(
            x + radius * 0.25f,
            y - radius * 0.45f,
            radius * 0.055f,
            paint
        )

        // Creature nose
        paint.color = Color.rgb(35, 25, 35)
        canvas.drawCircle(
            x,
            y - radius * 0.1f,
            radius * 0.1f,
            paint
        )

        // Elemental highlight
        paint.color = Color.WHITE
        paint.alpha = 180
        canvas.drawCircle(
            x + radius * 0.65f,
            y - radius * 0.65f,
            radius * 0.12f,
            paint
        )
        paint.alpha = 255
    }
}
