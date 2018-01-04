package daily_wagepro.xiaoai.com.daily_wagepro;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hxh.component.basicore.ui.mrecycleview.EmptyViewConfig;
import com.hxh.component.basicore.ui.mrecycleview.MDataSource;
import com.hxh.component.basicore.ui.mrecycleview.MRecycleView;
import com.hxh.component.basicore.util.aspj.annotation.PermissionCheck;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();
    }

    private void initdata() {
        MRecycleView recycleview = (MRecycleView) findViewById(R.id.recycleview);

        final List<String> str  = new ArrayList<>();
        recycleview.setAdapter(new Adap1(this))
                .getDataRepositoryBuilder()
                .setNoDataStateWhenRequest(new EmptyViewConfig.Build()
                        .enableRefreshWhenClickOtherPlace()
                        .build(), new MDataSource.NoDataCallback() {
                    @Override
                    public void onNoData() {
                        str.add("12312");
                    }
                })
                .fetch(str);
    }

    @PermissionCheck(permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION
    })
    @Override
    protected void onResume() {
        super.onResume();
    }
}
