package org.marsaud.zapod

import android.app.WallpaperManager
import android.app.WallpaperManager.FLAG_LOCK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val baseUrl = "https://apod.nasa.gov/apod/"
    var bmp: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // BOILERPLATE: Avoid "android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork"
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        bmp = getImage(getPage(baseUrl + "astropix.html"))
        if (bmp != null) {
            apod.setImageBitmap(bmp)
            val title = getPage(baseUrl + "astropix.html", 1)
            if (title != null) {
                titleTextView.text = title
            } else {
                titleTextView.visibility = View.GONE
            }
        } else {
            setWallpaperButton.visibility = View.GONE
            errorTextView.visibility = View.VISIBLE
        }
    }


    /**
     * Get apod.nasa.gov webpage and parse its content to find the APOD and tht title of it (using OKHTTP + Jsoup).
     *
     * @param url URL of the APOD homepage.
     * @param type Int of value "0" by default, which get the image URL; if it's different from 0, get the title instead.
     * @return Return a String containing the URL to the APOD.
     */
    fun getPage(url: String, type: Int = 0): String? {
        try {
            // Get webpage via OKHTTP
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseString = response.body()!!.string()

            // Parse webpage via JSoup
            val document = Jsoup.parse(responseString)
            var parsedString: String? = null
            if (type == 0) {
                val element = document.select("a").get(1) // using get(1) instead of first() (= 0) to have the second element
                parsedString = element.attr("href")
                return baseUrl + parsedString
            } else {
                val element = document.select("b").first()
                parsedString = element.text()
                return parsedString
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Get the APOD (using OKHTTP).
     *
     * @param url URL of the APOD.
     * @return Return a Bitmap type containing the APOD.
     */
    fun getImage(url: String?): Bitmap? {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url!!).build()
            val response = client.newCall(request).execute()
            val stream = response.body()!!.byteStream()
            return BitmapFactory.decodeStream(stream)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Action triggered by the "define" button.
     *
     * @param view Mandatory to use XML onClick attribute.
     */
    fun defineWallpaper(view: View) {
        try {
            val wallpaper = WallpaperManager.getInstance(applicationContext)
            wallpaper.setBitmap(bmp) // system wallpaper
            if (Build.VERSION.SDK_INT >= 24) { // only supported since Android 7.0 (Nougat)
                wallpaper.setBitmap(bmp, null, true, FLAG_LOCK) // lockscreen wallpaper
            }
            Snackbar.make(rootView, R.string.defined, Snackbar.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Show the AboutActivity.
     *
     * @param view Mandatory to use XML onClick attribute.
     */
    fun about(view: View) {
        startActivity(Intent(this, AboutActivity::class.java))
    }
}