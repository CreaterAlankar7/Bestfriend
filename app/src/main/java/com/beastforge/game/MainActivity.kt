package com.beastforge.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color

class MainActivity : Activity() {

    private fun showCollection() {
        setContentView(
            CollectionView(this) { creature ->
                showCreatureDetails(creature)
            }
        )
    }

    private fun showCreatureDetails(creature: Creature) {
        setContentView(
            CreatureDetailView(this, creature) {
                showCollection()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 15, 30)
        window.navigationBarColor = Color.rgb(12, 15, 30)

        showCollection()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showCollection()
    }
}
