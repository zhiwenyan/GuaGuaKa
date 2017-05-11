package com.example.zhiwenyan.guaguaka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private GuaView guaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        guaView = (GuaView) findViewById(R.id.guaView);
        guaView.setOnCompleteListener(new GuaView.onCompleteListener() {
            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this, "恭喜你，中了5百万", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
