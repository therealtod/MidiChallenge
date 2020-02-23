package com.francescofricano.midichallenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.francescofricano.midichallenge.auth.LoginActivity
import com.francescofricano.midichallenge.ui.friends.Friend
import com.francescofricano.midichallenge.ui.friends.FriendsFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), FriendsFragment.OnListFragmentInteractionListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setBackgroundDrawableResource(R.drawable.sfondo_7x15_300dpi)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        //toolbar.logo = getDrawable(R.drawable.logo_si_300dpi)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val navViewHeader = navView.getHeaderView(0)
        val loginButton: Button = navViewHeader.findViewById(R.id.login_button)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_friends, R.id.nav_gallery,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        loginButton.setOnClickListener { view ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun updateUI() {
        //Set the user name in the drawer header if available
        val user = FirebaseAuth.getInstance().currentUser
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navViewHeader = navView.getHeaderView(0)
        val loginButton: Button = navViewHeader.findViewById(R.id.login_button)
        val userNameTextView : TextView = navViewHeader.findViewById(R.id.navigation_drawer_username)
        val emailTextView : TextView = navViewHeader.findViewById(R.id.navigation_drawer_email)
        if (user != null) {
            loginButton.visibility = View.GONE
            userNameTextView.text = user.displayName
            emailTextView.text = user.email
            userNameTextView.visibility = View.VISIBLE
            emailTextView.visibility= View.VISIBLE
        }
        else {
            loginButton.visibility = View.VISIBLE
            userNameTextView.visibility = View.GONE
            emailTextView.visibility = View.GONE
        }



    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        //If the user is already logged in we can activate the Logout item
        Log.i("LOGGED:", FirebaseAuth.getInstance().currentUser.toString())
/*        if (FirebaseAuth.getInstance().currentUser != null) {
            val logoutOption = menu.findItem(R.id.action_logout)
            logoutOption.isVisible = true
        }*/
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onListFragmentInteraction(friend: Friend) {
        Log.i(this.javaClass.name, friend.name)
    }

}
