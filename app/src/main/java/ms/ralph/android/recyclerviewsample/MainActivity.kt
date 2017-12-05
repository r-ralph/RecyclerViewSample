package ms.ralph.android.recyclerviewsample

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
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
        val previousState = model.isFavorite
        model.isFavorite = !model.isFavorite
        val id = model.id
        Toast.makeText(this, "Start (id: $id, newState: ${model.isFavorite})", Toast.LENGTH_SHORT).show()
        adapter?.notifyItemChanged(position, SampleAdapter.PAYLOAD_UPDATE_STATE to model.isFavorite)
        if (!previousState) {
            ApiClient.like(id)
                    .bindToLifecycle(this)
                    .subscribeOnIoThread()
                    .observeOnMainThread()
                    .subscribe({
                        Toast.makeText(this, "Success like (id: $id, newState: ${model.isFavorite})", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(this, "Error like(id: $id, newState: ${model.isFavorite})", Toast.LENGTH_SHORT).show()
                        model.isFavorite = false
                        adapter?.notifyItemChanged(position, SampleAdapter.PAYLOAD_UPDATE_STATE to false)
                    })
        } else {
            ApiClient.unlike(id)
                    .bindToLifecycle(this)
                    .subscribeOnIoThread()
                    .observeOnMainThread()
                    .subscribe({
                        Toast.makeText(this, "Success unlike (id: $id, newState: ${model.isFavorite})", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(this, "Error unlike (id: $id, newState: ${model.isFavorite})", Toast.LENGTH_SHORT).show()
                        model.isFavorite = true
                        adapter?.notifyItemChanged(position, SampleAdapter.PAYLOAD_UPDATE_STATE to true)
                    })
        }
    }
}
