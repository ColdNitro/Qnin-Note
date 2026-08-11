package com.qnin.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
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

    // Used for Undo
    private Note deletedNote = null;
    private int deletedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("qnin_notes", MODE_PRIVATE);

        loadNotes();
        showMainScreen();
    }

    // ============================================================
    // MAIN SCREEN
    // ============================================================

    private void showMainScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        // --------------------------------------------------------
        // TOP BAR
        // --------------------------------------------------------

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(20), dp(18), dp(20), dp(10));

        TextView title = new TextView(this);
        title.setText("Qnin Note");
        title.setTextSize(27);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.BLACK);

        toolbar.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        // Add button
        TextView addButton = new TextView(this);
        addButton.setText("+");
        addButton.setTextSize(25);
        addButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        addButton.setTextColor(Color.WHITE);
        addButton.setGravity(Gravity.CENTER);

        GradientDrawable addBackground = new GradientDrawable();
        addBackground.setColor(PURPLE);
        addBackground.setCornerRadius(dp(14));

        addButton.setBackground(addBackground);
        addButton.setClickable(true);
        addButton.setFocusable(true);

        addButton.setOnClickListener(v -> showEditor(-1));

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(
                        dp(52),
                        dp(52)
                );

        addParams.setMargins(dp(10), 0, 0, 0);

        toolbar.addView(addButton, addParams);

        root.addView(toolbar);

        // --------------------------------------------------------
        // SEARCH
        // --------------------------------------------------------

        searchBox = new EditText(this);
        searchBox.setHint("Search notes...");
        searchBox.setTextSize(16);
        searchBox.setSingleLine(true);
        searchBox.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        GradientDrawable searchBackground = new GradientDrawable();
        searchBackground.setColor(Color.rgb(245, 245, 247));
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

        root.addView(searchBox, searchParams);

        searchBox.addTextChangedListener(new SimpleTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                displayNotes(s.toString());
            }
        });

        // --------------------------------------------------------
        // NOTE LIST
        // --------------------------------------------------------

        ScrollView scroll = new ScrollView(this);

        noteList = new LinearLayout(this);
        noteList.setOrientation(LinearLayout.VERTICAL);

        noteList.setPadding(
                dp(20),
                dp(4),
                dp(20),
                dp(100)
        );

        scroll.addView(noteList);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        setContentView(root);

        displayNotes("");
    }

    // ============================================================
    // DISPLAY NOTES
    // ============================================================

    private void displayNotes(String query) {

        if (noteList == null) {
            return;
        }

        noteList.removeAllViews();

        String q = query == null
                ? ""
                : query.trim().toLowerCase();

        ArrayList<Note> filtered = new ArrayList<>();

        for (Note note : notes) {

            if (q.isEmpty()
                    || note.title.toLowerCase().contains(q)
                    || note.body.toLowerCase().contains(q)) {

                filtered.add(note);
            }
        }

        // Pinned first, newest next
        Collections.sort(filtered, (a, b) -> {

            if (a.pinned != b.pinned) {
                return a.pinned ? -1 : 1;
            }

            return Long.compare(b.time, a.time);
        });

        if (filtered.isEmpty()) {

            TextView empty = new TextView(this);
            empty.setText(
                    q.isEmpty()
                            ? "No notes yet"
                            : "No matching notes"
            );

            empty.setTextSize(18);
            empty.setTextColor(Color.GRAY);
            empty.setGravity(Gravity.CENTER);

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

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(
                dp(18),
                dp(16),
                dp(18),
                dp(16)
        );

        GradientDrawable background = new GradientDrawable();
        background.setColor(LIGHT_PURPLE);
        background.setCornerRadius(dp(16));

        card.setBackground(background);
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

        // --------------------------------------------------------
        // TITLE
        // --------------------------------------------------------

        TextView title = new TextView(this);

        String displayTitle = note.title;

        if (note.pinned) {
            displayTitle = "📌 " + displayTitle;
        }

        title.setText(displayTitle);
        title.setTextSize(20);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        title.setTextColor(Color.BLACK);

        // --------------------------------------------------------
        // BODY
        // --------------------------------------------------------

        TextView body = new TextView(this);

        body.setText(note.body);
        body.setTextSize(16);
        body.setTextColor(Color.DKGRAY);
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

        card.addView(title);

        if (!note.body.isEmpty()) {
            card.addView(body);
        }

        // Tap = edit
        card.setOnClickListener(
                v -> showEditor(notes.indexOf(note))
        );

        // Long press = menu
        card.setOnLongClickListener(v -> {

            showNoteMenu(note);

            return true;
        });

        noteList.addView(card, cardParams);
    }

    // ============================================================
    // EDITOR
    // ============================================================

    private void showEditor(int index) {

        final boolean editing = index >= 0;

        final Note note;

        if (editing) {
            note = notes.get(index);
        } else {
            note = new Note();
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        // --------------------------------------------------------
        // EDITOR TOOLBAR
        // --------------------------------------------------------

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(8)
        );

        TextView back = new TextView(this);
        back.setText("‹");
        back.setTextSize(40);
        back.setTextColor(Color.BLACK);
        back.setGravity(Gravity.CENTER);

        back.setOnClickListener(v -> showMainScreen());

        toolbar.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(50),
                        dp(50)
                )
        );

        TextView heading = new TextView(this);

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
        heading.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        headingParams.setMargins(
                dp(8),
                0,
                0,
                0
        );

        toolbar.addView(
                heading,
                headingParams
        );

        root.addView(toolbar);

        // --------------------------------------------------------
        // TITLE INPUT
        // --------------------------------------------------------

        EditText titleInput = new EditText(this);

        titleInput.setHint("Title");
        titleInput.setText(note.title);
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

        // --------------------------------------------------------
        // BODY INPUT
        // --------------------------------------------------------

        EditText bodyInput = new EditText(this);

        bodyInput.setHint("Write your note...");
        bodyInput.setText(note.body);
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
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        root.addView(
                bodyInput,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        // --------------------------------------------------------
        // SAVE BUTTON
        // --------------------------------------------------------

        TextView saveButton = new TextView(this);

        saveButton.setText("Save");
        saveButton.setTextSize(18);
        saveButton.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        saveButton.setTextColor(Color.WHITE);
        saveButton.setGravity(Gravity.CENTER);

        GradientDrawable saveBackground =
                new GradientDrawable();

        saveBackground.setColor(PURPLE);
        saveBackground.setCornerRadius(dp(14));

        saveButton.setBackground(saveBackground);
        saveButton.setClickable(true);

        saveButton.setOnClickListener(v -> {

            note.title =
                    titleInput
                            .getText()
                            .toString()
                            .trim();

            if (note.title.isEmpty()) {
                note.title = "Untitled";
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
                dp(18)
        );

        root.addView(
                saveButton,
                saveParams
        );

        setContentView(root);
    }

    // ============================================================
    // NOTE MENU
    // ============================================================

    private void showNoteMenu(Note note) {

        String[] items = {
                note.pinned
                        ? "Unpin note"
                        : "Pin note",
                "Delete note"
        };

        new AlertDialog.Builder(this)
                .setTitle(note.title)
                .setItems(items, (dialog, which) -> {

                    if (which == 0) {

                        note.pinned =
                                !note.pinned;

                        saveNotes();

                        displayNotes(
                                searchBox == null
                                        ? ""
                                        : searchBox
                                                .getText()
                                                .toString()
                        );

                    } else if (which == 1) {

                        deleteNoteWithUndo(note);
                    }
                })
                .show();
    }

    // ============================================================
    // DELETE + UNDO
    // ============================================================

    private void deleteNoteWithUndo(Note note) {

        deletedIndex =
                notes.indexOf(note);

        deletedNote = note;

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

        displayNotes(currentSearch);

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
                        (dialog, which) -> {

                            if (deletedNote != null) {

                                int position =
                                        deletedIndex;

                                if (position < 0
                                        || position > notes.size()) {

                                    position =
                                            notes.size();
                                }

                                notes.add(
                                        position,
                                        deletedNote
                                );

                                saveNotes();

                                displayNotes(
                                        searchBox == null
                                                ? ""
                                                : searchBox
                                                        .getText()
                                                        .toString()
                                );

                                deletedNote = null;
                                deletedIndex = -1;
                            }
                        }
                )
                .show();
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
                .putString(                        "notes",
                        array.toString()
                )
                .apply();
    }

    private void loadNotes() {

        String data = prefs.getString(
                "notes",
                "[]"
        );

        try {

            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                Note n = new Note();

                n.title = obj.optString(
                        "title",
                        ""
                );

                n.body = obj.optString(
                        "body",
                        ""
                );

                n.pinned = obj.optBoolean(
                        "pinned",
                        false
                );

                n.time = obj.optLong(
                        "time",
                        System.currentTimeMillis()
                );

                notes.add(n);
            }

        } catch (Exception ignored) {
        }
    }

    private static class Note {

        String title = "";
        String body = "";
        boolean pinned = false;
        long time = System.currentTimeMillis();
    }

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
                android.text.Editable s
        ) {
        }
    }

    private static class GradientHelper {

        static android.graphics.drawable.GradientDrawable
        roundedBackground(
                int color,
                float radius
        ) {

            android.graphics.drawable.GradientDrawable drawable =
                    new android.graphics.drawable.GradientDrawable();

            drawable.setColor(color);
            drawable.setCornerRadius(radius);

            return drawable;
        }
    }
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

}
