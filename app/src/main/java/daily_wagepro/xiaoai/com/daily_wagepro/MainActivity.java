package daily_wagepro.xiaoai.com.daily_wagepro;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hxh.component.basicannotation.annotation.ApiServices;

@ApiServices
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
