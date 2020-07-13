package edu.cnm.deepdive.quotes;

import android.app.Application;
import edu.cnm.deepdive.quotes.service.GoogleSignInService;

public class QuotesClientApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    GoogleSignInService.setContext(this);
  }

}
