package com.beta.yihao.translite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.beta.yihao.translite.databinding.ActivityMainBinding
import com.beta.yihao.translite.utils.toastShort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MainActivity
 */
class MainActivity : AppCompatActivity() {

    //导航控制器
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private var isQuit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = dataBinding.drawerLayout
        val toolbar = dataBinding.toolbar
        val navigationView = dataBinding.navigationView

        navController = Navigation.findNavController(this, R.id.nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            //判断当前导航地址是否是主界面
            if (navController.currentDestination?.id == R.id.fragment_trans_main) {
                //双击退出逻辑
                if (!isQuit) {
                    toastShort(getString(R.string.press_once_more))
                    isQuit = true
                    CoroutineScope(Main).launch {
                        delay(2000)
                        isQuit = false
                    }
                } else {
                    finish()
                }
            } else {
                super.onBackPressed()
            }

        }
    }
}

