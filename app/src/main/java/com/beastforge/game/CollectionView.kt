
package com.beastforge.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View

class CollectionView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val density = resources.displayMetrics.density

    private val creatures = CreatureCollection.starterCreatures().toMutableList()
    private val fusedCreatures = mutableListOf<Creature>()
    private val selected = mutableSetOf<String>()

    private var message = "Select two creatures to fuse"
    private var scrollY = 0f

    private fun dp(value: Float) = value * density

    private fun text(
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
        paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        canvas.drawText(value, x, y, paint)
    }

    private fun rounded(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        color: Int,
        radius: Float = 14f
    ) {
        paint.color = color
        canvas.drawRoundRect(
            left, top, right, bottom,
            dp(radius), dp(radius), paint
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        canvas.drawColor(Color.rgb(12, 15, 30))

        text(canvas, "BEASTFORGE", dp(20f), dp(40f), 25f,
            Color.rgb(255, 190, 80), true)

        text(canvas, "CREATURE COLLECTION", dp(20f), dp(70f),
            13f, Color.LTGRAY, true)

        text(canvas, "Owned: ${creatures.size + fusedCreatures.size}",
            dp(20f), dp(105f), 14f, Color.WHITE)

        text(canvas, "FUSED: ${fusedCreatures.size}",
            w - dp(110f), dp(105f), 13f,
            Color.rgb(100, 220, 255), true)

        val cardWidth = (w - dp(55f)) / 2f
        val cardHeight = dp(105f)
        val startY = dp(130f)

        canvas.save()
        canvas.clipRect(0f, startY, w, h - dp(145f))

        creatures.forEachIndexed { index, creature ->

            val column = index % 2
            val row = index / 2

            val x = dp(20f) + column * (cardWidth + dp(15f))
            val y = startY + row * (cardHeight + dp(12f)) - scrollY

            val isSelected = selected.contains(creature.id)

            rounded(
                canvas,
                x, y,
                x + cardWidth, y + cardHeight,
                if (isSelected) Color.rgb(55, 85, 125)
                else Color.rgb(28, 34, 55)
            )
            CreatureRenderer.draw(
                canvas,
                creature,
                x + cardWidth - dp(35f),
                y + dp(53f),
                dp(19f)
            )

            if (isSelected) {
                paint.color = Color.rgb(100, 210, 255)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = dp(2f)
                canvas.drawRoundRect(
                    x, y, x + cardWidth, y + cardHeight,
                    dp(14f), dp(14f), paint
                )
                paint.style = Paint.Style.FILL
            }

            text(canvas, creature.name,
                x + dp(12f), y + dp(27f),
                15f, Color.WHITE, true)

            text(canvas, creature.element.name,
                x + dp(12f), y + dp(48f),
                10f, Color.rgb(150, 190, 255))

            text(canvas, "HP ${creature.hp}  ATK ${creature.attack}",
                x + dp(12f), y + dp(68f),
                10f, Color.LTGRAY)

            text(canvas, if (isSelected) "SELECTED" else "TAP TO SELECT",
                x + dp(12f), y + dp(88f),
                10f,
                if (isSelected) Color.CYAN else Color.GRAY)
        }

        canvas.restore()

        val bottom = h - dp(135f)

        rounded(canvas, dp(20f), bottom,
            w - dp(20f), bottom + dp(45f),
            Color.rgb(120, 75, 190), 12f)

        text(canvas, "FUSE SELECTED CREATURES",
            dp(35f), bottom + dp(29f),
            14f, Color.WHITE, true)

        text(canvas, message,
            dp(20f), bottom + dp(70f),
            12f, Color.rgb(230, 210, 160))

        text(canvas, "Fused Creatures: ${fusedCreatures.joinToString { it.name }}",
            dp(20f), bottom + dp(95f),
            11f, Color.rgb(110, 220, 255))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x
        val y = event.y
        val w = width.toFloat()
        val h = height.toFloat()

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {

                val bottom = h - dp(135f)

                // Fusion button
                if (y >= bottom && y <= bottom + dp(45f)) {
                    performFusion()
                    invalidate()
                    return true
                }

                // Creature selection
                if (y >= dp(130f) && y < bottom) {

                    val cardWidth = (w - dp(55f)) / 2f
                    val cardHeight = dp(105f)

                    val adjustedY = y - dp(130f) + scrollY

                    val column = if (x < w / 2f) 0 else 1
                    val row = (adjustedY / (cardHeight + dp(12f))).toInt()

                    val index = row * 2 + column

                    if (index in creatures.indices) {

                        val id = creatures[index].id

                        if (selected.contains(id)) {
                            selected.remove(id)
                        } else if (selected.size < 2) {
                            selected.add(id)
                        }

                        message = "Selected: ${selected.size}/2"
                    }

                    invalidate()
                }

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                return true
            }
        }

        return true
    }

    private fun performFusion() {

        if (selected.size != 2) {
            message = "Select exactly two creatures!"
            return
        }

        val chosen = creatures.filter { it.id in selected }

        val result = FusionSystem.fuse(chosen[0], chosen[1])

        if (result == null) {
            message = "No fusion recipe found!"
            selected.clear()
            return
        }

        if (fusedCreatures.any { it.id == result.id }) {
            message = "${result.name} already unlocked!"
            selected.clear()
            return
        }

        // Consume the two parent creatures
        creatures.removeAll { it.id in selected }

        // Add the new fused creature
        fusedCreatures.add(result)

        selected.clear()
        message = "${result.name} successfully created!"
    }
}
