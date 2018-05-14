package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.myapp.Model.DetailBean;
import com.example.myapp.Model.SimpleCarBean;
import com.example.myapp.R;
import com.example.myapp.adapter.SimpleAdapter;
import com.example.myapp.utils.ToastUtils;

import java.util.ArrayList;


/**
 * Created by asus on 2016/5/1.
 */
public class CarinfoActivity extends Activity implements View.OnClickListener{
    public  final static String SER_KEY = "cn.caiwb.intent.ser";
    private ListView infoList;
    private Button backBtn;
    private Button addBtn;
    private ArrayList<SimpleCarBean> list;
    private ArrayList<DetailBean> detailBeansList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carinfoactivity);


        //获取上一个ACTIVITY传来的list
        detailBeansList = (ArrayList<DetailBean>) getIntent().getSerializableExtra("carinfoList");

        list = new ArrayList<SimpleCarBean>();
        InitList();
        Log.e("CARINFO2","SimpleInfo " + list.size()+" ");




        infoList = (ListView) findViewById(R.id.id_list_carlist);
        backBtn = (Button) findViewById(R.id.id_btn_carinfo_back);
        addBtn = (Button) findViewById(R.id.id_btn_carinfo_add);



        //配置ListView
        SimpleAdapter adapter = new SimpleAdapter(CarinfoActivity.this, R.layout.carinfos_item,list);
        backBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        infoList.setAdapter(adapter);
        infoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetailBean detailInfo = detailBeansList.get(position);
                Intent intent = new Intent(CarinfoActivity.this,DetailCarInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("detailInfo",detailInfo);
                intent.putExtras(bundle);
                startActivity(intent);
                //ToastUtils.makeShortText("点击啦！",CarinfoActivity.this);

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_btn_carinfo_add:
                Intent intent1 =new Intent(CarinfoActivity.this,AddnewActivity.class);
                intent1.putExtra("index",3);
                startActivity(intent1);
                break;

            case R.id.id_btn_carinfo_back:
                Intent intent = new Intent(CarinfoActivity.this,MainActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void InitList(){

        Log.e("CARINFO2","detilBeanListSize " + detailBeansList.size());

        SimpleCarBean simpleCarBean = null;

        for (int i = 0; i < detailBeansList.size(); i ++){
            DetailBean temp = detailBeansList.get(i);
            String brand = temp.getBrandName();
            String model = temp.getModel();
            String license = temp.getLicenseNum();

            Log.e("CARINFO2",brand + " " + model + " " + license);

            simpleCarBean = new SimpleCarBean(brand,model,license);
            list.add(simpleCarBean);
        }
    }
}
