package top.cyixlq.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import top.cyixlq.annotion.RouterPath;
import top.cyixlq.crouter.CRouter;


@RouterPath("login/LoginActivity")
public class LoginActivity extends AppCompatActivity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tvResult = findViewById(R.id.tvResult);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name", "未知");
            final String text = tvResult.getText().toString() + name;
            tvResult.setText(text);
        }
    }

    public void setResult(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("result", "你好," + name);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void toSecondActivity(View view) {
        CRouter.get().open(this, "app/SecondActivity");
    }
}
