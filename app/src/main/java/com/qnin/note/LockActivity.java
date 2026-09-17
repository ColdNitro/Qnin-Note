package com.qnin.note;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class LockActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        View layoutLocked = findViewById(R.id.layoutLocked);
        EditText etPassword = findViewById(R.id.etUnlockPassword);
        ImageView ivToggle = findViewById(R.id.ivToggleUnlock);
        TextView btnUnlock = findViewById(R.id.btnUnlock);

        ivToggle.setOnClickListener(v -> {
            boolean hidden = etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            if (hidden) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggle.setImageResource(R.drawable.ic_eye);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggle.setImageResource(R.drawable.ic_eye_off);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnUnlock.setOnClickListener(v -> {
            String entered = etPassword.getText().toString();
            if (PasswordLockManager.checkPassword(this, entered)) {
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // block back button so the app stays locked
    }
}
