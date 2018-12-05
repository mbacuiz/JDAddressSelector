package chihane.jdaddressselector.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Create by jiancheung on 2018/12/5
 * <p>
 * 　＃＃＃　　＃＃＃＃　　　　　　＃＃　　　　　　　　＃＃＃　＃＃＃
 * 　　＃＃　　＃＃　　　　　　　　＃＃＃　　　　　　　　＃＃　＃＃
 * 　　＃＃＃　＃＃　　　　　　　　＃＃＃　　　　　　　　＃＃＃＃
 * 　　＃＃＃＃＃＃　　　　　　　＃＃＃＃＃　　　　　　　　＃＃＃
 * 　　＃＃＃＃＃＃　　　　　　　＃＃＃＃＃　　　　　　　＃＃＃＃
 * 　　＃　＃＃　＃　　　　　　＃＃　　＃＃　　　　　　　＃＃　＃＃
 * 　＃＃＃＃＃＃＃＃＃　　　＃＃＃　　＃＃＃　　　　＃＃＃＃　＃＃＃
 */
public class ResourceUtils {
    private static final int BUFFER_SIZE = 8192;

    public static String readAssets2String(Context context, final String assetsFilePath, final String charsetName) {
        InputStream is;
        try {
            is = context.getApplicationContext().getAssets().open(assetsFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        byte[] bytes = is2Bytes(is);
        if (bytes == null) return null;
        if (isSpace(charsetName)) {
            return new String(bytes);
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }


    private static byte[] is2Bytes(final InputStream is) {
        if (is == null) return null;
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            byte[] b = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point.y;
    }

}
