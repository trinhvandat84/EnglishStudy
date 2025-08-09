package jp.ac.jec.cm0122.android114;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jp.ac.jec.cm0122.android114.API.NetworkManager;
import jp.ac.jec.cm0122.android114.Helpers.UpdateWordCallback;
import jp.ac.jec.cm0122.android114.Models.Word;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private Context mContext;
    private List<Word> mWords;

    public WordAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Word> words) {
        this.mWords = words;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
            Word word = mWords.get(position);
            if (word == null) {
                return;
            }
            holder.txtEnglish.setText(word.getEnglish());
            holder.txtPronunciation.setText(word.getPronunciation());
            holder.txtMeaning.setText(word.getMeaning());
            holder.moreBtn.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.moreBtn);
                popupMenu.getMenuInflater().inflate(R.menu.option, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                // Handle edit action
                                EditWord.start(mContext, word);
                            //    Toast.makeText(mContext, "Edit", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.delete:
                                // Handle delete action
                                NetworkManager.deleteWord(word.getEnglish(), new UpdateWordCallback() {
                                    @Override
                                    public void onSuccess(boolean result) {
                                        if (result) {
                                            mWords.remove(word);
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                            //    Toast.makeText(mContext, "Delete", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            });
    }

    @Override
    public int getItemCount() {
        if (mWords != null) {
            return mWords.size();
        }
        return 0;
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        private TextView txtEnglish, txtPronunciation, txtMeaning;
        private ImageButton moreBtn;
        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEnglish = itemView.findViewById(R.id.txtEnglish);
            txtPronunciation = itemView.findViewById(R.id.txtPronunciation);
            txtMeaning = itemView.findViewById(R.id.txtMeaning);
            moreBtn = itemView.findViewById(R.id.btnMore);
        }
    }
}
