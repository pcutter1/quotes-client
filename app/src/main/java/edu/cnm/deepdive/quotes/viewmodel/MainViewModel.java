package edu.cnm.deepdive.quotes.viewmodel;

import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.quotes.model.Content;
import edu.cnm.deepdive.quotes.model.Quote;
import edu.cnm.deepdive.quotes.model.Source;
import edu.cnm.deepdive.quotes.service.GoogleSignInService;
import edu.cnm.deepdive.quotes.service.QuoteRepository;
import io.reactivex.disposables.CompositeDisposable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainViewModel extends ViewModel implements LifecycleObserver {

  private final MutableLiveData<Quote> random;
  private final MutableLiveData<Quote> daily;
  private final MutableLiveData<List<Quote>> quotes;
  private final MutableLiveData<List<Source>> sources;
  private final MutableLiveData<List<Content>> contents;
  private final MutableLiveData<Quote> quote;
  private final MutableLiveData<Throwable> throwable;
  private final QuoteRepository repository;
  private final CompositeDisposable pending;
  private final Map<Long, Quote> quoteMap;

  public MainViewModel() {
    repository = QuoteRepository.getInstance();
    pending = new CompositeDisposable();
    random = new MutableLiveData<>();
    daily = new MutableLiveData<>();
    quotes = new MutableLiveData<>();
    sources = new MutableLiveData<>();
    quote = new MutableLiveData<>();
    contents = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    quoteMap = new HashMap<>();
    refreshDaily();
    refreshQuotes();
    refreshSources();
    refreshContents();
  }

  public LiveData<Quote> getRandom() {
    return random;
  }

  public LiveData<Quote> getDaily() {
    return daily;
  }

  public LiveData<List<Quote>> getQuotes() {
    return quotes;
  }

  public LiveData<List<Source>> getSources() {
    return sources;
  }

  public LiveData<Quote> getQuote() {
    return quote;
  }

  public LiveData<List<Content>> getContents() {
    return contents;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void refreshRandom() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getRandom(account.getIdToken())
                  .subscribe(
                      random::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshDaily() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getQuoteOfToday(account.getIdToken())
                  .subscribe(
                      daily::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshQuotes() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllQuotes(account.getIdToken())
                  .subscribe(
                      (quotes) -> {
                        this.quotes.postValue(quotes);
                        for (Quote quote : quotes) {
                          quoteMap.put(quote.getId(), quote);
                        }
                      },
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshSources() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllSources(account.getIdToken(), false, true)
                  .subscribe(
                      sources::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void refreshContents() {
    throwable.postValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.getAllContent(account.getIdToken())
                  .subscribe(
                      contents::postValue,
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void save(Quote quote) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.save(account.getIdToken(), quote)
                  .subscribe(
                      () -> {
                        this.quote.postValue(null);
                        refreshDaily();
                        refreshContents();
                        refreshQuotes();
                        refreshSources();
                      },
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void remove(Quote quote) {
    throwable.setValue(null);
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          pending.add(
              repository.remove(account.getIdToken(), quote)
                  .subscribe(
                      () -> {
                        this.quote.postValue(null);
                        refreshDaily();
                        refreshContents();
                        refreshQuotes();
                      },
                      throwable::postValue
                  )
          );
        })
        .addOnFailureListener(throwable::postValue);
  }

  public void setQuoteId(UUID id) {
    quote.setValue(quoteMap.get(id));
  }

  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }

}