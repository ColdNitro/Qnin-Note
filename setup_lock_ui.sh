#!/data/data/com.termux/files/usr/bin/bash
set -e

MANIFEST=$(find . -name "AndroidManifest.xml" -not -path "*/build/*" | head -n1)
if [ -z "$MANIFEST" ]; then
  echo "Could not find AndroidManifest.xml — run this from your QninNote project root."
  exit 1
fi
MODULE_ROOT=$(dirname $(dirname $(dirname "$MANIFEST")))
RES="$MODULE_ROOT/res"
mkdir -p "$RES/drawable" "$RES/layout" "$RES/values"

JAVA_FILE=$(find "$MODULE_ROOT/java" -name "MainActivity.java" 2>/dev/null | head -n1)
if [ -z "$JAVA_FILE" ]; then
  JAVA_FILE=$(find . -name "MainActivity.java" -not -path "*/build/*" | head -n1)
fi
if [ -z "$JAVA_FILE" ]; then
  echo "Could not find MainActivity.java — you'll need to create the Java files manually."
  exit 1
fi
JAVA_DIR=$(dirname "$JAVA_FILE")
PACKAGE=$(grep -m1 "^package" "$JAVA_FILE" | sed 's/package //;s/;//')

echo "Using resources dir: $RES"
echo "Using java dir: $JAVA_DIR (package: $PACKAGE)"

# ---------- colors ----------
cat > "$RES/values/lock_colors.xml" << 'EOF'
<resources>
    <color name="bg_deep">#0B0912</color>
    <color name="surface_dialog">#17131F</color>
    <color name="surface_input">#1E1930</color>
    <color name="purple_primary">#7C5CFA</color>
    <color name="purple_light">#9B82FF</color>
    <color name="purple_glow">#4A3B80</color>
    <color name="text_primary">#FFFFFF</color>
    <color name="text_secondary">#A79FBD</color>
    <color name="divider">#2A2438</color>
</resources>
EOF

# ---------- drawables ----------
cat > "$RES/drawable/bg_dialog_card.xml" << 'EOF'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="@color/surface_dialog"/>
    <corners android:radius="28dp"/>
</shape>
EOF

cat > "$RES/drawable/bg_icon_circle.xml" << 'EOF'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
    <gradient android:type="radial" android:gradientRadius="60"
        android:centerColor="@color/purple_glow" android:endColor="@color/surface_dialog"/>
    <stroke android:width="1.5dp" android:color="@color/purple_primary"/>
</shape>
EOF

cat > "$RES/drawable/bg_input_field.xml" << 'EOF'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="@color/surface_input"/>
    <corners android:radius="16dp"/>
</shape>
EOF

cat > "$RES/drawable/bg_button_purple.xml" << 'EOF'
<ripple xmlns:android="http://schemas.android.com/apk/res/android" android:color="#33FFFFFF">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/purple_primary"/>
            <corners android:radius="16dp"/>
        </shape>
    </item>
</ripple>
EOF

# ---------- icons ----------
cat > "$RES/drawable/ic_lock_closed.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp" android:height="48dp" android:viewportWidth="48" android:viewportHeight="48">
    <path android:fillColor="@color/purple_light"
        android:pathData="M24,4C18.5,4 14,8.5 14,14V20H12C10.9,20 10,20.9 10,22V40C10,41.1 10.9,42 12,42H36C37.1,42 38,41.1 38,40V22C38,20.9 37.1,20 36,20H34V14C34,8.5 29.5,4 24,4ZM24,8C27.3,8 30,10.7 30,14V20H18V14C18,10.7 20.7,8 24,8ZM24,27C25.7,27 27,28.3 27,30C27,31.1 26.4,32 25.6,32.5L26,36H22L22.4,32.5C21.6,32 21,31.1 21,30C21,28.3 22.3,27 24,27Z"/>
</vector>
EOF

cat > "$RES/drawable/ic_swap_arrows.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp" android:height="48dp" android:viewportWidth="48" android:viewportHeight="48">
    <path android:fillColor="@color/purple_light"
        android:pathData="M14,10V16H20L16.5,12.5C19,10.4 22.2,9 26,9C33.2,9 39,14.8 39,22H35C35,17 30.9,13 26,13C23.3,13 20.9,14.1 19.1,15.9L23,20H14V10Z"/>
    <path android:fillColor="@color/purple_light"
        android:pathData="M34,38V32H28L31.5,35.5C29,37.6 25.8,39 22,39C14.8,39 9,33.2 9,26H13C13,31 17.1,35 22,35C24.7,35 27.1,33.9 28.9,32.1L25,28H34V38Z"/>
