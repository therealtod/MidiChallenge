package com.francescofricano.midichallenge

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import com.francescofricano.midichallenge.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val startButton: Button = findViewById(R.id.button)
        val loginButton: Button = findViewById(R.id.button3)
        val user= FirebaseAuth.getInstance().currentUser

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Set the user name in the drawer header if available
        if (user != null) {
            val navViewHeader = navView.getHeaderView(0)
            val userNameTextView : TextView = navViewHeader.findViewById(R.id.navigation_drawer_username)
            val emailTextView : TextView = navViewHeader.findViewById(R.id.navigation_drawer_email)
            userNameTextView.text = user.displayName
            emailTextView.text = user.email
        }

        startButton.setOnClickListener { view ->
            val intent = Intent(this, ClassicGameActivity::class.java)
            intent.putExtra("CURRENT_QUESTION", -1)
            startActivity(intent)
        }

        loginButton.setOnClickListener { view ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
    }

}
