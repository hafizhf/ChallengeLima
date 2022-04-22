package andlima.hafizhfy.challengelima.adapter

import andlima.hafizhfy.challengelima.R
import andlima.hafizhfy.challengelima.model.GetAllFilmResponseItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_film.view.*

class AdapterFilmBackup(private var onClick : (GetAllFilmResponseItem)-> Unit) : RecyclerView.Adapter<AdapterFilmBackup.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var dataFilm : List<GetAllFilmResponseItem>? = null

    fun setDataFilm(film : List<GetAllFilmResponseItem>) {
        this.dataFilm = film
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFilmBackup.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterFilmBackup.ViewHolder, position: Int) {
        this.let {
            Glide.with(holder.itemView.context).load(dataFilm!![position].image)
                .into(holder.itemView.iv_thumbnail_film)
        }
        holder.itemView.tv_judul_film.text = dataFilm!![position].title
        holder.itemView.tv_tanggal.text = dataFilm!![position].releaseDate
        holder.itemView.tv_sutradara.text = dataFilm!![position].director

        holder.itemView.item.setOnClickListener {
            onClick(dataFilm!![position])
        }
    }

    override fun getItemCount(): Int {
        return if (dataFilm != null) {
            dataFilm!!.size
        } else {
            0
        }
    }
}