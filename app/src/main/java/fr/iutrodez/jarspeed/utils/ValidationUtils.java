package fr.iutrodez.jarspeed.utils;

import android.content.Context;
import android.widget.EditText;
import androidx.core.content.ContextCompat;

import com.example.jarspeed.R;

public class ValidationUtils {

    // Checks if an EditText is empty
    public static boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    // Method to check email format
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    // Check password strength
    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";
        return password.matches(passwordRegex);
    }

    // Sets an error border for invalid EditText fields
    public static void setErrorBorder(Context context, EditText editText) {
        int paddingLeft = editText.getPaddingLeft();
        int paddingTop = editText.getPaddingTop();
        int paddingRight = editText.getPaddingRight();
        int paddingBottom = editText.getPaddingBottom();
        editText.setBackground(ContextCompat.getDrawable(context, R.drawable.edittext_error_border));
        editText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    // Resets the borders and hint text color of EditText fields
    public static void resetFieldBorders(Context context, EditText... editTexts) {
        for (EditText editText : editTexts) {
            int paddingLeft = editText.getPaddingLeft();
            int paddingTop = editText.getPaddingTop();
            int paddingRight = editText.getPaddingRight();
            int paddingBottom = editText.getPaddingBottom();
            editText.setBackground(ContextCompat.getDrawable(context, R.drawable.edittext_background));
            editText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }
}
