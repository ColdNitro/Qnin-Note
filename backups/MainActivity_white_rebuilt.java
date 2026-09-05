package com.qnin.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.SharedPreferences;
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
    private ArrayList<Note> notes = new ArrayList<>();

    private static final int PURPLE = Color.rgb(103, 80, 164);
    private static final int LIGHT_PURPLE = Color.rgb(245, 242, 248);

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.rgb(45, 45, 45));

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        prefs = getSharedPreferences("qnin_notes", MODE_PRIVATE);
        loadNotes();
        showMainScreen();
    }

    private void showMainScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(dp(20), dp(28), dp(20), dp(20));

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(this);
        title.setText("Qnin Note");
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.BLACK);

        toolbar.addView(title, new LinearLayout.LayoutParams(
                0, dp(60), 1));

        Button add = makePurpleButton("+ New Note");
        toolbar.addView(add, new LinearLayout.LayoutParams(dp(150), dp(60)));
        add.setOnClickListener(v -> showEditor(-1));

        root.addView(toolbar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(70)));

        searchBox = new EditText(this);
        searchBox.setHint("Search notes...");
        searchBox.setHintTextColor(Color.GRAY);
        searchBox.setTextColor(Color.BLACK);
        searchBox.setTextSize(18);
        searchBox.setSingleLine(true);
        searchBox.setPadding(dp(20), 0, dp(20), 0);

        android.graphics.drawable.GradientDrawable searchBg =
                new android.graphics.drawable.GradientDrawable();
        searchBg.setColor(Color.rgb(248, 248, 248));
        searchBg.setCornerRadius(dp(18));
        searchBg.setStroke(dp(2), Color.LTGRAY);
        searchBox.setBackground(searchBg);

        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(60));
        searchParams.setMargins(0, dp(16), 0, dp(18));
        root.addView(searchBox, searchParams);

        searchBox.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                displayNotes(s.toString());
            }
        });

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        noteList = new LinearLayout(this);
        noteList.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(noteList, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(root);
        displayNotes("");
    }

    private Button makePurpleButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(18);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(Color.WHITE);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(8), 0, dp(8), 0);

        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setColor(PURPLE);
        bg.setCornerRadius(dp(18));
        button.setBackground(bg);

        return button;
    }

    private void displayNotes(String query) {
        noteList.removeAllViews();

        ArrayList<Note> filtered = new ArrayList<>();
        String q = query == null ? "" : query.toLowerCase();

        for (Note n : notes) {
            if (q.isEmpty()
                    || n.title.toLowerCase().contains(q)
                    || n.body.toLowerCase().contains(q)) {
                filtered.add(n);
            }
        }

        Collections.sort(filtered, (a, b) -> {
            if (a.pinned != b.pinned) return a.pinned ? -1 : 1;
            return Long.compare(b.time, a.time);
        });

        if (filtered.isEmpty()) {
            LinearLayout emptyBox = new LinearLayout(this);
            emptyBox.setOrientation(LinearLayout.VERTICAL);
            emptyBox.setGravity(Gravity.CENTER);

            TextView emoji = new TextView(this);
            emoji.setText("📝");
            emoji.setTextSize(58);
            emoji.setGravity(Gravity.CENTER);

            TextView emptyTitle = new TextView(this);
            emptyTitle.setText("No notes yet");
            emptyTitle.setTextSize(24);
            emptyTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            emptyTitle.setTextColor(Color.DKGRAY);
            emptyTitle.setGravity(Gravity.CENTER);

            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("Tap + New Note to write\nsomething.");
            emptyMessage.setTextSize(17);
            emptyMessage.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            emptyMessage.setTextColor(Color.GRAY);
            emptyMessage.setGravity(Gravity.CENTER);

            emptyBox.addView(emoji, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(100)));
            emptyBox.addView(emptyTitle, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(55)));
            emptyBox.addView(emptyMessage, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(80)));

            noteList.addView(emptyBox, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(330)));
            return;
        }

        for (Note n : filtered) addNoteView(n);
    }

    private void addNoteView(Note note) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(20), dp(18), dp(20), dp(18));

        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setColor(LIGHT_PURPLE);
        bg.setCornerRadius(dp(16));
        card.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(14));

        TextView title = new TextView(this);
        title.setText(note.pinned ? "📌 " + note.title : note.title);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.BLACK);

        TextView body = new TextView(this);
        body.setText(note.body);
        body.setTextSize(16);
        body.setTextColor(Color.DKGRAY);
        body.setMaxLines(4);
        body.setPadding(0, dp(8), 0, 0);

        card.addView(title);
        card.addView(body);

        card.setOnClickListener(v -> showEditor(notes.indexOf(note)));
        card.setOnLongClickListener(v -> {
            showNoteMenu(note);
            return true;
        });

        noteList.addView(card, params);
    }

    private void showEditor(int index) {
        final boolean editing = index >= 0;
        final Note note = editing ? notes.get(index) : new Note();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(dp(20), dp(12), dp(20), dp(16));

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);

        Button back = makePurpleButton("←");
        toolbar.addView(back, new LinearLayout.LayoutParams(dp(90), dp(60)));

        TextView heading = new TextView(this);
        heading.setText(editing ? "Edit Note" : "New Note");
        heading.setTextSize(26);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setTextColor(Color.BLACK);
        heading.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(
                0, dp(60), 1);
        headingParams.setMargins(dp(16), 0, 0, 0);
        toolbar.addView(heading, headingParams);

        root.addView(toolbar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(70)));

        back.setOnClickListener(v -> showMainScreen());

        EditText title = new EditText(this);
        title.setHint("Title");
        title.setText(note.title);
        title.setTextSize(22);
        title.setTextColor(Color.BLACK);
        title.setHintTextColor(Color.GRAY);
        title.setSingleLine(true);
        title.setPadding(dp(4), 0, dp(4), 0);

        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(65)));

        EditText body = new EditText(this);
        body.setHint("Write your note...");
        body.setText(note.body);
        body.setTextSize(18);
        body.setTextColor(Color.BLACK);
        body.setHintTextColor(Color.GRAY);
        body.setGravity(Gravity.TOP);
        body.setPadding(dp(4), dp(16), dp(4), dp(10));
        body.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        root.addView(body, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        Button save = makePurpleButton("Save Note");
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(64));
        saveParams.setMargins(0, dp(12), 0, dp(8));
        root.addView(save, saveParams);

        save.setOnClickListener(v -> {
            note.title = title.getText().toString().trim();
            if (note.title.isEmpty()) note.title = "Untitled";
            note.body = body.getText().toString();
            note.time = System.currentTimeMillis();

            if (!editing) notes.add(note);

            saveNotes();
            showMainScreen();
        });

        setContentView(root);
    }

    private void showNoteMenu(Note note) {
        String[] items = {
                note.pinned ? "Unpin note" : "Pin note",
                "Delete note"
        };

        new AlertDialog.Builder(this)
                .setTitle(note.title)
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        note.pinned = !note.pinned;
                        saveNotes();
                        displayNotes(searchBox == null ? "" :
                                searchBox.getText().toString());
                    } else {
                        deleteNoteWithUndo(note);
                    }
                })
                .show();
    }

    private void deleteNoteWithUndo(Note note) {
        int deletedIndex = notes.indexOf(note);
        if (deletedIndex < 0) return;

        notes.remove(deletedIndex);
        saveNotes();

        String currentSearch = searchBox == null ? "" :
                searchBox.getText().toString();
        displayNotes(currentSearch);

        new AlertDialog.Builder(this)
                .setTitle("Note deleted")
                .setMessage("The note was deleted.")
                .setNegativeButton("OK", null)
                .setPositiveButton("UNDO", (dialog, which) -> {
                    if (deletedIndex >= 0 && deletedIndex <= notes.size()) {
                        notes.add(deletedIndex, note);
                    } else {
                        notes.add(note);
                    }

                    saveNotes();

                    String search = searchBox == null ? "" :
                            searchBox.getText().toString();
                    displayNotes(search);
                })
                .show();
    }

    private void saveNotes() {
        JSONArray array = new JSONArray();

        try {
            for (Note n : notes) {
                JSONObject obj = new JSONObject();
                obj.put("title", n.title);
                obj.put("body", n.body);
                obj.put("pinned", n.pinned);
                obj.put("time", n.time);
                array.put(obj);
            }
        } catch (Exception ignored) {
        }

        prefs.edit().putString("notes", array.toString()).apply();
    }

    private void loadNotes() {
        String data = prefs.getString("notes", "[]");

        try {
            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Note n = new Note();
                n.title = obj.optString("title", "");
                n.body = obj.optString("body", "");
                n.pinned = obj.optBoolean("pinned", false);
                n.time = obj.optLong("time", System.currentTimeMillis());

                notes.add(n);
            }
        } catch (Exception ignored) {
        }
    }

    static class Note {
        String title = "";
        String body = "";
        boolean pinned = false;
        long time = System.currentTimeMillis();
    }

    abstract static class SimpleTextWatcher
            implements android.text.TextWatcher {

        @Override
        public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(
                CharSequence s, int start, int before, int count) {
        }
    }
}
