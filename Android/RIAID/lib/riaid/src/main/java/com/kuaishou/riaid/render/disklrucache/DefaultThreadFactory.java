package com.kuaishou.riaid.render.disklrucache;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;

public class DefaultThreadFactory implements ThreadFactory {
  private static final AtomicInteger poolNumber = new AtomicInteger(1);
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final ThreadGroup group;
  private final String namePrefix;

  public DefaultThreadFactory(String poolName) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    namePrefix = poolName + '-' + poolNumber.getAndIncrement() + '-';
  }

  public Thread newThread(Runnable r) {
    r = r != null ? r : () -> {};
    RevisePriorityRunnable rr = new RevisePriorityRunnable(r, Process.THREAD_PRIORITY_BACKGROUND);
    Thread t = new Thread(group, rr, namePrefix + threadNumber.getAndIncrement(), 0);
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }

  class RevisePriorityRunnable implements Runnable {

    final int priority;
    final Runnable r;

    RevisePriorityRunnable(Runnable runnable, int priority) {
      r = runnable;
      this.priority = priority;
    }

    @Override
    public void run() {
      Process.setThreadPriority(priority);
      r.run();
    }
  }
}