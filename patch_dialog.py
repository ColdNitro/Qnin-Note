from pathlib import Path

p = Path("app/src/main/java/com/qnin/note/MainActivity.java")
s = p.read_text()
m = s.index("private void deleteNoteWithUndo")
a = s.index("        new AlertDialog.Builder(this, darkMode", m)
b = s.index("                .show();", a) + len("                .show();")

new = """        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Note deleted")
                .setMessage("The note was deleted.")
                .setNegativeButton("OK", null)
                .setPositiveButton("UNDO", (dialogInterface, which) -> {
                    if (deletedNote != null) {
                        int position = deletedIndex;
                        if (position < 0 || position > notes.size()) {
                            position = notes.size();
                        }
                        notes.add(position, deletedNote);
                        saveNotes();
                        displayNotes(searchBox == null ? "" : searchBox.getText().toString());
                        deletedNote = null;
                        deletedIndex = -1;
                    }
                })
                .create();

        dialog.setOnShowListener(view -> {
            int bg = darkMode ? Color.rgb(24, 24, 30) : Color.WHITE;
            int fg = darkMode ? Color.WHITE : Color.rgb(25, 24, 30);
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(bg));

            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            android.widget.TextView title = dialog.findViewById(titleId);
            if (title != null) title.setTextColor(fg);

            int messageId = getResources().getIdentifier("message", "id", "android");
            android.widget.TextView message = dialog.findViewById(messageId);
            if (message != null) message.setTextColor(fg);

            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(Color.rgb(103, 80, 164));
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(Color.rgb(103, 80, 164));
        });

        dialog.show();"""

s = s[:a] + new + s[b:]
p.write_text(s)
print("Dialog styling patched.")
