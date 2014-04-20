package net.cqs.storage;

public interface DataManager
{

ManagedReference createReference(Object object);

<T> T getBinding(String id, Class<T> type);
void setBinding(String id, Object object);
void removeBinding(String id);

}
