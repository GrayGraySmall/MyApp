package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Intent;
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
import com.example.myapp.utils.RegexUtils;
import com.example.myapp.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2016/5/1.
 */
public class RegisterActivity extends Activity implements  View.OnClickListener{
    private static final int CONNECT_SUCCESS = 1;
    private static final int ERROR = 0;

    private EditText usernameText;
    private EditText passwordText;
    private EditText confirmText;
    private EditText nameText;
    private EditText phoneText;
    private Button registerBtn;
    private RequestQueue mQueue = null;

    private String username;
    private String password;
    private String confirm;
    private String name;
    private String tel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mQueue = Volley.newRequestQueue(this);
        InitView();

        registerBtn.setOnClickListener(this);
    }
    private void InitView(){
        usernameText = (EditText) findViewById(R.id.id_text_reg_username);
        passwordText = (EditText) findViewById(R.id.id_text_reg_password);
        confirmText = (EditText) findViewById(R.id.id_text_reg_confirm);
        nameText = (EditText) findViewById(R.id.id_text_reg_name);
        phoneText = (EditText) findViewById(R.id.id_text_reg_phone);
        registerBtn = (Button) findViewById(R.id.id_btn_reg_register);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_btn_reg_register) {
            username = usernameText.getText().toString();
            password = passwordText.getText().toString();
            tel = phoneText.getText().toString();
            name = nameText.getText().toString();
            confirm = confirmText.getText().toString();
            System.out.println(username + " " + password + " " + confirm + " " + tel + " " + name);
            register();
        }
    }
    public void register(){
        String url = "http://192.168.191.1:8080/CarANet/servlet/RegisterServlet";
        Map<String,String> info = new HashMap<String,String>();
        info.put("username",username);
        info.put("password",password);
        info.put("name",name);
        info.put("tel",tel);

       if (check(username, password, confirm, tel, name)){

            JsonRequestWithAuth<Result> requset = new JsonRequestWithAuth<Result>(url, Result.class, new Response.Listener<Result>()
           {
               @Override
               public void onResponse(Result result)
               {
                   int res = result.getResult();
                   Log.e("REGISTER",res+"");
                   if (res == CONNECT_SUCCESS){
                       ToastUtils.showShort(RegisterActivity.this,"注册成功");
                       Intent intent  = new Intent(RegisterActivity.this,LoginActivity.class);
                       startActivity(intent);
                   }
                   if (res == ERROR){
                   ToastUtils.showShort(RegisterActivity.this,"用户名已被注册");
               }
               }
           }, info, new Response.ErrorListener(){
               @Override
               public void onErrorResponse(VolleyError error) {
                   Log.e("TAG", error.getMessage(), error);
               }
           });
           mQueue.add(requset);

       }



    }
    public boolean check(String username,String password,String confirm,String phonenum,String name ){
        boolean flag = true;
        if (TextUtils.isEmpty(username)){
            ToastUtils.showShort(this, "用户名不能为空");
            flag = false;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtils.showShort(this,"密码不能为空");
            flag = false;
        }
        if (TextUtils.isEmpty(name)){
            ToastUtils.showShort(this,"昵称不能为空");
            flag = false;
        }
        if (TextUtils.isEmpty(phonenum)){
            ToastUtils.showShort(this,"请输入手机号");
            flag = false;
        }
        if (!RegexUtils.checkMobile(phonenum)){
            ToastUtils.showShort(this,"请输入正确手机号");
            flag = false;
        }
        if (!password.equals(confirm)) {
            ToastUtils.showShort(this, "两次密码不一致");
            flag = false;
        }

        return flag;
    }
}
