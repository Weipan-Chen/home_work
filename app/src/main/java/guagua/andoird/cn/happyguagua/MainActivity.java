package guagua.andoird.cn.happyguagua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import guagua.andoird.cn.happyguagua.guaguag.android.cn.happyguagua.view.GuaGuaKa;

public class MainActivity extends AppCompatActivity {

    GuaGuaKa mGuaGuaKa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGuaGuaKa = (GuaGuaKa)findViewById(R.id.guaguaka);
        mGuaGuaKa.setOnGuaGuaKaCompleteListener(new GuaGuaKa.OnGuaGuaKaCompleteListener(){

            public void complete() {
                Toast.makeText(getApplicationContext(),"刮完了",Toast.LENGTH_SHORT).show();
            }
        });

        mGuaGuaKa.setText("牛逼啊");
    }
}