</vector>
EOF

cat > "$RES/drawable/ic_shield_plus.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp" android:height="48dp" android:viewportWidth="48" android:viewportHeight="48">
    <path android:fillColor="@color/purple_light"
        android:pathData="M24,4L40,10V22C40,32.6 33.4,40.8 24,44C14.6,40.8 8,32.6 8,22V10ZM24,8.4L12,12.8V22C12,30.4 17.1,36.9 24,39.6C30.9,36.9 36,30.4 36,22V12.8Z"/>
    <path android:fillColor="@color/purple_light"
        android:pathData="M22,16H26V22H32V26H26V32H22V26H16V22H22Z"/>
</vector>
EOF

cat > "$RES/drawable/ic_check_circle.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="40dp" android:height="40dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF" android:pathData="M9,16.2L4.8,12L3.4,13.4L9,19L21,7L19.6,5.6Z"/>
</vector>
EOF

cat > "$RES/drawable/ic_eye.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@color/text_secondary"
        android:pathData="M12,5C7,5 2.7,8.1 1,12C2.7,15.9 7,19 12,19C17,19 21.3,15.9 23,12C21.3,8.1 17,5 12,5ZM12,16.5C9.5,16.5 7.5,14.5 7.5,12C7.5,9.5 9.5,7.5 12,7.5C14.5,7.5 16.5,9.5 16.5,12C16.5,14.5 14.5,16.5 12,16.5ZM12,9.5C10.6,9.5 9.5,10.6 9.5,12C9.5,13.4 10.6,14.5 12,14.5C13.4,14.5 14.5,13.4 14.5,12C14.5,10.6 13.4,9.5 12,9.5Z"/>
</vector>
EOF

cat > "$RES/drawable/ic_eye_off.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@color/text_secondary"
        android:pathData="M12,5C7,5 2.7,8.1 1,12C1.9,13.9 3.3,15.5 5,16.7L2.4,19.3L3.8,20.7L21,3.5L19.6,2.1L16.4,5.3C15,4.5 13.5,4 12,4M12,7.5C14.5,7.5 16.5,9.5 16.5,12C16.5,12.7 16.3,13.4 16,14L9.6,7.6C10.3,7.5 11.1,7.5 12,7.5M6.3,8.5C4.7,9.7 3.4,11.3 2.5,13C4.1,16 7.7,18.5 12,18.5C12.9,18.5 13.8,18.4 14.6,18.1L12.6,16.1C12.4,16.1 12.2,16.2 12,16.2C9.5,16.2 7.5,14.2 7.5,11.7C7.5,11.5 7.5,11.3 7.6,11.1L6.3,8.5Z"/>
</vector>
EOF

