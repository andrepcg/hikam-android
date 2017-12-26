package org.jboss.netty.handler.codec.spdy;

import android.support.v4.view.ViewCompat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.jboss.netty.util.internal.StringUtil;

public class DefaultSpdySettingsFrame implements SpdySettingsFrame {
    private boolean clear;
    private final Map<Integer, Setting> settingsMap = new TreeMap();

    private static final class Setting {
        private boolean persist;
        private boolean persisted;
        private int value;

        Setting(int value, boolean persist, boolean persisted) {
            this.value = value;
            this.persist = persist;
            this.persisted = persisted;
        }

        int getValue() {
            return this.value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isPersist() {
            return this.persist;
        }

        void setPersist(boolean persist) {
            this.persist = persist;
        }

        boolean isPersisted() {
            return this.persisted;
        }

        void setPersisted(boolean persisted) {
            this.persisted = persisted;
        }
    }

    public Set<Integer> getIds() {
        return this.settingsMap.keySet();
    }

    public boolean isSet(int id) {
        return this.settingsMap.containsKey(Integer.valueOf(id));
    }

    public int getValue(int id) {
        Integer key = Integer.valueOf(id);
        if (this.settingsMap.containsKey(key)) {
            return ((Setting) this.settingsMap.get(key)).getValue();
        }
        return -1;
    }

    public void setValue(int id, int value) {
        setValue(id, value, false, false);
    }

    public void setValue(int id, int value, boolean persistValue, boolean persisted) {
        if (id < 0 || id > ViewCompat.MEASURED_SIZE_MASK) {
            throw new IllegalArgumentException("Setting ID is not valid: " + id);
        }
        Integer key = Integer.valueOf(id);
        if (this.settingsMap.containsKey(key)) {
            Setting setting = (Setting) this.settingsMap.get(key);
            setting.setValue(value);
            setting.setPersist(persistValue);
            setting.setPersisted(persisted);
            return;
        }
        this.settingsMap.put(key, new Setting(value, persistValue, persisted));
    }

    public void removeValue(int id) {
        Integer key = Integer.valueOf(id);
        if (this.settingsMap.containsKey(key)) {
            this.settingsMap.remove(key);
        }
    }

    public boolean isPersistValue(int id) {
        Integer key = Integer.valueOf(id);
        if (this.settingsMap.containsKey(key)) {
            return ((Setting) this.settingsMap.get(key)).isPersist();
        }
        return false;
    }

    public void setPersistValue(int id, boolean persistValue) {
        Integer key = Integer.valueOf(id);
        if (this.settingsMap.containsKey(key)) {
            ((Setting) this.settingsMap.get(key)).setPersist(persistValue);
        }
    }

    public boolean isPersisted(int id) {
        Integer key = Integer.valueOf(id);
        if (this.settingsMap.containsKey(key)) {
            return ((Setting) this.settingsMap.get(key)).isPersisted();
        }
        return false;
    }

    public void setPersisted(int id, boolean persisted) {
        Integer key = Integer.valueOf(id);
        if (this.settingsMap.containsKey(key)) {
            ((Setting) this.settingsMap.get(key)).setPersisted(persisted);
        }
    }

    public boolean clearPreviouslyPersistedSettings() {
        return this.clear;
    }

    public void setClearPreviouslyPersistedSettings(boolean clear) {
        this.clear = clear;
    }

    private Set<Entry<Integer, Setting>> getSettings() {
        return this.settingsMap.entrySet();
    }

    private void appendSettings(StringBuilder buf) {
        for (Entry<Integer, Setting> e : getSettings()) {
            Setting setting = (Setting) e.getValue();
            buf.append("--> ");
            buf.append(((Integer) e.getKey()).toString());
            buf.append(':');
            buf.append(setting.getValue());
            buf.append(" (persist value: ");
            buf.append(setting.isPersist());
            buf.append("; persisted: ");
            buf.append(setting.isPersisted());
            buf.append(')');
            buf.append(StringUtil.NEWLINE);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append(StringUtil.NEWLINE);
        appendSettings(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
}
