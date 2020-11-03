package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.agnekdev.bibleunan.BooksActivity;
import com.agnekdev.bibleunan.ChaptersActivity;
import com.agnekdev.bibleunan.R;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksHolder>{
    List<String> booksList;
    Context context;

    public BooksAdapter(Context context,List<String> booksList){
        this.booksList =booksList;
        this.context = context;
    }

    @NonNull
    @Override
    public BooksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book,parent,false);
        return new BooksHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksHolder holder, int position) {
        final String book = booksList.get(position);
        holder.mBookName.setText(book);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChaptersActivity.class);
                intent.putExtra("testament", BooksActivity.getTestament());
                intent.putExtra("book",book);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

    class BooksHolder extends RecyclerView.ViewHolder {
        TextView mBookName;
        CardView mCardView;
        public BooksHolder(@NonNull View itemView) {
            super(itemView);
            mBookName = itemView.findViewById(R.id.item_book_name);
            mCardView = itemView.findViewById(R.id.item_books_cv);
            //btnBook = itemView.findViewById(R.id.btn_bible_book);
        }
    }
}
