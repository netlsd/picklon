package id.co.picklon.utils;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Identify {
    private static String identity;

    public static String getIdentity() {
        File path = getAlbumStorageDir(".astore_identity");
        File file = new File(path, ".identify");
        String uuid = null;

        if (!isExternalStorageWritable()) {
            return null;
        }

        if (!path.exists()) {
            path.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream os = new FileOutputStream(file);
                byte[] contentInBytes = genIdentity().getBytes();
                os.write(contentInBytes);
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(file));

            byte[] by = new byte[1024];
            int len = in.read(by);

            uuid = new String(by, 0, len).trim();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uuid;
    }

    private static String genIdentity() {
        if (identity == null) {
            identity = UUID.randomUUID().toString();
        }
        return identity;
    }

    private static File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStorageDirectory(), albumName);
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