# ---------- layouts ----------
cat > "$RES/layout/dialog_enable_password.xml" << 'EOF'
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="300dp" android:layout_height="wrap_content"
    android:orientation="vertical" android:gravity="center_horizontal"
    android:background="@drawable/bg_dialog_card" android:padding="24dp">

    <FrameLayout android:layout_width="72dp" android:layout_height="72dp" android:background="@drawable/bg_icon_circle">
        <ImageView android:layout_width="34dp" android:layout_height="34dp" android:layout_gravity="center" android:src="@drawable/ic_lock_closed"/>
    </FrameLayout>

    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="16dp"
        android:text="Password lock" android:textColor="@color/text_primary" android:textSize="20sp" android:textStyle="bold"/>
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="4dp"
        android:text="Protect your notes with a password." android:textColor="@color/text_secondary" android:textSize="13sp" android:gravity="center"/>

    <LinearLayout android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="20dp"
        android:background="@drawable/bg_input_field" android:orientation="horizontal" android:gravity="center_vertical" android:paddingHorizontal="14dp">
        <ImageView android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_lock_closed" android:layout_marginEnd="10dp"/>
        <EditText android:id="@+id/etPassword" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:background="@null" android:hint="Password" android:textColorHint="@color/text_secondary" android:textColor="@color/text_primary"
            android:inputType="textPassword" android:textSize="14sp"/>
        <ImageView android:id="@+id/ivToggle1" android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_eye_off" android:clickable="true" android:focusable="true"/>
    </LinearLayout>

    <LinearLayout android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="10dp"
        android:background="@drawable/bg_input_field" android:orientation="horizontal" android:gravity="center_vertical" android:paddingHorizontal="14dp">
        <ImageView android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_lock_closed" android:layout_marginEnd="10dp"/>
        <EditText android:id="@+id/etConfirmPassword" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:background="@null" android:hint="Confirm password" android:textColorHint="@color/text_secondary" android:textColor="@color/text_primary"
            android:inputType="textPassword" android:textSize="14sp"/>
        <ImageView android:id="@+id/ivToggle2" android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_eye_off" android:clickable="true" android:focusable="true"/>
    </LinearLayout>

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginTop="12dp" android:orientation="horizontal" android:gravity="center_vertical">
        <CheckBox android:id="@+id/cbMinLength" android:layout_width="wrap_content" android:layout_height="wrap_content" android:checked="true"/>
        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="Must be at least 4 characters." android:textColor="@color/text_secondary" android:textSize="12sp"/>
    </LinearLayout>

    <View android:layout_width="match_parent" android:layout_height="1dp" android:background="@color/divider" android:layout_marginTop="20dp"/>

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginTop="16dp" android:orientation="horizontal" android:gravity="center_vertical">
        <TextView android:id="@+id/btnCancel" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:text="CANCEL" android:textColor="@color/text_secondary" android:textStyle="bold" android:gravity="center" android:clickable="true" android:focusable="true"/>
        <TextView android:id="@+id/btnEnable" android:layout_width="0dp" android:layout_height="48dp" android:layout_weight="1"
            android:text="ENABLE" android:textColor="@color/text_primary" android:textStyle="bold" android:gravity="center"
            android:background="@drawable/bg_button_purple" android:clickable="true" android:focusable="true"/>
    </LinearLayout>
</LinearLayout>
EOF

cat > "$RES/layout/dialog_change_password.xml" << 'EOF'
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="300dp" android:layout_height="wrap_content"
    android:orientation="vertical" android:gravity="center_horizontal"
    android:background="@drawable/bg_dialog_card" android:padding="24dp">

    <FrameLayout android:layout_width="72dp" android:layout_height="72dp" android:background="@drawable/bg_icon_circle">
        <ImageView android:layout_width="34dp" android:layout_height="34dp" android:layout_gravity="center" android:src="@drawable/ic_swap_arrows"/>
    </FrameLayout>

    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="16dp"
        android:text="Change password" android:textColor="@color/text_primary" android:textSize="20sp" android:textStyle="bold"/>
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="4dp"
        android:text="Enter your current password to change it." android:textColor="@color/text_secondary" android:textSize="13sp" android:gravity="center"/>

    <LinearLayout android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="20dp"
        android:background="@drawable/bg_input_field" android:orientation="horizontal" android:gravity="center_vertical" android:paddingHorizontal="14dp">
        <ImageView android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_lock_closed" android:layout_marginEnd="10dp"/>
        <EditText android:id="@+id/etCurrentPassword" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:background="@null" android:hint="Current password" android:textColorHint="@color/text_secondary" android:textColor="@color/text_primary"
            android:inputType="textPassword" android:textSize="14sp"/>
        <ImageView android:id="@+id/ivToggleCurrent" android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_eye_off" android:clickable="true" android:focusable="true"/>
    </LinearLayout>

    <LinearLayout android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="10dp"
        android:background="@drawable/bg_input_field" android:orientation="horizontal" android:gravity="center_vertical" android:paddingHorizontal="14dp">
        <ImageView android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_lock_closed" android:layout_marginEnd="10dp"/>
        <EditText android:id="@+id/etNewPassword" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:background="@null" android:hint="New password" android:textColorHint="@color/text_secondary" android:textColor="@color/text_primary"
            android:inputType="textPassword" android:textSize="14sp"/>
        <ImageView android:id="@+id/ivToggleNew" android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_eye_off" android:clickable="true" android:focusable="true"/>
    </LinearLayout>

    <LinearLayout android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="10dp"
        android:background="@drawable/bg_input_field" android:orientation="horizontal" android:gravity="center_vertical" android:paddingHorizontal="14dp">
        <ImageView android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_lock_closed" android:layout_marginEnd="10dp"/>
        <EditText android:id="@+id/etConfirmNewPassword" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:background="@null" android:hint="Confirm new password" android:textColorHint="@color/text_secondary" android:textColor="@color/text_primary"
            android:inputType="textPassword" android:textSize="14sp"/>
        <ImageView android:id="@+id/ivToggleConfirmNew" android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_eye_off" android:clickable="true" android:focusable="true"/>
    </LinearLayout>

    <View android:layout_width="match_parent" android:layout_height="1dp" android:background="@color/divider" android:layout_marginTop="20dp"/>

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginTop="16dp" android:orientation="horizontal" android:gravity="center_vertical">
        <TextView android:id="@+id/btnCancel" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:text="CANCEL" android:textColor="@color/text_secondary" android:textStyle="bold" android:gravity="center" android:clickable="true" android:focusable="true"/>
        <TextView android:id="@+id/btnEnable" android:layout_width="0dp" android:layout_height="48dp" android:layout_weight="1"
            android:text="ENABLE" android:textColor="@color/text_primary" android:textStyle="bold" android:gravity="center"
            android:background="@drawable/bg_button_purple" android:clickable="true" android:focusable="true"/>
    </LinearLayout>
