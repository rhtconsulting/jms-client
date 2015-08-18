package com.redhat.consulting.hornetq_client;

import java.io.File;
import java.io.FileFilter;

public class SerializedFileFilter implements FileFilter {
    private final String[] okFileExtensions = new String[] { "ser" };

    public boolean accept(File file) {
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
