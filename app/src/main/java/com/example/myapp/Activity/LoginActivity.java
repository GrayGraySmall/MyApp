package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myapp.Model.Result;
import com.example.myapp.R;
import com.example.myapp.URL.JsonRequestWithAuth;
import com.example.myapp.utils.DialogUtils;
import com.example.myapp.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by asus on 2016/4/12.
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    private static final int CONNECT_SUCCESS = 1;
    private static final int ERROR = 0;
    private Button loginButton;
    private Button registerButton;
    private EditText usernameText;
    private EditText passwordText;
    private String username;
    private String password;
    private RequestQueue mQueue = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginButton = (Button) findViewById(R.id.id_btn_login);
        registerButton = (Button) findViewById(R.id.id_btn_register);
        usernameText = (EditText) findViewById(R.id.id_text_username);
        passwordText = (EditText) findViewById(R.id.id_text_password);

        mQueue = Volley.newRequestQueue(this);

        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_btn_login:
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();
                if (TextUtils.isEmpty(username)){
                    //ToastUtils.showShort(LoginActivity.this,"请输入账号");
                    DialogUtils.Dialog_true(LoginActivity.this,"登陆","请输入账号");
                }else if (TextUtils.isEmpty(password)){
                    ToastUtils.showShort(LoginActivity.this,"请输入密码");
                }else {
                    Log.e("LOGIN", username + " " + password);
                    login(username, password);
                }
                break;
            case R.id.id_btn_register:
                Intent intent2 = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent2);
                break;

        }
    }

    private void login(String username,String password) {
        Map<String,String > infos = new HashMap<String,String>();
        infos.put("username",username);
        infos.put("password",password);
        String url = "http://192.168.191.1:8080/CarANet/servlet/LoginServlet";
        JsonRequestWithAuth<Result> userRequest = new JsonRequestWithAuth<Result>(url, Result.class, new Response.Listener<Result>()
        {
            @Override
            public void onResponse(Result response)
            {
                int res = response.getResult();
                Log.e("LOGIN",res+"");
                if (res == CONNECT_SUCCESS){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }else if (res == ERROR){
                    ToastUtils.showShort(LoginActivity.this,"账号或密码错误");
                }

            }
        }, infos, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(userRequest);
    }
    /**
     * 添加进sharedPreferance
     */
    public void addTosp(String username){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("username",username);
        editor.commit();
    }



}
