package code;

import code.strategy.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static void main(String[] args) {
        testStrategy(new HashMapStorageStrategy(), 10_000);
        testStrategy(new OurHashMapStorageStrategy(), 10_000);
        //testStrategy(new FileStorageStrategy(), 100);
        testStrategy(new OurHashBiMapStorageStrategy(), 10_000);
        testStrategy(new HashBiMapStorageStrategy(), 10_000);
        testStrategy(new DualHashBidiMapStorageStrategy(), 10_000);
    }

    public static Set<Long> getIds(Shortener shortener, Set<String> strings) {
        Set<Long> result = new HashSet<>();

        for (String str : strings) {
            result.add(shortener.getId(str));
        }

        return result;
    }

    public static Set<String> getStrings(Shortener shortener, Set<Long> keys) {
        Set<String> result = new HashSet<>();

        for (Long key : keys) {
            result.add(shortener.getString(key));
        }

        return result;
    }

    public static void testStrategy(StorageStrategy storageStrategy, long elementsNumber) {
        Helper.printMessage(storageStrategy.getClass().getSimpleName());

        Set<String> randomStringsSet = new HashSet<>();

        for (int i = 0; i < elementsNumber; i++) {
            randomStringsSet.add(Helper.generateRandomString());
        }

        Shortener shortener = new Shortener(storageStrategy);

        long startGetIds = new Date().getTime();
        Set<Long> idsSet = getIds(shortener, randomStringsSet);
        long timeGetIds = new Date().getTime() - startGetIds;
        Helper.printMessage(String.valueOf(timeGetIds));

        long startGetStrings = new Date().getTime();
        Set<String> stringsSet = getStrings(shortener, idsSet);
        long timeGetStrings = new Date().getTime() - startGetStrings;
        Helper.printMessage(String.valueOf(timeGetStrings));

        if (stringsSet.containsAll(randomStringsSet) && stringsSet.size() == randomStringsSet.size()) {
            Helper.printMessage("Тест пройден.");
        } else {
            Helper.printMessage("Тест не пройден.");
        }
    }
}
