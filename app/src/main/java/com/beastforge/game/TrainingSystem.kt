
package com.beastforge.game

data class TrainingResult(
    val creature: Creature,
    val xp: Int,
    val levelledUp: Boolean,
    val message: String
)

object TrainingSystem {

    private val trainedCreatures = mutableMapOf<String, Creature>()
    private val experience = mutableMapOf<String, Int>()

    fun getCreature(creature: Creature): Creature {
        return trainedCreatures[creature.id] ?: creature
    }

    fun getXP(creature: Creature): Int {
        return experience[creature.id] ?: 0
    }

    fun train(original: Creature): TrainingResult {

        var creature = getCreature(original)

        if (creature.level >= 20) {
            return TrainingResult(
                creature,
                getXP(creature),
                false,
                "Maximum level reached!"
            )
        }

        var xp = getXP(creature) + 25
        var levelledUp = false
        var message = "Training completed! +25 XP"

        if (xp >= 100) {

            xp -= 100
            levelledUp = true

            creature = creature.copy(
                level = creature.level + 1,
                hp = creature.hp + 8,
                attack = creature.attack + 3,
                defence = creature.defence + 2,
                speed = creature.speed + 2
            )

            message = "LEVEL UP! ${creature.name} is now Level ${creature.level}"
        }

        trainedCreatures[creature.id] = creature
        experience[creature.id] = xp

        return TrainingResult(
            creature,
            xp,
            levelledUp,
            message
        )
    }
}
