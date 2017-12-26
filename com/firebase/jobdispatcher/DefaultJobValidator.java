package com.firebase.jobdispatcher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.firebase.jobdispatcher.JobTrigger.ExecutionWindowTrigger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpStatus;

public class DefaultJobValidator implements JobValidator {
    public static final int MAX_EXTRAS_SIZE_BYTES = 10240;
    public static final int MAX_TAG_LENGTH = 100;
    private final Context context;

    public DefaultJobValidator(Context context) {
        this.context = context;
    }

    private static int measureBundleSize(Bundle extras) {
        Parcel p = Parcel.obtain();
        extras.writeToParcel(p, 0);
        int sizeInBytes = p.dataSize();
        p.recycle();
        return sizeInBytes;
    }

    @Nullable
    private static List<String> mergeErrorLists(@Nullable List<String> errors, @Nullable List<String> newErrors) {
        if (errors == null) {
            return newErrors;
        }
        if (newErrors == null) {
            return errors;
        }
        errors.addAll(newErrors);
        return errors;
    }

    @Nullable
    private static List<String> addError(@Nullable List<String> errors, String newError) {
        if (newError == null) {
            return errors;
        }
        if (errors == null) {
            return getMutableSingletonList(newError);
        }
        Collections.addAll(errors, new String[]{newError});
        return errors;
    }

    @Nullable
    private static List<String> addErrorsIf(boolean condition, List<String> errors, String newErr) {
        if (condition) {
            return addError(errors, newErr);
        }
        return errors;
    }

    @Nullable
    @CallSuper
    public List<String> validate(JobParameters job) {
        List<String> errors = mergeErrorLists(mergeErrorLists(null, validate(job.getTrigger())), validate(job.getRetryStrategy()));
        if (job.isRecurring() && job.getTrigger() == Trigger.NOW) {
            errors = addError(errors, "ImmediateTriggers can't be used with recurring jobs");
        }
        errors = mergeErrorLists(errors, validateForTransport(job.getExtras()));
        if (job.getLifetime() > 1) {
            errors = mergeErrorLists(errors, validateForPersistence(job.getExtras()));
        }
        return mergeErrorLists(mergeErrorLists(errors, validateTag(job.getTag())), validateService(job.getService()));
    }

    @Nullable
    @CallSuper
    public List<String> validate(JobTrigger trigger) {
        if (trigger == Trigger.NOW || (trigger instanceof ExecutionWindowTrigger)) {
            return null;
        }
        return getMutableSingletonList("Unknown trigger provided");
    }

    @Nullable
    @CallSuper
    public List<String> validate(RetryStrategy retryStrategy) {
        boolean z = true;
        int policy = retryStrategy.getPolicy();
        int initial = retryStrategy.getInitialBackoff();
        int maximum = retryStrategy.getMaximumBackoff();
        boolean z2 = (policy == 1 || policy == 2) ? false : true;
        List<String> errors = addErrorsIf(z2, null, "Unknown retry policy provided");
        if (maximum < initial) {
            z2 = true;
        } else {
            z2 = false;
        }
        errors = addErrorsIf(z2, errors, "Maximum backoff must be greater than or equal to initial backoff");
        if (HttpStatus.SC_MULTIPLE_CHOICES > maximum) {
            z2 = true;
        } else {
            z2 = false;
        }
        errors = addErrorsIf(z2, errors, "Maximum backoff must be greater than 300s (5 minutes)");
        if (initial >= 30) {
            z = false;
        }
        return addErrorsIf(z, errors, "Initial backoff must be at least 30s");
    }

    @Nullable
    private List<String> validateForPersistence(Bundle extras) {
        List<String> errors = null;
        if (extras != null) {
            for (String k : extras.keySet()) {
                errors = addError(errors, validateExtrasType(extras, k));
            }
        }
        return errors;
    }

    @Nullable
    private List<String> validateForTransport(Bundle extras) {
        if (extras == null || measureBundleSize(extras) <= 10240) {
            return null;
        }
        return getMutableSingletonList(String.format(Locale.US, "Extras too large: %d bytes is > the max (%d bytes)", new Object[]{Integer.valueOf(measureBundleSize(extras)), Integer.valueOf(10240)}));
    }

    @Nullable
    private String validateExtrasType(Bundle extras, String key) {
        String str = null;
        Object o = extras.get(key);
        if (o == null || (o instanceof Integer) || (o instanceof Long) || (o instanceof Double) || (o instanceof String) || (o instanceof Boolean)) {
            return null;
        }
        Locale locale = Locale.US;
        String str2 = "Received value of type '%s' for key '%s', but only the following extra parameter types are supported: Integer, Long, Double, String, and Boolean";
        Object[] objArr = new Object[2];
        objArr[0] = key;
        if (o != null) {
            str = o.getClass();
        }
        objArr[1] = str;
        return String.format(locale, str2, objArr);
    }

    private List<String> validateService(String service) {
        if (service == null || service.isEmpty()) {
            return getMutableSingletonList("Service can't be empty");
        }
        if (this.context == null) {
            return getMutableSingletonList("Context is null, can't query PackageManager");
        }
        PackageManager pm = this.context.getPackageManager();
        if (pm == null) {
            return getMutableSingletonList("PackageManager is null, can't validate service");
        }
        String msg = "Couldn't find a registered service with the name " + service + ". Is it declared in the manifest with the right intent-filter?";
        Intent executeIntent = new Intent("com.firebase.jobdispatcher.ACTION_EXECUTE");
        executeIntent.setClassName(this.context, service);
        List<ResolveInfo> intentServices = pm.queryIntentServices(executeIntent, 0);
        if (intentServices == null || intentServices.isEmpty()) {
            return getMutableSingletonList(msg);
        }
        for (ResolveInfo info : intentServices) {
            if (info.serviceInfo != null && info.serviceInfo.enabled) {
                return null;
            }
        }
        return getMutableSingletonList(msg);
    }

    private List<String> validateTag(String tag) {
        if (tag == null) {
            return getMutableSingletonList("Tag can't be null");
        }
        if (tag.length() > 100) {
            return getMutableSingletonList("Tag must be shorter than 100");
        }
        return null;
    }

    @NonNull
    private static List<String> getMutableSingletonList(String msg) {
        ArrayList<String> strings = new ArrayList();
        strings.add(msg);
        return strings;
    }
}
