package com.qnin.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

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

    private Note deletedNote = null;
    private int deletedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(
                "qnin_notes",
                MODE_PRIVATE
        );

        darkMode = prefs.getBoolean(
                "dark_mode",
                false
        );

        loadNotes();
        applySystemBars();
        showMainScreen();
    }

    private void applySystemBars() {

        getWindow().setStatusBarColor(
                darkMode
                        ? Color.rgb(30, 30, 30)
                        : PURPLE
        );

        getWindow().setNavigationBarColor(
                Color.BLACK
        );

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            int flags =
                    getWindow()
                            .getDecorView()
                            .getSystemUiVisibility();

            if (!darkMode) {
                flags |=
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &=
                        ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }

            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(flags);
        }
    }

    private int backgroundColor() {
        return darkMode
                ? Color.rgb(18, 18, 18)
                : Color.WHITE;
    }

    private int textColor() {
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

    private int primaryText() {
        return darkMode ? Color.WHITE : Color.rgb(35, 35, 35);
    }

    private GradientDrawable sheetBackground() {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(backgroundColor());
        bg.setCornerRadii(new float[]{dp(24), dp(24), dp(24), dp(24), 0, 0, 0, 0});
        return bg;
    }

    private void applyBarColors() {
        getWindow().setStatusBarColor(darkMode ? Color.BLACK : PURPLE);
        getWindow().setNavigationBarColor(Color.BLACK);
    }

    // ============================================================
    // MAIN SCREEN
    // ============================================================

    private void showMainScreen() {

        applySystemBars();

        FrameLayout root =
                new FrameLayout(this);

        root.setBackgroundColor(
                backgroundColor()
        );

        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setBackgroundColor(
                backgroundColor()
        );        // ========================================================
        // TOP BAR
        // ========================================================

        LinearLayout toolbar =
                new LinearLayout(this);

        toolbar.setOrientation(
                LinearLayout.HORIZONTAL
        );

        toolbar.setGravity(
                Gravity.CENTER_VERTICAL
        );

        toolbar.setPadding(
                dp(20),
                dp(12),
                dp(12),
                dp(8)
        );

        TextView title =
                new TextView(this);

        title.setText("Qnin Note");
        title.setTextSize(27);

        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        title.setTextColor(
                textColor()
        );

        toolbar.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        // ========================================================
        TextView settings = new TextView(this);
        settings.setText("⚙");
        settings.setTextSize(24);
        settings.setGravity(Gravity.CENTER);
        settings.setTextColor(primaryText());
        settings.setClickable(true);
        settings.setOnClickListener(v -> showSettingsDialog());
        toolbar.addView(settings, new LinearLayout.LayoutParams(dp(48), dp(48)));

        content.addView(toolbar);

        // ========================================================
        // SEARCH
        // ========================================================

        searchBox =
                new EditText(this);

        searchBox.setHint(
                "Search notes..."
        );

        searchBox.setHintTextColor(
                darkMode
                        ? Color.rgb(150, 150, 150)
                        : Color.GRAY
        );

        searchBox.setTextColor(
                textColor()
        );

        searchBox.setTextSize(16);
        searchBox.setSingleLine(true);

        searchBox.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        GradientDrawable searchBackground =
                new GradientDrawable();

        searchBackground.setColor(
                searchColor()
        );

        searchBackground.setCornerRadius(
                dp(14)
        );

        searchBox.setBackground(
                searchBackground
        );

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

        content.addView(
                searchBox,
                searchParams
        );

        searchBox.addTextChangedListener(
                new SimpleTextWatcher() {

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                        displayNotes(
                                s.toString()
                        );
                    }
                }
        );

        // ========================================================
        // NOTE LIST
        // ========================================================

        ScrollView scroll =
                new ScrollView(this);

        noteList =
                new LinearLayout(this);

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
        );        // ========================================================
        // FLOATING ADD BUTTON
        // ========================================================

        TextView addButton =
                new TextView(this);

        addButton.setText("+");
        addButton.setTextSize(27);

        addButton.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        addButton.setTextColor(
                Color.WHITE
        );

        addButton.setGravity(
                Gravity.CENTER
        );

        addButton.setClickable(true);
        addButton.setFocusable(true);

        GradientDrawable addBackground =
                new GradientDrawable();

        addBackground.setColor(
                PURPLE
        );

        addBackground.setCornerRadius(
                dp(29)
        );

        addButton.setBackground(
                addBackground
        );

        addButton.setElevation(
                dp(8)
        );

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

        root.addView(
                addButton,
                addParams
        );

        // Keep the button above Android's navigation bar.
        root.setOnApplyWindowInsetsListener(
                (v, insets) -> {

                    if (android.os.Build.VERSION.SDK_INT >= 30) {

                        int bottom =
                                insets.getInsets(
                                        android.view.WindowInsets.Type
                                                .navigationBars()
                                ).bottom;

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
    // DISPLAY NOTES
    // ============================================================

    private void displayNotes(
            String query
    ) {

        if (noteList == null) {
            return;
        }

        noteList.removeAllViews();

        String q =
                query == null
                        ? ""
                        : query
                        .trim()
                        .toLowerCase();

        ArrayList<Note> filtered =
                new ArrayList<>();

        for (Note note : notes) {

            if (
                    q.isEmpty()
                            || note.title
                            .toLowerCase()
                            .contains(q)
                            || note.body
                            .toLowerCase()
                            .contains(q)
            ) {
                filtered.add(note);
            }
        }

        Collections.sort(
                filtered,
                (a, b) -> {

                    if (a.pinned != b.pinned) {
                        return a.pinned ? -1 : 1;
                    }

                    return Long.compare(
                            b.time,
                            a.time
                    );
                }
        );

        if (filtered.isEmpty()) {

            TextView empty =
                    new TextView(this);

            empty.setText(
                    q.isEmpty()
                            ? "No notes yet"
                            : "No matching notes"
            );

            empty.setTextSize(18);

            empty.setTextColor(
                    darkMode
                            ? Color.rgb(160, 160, 160)
                            : Color.GRAY
            );

            empty.setGravity(
                    Gravity.CENTER
            );

            noteList.addView(
                    empty,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(300)
                    )
            );

            return;
        }

        for (Note note : filtered) {
            addNoteView(note);
        }
    }

    // ============================================================
    // NOTE CARD
    // ============================================================

    private void addNoteView(Note note) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(18),
                dp(16),
                dp(18),
                dp(16)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                cardColor()
        );

        background.setCornerRadius(
                dp(16)
        );

        card.setBackground(
                background
        );

        card.setClickable(true);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(14)
        );

        TextView title =
                new TextView(this);

        String displayTitle =
                note.title;

        if (note.pinned) {
            displayTitle =
                    "📌 " + displayTitle;
        }

        title.setText(
                displayTitle
        );

        title.setTextSize(20);

        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        title.setTextColor(
                textColor()
        );

        card.addView(title);

        TextView body =
                new TextView(this);

        body.setText(
                note.body
        );

        body.setTextSize(16);

        body.setTextColor(
                secondaryTextColor()
        );

        body.setMaxLines(4);

        body.setEllipsize(
                android.text.TextUtils.TruncateAt.END
        );

        body.setPadding(
                0,
                dp(8),
                0,
                0
        );

        if (!note.body.isEmpty()) {
            card.addView(body);
        }

        // Tap a note to edit it.
        card.setOnClickListener(
                v -> showEditor(
                        notes.indexOf(note)
                )
        );

        // Long press opens the pin/delete menu.
        card.setOnLongClickListener(
                v -> {

                    showNoteMenu(note);

                    return true;
                }
        );

        noteList.addView(
                card,
                cardParams
        );
    }    // ============================================================
    // EDITOR
    // ============================================================

    private void showEditor(int index) {

        final boolean editing = index >= 0 && index < notes.size();

        final Note note;
        if (editing) {
            note = notes.get(index);
        } else {
            note = new Note();
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(backgroundColor());

        // ============================================================
        // EDITOR TOOLBAR
        // ============================================================

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(20), dp(8), dp(20), dp(8));

        // BACK BUTTON
        TextView back = new TextView(this);
        back.setText("←");
        back.setTextSize(27);
        back.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        back.setGravity(Gravity.CENTER);
        back.setTextColor(PURPLE);
        back.setClickable(true);
        back.setFocusable(true);

        GradientDrawable backBackground = new GradientDrawable();
        backBackground.setColor(
                isDarkMode()
                        ? Color.rgb(48, 38, 62)
                        : Color.rgb(245, 239, 252)
        );
        backBackground.setCornerRadius(dp(24));
        back.setBackground(backBackground);
        back.setElevation(dp(2));
        back.setTranslationY(dp(-10));

        back.setOnClickListener(v -> showMainScreen());

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(dp(48), dp(48));

        toolbar.addView(back, backParams);

        // HEADER
        TextView heading = new TextView(this);
        heading.setText(editing ? "Edit Note" : "New Note");
        heading.setTextSize(22);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setTextColor(textColor());
        heading.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1
                );

        headingParams.setMargins(dp(18), 0, 0, 0);
        toolbar.addView(heading, headingParams);

        root.addView(
                toolbar,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(72)
                )
        );

        // ============================================================
        // TITLE
        // ============================================================

        EditText titleInput = new EditText(this);
        titleInput.setHint("Title");
        titleInput.setText(note.title);
        titleInput.setTextSize(23);
        titleInput.setSingleLine(true);
        titleInput.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        titleInput.setTextColor(textColor());
        titleInput.setHintTextColor(
                isDarkMode()
                        ? Color.rgb(145, 145, 145)
                        : Color.rgb(120, 120, 120)
        );
        titleInput.setPadding(dp(20), dp(4), dp(20), 0);
        titleInput.setBackgroundColor(Color.TRANSPARENT);

        root.addView(
                titleInput,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(64)
                )
        );

        // TITLE DIVIDER
        View titleDivider = new View(this);
        titleDivider.setBackgroundColor(
                isDarkMode()
                        ? Color.rgb(65, 55, 72)
                        : Color.rgb(190, 185, 195)
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(2)
                );

        dividerParams.setMargins(dp(20), 0, dp(20), 0);
        root.addView(titleDivider, dividerParams);

        // ============================================================
        // BODY
        // ============================================================

        EditText bodyInput = new EditText(this);
        bodyInput.setHint("Write your note...");
        bodyInput.setText(note.body);
        bodyInput.setTextSize(18);
        bodyInput.setGravity(Gravity.TOP | Gravity.START);
        bodyInput.setTextColor(textColor());
        bodyInput.setHintTextColor(
                isDarkMode()
                        ? Color.rgb(145, 145, 145)
                        : Color.rgb(120, 120, 120)
        );
        bodyInput.setPadding(dp(20), dp(20), dp(20), dp(12));
        bodyInput.setBackgroundColor(Color.TRANSPARENT);
        bodyInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        root.addView(
                bodyInput,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        // ============================================================
        // BOTTOM DIVIDER
        // ============================================================

        View bottomDivider = new View(this);
        bottomDivider.setBackgroundColor(
                isDarkMode()
                        ? Color.rgb(55, 50, 60)
                        : Color.rgb(210, 205, 215)
        );

        LinearLayout.LayoutParams bottomDividerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        bottomDividerParams.setMargins(dp(8), 0, dp(8), 0);
        root.addView(bottomDivider, bottomDividerParams);

        // ============================================================
        // SAVE BUTTON
        // ============================================================

        TextView saveButton = new TextView(this);
        saveButton.setText("Save");
        saveButton.setTextSize(18);
        saveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        saveButton.setTextColor(Color.WHITE);
        saveButton.setGravity(Gravity.CENTER);
        saveButton.setClickable(true);
        saveButton.setFocusable(true);

        GradientDrawable saveBackground = new GradientDrawable();
        saveBackground.setColor(PURPLE);
        saveBackground.setCornerRadius(dp(24));
        saveButton.setBackground(saveBackground);
        saveButton.setElevation(dp(2));

        saveButton.setOnClickListener(v -> {

            note.title = titleInput.getText().toString().trim();

            if (note.title.isEmpty()) {
                note.title = "Untitled";
            }

            note.body = bodyInput.getText().toString();
            note.time = System.currentTimeMillis();

            if (!editing) {
                notes.add(note);
            }

            saveNotes();
            showMainScreen();
        });

        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        saveParams.setMargins(
                dp(20),
                dp(8),
                dp(20),
                dp(64)
        );

        root.addView(saveButton, saveParams);

        setContentView(root);
        applyBarColors();
    }

    // ============================================================
    // SETTINGS
    // ============================================================

    private void showSettings() {

        final LinearLayout layout =
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

        layout.setBackgroundColor(
                backgroundColor()
        );

        TextView darkModeLabel =
                new TextView(this);

        darkModeLabel.setText(
                "Dark mode"
        );

        darkModeLabel.setTextSize(18);

        darkModeLabel.setTextColor(
                textColor()
        );

        darkModeLabel.setGravity(
                Gravity.CENTER_VERTICAL
        );

        Switch darkSwitch =
                new Switch(this);

        darkSwitch.setText(
                "Use dark theme"
        );

        darkSwitch.setTextSize(16);

        darkSwitch.setTextColor(
                textColor()
        );

        darkSwitch.setChecked(
                darkMode
        );

        layout.addView(
                darkModeLabel,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(45)
                )
        );

        layout.addView(
                darkSwitch,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(55)
                )
        );

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

                    applySystemBars();

                    showMainScreen();
                }
        );

        dialog.show();
    }    // ============================================================
    // NOTE MENU
    // ============================================================

    private void showNoteMenu(Note note) {
        final Dialog dialog = new Dialog(this);

        LinearLayout sheet = new LinearLayout(this);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setPadding(dp(20), dp(8), dp(20), dp(20));
        sheet.setBackground(sheetBackground());

        TextView handle = new TextView(this);
        handle.setText("—");
        handle.setTextSize(28);
        handle.setGravity(Gravity.CENTER);
        handle.setTextColor(isDarkMode() ? Color.GRAY : Color.LTGRAY);
        sheet.addView(handle, new LinearLayout.LayoutParams(-1, dp(30)));

        TextView heading = new TextView(this);
        heading.setText(note.title);
        heading.setTextSize(20);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setTextColor(primaryText());
        heading.setPadding(dp(6), dp(4), dp(6), dp(10));
        sheet.addView(heading);

        TextView pin = createSheetOption(note.pinned ? "📌   Unpin note" : "📌   Pin note");
        TextView delete = createSheetOption("🗑   Delete note");
        delete.setTextColor(Color.rgb(220, 70, 70));
        sheet.addView(pin);
        sheet.addView(delete);

        pin.setOnClickListener(v -> {
            note.pinned = !note.pinned;
            saveNotes();
            dialog.dismiss();
            displayNotes(searchBox == null ? "" : searchBox.getText().toString());
        });
        delete.setOnClickListener(v -> {
            dialog.dismiss();
            deleteNoteWithUndo(note);
        });

        dialog.setContentView(sheet);
        dialog.show();
        setupBottomSheet(dialog);
    }

    private TextView createSheetOption(String text) {
        TextView item = new TextView(this);
        item.setText(text);
        item.setTextSize(18);
        item.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setTextColor(primaryText());
        item.setPadding(dp(14), 0, dp(14), 0);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(isDarkMode() ? Color.rgb(45, 45, 48) : Color.rgb(247, 246, 249));
        bg.setCornerRadius(dp(16));
        item.setBackground(bg);
        item.setClickable(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(58));
        lp.setMargins(0, dp(5), 0, dp(5));
        item.setLayoutParams(lp);
        return item;
    }

    // ============================================================
    // DELETE + UNDO
    // ============================================================

    private void deleteNoteWithUndo(Note note) {
        deletedIndex = notes.indexOf(note);
        deletedNote = note;
        if (deletedIndex >= 0) notes.remove(deletedIndex);
        saveNotes();
        displayNotes(searchBox == null ? "" : searchBox.getText().toString());

        final Dialog undoDialog = new Dialog(this);
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(18), dp(8), dp(8), dp(8));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(isDarkMode() ? Color.rgb(45,45,48) : Color.rgb(35,35,38));
        bg.setCornerRadius(dp(18));
        bar.setBackground(bg);

        TextView msg = new TextView(this);
        msg.setText("Note deleted");
        msg.setTextSize(16);
        msg.setTextColor(Color.WHITE);
        bar.addView(msg, new LinearLayout.LayoutParams(0, dp(52), 1));

        TextView undo = new TextView(this);
        undo.setText("UNDO");
        undo.setTextSize(15);
        undo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        undo.setTextColor(Color.rgb(210,190,255));
        undo.setGravity(Gravity.CENTER);
        undo.setPadding(dp(16), 0, dp(16), 0);
        bar.addView(undo, new LinearLayout.LayoutParams(dp(90), dp(52)));

        undo.setOnClickListener(v -> {
            if (deletedNote != null) {
                int position = deletedIndex;
                if (position < 0 || position > notes.size()) position = notes.size();
                notes.add(position, deletedNote);
                saveNotes();
                displayNotes(searchBox == null ? "" : searchBox.getText().toString());
                deletedNote = null;
                deletedIndex = -1;
            }
            undoDialog.dismiss();
        });
        undoDialog.setContentView(bar);
        undoDialog.show();
        Window w = undoDialog.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            w.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            w.setLayout(-1, -2);
        }
        WindowManager.LayoutParams lp = undoDialog.getWindow().getAttributes();
        lp.width = -1;
        lp.height = -2;
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.y = dp(16);
        undoDialog.getWindow().setAttributes(lp);
    }

    // ============================================================
    // SETTINGS / DARK MODE
    // ============================================================

    private void showSettingsDialog() {
        final Dialog dialog = new Dialog(this);
        LinearLayout sheet = new LinearLayout(this);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setPadding(dp(20), dp(8), dp(20), dp(24));
        sheet.setBackground(sheetBackground());

        TextView handle = new TextView(this);
        handle.setText("—");
        handle.setTextSize(28);
        handle.setGravity(Gravity.CENTER);
        handle.setTextColor(isDarkMode() ? Color.GRAY : Color.LTGRAY);
        sheet.addView(handle, new LinearLayout.LayoutParams(-1, dp(30)));

        TextView heading = new TextView(this);
        heading.setText("Settings");
        heading.setTextSize(22);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setTextColor(primaryText());
        heading.setPadding(dp(6), dp(4), dp(6), dp(14));
        sheet.addView(heading);

        LinearLayout modeRow = new LinearLayout(this);
        modeRow.setGravity(Gravity.CENTER_VERTICAL);
        modeRow.setPadding(dp(14), 0, dp(8), 0);
        GradientDrawable rowBg = new GradientDrawable();
        rowBg.setColor(isDarkMode() ? Color.rgb(45,45,48) : Color.rgb(247,246,249));
        rowBg.setCornerRadius(dp(16));
        modeRow.setBackground(rowBg);

        TextView modeText = new TextView(this);
        modeText.setText("Dark mode");
        modeText.setTextSize(18);
        modeText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        modeText.setTextColor(primaryText());
        modeRow.addView(modeText, new LinearLayout.LayoutParams(0, dp(58), 1));

        TextView toggle = new TextView(this);
        toggle.setText(isDarkMode() ? "ON" : "OFF");
        toggle.setTextSize(14);
        toggle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        toggle.setGravity(Gravity.CENTER);
        toggle.setTextColor(Color.WHITE);
        toggle.setBackground(GradientHelper.roundedBackground(isDarkMode() ? PURPLE : Color.GRAY, dp(18)));
        modeRow.addView(toggle, new LinearLayout.LayoutParams(dp(64), dp(36)));
        sheet.addView(modeRow);

        modeRow.setOnClickListener(v -> {
            boolean next = !isDarkMode();
            darkMode = next;
            prefs.edit().putBoolean("dark_mode", next).apply();
            dialog.dismiss();
            applyBarColors();
            showMainScreen();
        });

        dialog.setContentView(sheet);
        dialog.show();
        setupBottomSheet(dialog);
    }

    // ============================================================
    // SAVE NOTES
    // ============================================================

    private void saveNotes() {

        JSONArray array =
                new JSONArray();

        try {

            for (Note note : notes) {

                JSONObject obj =
                        new JSONObject();

                obj.put(
                        "title",
                        note.title
                );

                obj.put(
                        "body",
                        note.body
                );

                obj.put(
                        "pinned",
                        note.pinned
                );

                obj.put(
                        "time",
                        note.time
                );

                array.put(obj);
            }

        } catch (Exception ignored) {
        }

        prefs.edit()
                .putString(
                        "notes",
                        array.toString()
                )
                .apply();
    }

    // ============================================================
    // LOAD NOTES
    // ============================================================

    private void loadNotes() {

        String data =
                prefs.getString(
                        "notes",
                        "[]"
                );

        try {

            JSONArray array =
                    new JSONArray(data);

            for (
                    int i = 0;
                    i < array.length();
                    i++
            ) {

                JSONObject obj =
                        array.getJSONObject(i);

                Note n =
                        new Note();

                n.title =
                        obj.optString(
                                "title",
                                ""
                        );

                n.body =
                        obj.optString(
                                "body",
                                ""
                        );

                n.pinned =
                        obj.optBoolean(
                                "pinned",
                                false
                        );

                n.time =
                        obj.optLong(
                                "time",
                                System.currentTimeMillis()
                        );

                notes.add(n);
            }

        } catch (Exception ignored) {
        }
    }    // ============================================================
    // NOTE DATA
    // ============================================================

    private static class Note {

        String title = "";

        String body = "";

        boolean pinned = false;

        long time =
                System.currentTimeMillis();
    }

    // ============================================================
    // TEXT WATCHER
    // ============================================================

    private static abstract class SimpleTextWatcher
            implements android.text.TextWatcher {

        @Override
        public void beforeTextChanged(
                CharSequence s,
                int start,
                int count,
                int after
        ) {
        }

        @Override
        public void onTextChanged(
                CharSequence s,
                int start,
                int before,
                int count
        ) {
        }

        @Override
        public void afterTextChanged(
                Editable s
        ) {
        }
    }    // ============================================================
    // ROUNDED BACKGROUND HELPER
    // ============================================================

    private static class GradientHelper {

        static GradientDrawable roundedBackground(
                int color,
                float radius
        ) {

            GradientDrawable drawable =
                    new GradientDrawable();

            drawable.setColor(color);

            drawable.setCornerRadius(
                    radius
            );

            return drawable;
        }
    }
// ============================================================
// DARK MODE HELPER
// ============================================================

private boolean isDarkMode() {
    return prefs.getBoolean("dark_mode", false);
}
    // ============================================================
    // DP HELPER
    // ============================================================

    private int dp(int value) {

        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
                        + 0.5f
        );
    }

private void setupBottomSheet(Dialog dialog) {
    Window window = dialog.getWindow();

    if (window == null) return;

    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );

    window.setGravity(Gravity.BOTTOM);

    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    lp.gravity = Gravity.BOTTOM;
    window.setAttributes(lp);
}

}
