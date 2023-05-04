package com.bk.bkskup3.runtime.progress;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 24.06.11
 * Time: 09:54
 */
public interface ComplexProgress
{
   void setActivity(String activityTitle,int taskCount);
   void setCurrentTask(String taskName,int maxSteps);
   boolean stepActivity();
   boolean stepTask();
   void completed();
   void aborted();
   void error(Exception e);

}
