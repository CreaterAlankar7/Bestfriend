
package com.beastforge.game

data class FusionRecipe(
    val first: String,
    val second: String,
    val result: Creature
)

object FusionSystem {

    private val recipes = listOf(

        FusionRecipe(
            "wolf", "eagle",
            Creature(
                "sky_wolf", "Sky Wolf",
                Element.AIR, 125, 95, 65, 110,
                isFused = true
            )
        ),

        FusionRecipe(
            "tiger", "snake",
            Creature(
                "venom_tiger", "Venom Tiger",
                Element.NATURE, 130, 115, 65, 90,
                isFused = true
            )
        ),

        FusionRecipe(
            "lion", "eagle",
            Creature(
                "sky_lion", "Sky Lion",
                Element.LIGHTNING, 135, 110, 70, 105,
                isFused = true
            )
        ),

        FusionRecipe(
            "shark", "crocodile",
            Creature(
                "aqua_beast", "Aqua Beast",
                Element.WATER, 160, 115, 95, 75,
                isFused = true
            )
        ),

        FusionRecipe(
            "bear", "gorilla",
            Creature(
                "iron_guardian", "Iron Guardian",
                Element.EARTH, 190, 110, 120, 55,
                isFused = true
            )
        )
    )

    fun fuse(
        first: Creature,
        second: Creature
    ): Creature? {

        val recipe = recipes.firstOrNull {
            (it.first == first.id && it.second == second.id) ||
            (it.first == second.id && it.second == first.id)
        }

        return recipe?.result
    }

    fun availableFusions(
        collection: List<Creature>
    ): List<Creature> {

        val ids = collection.map { it.id }.toSet()

        return recipes.filter {
            ids.contains(it.first) && ids.contains(it.second)
        }.map { it.result }
    }
}
