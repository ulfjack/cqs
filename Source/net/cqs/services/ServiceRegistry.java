package net.cqs.services;

public interface ServiceRegistry
{

<T extends Service> void registerService(Class<T> type, T provider);
<T extends Service> T findService(Class<T> type);
<T extends Service> boolean hasService(Class<T> type);

}
