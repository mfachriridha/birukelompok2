package com.example.birukelompok2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.birukelompok2.databinding.ActivityMain046Binding
import com.example.birukelompok2.utils.SessionManager046
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity046 : AppCompatActivity() {
    private lateinit var binding: ActivityMain046Binding
    private lateinit var session: SessionManager046

    private val onNavItemSelected = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_rooms -> {
                loadFragment(RoomsFragment046())
                true
            }
            R.id.nav_bookings -> {
                loadFragment(BookingsFragment046())
                true
            }
            R.id.nav_users -> {
                if (session.isAdmin()) {
                    loadFragment(UsersFragment046())
                } else {
                    loadFragment(BookingsFragment046())
                }
                true
            }
            R.id.nav_report -> {
                loadFragment(ReportFragment046())
                true
            }
            R.id.nav_profile -> {
                loadFragment(ProfileFragment046())
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain046Binding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager046(this)

        if (!session.isLoggedIn()) {
            startActivity(android.content.Intent(this, LoginActivity046::class.java))
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)

        binding.bottomNav.setOnNavigationItemSelectedListener(onNavItemSelected)

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_rooms
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
