package com.drini.driniapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.drini.driniapp.R;
import com.drini.driniapp.databinding.ActivityLanguagesBinding;
import com.drini.driniapp.databinding.ActivityPaymentBinding;
import com.drini.driniapp.utilities.AlwaysOnRun;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private TextView back,donatebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        back = binding.backPayment;
        donatebtn=binding.donateBtn;
        AlwaysOnRun.AlwaysRun(this);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_to_right_in,R.anim.left_to_right_out);
            }
        });

        donatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://boosty.to/hddevelopers"));
                startActivity(intent);
            }
        });
    }
}