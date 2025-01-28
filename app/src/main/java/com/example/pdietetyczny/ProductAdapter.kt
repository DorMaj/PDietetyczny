import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.pdietetyczny.databinding.ItemProductBinding
import com.example.pdietetyczny.models.FoodItem

class ProductAdapter(private val onItemClick: (FoodItem) -> Unit) :
    ListAdapter<FoodItem, ProductAdapter.ProductViewHolder>(FoodItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(product: FoodItem) {
            binding.productName.text = product.name
            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    class FoodItemDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id == newItem.id // Porównanie po id (unikalnym identyfikatorze)
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem // Porównanie zawartości
        }
    }
}
