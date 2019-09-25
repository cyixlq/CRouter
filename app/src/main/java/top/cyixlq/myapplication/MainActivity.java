package top.cyixlq.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import top.cyixlq.annotion.RouterPath;
import top.cyixlq.crouter.CRouter;
import top.cyixlq.login.BlankFragment;

@RouterPath("main")
public class MainActivity extends AppCompatActivity {

    private TextView tvInfo;
    private BlankFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = findViewById(R.id.tvInfo);
        fragment = (BlankFragment) CRouter.get().open("blankFragment");
    }

    public void toLogin(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("name", "cyixlq");
        CRouter.get().openFroResult(this, "login", bundle, 666);
    }

    public void addFragment(View view) {
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, fragment).commitNow();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    final String result = tvInfo.getText().toString() + extras.getString("result", "没有结果");
                    tvInfo.setText(result);
                }
            }
        }
    }
}
