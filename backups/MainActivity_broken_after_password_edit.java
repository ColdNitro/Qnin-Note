package com.qnin.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private LinearLayout noteList;
    private EditText searchBox;
    private SharedPreferences prefs;

    private final ArrayList<Note> notes = new ArrayList<>();

    private static final int PURPLE = Color.rgb(103, 80, 164);
    private static final int LIGHT_PURPLE = Color.rgb(245, 242, 248);
    private static final int SOFT_PURPLE = Color.rgb(243, 238, 250);

    private boolean darkMode = false;
    private boolean appUnlocked = false;

    private Note deletedNote = null;
    private int deletedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("qnin_notes", MODE_PRIVATE);
        darkMode = prefs.getBoolean("dark_mode", false);

        loadNotes();
        applySystemBars();

        if (isPasswordEnabled()) {
            showPasswordUnlockDialog();
        } else {
            appUnlocked = true;
            showMainScreen();
        }
    }

    // ============================================================
    // COLORS
    // ============================================================

    private int backgroundColor() {
        return darkMode
                ? Color.rgb(18, 18, 18)
                : Color.WHITE;
    }

    private int primaryTextColor() {
        return darkMode
                ? Color.WHITE
                : Color.BLACK;
    }

    private int secondaryTextColor() {
        return darkMode
                ? Color.rgb(190, 190, 190)
                : Color.DKGRAY;
    }

    private int cardColor() {
        return darkMode
                ? Color.rgb(38, 38, 38)
                : LIGHT_PURPLE;
    }

    private int searchColor() {
        return darkMode
                ? Color.rgb(42, 42, 42)
                : Color.rgb(245, 245, 247);
    }

    private int softColor() {
        return darkMode
                ? Color.rgb(55, 45, 65)
                : SOFT_PURPLE;
    }

    // ============================================================
    // SYSTEM BARS
    // ============================================================

    private void applySystemBars() {

        if (darkMode) {
            getWindow().setStatusBarColor(Color.rgb(30, 30, 30));
        } else {
            getWindow().setStatusBarColor(PURPLE);
        }

        getWindow().setNavigationBarColor(Color.BLACK);

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            int flags =
                    getWindow()
                            .getDecorView()
                            .getSystemUiVisibility();

            if (darkMode) {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }

            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(flags);
        }
    }

    // ============================================================
    // MAIN SCREEN
    // ============================================================

    private void showMainScreen() {

        applySystemBars();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(backgroundColor());

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundColor(backgroundColor());

        // TOP BAR

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);

        toolbar.setPadding(
                dp(20),
                dp(12),
                dp(12),
                dp(8)
        );

        TextView title = new TextView(this);
        title.setText("Qnin Note");
        title.setTextSize(27);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(primaryTextColor());

        toolbar.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView settingsButton = new TextView(this);

        settingsButton.setText("⚙");
        settingsButton.setTextSize(25);
        settingsButton.setGravity(Gravity.CENTER);
        settingsButton.setTextColor(primaryTextColor());
        settingsButton.setClickable(true);
        settingsButton.setFocusable(true);

        settingsButton.setOnClickListener(
                v -> showSettings()
        );

        toolbar.addView(
                settingsButton,
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                )
        );

        content.addView(toolbar);

        // SEARCH

        searchBox = new EditText(this);
        searchBox.setHint("Search notes...");
        searchBox.setTextSize(16);
        searchBox.setSingleLine(true);

        searchBox.setHintTextColor(
                darkMode
                        ? Color.rgb(150, 150, 150)
                        : Color.GRAY
        );

        searchBox.setTextColor(primaryTextColor());

        searchBox.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        GradientDrawable searchBackground =
                new GradientDrawable();

        searchBackground.setColor(searchColor());
        searchBackground.setCornerRadius(dp(14));

        searchBox.setBackground(searchBackground);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(52)
                );

        searchParams.setMargins(
                dp(20),
                dp(8),
                dp(20),
                dp(14)
        );

        content.addView(searchBox, searchParams);

        searchBox.addTextChangedListener(
                new SimpleTextWatcher() {
                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                        displayNotes(s.toString());
                    }
                }
        );

        // NOTE LIST

        ScrollView scroll = new ScrollView(this);

        noteList = new LinearLayout(this);
        noteList.setOrientation(
                LinearLayout.VERTICAL
        );

        noteList.setPadding(
                dp(20),
                dp(4),
                dp(20),
                dp(110)
        );

        scroll.addView(noteList);

        content.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        root.addView(
                content,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        // FLOATING ADD BUTTON

        TextView addButton =
                new TextView(this);

        addButton.setText("+");
        addButton.setTextSize(27);
        addButton.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        addButton.setTextColor(Color.WHITE);
        addButton.setGravity(Gravity.CENTER);
        addButton.setClickable(true);

        GradientDrawable addBackground =
                new GradientDrawable();

        addBackground.setColor(PURPLE);
        addBackground.setCornerRadius(dp(28));

        addButton.setBackground(addBackground);
        addButton.setElevation(dp(8));

        addButton.setOnClickListener(
                v -> showEditor(-1)
        );

        FrameLayout.LayoutParams addParams =
                new FrameLayout.LayoutParams(
                        dp(58),
                        dp(58),
                        Gravity.BOTTOM | Gravity.END
                );

        addParams.setMargins(
                0,
                0,
                dp(20),
                dp(24)
        );

        root.addView(addButton, addParams);

        root.setOnApplyWindowInsetsListener(
                (v, insets) -> {

                    if (android.os.Build.VERSION.SDK_INT >= 30) {

                        int top =
                                insets.getInsets(
                                        android.view.WindowInsets
                                                .Type
                                                .statusBars()
                                ).top;

                        int bottom =
                                insets.getInsets(
                                        android.view.WindowInsets
                                                .Type
                                                .navigationBars()
                                ).bottom;

                        content.setPadding(
                                0,
                                top,
                                0,
                                0
                        );

                        FrameLayout.LayoutParams lp =
                                (FrameLayout.LayoutParams)
                                        addButton.getLayoutParams();

                        lp.bottomMargin =
                                bottom + dp(20);

                        addButton.setLayoutParams(lp);
                    }

                    return insets;
                }
        );

        setContentView(root);

        root.requestApplyInsets();

        displayNotes("");
    }

    // ============================================================
    // SETTINGS
    // ============================================================

    private void showSettings() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                dp(24),
                dp(8),
                dp(24),
                dp(8)
        );

        TextView darkModeLabel =
                new TextView(this);

        darkModeLabel.setText("Dark mode");
        darkModeLabel.setTextSize(18);
        darkModeLabel.setTextColor(
                primaryTextColor()
        );

        Switch darkSwitch =
                new Switch(this);

        darkSwitch.setText("Use dark theme");
        darkSwitch.setTextColor(
                primaryTextColor()
        );

        darkSwitch.setChecked(darkMode);

        layout.addView(
                darkModeLabel,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(40)
                )
        );

        layout.addView(darkSwitch);

        TextView passwordButton =
                createSettingsItem(
                        isPasswordEnabled()
                                ? "Password lock: On"
                                : "Password lock: Off"
                );

        layout.addView(passwordButton);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Settings")
                        .setView(layout)
                        .setPositiveButton(
                                "Done",
                                null
                        )
                        .create();

        darkSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    darkMode = isChecked;

                    prefs.edit()
                            .putBoolean(
                                    "dark_mode",
                                    darkMode
                            )
                            .apply();

                    dialog.dismiss();

                    showMainScreen();
                }
        );

        passwordButton.setOnClickListener(
                v -> {

                    dialog.dismiss();

                    showPasswordSettings();
                }
        );

        dialog.show();

        styleDialog(dialog);
    }

    private TextView createSettingsItem(
            String text
    ) {

        TextView item =
                new TextView(this);

        item.setText(text);
        item.setTextSize(17);
        item.setTextColor(primaryTextColor());

        item.setGravity(
                Gravity.CENTER_VERTICAL
        );

        item.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        item.setClickable(true);

        return item;
    }

    // ============================================================
    // PASSWORD SETTINGS
    // ============================================================

    private void showPasswordSettings() {

        if (!isPasswordEnabled()) {

            new AlertDialog.Builder(this)
                    .setTitle("Password lock")
                    .setMessage(
                            "Protect your notes with a password."
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Enable",
                            (dialog, which) ->
                                    showCreatePasswordDialog()
                    )
                    .show();

            return;
        }

        final String[] options = {
                "Change password",
                "Disable password"
        };

        new AlertDialog.Builder(this)
                .setTitle("Password lock")
                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {
                                showChangePasswordDialog();
                            } else {
                                showDisablePasswordDialog();
                            }
                        }
                )
                .show();
    }

    private void showCreatePasswordDialog() {

        LinearLayout layout =
                createPasswordLayout();

        EditText password =
                (EditText) layout.getChildAt(0);

        EditText confirm =
                (EditText) layout.getChildAt(1);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Create password")
                        .setMessage(
                                "Your notes will be locked when password protection is enabled."
                        )
                        .setView(layout)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Enable",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(
                            v -> {

                                String p =
                                        password.getText()
                                                .toString();

                                String c =
                                        confirm.getText()
                                                .toString();

                                if (p.length() < 4) {

                                    password.setError(
                                            "Password must be at least 4 characters"
                                    );

                                    return;
                                }

                                if (!p.equals(c)) {

                                    confirm.setError(
                                            "Passwords do not match"
                                    );

                                    return;
                                }

                                savePassword(p);

                                dialog.dismiss();
                            }
                    );
                }
        );

        dialog.show();

        styleDialog(dialog);
        }


    private void showChangePasswordDialog(){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(
                dp(20),
                dp(8),
                dp(20),
                dp(8)
        );

        EditText oldPassword =
                createPasswordInput("Current password");

        EditText newPassword =
                createPasswordInput("New password");

        EditText confirm =
                createPasswordInput("Confirm new password");

        layout.addView(oldPassword);
        layout.addView(newPassword);
        layout.addView(confirm);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Change password")
                        .setView(layout)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String old =
                        oldPassword.getText()
                                .toString();

                String newer =
                        newPassword.getText()
                                .toString();

                String conf =
                        confirm.getText()
                                .toString();

                if (!verifyPassword(old)) {

                    oldPassword.setError(
                            "Incorrect password"
                    );

                    return;
                }

                if (newer.length() < 4) {

                    newPassword.setError(
                            "Password must be at least 4 characters"
                    );

                    return;
                }

                if (!newer.equals(conf)) {

                    confirm.setError(
                            "Passwords do not match"
                    );

                    return;
                }

                savePassword(newer);

                dialog.dismiss();

                new AlertDialog.Builder(
                        MainActivity.this
                )
                        .setTitle("Password changed")
                        .setMessage(
                                "Your password has been updated successfully."
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .show();
            });
        });

        dialog.show();

        styleDialog(dialog);
    }


    private void disablePassword() {

        EditText password =
                createPasswordInput(
                        "Current password"
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Disable password"
                        )
                        .setMessage(
                                "Enter your current password to disable protection."
                        )
                        .setView(password)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Disable",
                                null
                        )
                        .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String entered =
                        password.getText()
                                .toString();

                if (!verifyPassword(entered)) {

                    password.setError(
                            "Incorrect password"
                    );

                    return;
                }

                prefs.edit()
                        .remove("password")
                        .apply();

                dialog.dismiss();

                new AlertDialog.Builder(
                        MainActivity.this
                )
                        .setTitle(
                                "Password disabled"
                        )
                        .setMessage(
                                "Password protection has been removed."
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .show();
            });
        });

        dialog.show();

        styleDialog(dialog);
    }


    private void savePassword(
            String password
    ) {

        prefs.edit()
                .putString(
                        "password",
                        password
                )
                .apply();
    }


    private boolean hasPassword() {

        String password =
                prefs.getString(
                        "password",
                        ""
                );

        return password != null
                && !password.isEmpty();
    }


    private boolean verifyPassword(
            String entered
    ) {

        String saved =
                prefs.getString(
                        "password",
                        ""
                );

        return saved != null
                && saved.equals(entered);
    }


    private EditText createPasswordInput(
            String hint
    ) {

        EditText input =
                new EditText(this);

        input.setHint(hint);

        input.setSingleLine(true);

        input.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        input.setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                dp(6),
                0,
                dp(6)
        );

        input.setLayoutParams(params);

        return input;
    }
}
