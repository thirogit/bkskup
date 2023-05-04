package com.bk.bkskup3.runtime.progress;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/4/12
 * Time: 11:34 AM
 */
public class ForwardSimpleProgress implements SimpleProgress
{
   @Override
   public void setTaskSteps(int maxSteps) {}

   @Override
   public boolean stepTask() { return true; }

   @Override
   public void completed() {}

   @Override
   public void aborted() {}

   @Override
   public void error(Exception e) {}
}
