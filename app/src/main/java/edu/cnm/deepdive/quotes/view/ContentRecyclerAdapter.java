package edu.cnm.deepdive.quotes.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import edu.cnm.deepdive.quotes.R;
import edu.cnm.deepdive.quotes.model.Content;
import edu.cnm.deepdive.quotes.model.Quote;
import edu.cnm.deepdive.quotes.model.Source;
import edu.cnm.deepdive.quotes.view.ContentRecyclerAdapter.ContentHolder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentRecyclerAdapter extends RecyclerView.Adapter<ContentHolder> {

  private final Context context;
  private final List<Content> contents;
  private final OnQuoteClickListener listener;
  // Map of Content subclasses to Integer layout resource IDs.
  private final Map<Class<? extends Content>, Integer> layouts;
  // Map of Integer layout resource IDs to ContentHolder subclasses.
  private final Map<Integer, Class<? extends ContentHolder>> holders;

  @SuppressLint("UseSparseArrays")
  public ContentRecyclerAdapter(Context context, List<Content> contents,
      OnQuoteClickListener listener) {
    this.context = context;
    this.contents = contents;
    this.listener = listener;
    layouts = new HashMap<>();
    holders = new HashMap<>();
    // Populate map of Content subclasses to corresponding layout resource IDs.
    layouts.put(Source.class, R.layout.item_content_source);
    layouts.put(Quote.class, R.layout.item_content_quote);
    // Populate map of layout resource IDs to corresponding ContentHolder subclasses.
    holders.put(R.layout.item_content_source, SourceHolder.class);
    holders.put(R.layout.item_content_quote, QuoteHolder.class);
  }

  @Override
  public int getItemViewType(int position) {
    // Retrieve and return layout resource ID corresponding to Content subclass of item at position.
    return layouts.get(contents.get(position).getClass());
  }

  @NonNull
  @Override
  public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    try {
      Class<? extends ContentHolder> holderClass = holders.get(viewType);
      // Non-static inner class constructors have implicit 1st parameter of the enclosing class type.
      // Use getDeclaredConstructor to return public and non-public constructors.
      Constructor<? extends ContentHolder> constructor =
          holderClass.getDeclaredConstructor(ContentRecyclerAdapter.class, View.class);
      // Inflate layout with resource ID specified by viewType.
      View root = LayoutInflater.from(context).inflate(viewType, parent, false);
      return constructor.newInstance(this, root);
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InstantiationException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull ContentHolder holder, int position) {
    holder.bind(contents.get(position));
  }

  @Override
  public int getItemCount() {
    return contents.size();
  }

  @FunctionalInterface
  public interface OnQuoteClickListener {

    void onQuoteClick(int position, Quote quote);

  }

  abstract static class ContentHolder extends ViewHolder {

    public ContentHolder(@NonNull View itemView) {
      super(itemView);
    }

    public abstract void bind(Content content);

  }

  private class SourceHolder extends ContentHolder {

    private final TextView sourceName;

    public SourceHolder(@NonNull View itemView) {
      super(itemView);
      sourceName = itemView.findViewById(R.id.source_name);
    }

    @Override
    public void bind(Content content) {
      Source source = (Source) content;
      String name = source.getName();
      sourceName.setText((name != null) ? name : context.getString(R.string.unattributed_source));
    }

  }

  private class QuoteHolder extends ContentHolder {

    private final TextView quoteText;

    public QuoteHolder(@NonNull View itemView) {
      super(itemView);
      quoteText = itemView.findViewById(R.id.quote_text);
    }

    @Override
    public void bind(Content content) {
      Quote quote = (Quote) content;
      quoteText.setText(context.getString(R.string.quote_format, quote.getText()));
      itemView.setOnClickListener((v) -> listener.onQuoteClick(getAdapterPosition(), quote));
      itemView.setTag(quote);
    }

  }

}