</LinearLayout>
EOF

cat > "$RES/layout/dialog_disable_password.xml" << 'EOF'
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="300dp" android:layout_height="wrap_content"
    android:orientation="vertical" android:gravity="center_horizontal"
    android:background="@drawable/bg_dialog_card" android:padding="24dp">

    <FrameLayout android:layout_width="72dp" android:layout_height="72dp" android:background="@drawable/bg_icon_circle">
        <ImageView android:layout_width="34dp" android:layout_height="34dp" android:layout_gravity="center" android:src="@drawable/ic_shield_plus"/>
    </FrameLayout>

    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="16dp"
        android:text="Disable password" android:textColor="@color/text_primary" android:textSize="20sp" android:textStyle="bold"/>
    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="4dp"
        android:text="Enter your current password to disable protection." android:textColor="@color/text_secondary" android:textSize="13sp" android:gravity="center"/>

    <LinearLayout android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="20dp"
        android:background="@drawable/bg_input_field" android:orientation="horizontal" android:gravity="center_vertical" android:paddingHorizontal="14dp">
        <ImageView android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_lock_closed" android:layout_marginEnd="10dp"/>
        <EditText android:id="@+id/etCurrentPassword" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:background="@null" android:hint="Current password" android:textColorHint="@color/text_secondary" android:textColor="@color/text_primary"
            android:inputType="textPassword" android:textSize="14sp"/>
        <ImageView android:id="@+id/ivToggleCurrent" android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_eye_off" android:clickable="true" android:focusable="true"/>
    </LinearLayout>

    <View android:layout_width="match_parent" android:layout_height="1dp" android:background="@color/divider" android:layout_marginTop="20dp"/>

    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content" android:layout_marginTop="16dp" android:orientation="horizontal" android:gravity="center_vertical">
        <TextView android:id="@+id/btnCancel" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
            android:text="CANCEL" android:textColor="@color/text_secondary" android:textStyle="bold" android:gravity="center" android:clickable="true" android:focusable="true"/>
        <TextView android:id="@+id/btnDisable" android:layout_width="0dp" android:layout_height="48dp" android:layout_weight="1"
            android:text="DISABLE" android:textColor="@color/text_primary" android:textStyle="bold" android:gravity="center"
            android:background="@drawable/bg_button_purple" android:clickable="true" android:focusable="true"/>
    </LinearLayout>
</LinearLayout>
EOF

