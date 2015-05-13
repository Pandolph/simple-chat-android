package com.leancloud.im.guide;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
  public static final String KEY_CLIENT_ID = "client_id";
  EditText clientIdEditText;
  SharedPreferences preferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    if (!TextUtils.isEmpty(getClientIdFromPre())) {
      ConversationActivity.startActivity(this, getClientIdFromPre());
      finish();
    }

    setContentView(R.layout.activity_main);
    clientIdEditText = (EditText) findViewById(R.id.client_id);

    findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String name = clientIdEditText.getText().toString();
        if (!TextUtils.isEmpty(name)) {
          setClientIdToPre(name);
          ConversationActivity
              .startActivity(MainActivity.this, name);
        }
      }
    });

  }

  private String getClientIdFromPre() {
    return preferences.getString(KEY_CLIENT_ID, "");
  }

  private void setClientIdToPre(String id) {
    preferences.edit().putString(KEY_CLIENT_ID, id).commit();
  }
}
