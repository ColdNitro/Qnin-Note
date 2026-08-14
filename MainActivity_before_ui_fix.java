package com.qnin.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
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
        // SETTINGS BUTTON
        // ========================================================

        TextView settingsButton =
                new TextView(this);

        settingsButton.setText("⚙");
        settingsButton.setTextSize(25);

        settingsButton.setGravity(
                Gravity.CENTER
        );

        settingsButton.setTextColor(
                textColor()
        );

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

        final boolean editing =
                index >= 0;

        final Note note;

        if (editing) {
            note = notes.get(index);
        } else {
            note = new Note();
        }

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setBackgroundColor(
                backgroundColor()
        );

        // ========================================================
        // EDITOR TOOLBAR
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
                dp(16),
                dp(8),
                dp(16),
                dp(8)
        );

        // ========================================================
        // BACK BUTTON
        // ========================================================

        TextView back =
                new TextView(this);

        back.setText("←");
        back.setTextSize(27);

        back.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        back.setTextColor(
                PURPLE
        );

        back.setGravity(
                Gravity.CENTER
        );

        back.setClickable(true);
        back.setFocusable(true);

        GradientDrawable backBackground =
                new GradientDrawable();

        backBackground.setColor(
                softColor()
        );

        backBackground.setCornerRadius(
                dp(24)
        );

        back.setBackground(
                backBackground
        );

        back.setElevation(
                dp(2)
        );

        back.setOnClickListener(
                v -> showMainScreen()
        );

        toolbar.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                )
        );

        // ========================================================
        // EDITOR TITLE
        // ========================================================

        TextView heading =
                new TextView(this);

        heading.setText(
                editing
                        ? "Edit Note"
                        : "New Note"
        );

        heading.setTextSize(22);

        heading.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        heading.setTextColor(
                textColor()
        );

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        headingParams.setMargins(
                dp(12),
                0,
                0,
                0
        );

        toolbar.addView(
                heading,
                headingParams
        );

        root.addView(toolbar);

        // ========================================================
        // TITLE INPUT
        // ========================================================

        EditText titleInput =
                new EditText(this);

        titleInput.setHint("Title");

        titleInput.setHintTextColor(
                darkMode
                        ? Color.rgb(145, 145, 145)
                        : Color.GRAY
        );

        titleInput.setText(
                note.title
        );

        titleInput.setTextColor(
                textColor()
        );

        titleInput.setTextSize(23);

        titleInput.setSingleLine(true);

        titleInput.setPadding(
                dp(16),
                dp(8),
                dp(16),
                dp(8)
        );

        root.addView(
                titleInput,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(65)
                )
        );

        // ========================================================
        // BODY INPUT
        // ========================================================

        EditText bodyInput =
                new EditText(this);

        bodyInput.setHint(
                "Write your note..."
        );

        bodyInput.setHintTextColor(
                darkMode
                        ? Color.rgb(145, 145, 145)
                        : Color.GRAY
        );

        bodyInput.setText(
                note.body
        );

        bodyInput.setTextColor(
                textColor()
        );

        bodyInput.setTextSize(18);

        bodyInput.setGravity(
                Gravity.TOP | Gravity.START
        );

        bodyInput.setPadding(
                dp(16),
                dp(12),
                dp(16),
                dp(12)
        );

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

        // ========================================================
        // SAVE BUTTON
        // ========================================================

        TextView saveButton =
                new TextView(this);

        saveButton.setText("Save");

        saveButton.setTextSize(18);

        saveButton.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        saveButton.setTextColor(
                Color.WHITE
        );

        saveButton.setGravity(
                Gravity.CENTER
        );

        saveButton.setClickable(true);
        saveButton.setFocusable(true);

        GradientDrawable saveBackground =
                new GradientDrawable();

        saveBackground.setColor(
                PURPLE
        );

        saveBackground.setCornerRadius(
                dp(16)
        );

        saveButton.setBackground(
                saveBackground
        );

        saveButton.setElevation(
                dp(2)
        );

        saveButton.setOnClickListener(
                v -> {

                    note.title =
                            titleInput
                                    .getText()
                                    .toString()
                                    .trim();

                    if (note.title.isEmpty()) {
                        note.title =
                                "Untitled";
                    }

                    note.body =
                            bodyInput
                                    .getText()
                                    .toString();

                    note.time =
                            System.currentTimeMillis();

                    if (!editing) {
                        notes.add(note);
                    }

                    saveNotes();

                    showMainScreen();
                }
        );

        // Leave enough space above the Android navigation bar.
        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        saveParams.setMargins(
                dp(20),
                dp(8),
                dp(20),
                dp(24)
        );

        root.addView(
                saveButton,
                saveParams
        );

        setContentView(root);
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

    final Dialog dialog =
            new Dialog(this);

    LinearLayout sheet =
            new LinearLayout(this);

    sheet.setOrientation(
            LinearLayout.VERTICAL
    );

    sheet.setPadding(
            dp(22),
            dp(10),
            dp(22),
            dp(24)
    );

    int bgColor =
            isDarkMode()
                    ? Color.rgb(30, 30, 30)
                    : Color.WHITE;

    GradientDrawable background =
            new GradientDrawable();

    background.setColor(bgColor);

    background.setCornerRadii(
            new float[]{
                    dp(24), dp(24),
                    dp(24), dp(24),
                    0, 0,
                    0, 0
            }
    );

    sheet.setBackground(background);

    TextView handle =
            new TextView(this);

    handle.setText("━━");
    handle.setTextSize(18);
    handle.setGravity(Gravity.CENTER);
    handle.setTextColor(
            isDarkMode()
                    ? Color.GRAY
                    : Color.LTGRAY
    );

    sheet.addView(
            handle,
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(32)
            )
    );

    TextView heading =
            new TextView(this);

    heading.setText(note.title);
    heading.setTextSize(20);
    heading.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
    );

    heading.setTextColor(
            isDarkMode()
                    ? Color.WHITE
                    : Color.BLACK
    );

    heading.setPadding(
            0,
            dp(4),
            0,
            dp(14)
    );

    sheet.addView(heading);

    TextView pin =
            createSheetOption(
                    note.pinned
                            ? "📌  Unpin note"
                            : "📌  Pin note"
            );

    pin.setOnClickListener(v -> {

        note.pinned =
                !note.pinned;

        saveNotes();

        dialog.dismiss();

        displayNotes(
                searchBox == null
                        ? ""
                        : searchBox
                        .getText()
                        .toString()
        );
    });

    sheet.addView(pin);

    TextView delete =
            createSheetOption(
                    "🗑  Delete note"
            );

    delete.setTextColor(
            Color.rgb(220, 70, 70)
    );

    delete.setOnClickListener(v -> {

        dialog.dismiss();

        deleteNoteWithUndo(note);
    });

    sheet.addView(delete);

    dialog.setContentView(sheet);

    Window window =
            dialog.getWindow();

    if (window != null) {

        window.setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT
                )
        );

        window.setGravity(
                Gravity.BOTTOM
        );

        window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    dialog.show();

    setupBottomSheet(dialog);
}

    // ============================================================
    // DELETE CONFIRMATION
    // ============================================================

    private void confirmDelete(Note note) {

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Delete note?")
                        .setMessage(
                                "Are you sure you want to delete \""
                                        + note.title
                                        + "\"?"
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Delete",
                                (d, which) ->
                                        deleteNoteWithUndo(note)
                        )
                        .create();

        dialog.show();
    }    // ============================================================
    // DELETE + UNDO
    // ============================================================

    private void deleteNoteWithUndo(Note note) {

        deletedIndex =
                notes.indexOf(note);

        deletedNote =
                note;

        if (deletedIndex >= 0) {
            notes.remove(deletedIndex);
        }

        saveNotes();

        String currentSearch =
                searchBox == null
                        ? ""
                        : searchBox
                        .getText()
                        .toString();

        displayNotes(
                currentSearch
        );

        showUndoDialog();
    }

    private void showUndoDialog() {

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Note deleted")
                        .setMessage(
                                "The note was deleted."
                        )
                        .setNegativeButton(
                                "OK",
                                null
                        )
                        .setPositiveButton(
                                "UNDO",
                                (d, which) -> undoDelete()
                        )
                        .create();

        dialog.show();
    }

    private void undoDelete() {

        if (deletedNote == null) {
            return;
        }

        int position =
                deletedIndex;

        if (
                position < 0
                        || position > notes.size()
        ) {
            position =
                    notes.size();
        }

        notes.add(
                position,
                deletedNote
        );

        saveNotes();

        String currentSearch =
                searchBox == null
                        ? ""
                        : searchBox
                        .getText()
                        .toString();

        displayNotes(
                currentSearch
        );

        deletedNote = null;
        deletedIndex = -1;
    }    // ============================================================
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

private TextView createSheetOption(String text) {

    TextView option =
            new TextView(this);

    option.setText(text);
    option.setTextSize(18);
    option.setGravity(
            Gravity.CENTER_VERTICAL
    );

    option.setTypeface(
            Typeface.DEFAULT,
            Typeface.BOLD
    );

    option.setPadding(
            dp(12),
            0,
            dp(12),
            0
    );

    option.setTextColor(
            isDarkMode()
                    ? Color.WHITE
                    : Color.rgb(35, 35, 35)
    );

    option.setBackgroundColor(
            Color.TRANSPARENT
    );

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(58)
            );

    params.setMargins(
            0,
            dp(2),
            0,
            dp(2)
    );

    option.setLayoutParams(params);

    return option;
}
}
