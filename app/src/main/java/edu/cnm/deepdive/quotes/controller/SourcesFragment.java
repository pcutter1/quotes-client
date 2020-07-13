package edu.cnm.deepdive.quotes.controller;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.cnm.deepdive.quotes.R;
import edu.cnm.deepdive.quotes.model.Quote;
import edu.cnm.deepdive.quotes.model.User;
import edu.cnm.deepdive.quotes.view.ContentRecyclerAdapter;
import edu.cnm.deepdive.quotes.view.ContextMenuRecyclerView;
import edu.cnm.deepdive.quotes.viewmodel.MainViewModel;
import edu.cnm.deepdive.quotes.viewmodel.UserViewModel;

public class SourcesFragment extends Fragment {

  private RecyclerView contentList;
  private MainViewModel viewModel;
  private User user;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_sources, container, false);
    setupUI(root);
    return root;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupViewModel();
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
      @Nullable ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    ContextMenuRecyclerView.ContextMenuInfo info =
        (ContextMenuRecyclerView.ContextMenuInfo) menuInfo;
    Quote quote = (Quote) info.getView().getTag();
    if (quote.isEditableBy(user)) {
      getActivity().getMenuInflater().inflate(R.menu.quote_context, menu);
      menu.findItem(R.id.edit_quote).setOnMenuItemClickListener(item -> {
        QuoteEditFragment.createAndShow(getChildFragmentManager(), quote.getId());
        return true;
      });
      menu.findItem(R.id.delete_quote).setOnMenuItemClickListener(item -> {
        viewModel.remove(quote);
        return true;
      });
    }
  }

  private void setupUI(View root) {
    contentList = root.findViewById(R.id.content_list);
    registerForContextMenu(contentList);
    FloatingActionButton addQuote = root.findViewById(R.id.add_quote);
    addQuote.setOnClickListener((v) ->
        QuoteEditFragment.createAndShow(getChildFragmentManager(), null));
  }

  private void setupViewModel() {
    ViewModelProvider provider = new ViewModelProvider(getActivity());
    viewModel = provider.get(MainViewModel.class);
    viewModel.getContents().observe(getViewLifecycleOwner(), (contents) -> {
      ContentRecyclerAdapter adapter =
          new ContentRecyclerAdapter(getContext(), contents, (pos, quote) -> {
            if (quote.isEditableBy(user)) {
              QuoteEditFragment.createAndShow(getChildFragmentManager(), quote.getId());
            }
          });
      contentList.setAdapter(adapter);
    });
    UserViewModel userViewModel = provider.get(UserViewModel.class);
    userViewModel.getUser().observe(getViewLifecycleOwner(), (user) -> this.user = user);
  }

}