package pro.siper.myzuka;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Андрей on 19.01.2017.
 */

public class Helpers {
    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
