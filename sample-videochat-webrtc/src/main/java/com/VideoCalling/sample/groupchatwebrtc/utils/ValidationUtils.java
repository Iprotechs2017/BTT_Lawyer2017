package com.VideoCalling.sample.groupchatwebrtc.utils;

import android.content.Context;
import android.widget.EditText;

import com.VideoCalling.sample.groupchatwebrtc.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tereha on 03.06.16.
 */
public class ValidationUtils {

    private static boolean isEnteredTextValid(Context context, EditText editText, int resFieldName, int maxLength, boolean checkName) {

        boolean isCorrect;
        Pattern p;
        if (checkName) {
            p = Pattern.compile("^[a-zA-Z][a-zA-Z 0-9]{2," + (maxLength - 1) + "}+$");
        } else {
            p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{2," + (maxLength - 1) + "}+$");
        }

        Matcher m = p.matcher(editText.getText().toString().trim());
        isCorrect = m.matches();

        if (!isCorrect) {
            editText.setError(String.format(context.getString(R.string.error_name_must_not_contain_special_characters_from_app),
                    context.getString(resFieldName),
                    maxLength));
            return false;
        } else {
            return true;
        }
    }

    public static boolean isUserNameValid(Context context, EditText editText) {
        return isEnteredTextValid(context, editText, R.string.field_name_user_name, 20, true);
    }

    public static boolean isRoomNameValid(Context context, EditText editText) {
        return isEnteredTextValid(context, editText, R.string.field_name_chat_room_name, 15, false);
    }
    public static boolean isMailidValid(Context context, EditText editText) {
        return isEnteredTextValid(context, editText, R.string.field_name_chat_room_name, 15, false);
    }
    public static boolean emailValidator(Context context,EditText editText) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(editText.getText().toString());
        if (!matcher.matches())
        {
            editText.setError("Enter valid mailId");
        }
        else
        {
            editText.setError(null);
        }
    return matcher.matches();
}
}
