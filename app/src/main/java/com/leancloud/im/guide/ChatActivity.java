package com.leancloud.im.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

/**
 * Created by zhangxiaobo on 15/4/16.
 */
public class ChatActivity extends ActionBarActivity {
  private static final String EXTRA_CONVERSATION_ID = "conversation_id";
  private static final String TAG = ChatActivity.class.getSimpleName();

  private AVIMConversation conversation;
  MessageAdapter adapter;

  private EditText messageEditText;
  private ListView listView;
  private View sendButton;
  private ChatHandler handler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    // init component
    listView = (ListView) findViewById(R.id.listview);
    sendButton = findViewById(R.id.send);
    messageEditText = (EditText) findViewById(R.id.message);
    adapter = new MessageAdapter(ChatActivity.this,Application.getClientIdFromPre());
    listView.setAdapter(adapter);

    // get argument
    final String conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
    Log.d(TAG, "会话 id: " + conversationId);


    // register callback
    handler = new ChatHandler(adapter);
    AVIMMessageManager.registerMessageHandler(AVIMTextMessage.class, handler);


    conversation=Application.getIMClient().getConversation(conversationId);

    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (null == conversation) {
            return;
        }
        final AVIMTextMessage message = new AVIMTextMessage();
        message.setText(messageEditText.getText().toString());
        conversation.sendMessage(message, new AVIMConversationCallback() {
          @Override
          public void done(AVException exception) {
            if (null != exception) {
              exception.printStackTrace();
            } else {
              messageEditText.setText(null);
              Log.d(TAG, "发送成功，msgId=" + message.getMessageId());
            }
          }
        });
        adapter.addMessage(message);
        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
      }
    });

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    AVIMMessageManager.unregisterMessageHandler(AVIMTextMessage.class, handler);
  }

  public static void startActivity(Context context, String conversationId) {
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra(EXTRA_CONVERSATION_ID, conversationId);
    context.startActivity(intent);
  }

  public class ChatHandler extends AVIMTypedMessageHandler<AVIMTextMessage> {
    private MessageAdapter adapter;

    public ChatHandler(MessageAdapter adapter) {
      this.adapter = adapter;
    }

    @Override
    public void onMessage(AVIMTextMessage message, AVIMConversation conversation,
        AVIMClient client) {
      adapter.addMessage(message);
    }
  }
}
