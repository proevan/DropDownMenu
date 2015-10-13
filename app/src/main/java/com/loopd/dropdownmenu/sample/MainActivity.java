package com.loopd.dropdownmenu.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopd.dropdownmenu.DropDownMenu;

public class MainActivity extends AppCompatActivity {

    private ImageView mDropDownButton;
    private com.loopd.dropdownmenu.DropDownMenu mDropDownMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDropDownMenu = (DropDownMenu) findViewById(R.id.drop_down_menu);
        mDropDownButton = (ImageView) findViewById(R.id.drop_down_btn);
        mDropDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropDownButton.setVisibility(View.INVISIBLE);
                mDropDownMenu.open();
            }
        });
        initDropDownMenu();
    }

    private void initDropDownMenu() {
        mDropDownMenu.setCloseButtonDrawable(R.drawable.drop_down_btn);
        mDropDownMenu.addMenuButton("button1", R.drawable.drop_down_btn);
        mDropDownMenu.addMenuButton("button2", R.drawable.drop_down_btn);
        mDropDownMenu.addMenuButton("button3", R.drawable.drop_down_btn);
        mDropDownMenu.setOnMenuCollapsedListener(new DropDownMenu.OnMenuCollapsedListener() {
            @Override
            public void onCollapsed() {
                mDropDownButton.setVisibility(View.VISIBLE);
            }
        });
        mDropDownMenu.setOnMenuButtonClickListener(new DropDownMenu.OnMenuButtonClickListener() {
            @Override
            public void onMenuButtonClick(int position) {
                Toast.makeText(MainActivity.this, "click: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
