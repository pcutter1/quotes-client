package edu.cnm.deepdive.quotes.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.quotes.model.User;
import edu.cnm.deepdive.quotes.service.GoogleSignInService;
import edu.cnm.deepdive.quotes.service.QuoteRepository;

public class UserViewModel extends AndroidViewModel {

  private final QuoteRepository repository;
  private final MutableLiveData<User> user;

  public UserViewModel(@NonNull Application application) {
    super(application);
    repository = QuoteRepository.getInstance();
    user = new MutableLiveData<>();
    GoogleSignInService.getInstance().refresh()
        .addOnSuccessListener((account) -> {
          repository.getMe(account.getIdToken())
              .subscribe(user::postValue);
        });
  }

  public LiveData<User> getUser() {
    return user;
  }


}
