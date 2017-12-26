package com.firebase.jobdispatcher;

import android.support.annotation.NonNull;

public interface Driver {
    int cancel(@NonNull String str);

    int cancelAll();

    @NonNull
    JobValidator getValidator();

    boolean isAvailable();

    int schedule(@NonNull Job job);
}
