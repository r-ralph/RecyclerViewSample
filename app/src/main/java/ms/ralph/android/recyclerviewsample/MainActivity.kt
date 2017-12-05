package ms.ralph.android.recyclerviewsample

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity() {

    private var adapter: SampleAdapter? = null

    private lateinit var data: Array<SampleData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        data = (1..20).map { SampleData(it, "Data " + it, false) }.toTypedArray()
        adapter = SampleAdapter(applicationContext, data).also {
            it.observable.bindToLifecycle(this)
                    .subscribeOnIoThread()
                    .observeOnMainThread()
                    .subscribe({
                        changeLikeState(it)
                    })
        }
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(applicationContext)

    }

    private fun changeLikeState(position: Int) {
        val model = data[position]
        model.isFavorite = !model.isFavorite
        val id = model.id
        Log.d("MainActivity", "Start (id: $id, newState: ${model.isFavorite})")
        if (!model.isFavorite) {
            ApiClient.like(id)
                    .bindToLifecycle(this)
                    .subscribeOnIoThread()
                    .observeOnMainThread()
                    .subscribe({
                        Log.d("MainActivity", "Success (id: $id, newState: ${model.isFavorite})")
                    }, {
                        Log.d("MainActivity", "Error (id: $id, newState: ${model.isFavorite})")
                        model.isFavorite = false
                    })
        } else {
            ApiClient.unlike(id)
                    .bindToLifecycle(this)
                    .subscribeOnIoThread()
                    .observeOnMainThread()
                    .subscribe({
                        Log.d("MainActivity", "Success (id: $id, newState: ${model.isFavorite})")
                    }, {
                        Log.d("MainActivity", "Error (id: $id, newState: ${model.isFavorite})")
                        model.isFavorite = true
                    })
        }
    }
}
