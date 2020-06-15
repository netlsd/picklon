package id.co.picklon.push.service;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UUID {
    private static final String TAG = "UUID TAG";

    private static String identity;

    public static String getIdentity() {
        if (FileUtils.isExternalStorageWritable()) {
            File path = getAlbumStorageDir(".apush_identity");
            File file = new File(path, ".identify");

            if (file.exists()) {
                InputStream in = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(file));

                    byte[] by = new byte[1024];
                    int len = in.read(by);

                    String uuid = new String(by, 0, len).trim();

                    in.close();

                    return uuid;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            	UUID.genUuidFile();
            	UUID.getIdentity();
                return null;
            }
        }
        return null;
    }

    public static void genUuidFile() {
        if (FileUtils.isExternalStorageWritable()) {
            File path = getAlbumStorageDir(".apush_identity");
            File file = new File(path, ".identify");

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
        }
    }

    public static String genIdentity() {
        if (identity == null) {
            identity = java.util.UUID.randomUUID().toString();
        }
        return identity;
    }

    public static File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStorageDirectory(), albumName);

        if (!file.mkdirs() && !file.exists()) {
//            LOGI("UUID_DIR", "Directory not created");
        }
        return file;
    }
}
