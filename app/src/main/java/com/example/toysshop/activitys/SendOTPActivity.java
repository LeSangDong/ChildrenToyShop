package com.example.toysshop.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.toysshop.R;
import com.example.toysshop.database.UserRepository;
import com.example.toysshop.databinding.ActivitySendOtpactivityBinding;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendOTPActivity extends AppCompatActivity {
    private ActivitySendOtpactivityBinding binding;

    private String phoneNumber;
    private String address;
    private Long timeOut = 60L;
    private FirebaseAuth mAuth;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getExtras().getString("phone");
        sendOTP(phoneNumber, false);

        binding.btnContinue.setOnClickListener(v->{
            String enteredOTP = binding.otpview.getText().toString().trim();

            if (verificationCode != null && !enteredOTP.isEmpty()) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOTP);
                signIn(credential);
                binding.tvBtn2.setVisibility(View.GONE);
                binding.progress2.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(SendOTPActivity.this, "Vui lòng nhập OTP!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvResend.setOnClickListener(v -> sendOTP(phoneNumber, true));

        startSmsUserConsent();



    }

    private void startSmsUserConsent() {
        SmsRetriever.getClient(this).startSmsUserConsent(null);
        registerReceiver(smsVerificationReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
    }

    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                try {
                    smsConsentLauncher.launch(consentIntent);
                } catch (Exception e) {
                    Log.e("LOG", "Could not start activity for result: " + e.getMessage());
                }
            }
        }
    };

    private final ActivityResultLauncher<Intent> smsConsentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                        if (message != null) {
                            String oneTimeCode = extractOtpFromMessage(message);
                            binding.otpview.setText(oneTimeCode);
                        }
                    }
                } else {
                    Toast.makeText(this, "Consent canceled", Toast.LENGTH_SHORT).show();
                }
            });

    private String extractOtpFromMessage(String message) {
        // Extract the OTP using regex or any other method you prefer
        // Assuming OTP is 6 digits long
        Pattern pattern = Pattern.compile("(\\d{6})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        binding.tvBtn2.setVisibility(View.GONE);
        binding.progress2.setVisibility(View.VISIBLE);

//        mAuth.signInWithCredential(phoneAuthCredential)
//                .addOnCompleteListener(task -> {
//                    binding.progress2.setVisibility(View.GONE);
//                    binding.tvBtn2.setVisibility(View.VISIBLE);
//                    if (task.isSuccessful()) {
//                        Intent intent = new Intent(SendOTPActivity.this, AddressActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(SendOTPActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                    }
//                });

        saveUserInfoAndProceed();


    }

    private void saveUserInfoAndProceed() {

        Intent intent = new Intent(SendOTPActivity.this, AddressActivity.class);
        intent.putExtra("phone_verified",phoneNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendOTP(String phoneNumber, boolean isResend) {
        startResendTimer();
        binding.tvBtn2.setVisibility(View.GONE);
        binding.progress2.setVisibility(View.VISIBLE);

        PhoneAuthOptions.Builder builder = new PhoneAuthOptions.Builder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeOut, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String smsCode = phoneAuthCredential.getSmsCode();
                        if (smsCode != null) {
                            binding.otpview.setText(smsCode);
                        }
                        binding.progress2.setVisibility(View.GONE);
                        binding.tvBtn2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(SendOTPActivity.this, "Xác thực thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LOG", e.getMessage());
                        binding.progress2.setVisibility(View.GONE);
                        binding.tvBtn2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                       // Toast.makeText(SendOTPActivity.this, "Gửi OTP thành công", Toast.LENGTH_SHORT).show();
                        binding.progress2.setVisibility(View.GONE);
                        binding.tvBtn2.setVisibility(View.VISIBLE);
                    }
                });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }


    }

    private void startResendTimer() {
        binding.tvResend.setEnabled(false);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            private long timeRemaining = timeOut;

            @Override
            public void run() {
                if (timeRemaining > 0) {
                    binding.tvResend.setText("Gửi lại OTP sau " + timeRemaining + " giây");
                    timeRemaining--;
                    handler.postDelayed(this, 1000);
                } else {
                    timeRemaining = timeOut;
                    binding.tvResend.setEnabled(true);
                    binding.tvResend.setText("Gửi lại OTP");
                }
            }
        });
    }
}