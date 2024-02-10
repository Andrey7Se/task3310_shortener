package code.strategy;

public class FileStorageStrategy implements StorageStrategy {
    private static final int DEFAULT_INITIAL_CAPACITY = 16; // кол-во ячеек - файлов по-умолчанию
    private static final long DEFAULT_BUCKET_SIZE_LIMIT = 1000L; // макс.размер одного файла
    private FileBucket[] table = new FileBucket[DEFAULT_INITIAL_CAPACITY]; // массив всех путей-файлов
    private int size; // текущий размер массива бакетов
    private long bucketSizeLimit = DEFAULT_BUCKET_SIZE_LIMIT; // удваивать массив бакетов при достижении
    private long maxBucketSize; // максимальный размер массива бакетов

    public FileStorageStrategy() {
        for (int i = 0; i < table.length; i++) {
            table[i] = new FileBucket();
        }
    }

    // получает массив первых Entry из массива файлов-бакетов
    public Entry[] getEntriesArr(FileBucket[] bucketsArr) {
        Entry[] entriesArr = new Entry[bucketsArr.length];

        for (int i = 0; i < bucketsArr.length; i++) {
            entriesArr[i] = bucketsArr[i].getEntry();
        }

        return entriesArr;
    }

    // записывает массив Entry в массив файлов-бакетов
    public FileBucket[] setEntriesArr(Entry[] entriesArr) {
        FileBucket[] fileBucketsArr = new FileBucket[entriesArr.length];

        for (int i = 0; i < entriesArr.length; i++) {
            fileBucketsArr[i].putEntry(entriesArr[i]);
        }

        return fileBucketsArr;
    }

    public long getBucketSizeLimit() {
        return bucketSizeLimit;
    }

    public void setBucketSizeLimit(long bucketSizeLimit) {
        this.bucketSizeLimit = bucketSizeLimit;
    }

    @Override
    public boolean containsKey(Long key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        Entry[] tab = getEntriesArr(table);

        for (Entry entry : tab) {
            for (Entry e = entry; e != null; e = e.next) {
                if (value.equals(e.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void put(Long key, String value) {
        int hash = hash((long) key.hashCode());
        int i = indexFor(hash, table.length);

        for (Entry e = table[i].getEntry(); e != null; e = e.next) {
            Object k;

            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                e.value = value;
            }
        }

        addEntry(hash, key, value, i);
    }

    @Override
    public Long getKey(String value) {
        for (FileBucket fileBucket : table) {
            if (fileBucket == null) {
                continue;
            }

            for (Entry e = fileBucket.getEntry(); e != null; e = e.next) {
                if (value.equals(e.value)) {
                    return e.key;
                }
            }
        }

        return null;
    }

    @Override
    public String getValue(Long key) {
        if (containsKey(key)) {
            return this.getEntry(key).getValue();
        } else {
            return null;
        }
    }

    public int hash(Long k) {
        int h;

        return k == null ? 0 : (h = k.hashCode()) ^ h >>> 16;
    }

    public int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    public Entry getEntry(Long key) {
        int hash = (key == null) ? 0 : hash((long) key.hashCode());

        for (Entry e = table[indexFor(hash, table.length)].getEntry(); e != null; e = e.next) {
            Object k;

            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
                return e;
            }
        }

        return null;
    }

    public void resize(int newCapacity) {
        if (table.length == maxBucketSize) {
            maxBucketSize = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = setEntriesArr(newTable);
        maxBucketSize = (int) (newCapacity * 0.75);
    }

    public void transfer(Entry[] newTable) {
        Entry[] src = getEntriesArr(table);
        int newCapacity = newTable.length;

        for (int j = 0; j < src.length; j++) {
            Entry e = src[j];

            if (e != null) {
                src[j] = null;

                do {
                    Entry next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

    public void addEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex].getEntry();
        table[bucketIndex].putEntry(new Entry(hash, key, value, e));

        if (size++ >= getBucketSizeLimit()) {
            resize(2 * table.length);
        }
    }

    public void createEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex].getEntry();
        table[bucketIndex].putEntry(new Entry(hash, key, value, e));
        size++;
    }
}
