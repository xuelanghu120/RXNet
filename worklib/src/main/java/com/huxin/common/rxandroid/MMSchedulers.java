package com.huxin.common.rxandroid;

import com.huxin.common.application.Global;

import java.util.concurrent.atomic.AtomicReference;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by 56417 on 2016/10/10.
 */

public class MMSchedulers {
    private static final AtomicReference<MMSchedulers> INSTANCE = new AtomicReference<>();
    private final Scheduler workThreadScheduler;

    private static MMSchedulers getInstance() {
        for (; ; ) {
            MMSchedulers current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new MMSchedulers();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    private MMSchedulers() {
        workThreadScheduler = AndroidSchedulers.from(Global.getWorkThreadLooper());
    }

    public static Scheduler workThread() {
        return getInstance().workThreadScheduler;
    }
}
