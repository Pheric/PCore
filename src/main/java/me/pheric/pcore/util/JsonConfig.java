package me.pheric.pcore.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creates a JSON config file
 *
 * @author Eric
 * @since 1.1.4
 */
public final class JsonConfig {
    public File file;

    public JsonConfig(File file, InputStream defaults, boolean overwrite) {
        this.file = file;

        boolean writeDefaults = false;

        if (!file.exists()) {
            writeDefaults = true;
            assert file.mkdirs();
            try {
                assert file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (defaults != null && (overwrite || writeDefaults)) {
            try {
                FileUtils.copyInputStreamToFile(defaults, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JsonConfig(File file, boolean overwrite) {
        this(file, null, false);
    }
}
