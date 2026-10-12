from pathlib import Path

p = Path("app/src/main/java/com/qnin/note/MainActivity.java")
s = p.read_text()

method = s.index("private void deleteNoteWithUndo")
start = s.index("new AlertDialog.Builder(this)", method)

old = "new AlertDialog.Builder(this)"
new = """new AlertDialog.Builder(this, darkMode
                ? android.R.style.Theme_Material_Dialog_Alert
                : android.R.style.Theme_Material_Light_Dialog_Alert)"""

s = s[:start] + s[start:].replace(old, new, 1)
p.write_text(s)

print("Delete dialog theme updated.")
