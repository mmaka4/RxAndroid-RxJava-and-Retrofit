package com.example.rxandroid.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroid.adapter.ListFruitsAdapter
import com.example.rxandroid.api.ServerApi
import com.example.rxandroid.model.MatundaResponse
import com.example.rxandroid.model.Tunda
import com.example.rxandroid.model.User
import com.example.rxandroid.R
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_fruits_layout.*
import kotlinx.android.synthetic.main.navigation_header.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ListFruits : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var lfAdapter: ListFruitsAdapter
    lateinit var mData: ArrayList<Tunda>

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    lateinit var matundaResponse : MatundaResponse

    val gson = Gson()
    private lateinit var userString: String
    lateinit var userInfo: User

    private var myCompositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_fruits_layout)

        myCompositeDisposable = CompositeDisposable()

//        userString = intent.getStringExtra("userData")
//
//        userInfo = gson.fromJson<User>(
//            userString,
//            User::class.java
//        )
//
//        Log.i("Fetched User Data", userInfo.id + " " + userInfo.email + " " + userInfo.image)

        navView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val navUseremail = headerView.user_email
        val navUserpic = headerView.user_profilepic

//        if (userInfo.email!!.isNotEmpty()) {
//            navUseremail.text = userInfo.email
//        }

        val imgUrl = ""
//        Log.i("imageURL", imgUrl)

        if (imgUrl.isEmpty()) { //url.isEmpty()
            Picasso.get()
                .load(R.drawable.profile_pic2)
                .placeholder(R.drawable.profile_pic2)
                .error(R.drawable.profile_pic2)
                .into(navUserpic)

        } else {
            Picasso.get()
                .load(resources.getString(R.string.userImageURL) + "")
                .placeholder(R.drawable.profile_pic2)
                .error(R.drawable.profile_pic2)
                .into(navUserpic) //this is your ImageView
        }

        listFruitscyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listFruitscyclerView.addOnItemTouchListener(
            object : RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onInterceptTouchEvent(
                    rv: RecyclerView, e:
                    MotionEvent
                ): Boolean {
                    if (e.action == MotionEvent.ACTION_DOWN &&
                        rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
                    ) {
                        rv.stopScroll()
                    }
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(
                    disallowIntercept: Boolean
                ) {
                }
            })

        val resId = R.anim.slide_down
        val animation = AnimationUtils.loadAnimation(this, resId)
        listFruitscyclerView.startAnimation(animation)

        loadFruits()

        //** Set the colors of the Pull To Refresh View
        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        itemsswipetorefresh.setOnRefreshListener {
            mData.clear()
            loadFruits()
            itemsswipetorefresh.isRefreshing = false
        }


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.nav_drawer)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        backLayout.setOnClickListener {
            //finish()
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> {
                Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, orderAcivity::class.java)
//                intent.putExtra("userData", gson.toJson(userInfo))
//                startActivity(intent)
            }
            R.id.settings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.edit_profile -> {
                Toast.makeText(this, "Edit profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.sign_out -> {
                Toast.makeText(this, "Sign  out clicked", Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.share -> {
                Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.rate_app -> {
                Toast.makeText(this, "Rate App clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.app_info -> {
                Toast.makeText(this, "App Info clicked", Toast.LENGTH_SHORT).show()
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("CheckResult")
    private fun loadFruits() {

        val retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.serverURL))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val api = retrofit.create(ServerApi::class.java)

        api.getMatunda()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                matundaResponse = it

                Log.i("ResponseString", gson.toJson(it.matunda))

                    shimmer_frame2.stopShimmer()
                    shimmer_frame2.visibility = View.GONE
                    listFruitscyclerView.visibility = View.VISIBLE

                mData = ArrayList()

                    mData = it.matunda!!

                    lfAdapter =
                        ListFruitsAdapter(
                            mData,
                            applicationContext,
                            this@ListFruits
                        )

                    lfAdapter.notifyDataSetChanged()

                    listFruitscyclerView.adapter = lfAdapter

            } , {
//                Toast.makeText(applicationContext, matundaResponse.message, Toast.LENGTH_SHORT)
                Log.i("ResponseFailure", it.message)
            })

//        myCompositeDisposable?.add(api.getMatunda()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe(System.out::println))

    }

    override fun onDestroy() {
        super.onDestroy()

//Clear all your disposables//

        myCompositeDisposable?.clear()

    }
}