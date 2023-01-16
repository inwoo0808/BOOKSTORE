package ddwu.mobile.finalproject.ui.diary;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ddwu.mobile.finalproject.databinding.ItemDiaryBinding;
import ddwu.mobile.finalproject.model.Diary;

public class DiaryListAdapter extends ListAdapter<Diary, DiaryListAdapter.MyDiaryItemViewHolder> {
    public interface OnItemClickListener {
        public void onItemClicked(Diary diary);

        public void onItemLongClicked(Diary diary);
    }

    private OnItemClickListener onItemClickListener;

    protected DiaryListAdapter() {
        super(new DiffUtil.ItemCallback<Diary>() {
            @Override
            public boolean areItemsTheSame(@NonNull Diary oldItem, @NonNull Diary newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Diary oldItem, @NonNull Diary newItem) {
                return TextUtils.equals(oldItem.title, newItem.title) &&
                        TextUtils.equals(oldItem.bookTitle, newItem.bookTitle) &&
                        TextUtils.equals(oldItem.writer, newItem.writer) &&
                        TextUtils.equals(oldItem.publish, newItem.publish) &&
                        TextUtils.equals(oldItem.review, newItem.review);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyDiaryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDiaryBinding binding = ItemDiaryBinding.inflate(inflater, parent, false);
        return new MyDiaryItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDiaryItemViewHolder holder, int position) {
        Diary item = getItem(position);
        ItemDiaryBinding binding = holder.binding;

        Glide.with(binding.imageView)
                .load(item.getBookImageUrl())
                .centerCrop()
                .into(binding.imageView);

        binding.bookTitleTextView.setText(item.bookTitle);
        binding.reviewTitleTextView.setText(item.title);
        binding.reviewContentTextView.setText(item.review);

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

    public static class MyDiaryItemViewHolder extends RecyclerView.ViewHolder {
        final public ItemDiaryBinding binding;

        public MyDiaryItemViewHolder(ItemDiaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}