package org.apache.http.impl.conn.tsccm;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.AbstractPoolEntry;
import org.apache.http.impl.conn.AbstractPooledConnAdapter;

@Deprecated
public class BasicPooledConnAdapter extends AbstractPooledConnAdapter {
    protected BasicPooledConnAdapter(ThreadSafeClientConnManager tsccm, AbstractPoolEntry entry) {
        super((ClientConnectionManager) null, (AbstractPoolEntry) null);
        throw new RuntimeException("Stub!");
    }

    protected ClientConnectionManager getManager() {
        throw new RuntimeException("Stub!");
    }

    protected AbstractPoolEntry getPoolEntry() {
        throw new RuntimeException("Stub!");
    }

    protected void detach() {
        throw new RuntimeException("Stub!");
    }
}
