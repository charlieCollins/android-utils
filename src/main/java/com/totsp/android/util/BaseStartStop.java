package com.totsp.android.util;

/**
 * Classes that have some setup and cleanup to do, therefore need to start/stop. Implementations
 * must override start/stop and callers should use them (else resources may leak, and a warning will
 * be issued in log via CloseGuard).
 * 
 * @author ccollins
 * 
 */
public abstract class BaseStartStop {

   private CloseGuardLocal guard = CloseGuardLocal.get();

   public void start() {
      guard.open("stop");
   }

   public abstract void stop();
}
