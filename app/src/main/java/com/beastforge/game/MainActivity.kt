
package com.beastforge.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(15, 18, 35)
        window.navigationBarColor = Color.rgb(15, 18, 35)

        window.decorView.systemUiVisibility = 0

        val gameView = GameView(this)

        setContentView(gameView)
    }
}
