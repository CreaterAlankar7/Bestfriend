
package com.beastforge.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 15, 30)
        window.navigationBarColor = Color.rgb(12, 15, 30)

        val collectionView = CollectionView(this)
        setContentView(collectionView)
    }
}
