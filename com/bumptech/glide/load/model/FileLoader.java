package com.bumptech.glide.load.model;

import android.net.Uri;
import com.bumptech.glide.load.data.DataFetcher;
import java.io.File;

public class FileLoader<T> implements ModelLoader<File, T> {
    private final ModelLoader<Uri, T> uriLoader;

    public FileLoader(ModelLoader<Uri, T> uriLoader) {
        this.uriLoader = uriLoader;
    }

    public DataFetcher<T> getResourceFetcher(File model, int width, int height) {
        return this.uriLoader.getResourceFetcher(Uri.fromFile(model), width, height);
    }
}
