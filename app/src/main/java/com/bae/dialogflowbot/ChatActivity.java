package com.bae.dialogflowbot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bae.dialogflowbot.adapters.ChatAdapter;
import com.bae.dialogflowbot.helpers.SendMessageInBg;
import com.bae.dialogflowbot.interfaces.BotReply;
import com.bae.dialogflowbot.models.Message;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements BotReply, View.OnClickListener {

    RecyclerView chatView;
    ChatAdapter chatAdapter;
    List<Message> messageList = new ArrayList<>();
    EditText editMessage;
    ImageView btnSend;
    private String message = "";
    private String topicFlaot = "";
    private Animation animShow, animHide;
    private Button temp1, temp2, temp3, temp4, temp5, temp6, temp7, temp8, temp9, temp10;

    //dialogFlow
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "mainactivity";
    private RelativeLayout floatingTextLayouts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initAnimation();
        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        getSupportActionBar().hide();
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
        floatingTextLayouts = findViewById(R.id.floating_text);


        insitializeBtn();
        chatAdapter = new ChatAdapter(messageList, this);
        chatView.setAdapter(chatAdapter);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = editMessage.getText().toString();
                if (!message.isEmpty()) {
                    messageList.add(new Message(message, false));
                    editMessage.setText("");
                    sendMessageToBot(message);
                    Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                    Objects.requireNonNull(chatView.getLayoutManager())
                            .scrollToPosition(messageList.size() - 1);
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUpBot();


    }

    private void setUpBot() {
        try {
            InputStream stream = this.getResources().openRawResource(R.raw.credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    private void sendMessageToBot(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
        responseFloat(message);
    }

    @Override
    public void callback(DetectIntentResponse returnResponse) {
        if (returnResponse != null) {
            String botReply = returnResponse.getQueryResult().getFulfillmentText();
            if (!botReply.isEmpty()) {
                messageList.add(new Message(botReply, true));
                chatAdapter.notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
            } else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Toast.makeText(this, "failed to connect!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        String msgfromOption = null;
        switch (v.getId()) {
            case R.id.temp1:

                msgfromOption = temp1.getText().toString();
                if (msgfromOption.equalsIgnoreCase("Again?")) {
                    msgfromOption = "English Grammar Tips?";
                }
                break;
            case R.id.temp2:
                msgfromOption = temp2.getText().toString();
                if (msgfromOption.equalsIgnoreCase("Again?")) {
                    msgfromOption = "English Writing Tips?";
                }
                break;
            case R.id.temp3:
                msgfromOption = temp3.getText().toString();
                if (msgfromOption.equalsIgnoreCase("Again?")){
                    msgfromOption="No I don't";
                }
                break;
            case R.id.temp4:
                msgfromOption = temp4.getText().toString();
                if (msgfromOption.equalsIgnoreCase("Again?")){
                    msgfromOption = "English Reading Tips?";
                }
                break;
            case R.id.temp5:
                msgfromOption = temp5.getText().toString();
                if (msgfromOption.equalsIgnoreCase("Again?")){
                    msgfromOption = "English Speaking Tips?";
                }
                break;
            case R.id.temp6:
                msgfromOption = temp6.getText().toString();
                if (msgfromOption.equalsIgnoreCase("Again?")){
                    msgfromOption = "English Listening Tips?";
                }
                break;
            case R.id.temp7:
                msgfromOption = temp7.getText().toString();
                break;
            case R.id.temp8:
                msgfromOption = temp8.getText().toString();
                break;
            case R.id.temp9:
                msgfromOption = temp9.getText().toString();
                break;
            case R.id.temp10:
                msgfromOption = temp10.getText().toString();
                if (msgfromOption.equalsIgnoreCase("Restart?")) {
                    msgfromOption = "Start?";
                }
                break;
            default:
                break;
        }
        message = msgfromOption;
        messageList.add(new Message(message, false));
        editMessage.setText("");
        sendMessageToBot(message);
        Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
        Objects.requireNonNull(chatView.getLayoutManager())
                .scrollToPosition(messageList.size() - 1);
        setUpBot();
//        floatingTextLayouts.animate()
//                .translationY(floatingTextLayouts.getHeight())
//                .alpha(0.0f)
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        floatingTextLayouts.setVisibility(View.GONE);
//                    }
//                });
        responseFloat(message);
//        floatingTextLayouts.setVisibility(View.GONE);
////        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void insitializeBtn() {
        temp1 = findViewById(R.id.temp1);
        temp2 = findViewById(R.id.temp2);
        temp3 = findViewById(R.id.temp3);
        temp4 = findViewById(R.id.temp4);
        temp5 = findViewById(R.id.temp5);
        temp6 = findViewById(R.id.temp6);
        temp7 = findViewById(R.id.temp7);
        temp8 = findViewById(R.id.temp8);
        temp9 = findViewById(R.id.temp9);
        temp10 = findViewById(R.id.temp10);

        temp1.setOnClickListener(this::onClick);
        temp2.setOnClickListener(this::onClick);
        temp3.setOnClickListener(this::onClick);
        temp4.setOnClickListener(this::onClick);
        temp5.setOnClickListener(this::onClick);
        temp6.setOnClickListener(this::onClick);
        temp7.setOnClickListener(this::onClick);
        temp8.setOnClickListener(this::onClick);
        temp9.setOnClickListener(this::onClick);
        temp10.setOnClickListener(this::onClick);

    }

    @Override
    protected void onStart() {
        super.onStart();
        floatingTextLayouts.setVisibility(View.VISIBLE);
//        floatingTextLayouts.animate()
//                .translationY(floatingTextLayouts.getHeight())
//                .alpha(0.0f)
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        floatingTextLayouts.setVisibility(View.VISIBLE);
//                    }
//                });
    }

    private void responseFloat(String mymessage) {

        floatingTextLayouts.setVisibility(View.VISIBLE);
        hideAllTemp();
//        String str1 = temp1.getText().toString();
        if (mymessage.equalsIgnoreCase("hello") || mymessage.equalsIgnoreCase("hlw") || mymessage.equalsIgnoreCase("hey")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            hideAllTemp();
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Start?");
            temp2.setVisibility(View.GONE);
            temp3.setVisibility(View.GONE);
            temp10.setVisibility(View.GONE);
        }
        else {
            temp10.setVisibility(View.VISIBLE);
            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Start?")) {
            hideAllTemp();
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp2.setVisibility(View.VISIBLE);
            temp1.setText("Ask Query About English");
            temp2.setText("Make English Conversation");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Ask Query About English")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp2.setVisibility(View.VISIBLE);
            temp3.setVisibility(View.VISIBLE);
            temp4.setVisibility(View.VISIBLE);
            temp5.setVisibility(View.VISIBLE);
            temp1.setText("English Grammar Tips?");
            temp2.setText("English Writing Tips?");
            temp3.setText("English Reading Tips?");
            temp4.setText("English Speaking Tips?");
            temp5.setText("English Listening Tips?");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("English Writing Tips?")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
//            temp1.setVisibility(View.VISIBLE);
            temp2.setVisibility(View.VISIBLE);
//            temp1.setText("Again");
            temp2.setText("Again?");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("English Grammar Tips?")) {
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Again?");
        }
        if (mymessage.equalsIgnoreCase("English Reading Tips?")) {
            temp4.setVisibility(View.VISIBLE);
            temp4.setText("Again?");
        }
        if (mymessage.equalsIgnoreCase("English Speaking Tips?")) {
            temp5.setVisibility(View.VISIBLE);
            temp5.setText("Again?");
        }
        if (mymessage.equalsIgnoreCase("English Listening Tips?")) {
            temp6.setVisibility(View.VISIBLE);
            temp6.setText("Again?");
        }
        if (mymessage.equalsIgnoreCase("Make English Conversation")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Holiday");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("Food");
            temp3.setVisibility(View.VISIBLE);
            temp3.setText("Travel");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Food")) {
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Healthy");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("Junk");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Healthy")) {
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Yeah I Know");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("No I don't");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("No I don't")) {
            temp3.setVisibility(View.VISIBLE);
            temp3.setText("Again?");
        }
        if (mymessage.equalsIgnoreCase("Travel")) {
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Yeah I went");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("I don't");
        }
        if (mymessage.equalsIgnoreCase("Yeah I went")) {
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Family");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("Friends or Alone");
        }
        if (mymessage.equalsIgnoreCase("Family")||mymessage.equalsIgnoreCase("Friends or Alone")||mymessage.equalsIgnoreCase("No I don't")) {
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Yap");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("Nop");
        }
        if (mymessage.equalsIgnoreCase("Holiday")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Religious Holidays");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("National Holidays");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("National Holidays")) {
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Religious Holidays")||mymessage.equalsIgnoreCase("National Holidays")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Yes");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("No");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Yes")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("More");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("Less");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Less")) {
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("More")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("Yap");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("Nop");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Yap")) {
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("Nop")) {
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage.equalsIgnoreCase("No")) {
//            Toast.makeText(getApplicationContext(), mymessage, Toast.LENGTH_LONG).show();
            temp1.setVisibility(View.VISIBLE);
            temp1.setText("More");
            temp2.setVisibility(View.VISIBLE);
            temp2.setText("Less");
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }
        if (mymessage == "" || mymessage != "") {
//            temp10.setVisibility(View.VISIBLE);
//            temp10.setText("Restart?");
        }


        chatView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    floatingTextLayouts.setVisibility(View.VISIBLE);
                    floatingTextLayouts.setAnimation(animShow);
                }
                if (dy < 0) {
                    floatingTextLayouts.setVisibility(View.GONE);
                    floatingTextLayouts.setAnimation(animHide);
                }
            }
        });
    }

    private void hideAllTemp() {
        temp1.setVisibility(View.GONE);
        temp2.setVisibility(View.GONE);
        temp3.setVisibility(View.GONE);
        temp4.setVisibility(View.GONE);
        temp5.setVisibility(View.GONE);
        temp6.setVisibility(View.GONE);
        temp7.setVisibility(View.GONE);
        temp8.setVisibility(View.GONE);
        temp9.setVisibility(View.GONE);
//        temp10.setVisibility(View.GONE);
    }

    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);
    }
}
