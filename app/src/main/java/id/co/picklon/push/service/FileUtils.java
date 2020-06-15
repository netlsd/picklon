package id.co.picklon.push.service;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import id.co.picklon.utils.ShareUtils;

public class FileUtils {
    public static void deleteAllFile(File path) {
        // get the folder list
        File[] array = path.listFiles();

        for (File file : array) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String getVersionUri(Context context) {
        return "/app_version";
    }

    public static void putVersion(Context context, String version) {
        try {
            PrintWriter writer = new PrintWriter(getVersionUri(context), "UTF-8");
            writer.println(version);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getVersion(Context context) {
        String version = "";

        try {
            InputStream fis = new FileInputStream(getVersionUri(context));
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            version = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (version.equals("")) {
            version = "0.0";
        }

        return version;
    }

    public static void putInitVersion(Context context) {
        File file = new File(getVersionUri(context));
        if (!file.exists()) {
            putVersion(context, "0.0");
        }
    }

    public static void copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();

        try {
            InputStream in = assetManager.open("picklon_share.png");
            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + ShareUtils.SHARE_FILENAME);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
