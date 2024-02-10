package test;

import code.Helper;
import code.Shortener;
import code.strategy.HashBiMapStorageStrategy;
import code.strategy.HashMapStorageStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SpeedTest {

    public long getTimeToGetIds(Shortener shortener, Set<String> strings, Set<Long> ids) {
        long start = new Date().getTime();

        for (String str : strings) {
            ids.add(shortener.getId(str));
        }

        return new Date().getTime() - start;
    }

    public long getTimeToGetStrings(Shortener shortener, Set<Long> ids, Set<String> strings) {
        long start = new Date().getTime();

        for (long id : ids) {
            strings.add(shortener.getString(id));
        }

        return new Date().getTime() - start;
    }

    @Test
    public void testHashMapStorage() {
        Shortener shortener1 = new Shortener(new HashMapStorageStrategy());
        Shortener shortener2 = new Shortener(new HashBiMapStorageStrategy());

        Set<String> origStrings = new HashSet<>();

        for (int i = 0; i < 10_000; i++) {
            origStrings.add(Helper.generateRandomString());
        }

        Set<Long> ids = new HashSet<>();
        long time1 = getTimeToGetIds(shortener1, origStrings, ids);
        long time2 = getTimeToGetIds(shortener2, origStrings, ids);

        Assert.assertTrue(time1 > time2);

        Set<String> gettingString = new HashSet<>();
        long time3 = getTimeToGetStrings(shortener1, ids, gettingString);
        long time4 = getTimeToGetStrings(shortener2, ids, gettingString);

        Assert.assertEquals(time3, time4, 30f);
    }
}
