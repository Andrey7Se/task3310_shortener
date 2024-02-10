package code;

import code.strategy.StorageStrategy;

public class Shortener {
    private Long lastId = 0L;
    private final StorageStrategy storageStrategy;

    public Shortener(StorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    public synchronized Long getId(String string) {
        if (this.storageStrategy.containsValue(string)) {
            return this.storageStrategy.getKey(string);
        } else {
            this.lastId++;
            storageStrategy.put(this.lastId, string);
            return this.lastId;
        }
    }

    public synchronized String getString(Long id) {
        return this.storageStrategy.getValue(id);
    }
}
