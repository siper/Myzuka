package pro.siper.myzuka;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Андрей on 18.01.2017.
 */

public class NotificationId {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getId() {
        return c.incrementAndGet();
    }
}
