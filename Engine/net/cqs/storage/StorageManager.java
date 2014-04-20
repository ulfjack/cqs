package net.cqs.storage;

import net.cqs.storage.profiling.ProfileAggregator;

public interface StorageManager extends Storage
{

//void execute(Task task);
//<T> T execute(ValueTask<T> task);
//<T> T getCopy(final String id, final Class<T> type);
//<T> T getAndRemove(final String id, final Class<T> type);
void shutdown();
void sync();
ProfileAggregator getProfileAggregator();
void setProfileAggregator(ProfileAggregator aggregator);

}
