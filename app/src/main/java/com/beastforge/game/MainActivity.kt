
package com.beastforge.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color

class MainActivity : Activity() {

    private lateinit var collectionView: CollectionView

    private fun showCollection() {
        setContentView(collectionView)
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

        collectionView = CollectionView(this) { creature ->
            showCreatureDetails(creature)
        }

        showCollection()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showCollection()
    }
}
