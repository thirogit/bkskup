package com.bk.bkskup3.runtime.progress;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 24.06.11
 * Time: 09:54
 */
public interface SimpleProgress
{
   void setTaskSteps(int maxSteps);
   boolean stepTask();
   void completed();
   void aborted();
   void error(Exception e);
}
