package ddwu.mobile.finalproject.ui.book;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ddwu.mobile.finalproject.databinding.ItemBookBinding;
import ddwu.mobile.finalproject.model.NaverBook;

public class BookAdapter extends ListAdapter<NaverBook, BookAdapter.BookItemViewHolder> {
    public interface OnItemClickListener {
        public void onItemClicked(NaverBook book);

        public void onItemLongClicked(NaverBook book);
    }

    private OnItemClickListener onItemClickListener;

    protected BookAdapter() {
        super(new DiffUtil.ItemCallback<NaverBook>() {
            @Override
            public boolean areItemsTheSame(@NonNull NaverBook oldItem, @NonNull NaverBook newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull NaverBook oldItem, @NonNull NaverBook newItem) {
                return TextUtils.equals(oldItem.getTitle(), newItem.getTitle()) &&
                        TextUtils.equals(oldItem.getAuthor(), newItem.getAuthor()) &&
                        TextUtils.equals(oldItem.getLink(), newItem.getLink()) &&
                        TextUtils.equals(oldItem.getImage(), newItem.getImage());
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BookItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBookBinding binding = ItemBookBinding.inflate(inflater, parent, false);
        return new BookItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookItemViewHolder holder, int position) {
        NaverBook item = getItem(position);
        ItemBookBinding binding = holder.binding;

        Glide.with(binding.imageView)
                .load(item.getImage())
                .centerCrop()
                .into(binding.imageView);

        binding.titleTextView.setText(item.getTitle());
        binding.authorTextView.setText(TextUtils.isEmpty(item.getAuthor()) ? item.getPublisher() : item.getAuthor());
        binding.publisherTextView.setText(item.getPublisher());

        binding.getRoot().setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClicked(item);
            }
        });

        binding.getRoot().setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemLongClicked(item);
            }

            return true;
        });
    }

    public static class BookItemViewHolder extends RecyclerView.ViewHolder {
        public ItemBookBinding binding;

        public BookItemViewHolder(ItemBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
