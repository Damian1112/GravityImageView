package csiw.imageviews.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import csiw.imageviews.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
    }

}