cat > "$RES/layout/activity_lock.xml" << 'EOF'
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent" android:background="@color/bg_deep">

    <LinearLayout android:id="@+id/layoutLocked" android:layout_width="match_parent" android:layout_height="wrap_content"
        android:layout_gravity="center" android:orientation="vertical" android:gravity="center_horizontal" android:paddingHorizontal="32dp">

        <FrameLayout android:layout_width="88dp" android:layout_height="88dp" android:background="@drawable/bg_icon_circle">
            <ImageView android:layout_width="42dp" android:layout_height="42dp" android:layout_gravity="center" android:src="@drawable/ic_lock_closed"/>
        </FrameLayout>

        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="20dp"
            android:text="Qnin Note locked" android:textColor="@color/text_primary" android:textSize="22sp" android:textStyle="bold"/>
        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="4dp"
            android:text="Enter your password to continue." android:textColor="@color/text_secondary" android:textSize="13sp"/>

        <LinearLayout android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="28dp"
            android:background="@drawable/bg_input_field" android:orientation="horizontal" android:gravity="center_vertical" android:paddingHorizontal="14dp">
            <ImageView android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_lock_closed" android:layout_marginEnd="10dp"/>
            <EditText android:id="@+id/etUnlockPassword" android:layout_width="0dp" android:layout_height="wrap_content" android:layout_weight="1"
                android:background="@null" android:hint="Password" android:textColorHint="@color/text_secondary" android:textColor="@color/text_primary" android:inputType="textPassword"/>
            <ImageView android:id="@+id/ivToggleUnlock" android:layout_width="18dp" android:layout_height="18dp" android:src="@drawable/ic_eye_off" android:clickable="true" android:focusable="true"/>
        </LinearLayout>

        <TextView android:id="@+id/btnUnlock" android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="16dp"
            android:background="@drawable/bg_button_purple" android:gravity="center" android:text="UNLOCK" android:textColor="@color/text_primary" android:textStyle="bold"/>
    </LinearLayout>

    <LinearLayout android:id="@+id/layoutWelcome" android:layout_width="match_parent" android:layout_height="wrap_content"
        android:layout_gravity="center" android:orientation="vertical" android:gravity="center_horizontal" android:paddingHorizontal="32dp" android:visibility="gone">

        <FrameLayout android:layout_width="88dp" android:layout_height="88dp" android:background="@drawable/bg_button_purple">
            <ImageView android:layout_width="42dp" android:layout_height="42dp" android:layout_gravity="center" android:src="@drawable/ic_check_circle"/>
        </FrameLayout>

        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="20dp"
            android:text="Welcome back!" android:textColor="@color/text_primary" android:textSize="22sp" android:textStyle="bold"/>
        <TextView android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="4dp"
            android:text="You're all set." android:textColor="@color/text_secondary" android:textSize="13sp"/>

        <TextView android:id="@+id/btnContinue" android:layout_width="match_parent" android:layout_height="52dp" android:layout_marginTop="24dp"
            android:background="@drawable/bg_button_purple" android:gravity="center" android:text="CONTINUE" android:textColor="@color/text_primary" android:textStyle="bold"/>
    </LinearLayout>
</FrameLayout>
EOF

# ---------- Java: PasswordLockManager ----------
cat > "$JAVA_DIR/PasswordLockManager.java" << EOF
package $PACKAGE;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class PasswordLockManager {
    private static final String PREFS = "qnin_prefs";
    private static final String KEY_HASH = "password_hash";
    private static final String KEY_ENABLED = "password_lock_enabled";

    public static boolean isEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false);
    }

    public static void setPassword(Context ctx, String password) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_HASH, hash(password)).putBoolean(KEY_ENABLED, true).apply();
    }

    public static boolean checkPassword(Context ctx, String password) {
        String stored = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_HASH, "");
        return stored.equals(hash(password));
    }

    public static void disable(Context ctx) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(KEY_HASH).putBoolean(KEY_ENABLED, false).apply();
    }

    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
EOF

# ---------- Java: PasswordDialogHelper ----------
cat > "$JAVA_DIR/PasswordDialogHelper.java" << EOF
package $PACKAGE;

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
EOF

# ---------- Java: LockActivity ----------
cat > "$JAVA_DIR/LockActivity.java" << EOF
package $PACKAGE;

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
        View layoutWelcome = findViewById(R.id.layoutWelcome);
        EditText etPassword = findViewById(R.id.etUnlockPassword);
        ImageView ivToggle = findViewById(R.id.ivToggleUnlock);
        TextView btnUnlock = findViewById(R.id.btnUnlock);
        TextView btnContinue = findViewById(R.id.btnContinue);

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
                layoutLocked.setVisibility(View.GONE);
                layoutWelcome.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            }
        });

        btnContinue.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // block back button so the app stays locked
    }
}
EOF

echo ""
echo "Done. Created:"
echo "  - $RES/values/lock_colors.xml"
echo "  - $RES/drawable/ (8 files)"
echo "  - $RES/layout/ (4 files)"
echo "  - $JAVA_DIR/PasswordLockManager.java"
echo "  - $JAVA_DIR/PasswordDialogHelper.java"
echo "  - $JAVA_DIR/LockActivity.java"
echo ""
echo "One manual step left: add this line inside <application> in AndroidManifest.xml:"
echo "  <activity android:name=\".LockActivity\" />"
