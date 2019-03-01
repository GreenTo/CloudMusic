package com.example.cloudmusic.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.coder.zzq.smartshow.toast.SmartToast;
import com.example.cloudmusic.R;
import com.example.cloudmusic.util.OKhttpUtil;
import com.example.cloudmusic.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {

    EditText usernameText;

    EditText passwordText;

    private String username;

    private String password;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    //private Account account;

    static final String TAG = "LoginActivity";

    private long id;

    private static long SERVE_ERROR = 1;

    private static long CLIENT_ERROR = 2;

    private static long LOGIN_SUCCESS = 3;

    private long status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.username_text);
        passwordText = findViewById(R.id.password_text);

        pref = getPreferences(MODE_PRIVATE);
        username = pref.getString("username", "");
        password = pref.getString("password", "");

        if (!username.isEmpty() && !password.isEmpty()){
            usernameText.setText(username);
            passwordText.setText(password);
        }

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();

                OKhttpUtil.loginRequest(username, password, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SmartToast.error("服务器发生错误，请稍后再试...");
                                status = SERVE_ERROR;
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody body = response.body();
                        byte[] bytes = body.bytes();
                        String responseData = new String(bytes);
                        if (!responseData.contains("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SmartToast.error("手机号码或密码错误！");
                                    status = CLIENT_ERROR;
                                }
                            });
                        }else {
                            editor = pref.edit();
                            editor.putString("username",username);
                            editor.putString("password",password);
                            editor.apply();
                            try {
                                JSONObject jsonObject = new JSONObject(responseData).getJSONObject("account");
                                id = jsonObject.getLong("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LogUtil.i(TAG,"UID:" + id);
                            status = LOGIN_SUCCESS;
                            if (status == LOGIN_SUCCESS) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        intent.putExtra("uid",id);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status = 0;
    }
}
