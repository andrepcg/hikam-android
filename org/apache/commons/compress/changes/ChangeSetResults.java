package org.apache.commons.compress.changes;

import java.util.ArrayList;
import java.util.List;

public class ChangeSetResults {
    private final List<String> addedFromChangeSet = new ArrayList();
    private final List<String> addedFromStream = new ArrayList();
    private final List<String> deleted = new ArrayList();

    void deleted(String fileName) {
        this.deleted.add(fileName);
    }

    void addedFromStream(String fileName) {
        this.addedFromStream.add(fileName);
    }

    void addedFromChangeSet(String fileName) {
        this.addedFromChangeSet.add(fileName);
    }

    public List<String> getAddedFromChangeSet() {
        return this.addedFromChangeSet;
    }

    public List<String> getAddedFromStream() {
        return this.addedFromStream;
    }

    public List<String> getDeleted() {
        return this.deleted;
    }

    boolean hasBeenAdded(String filename) {
        if (this.addedFromChangeSet.contains(filename) || this.addedFromStream.contains(filename)) {
            return true;
        }
        return false;
    }
}
