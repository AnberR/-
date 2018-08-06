package a5i5j.com.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        netView netView1 = findViewById(R.id.net);
        netView1.setLayerCount(5);//设置层数
        netView1.setCount(5);//设置边数
        netView1.setTitle(2);//设置指数值已经小圆点
    }
}
