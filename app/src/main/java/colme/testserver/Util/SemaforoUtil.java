package colme.testserver.Util;

import java.util.concurrent.Semaphore;

/**
 * Created by colme on 19/03/2016.
 */
public class SemaforoUtil {
    public static final Semaphore LOCK = new Semaphore(1);
    public static final Semaphore LOCK2 = new Semaphore(1);
}

