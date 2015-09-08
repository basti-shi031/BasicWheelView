package basti.coryphaei.com.wheelview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private WheelView wheelView;
    private TextView change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wheelView = (WheelView) findViewById(R.id.wheelview);
        change = (TextView) findViewById(R.id.text);

        wheelView.setData(getData());
        wheelView.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                Log.i(String.valueOf(id),text);
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
    }

    private ArrayList<String> getData1() {
        ArrayList<String> mDatas = new ArrayList<>();
        for (int i = 0;i<100;i++){
            mDatas.add(""+i);
        }
        return mDatas;
    }

    private ArrayList<String> getData() {

        ArrayList<String> mDatas = new ArrayList<>();
        for (int i = 0;i<10;i++){
            mDatas.add(""+i);
        }
        return mDatas;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
