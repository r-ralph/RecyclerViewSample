package ms.ralph.android.recyclerviewsample

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_list_item_sample.view.*
import ms.ralph.android.recyclerviewsample.likeicon.LikeButtonView

class SampleAdapter(
        context: Context,
        val data: Array<SampleData>) : RecyclerView.Adapter<SampleAdapter.SampleDataViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private val subject = PublishSubject.create<Int>()

    val observable: Observable<Int>
        get() = subject.hide()

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SampleDataViewHolder =
            SampleDataViewHolder(inflater.inflate(R.layout.view_list_item_sample, parent, false))

    override fun onBindViewHolder(holder: SampleDataViewHolder, position: Int) {
        val item = data[position]

        holder.textView.text = item.name
        holder.likeButton.setState(item.isFavorite, false)
        holder.likeButton.setOnClickListener { subject.onNext(position) }
    }

    override fun onBindViewHolder(holder: SampleDataViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.any()) {
            val payload = payloads[0] as? Pair<*, *>
            when (payload?.first as? String) {
                PAYLOAD_UPDATE_STATE -> {
                    (payload.second as? Boolean)?.let {
                        holder.likeButton.setState(it, false)
                    }
                }
            }
        }
        onBindViewHolder(holder, position)
    }

    class SampleDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.textView
        val likeButton: LikeButtonView = view.likeButton
    }

    companion object {
        const val PAYLOAD_UPDATE_STATE = "update_state"
    }
}