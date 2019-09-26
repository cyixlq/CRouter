package top.cyixlq.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import top.cyixlq.annotion.RouterPath;

@RouterPath("second")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
