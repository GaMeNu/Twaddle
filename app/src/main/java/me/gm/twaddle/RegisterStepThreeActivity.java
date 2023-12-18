package me.gm.twaddle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterStepThreeActivity extends AppCompatActivity {

    private TextView errTitle;
    private TextView errDesc;

    private Button btnBack;
    private Button btnConfirm;

    private EditText etDisplayName;
    private EditText etTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);

        errTitle = findViewById(R.id.tv_errTitle_registerNames);
        errDesc = findViewById(R.id.tv_errDesc_registerNames);

        btnBack = findViewById(R.id.btn_register_namesBack);
        btnConfirm = findViewById(R.id.btn_register_namesConfirm);

        btnConfirm.setOnClickListener(this::onClick_confirm);
        btnBack.setOnClickListener(this::onClick_back);
    }

    private void onClick_confirm(View view) {
        if (view.getId() != btnConfirm.getId()){
            return;
        }
    }

    private void onClick_back(View view) {
        if (view.getId() != btnBack.getId()){
            return;
        }
        finish();
    }


}