package com.example.jinxi.qq;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.menu_listview)
    ListView menuListview;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.main_listview)
    ListView mainListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //填充数据
        mainListview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
                Constant.NAMES));

        menuListview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,
                Constant.sCheeseStrings){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView= (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(30);
                return textView;
            }
        });
    }
}
