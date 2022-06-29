package dev.toriyama.android.hidakaya

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.toriyama.android.hidakaya.http.API
import dev.toriyama.android.hidakaya.http.Menu
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.io.InputStream
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setLogo(R.mipmap.ic_launcher)
        toolbar.setTitle(R.string.app_name)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setSubtitle(R.string.toolbar_title)
        toolbar.setSubtitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        val layout = LinearLayoutManager(this@MainActivity)
        val listviewMenu = findViewById<RecyclerView>(R.id.menu);
        listviewMenu.layoutManager = layout;


        lifecycleScope.launch{
            val result = fetchAllMenu();
            val menuList: MutableList<MutableMap<String, Any>> = mutableListOf();
            for (menu in result) {
                println(menu)
                menuList.add(mutableMapOf<String, Any>(
                    "name" to menu.name,
                    "price" to menu.price,
                    "thumbnail" to menu.thumbnail
                ));
            }
            val adapter = RecyclerListAdapter(menuList)
            listviewMenu.adapter = adapter
        }
    }

    /**
    private fun createMenuList(): MutableList<MutableMap<String, Any>> {
        val menuList: MutableList<MutableMap<String, Any>> = mutableListOf();
        menuList.add(mutableMapOf<String, Any>(
            "name" to "ラーメン",
            "price" to 560
        ));
        return menuList
    }
    */

    private suspend fun fetchAllMenu(): List<Menu> {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://yuukitoriyama.github.io/hidakaya/").addConverterFactory(
            MoshiConverterFactory.create(moshi)
        ).build();

        val api = retrofit.create<API>(API::class.java)
        return api.getAllMenu()
    }

    private inner class RecyclerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMenuNameRow: TextView
        var tvMenuPriceRow: TextView
        var thumbnail: ImageView

        init {
            tvMenuNameRow = itemView.findViewById(R.id.tvMenuNameRow)
            tvMenuPriceRow = itemView.findViewById(R.id.tvMenuPriceRow)
            thumbnail = itemView.findViewById(R.id.thumbnail)
        }
    }

    private inner class RecyclerListAdapter(private val listData: MutableList<MutableMap<String, Any>>) : RecyclerView.Adapter<RecyclerListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListViewHolder {
            val inflater = LayoutInflater.from(this@MainActivity)
            val view = inflater.inflate(R.layout.row, parent, false)
            return  RecyclerListViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerListViewHolder, position: Int) {
            val item = listData[position]
            val menuName = item["name"] as String
            val menuPrice = (item["price"] as Int).toString()
            holder.tvMenuNameRow.text = menuName
            holder.tvMenuPriceRow.text = menuPrice + "円"
            val imageTask = GetImage(holder.thumbnail)
            imageTask.execute(item["thumbnail"] as String)
        }

        override fun getItemCount(): Int {
           return listData.size
        }
    }

    inner class GetImage(private val image: ImageView) :
        AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg params: String): Bitmap? {
            var image: Bitmap? = null
            try {
                val imageUrl = URL(params[0])
                val imageIs: InputStream
                imageIs = imageUrl.openStream()
                image = BitmapFactory.decodeStream(imageIs)
            }  catch (e: IOException) {
                println("エラー")
            }
            return image
        }
        override fun onPostExecute(result: Bitmap) {
            // 取得した画像をImageViewに設定します。
            image.setImageBitmap(result)
        }
    }
}