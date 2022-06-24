package com.juseung.instagram_clone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.juseung.instagram_clone.databinding.ActivityMainBinding
import navigation.AlarmFragment
import navigation.DetailViewFragment
import navigation.GridFragment
import navigation.UserFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.bottomNavigation.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.action_home -> {
                        var detailViewFragment = DetailViewFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                        true
                    }
                    R.id.action_search -> {
                        var gridFragment = GridFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
                        true
                    }
                    R.id.action_photo -> {
                        true
                    }
                    R.id.action_favorite_alarm -> {
                        var alarmFragment = AlarmFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                        true
                    }
                    R.id.action_account -> {
                        var userFragment = UserFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                        true
                    }
                }
                true
            }
            selectedItemId = R.id.action_search
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }


}





