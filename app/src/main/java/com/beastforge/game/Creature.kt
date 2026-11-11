
package com.beastforge.game

enum class Element {
    FIRE, WATER, EARTH, AIR, SHADOW, LIGHTNING, NATURE
}

data class Creature(
    val id: String,
    val name: String,
    val element: Element,
    val hp: Int,
    val attack: Int,
    val defence: Int,
    val speed: Int,
    val level: Int = 1,
    val isFused: Boolean = false
)

object CreatureCollection {

    fun starterCreatures(): List<Creature> = listOf(
        Creature("wolf", "Wolf", Element.SHADOW, 100, 75, 55, 90),
        Creature("tiger", "Tiger", Element.FIRE, 110, 90, 55, 85),
        Creature("eagle", "Eagle", Element.AIR, 85, 75, 45, 100),
        Creature("bear", "Bear", Element.EARTH, 150, 75, 90, 45),
        Creature("snake", "Snake", Element.NATURE, 80, 85, 40, 95),
        Creature("crocodile", "Crocodile", Element.WATER, 130, 80, 80, 45),
        Creature("lion", "Lion", Element.FIRE, 120, 90, 65, 80),
        Creature("shark", "Shark", Element.WATER, 115, 90, 60, 85),
        Creature("deer", "Deer", Element.NATURE, 90, 55, 50, 95),
        Creature("gorilla", "Gorilla", Element.EARTH, 145, 85, 85, 50)
    )
}
