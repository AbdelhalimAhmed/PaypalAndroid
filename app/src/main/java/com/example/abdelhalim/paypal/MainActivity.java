package com.example.abdelhalim.paypal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    PayPalConfiguration mConfiguration;
    String mPaypalClientID = "Aenxt8hfL4ixYkwYOWM9PT-dcsUAKZMwDpJSFhpqqeUNL5ywl8bBLOIxACMT8jIKILdWZdyl2HBxATbK";
    Intent mService;
    int mPaypalRequestCode = 999;
    TextView mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResponse = (TextView) findViewById(R.id.response);

        mConfiguration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)//for test only, "Production" for real
                .clientId(mPaypalClientID);

        mService = new Intent(this, PayPalService.class);
        mService.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mConfiguration);
        startService(mService);

    }

    void Pay(View view){

        PayPalPayment payment = new PayPalPayment(new BigDecimal(10), "USD", "Test payment with PayPal",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, mPaypalRequestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == mPaypalRequestCode){
            if(resultCode == Activity.RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null)
                {
                    String state = confirmation.getProofOfPayment().getState();

                    if(state.equals("approved")) // if the payment worked, the state equals approved
                        mResponse.setText("payment approved");
                    else
                        mResponse.setText("error in the payment");
                }
                else
                    mResponse.setText("confirmation is null");
            }

        }
    }
}

