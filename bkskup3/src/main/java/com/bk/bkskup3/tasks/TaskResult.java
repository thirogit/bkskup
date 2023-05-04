package com.bk.bkskup3.tasks;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/7/13
 * Time: 4:20 PM
 */
public class TaskResult<T>
{
    private T result;
    private Exception exception;

    private TaskResult()
    {

    }

    public T getResult()
    {
        return result;
    }

    public Exception getException()
    {
        return exception;
    }


    public boolean isError()
    {
        return exception != null;
    }

    public static<T> TaskResult<T> withResult(T resultObj)
    {
        TaskResult<T> result = new TaskResult<T>();
        result.result = resultObj;
        return result;
    }

    public static<T> TaskResult<T> withError(Exception e)
    {
        TaskResult<T> result = new TaskResult<T>();
        result.exception = e;
        return result;
    }
}
