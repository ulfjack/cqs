package net.cqs.storage;

public interface Storage
{

void execute(Task task);
<T> T execute(ValueTask<T> task);
<T> T getCopy(String id, Class<T> type);
<T> T getAndRemove(String id, Class<T> type);
void set(String id, Object o);

}
