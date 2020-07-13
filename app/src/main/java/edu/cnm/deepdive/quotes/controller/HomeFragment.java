package edu.cnm.deepdive.quotes.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.cnm.deepdive.quotes.R;
import edu.cnm.deepdive.quotes.model.Quote;
import edu.cnm.deepdive.quotes.viewmodel.MainViewModel;

public class HomeFragment extends Fragment {

  private MainViewModel viewModel;
  private TextView quoteText;
  private TextView sourceName;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_home, container, false);
    setupView(root);
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //noinspection ConstantConditions
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    viewModel.getRandom().observe(getViewLifecycleOwner(), this::presentQuote);
    viewModel.getDaily().observe(getViewLifecycleOwner(), this::presentQuote);
  }

  private void setupView(View root) {
    quoteText = root.findViewById(R.id.quote_text);
    sourceName = root.findViewById(R.id.source_name);
    FloatingActionButton getRandom = root.findViewById(R.id.get_random);
    FloatingActionButton getToday = root.findViewById(R.id.get_today);
    getRandom.setOnClickListener((v) -> viewModel.refreshRandom());
    getToday.setOnClickListener((v) -> viewModel.refreshDaily());
  }

  private void presentQuote(Quote quote) {
    quoteText.setText(getString(R.string.quote_format, quote.getText()));
    sourceName.setText((quote.getSource() != null)
        ? getString(R.string.attribution_format, quote.getSource().getName())
        : getString(R.string.unattributed_source));
  }

}