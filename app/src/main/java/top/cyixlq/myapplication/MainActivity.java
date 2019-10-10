package top.cyixlq.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import top.cyixlq.annotion.RouterPath;
import top.cyixlq.crouter.CRouter;

@RouterPath("app/MainActivity")
public class MainActivity extends AppCompatActivity {

    private TextView tvInfo;
    private Fragment fragment;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = findViewById(R.id.tvInfo);
        btnLogin = findViewById(R.id.btnLogin);
        fragment = (Fragment) CRouter.get().open("login/BlankFragment");
    }

    public void toLogin(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("name", "cyixlq");
        CRouter.get().openForResult(this, "login/LoginActivity", bundle, 666);
    }

    public void addFragment(View view) {
        if (fragment == null) return;
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, fragment).commitNow();
        }
    }

    public void notFound(View view) {
        CRouter.get().open(this, "app/NotFoundActivity");
    }

    public void toSecondActivity(View view) {
        CRouter.get().open(this, "app/SecondActivity");
    }

    @SuppressLint("SetTextI18n")
    public void login(View view) {
        if (MyApplication.getMyApplication().isLogin) {
            MyApplication.getMyApplication().isLogin = false;
            btnLogin.setText("Login");
        } else {
            MyApplication.getMyApplication().isLogin = true;
            btnLogin.setText("Log Out");
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment == null) return;
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
