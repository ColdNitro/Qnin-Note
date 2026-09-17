package com.qnin.note;

import android.app.Activity;
import android.app.Dialog;
import android.text.InputType;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordDialogHelper {

    private static void setupToggle(EditText et, ImageView icon) {
        icon.setOnClickListener(v -> {
            boolean hidden = et.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            if (hidden) {
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                icon.setImageResource(R.drawable.ic_eye);
            } else {
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                icon.setImageResource(R.drawable.ic_eye_off);
            }
            et.setSelection(et.getText().length());
        });
    }

    public static void showEnableDialog(Activity activity, Runnable onSuccess) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enable_password);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etPass = dialog.findViewById(R.id.etPassword);
        EditText etConfirm = dialog.findViewById(R.id.etConfirmPassword);
        setupToggle(etPass, dialog.findViewById(R.id.ivToggle1));
        setupToggle(etConfirm, dialog.findViewById(R.id.ivToggle2));

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnEnable).setOnClickListener(v -> {
            String p1 = etPass.getText().toString();
            String p2 = etConfirm.getText().toString();
            if (p1.length() < 4) { Toast.makeText(activity, "Password too short", Toast.LENGTH_SHORT).show(); return; }
            if (!p1.equals(p2)) { Toast.makeText(activity, "Passwords don't match", Toast.LENGTH_SHORT).show(); return; }
            PasswordLockManager.setPassword(activity, p1);
            dialog.dismiss();
            if (onSuccess != null) onSuccess.run();
        });
        dialog.show();
    }

    public static void showChangeDialog(Activity activity, Runnable onSuccess) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etCurrent = dialog.findViewById(R.id.etCurrentPassword);
        EditText etNew = dialog.findViewById(R.id.etNewPassword);
        EditText etConfirmNew = dialog.findViewById(R.id.etConfirmNewPassword);
        setupToggle(etCurrent, dialog.findViewById(R.id.ivToggleCurrent));
        setupToggle(etNew, dialog.findViewById(R.id.ivToggleNew));
        setupToggle(etConfirmNew, dialog.findViewById(R.id.ivToggleConfirmNew));

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnEnable).setOnClickListener(v -> {
            String current = etCurrent.getText().toString();
            String p1 = etNew.getText().toString();
            String p2 = etConfirmNew.getText().toString();
            if (!PasswordLockManager.checkPassword(activity, current)) { Toast.makeText(activity, "Current password is wrong", Toast.LENGTH_SHORT).show(); return; }
            if (p1.length() < 4) { Toast.makeText(activity, "New password too short", Toast.LENGTH_SHORT).show(); return; }
            if (!p1.equals(p2)) { Toast.makeText(activity, "Passwords don't match", Toast.LENGTH_SHORT).show(); return; }
            PasswordLockManager.setPassword(activity, p1);
            dialog.dismiss();
            if (onSuccess != null) onSuccess.run();
        });
        dialog.show();
      }
public static void showDisableDialog(Activity activity, Runnable onSuccess) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_disable_password);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etCurrent = dialog.findViewById(R.id.etCurrentPassword);
        setupToggle(etCurrent, dialog.findViewById(R.id.ivToggleCurrent));

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnDisable).setOnClickListener(v -> {
            String current = etCurrent.getText().toString();
            if (!PasswordLockManager.checkPassword(activity, current)) { Toast.makeText(activity, "Password is wrong", Toast.LENGTH_SHORT).show(); return; }
            PasswordLockManager.disable(activity);
            dialog.dismiss();
            if (onSuccess != null) onSuccess.run();
        });
        dialog.show();
    }
}
