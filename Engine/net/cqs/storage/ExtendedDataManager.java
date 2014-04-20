package net.cqs.storage;

import net.cqs.storage.profiling.ProfileAggregator;

// Internal interface!
public interface ExtendedDataManager extends DataManager
{

<T> T get(Long id, Class<T> type);
<T> T call(ValueTask<T> task);
void call(final Task task);

void shutdown();
void sync();
ProfileAggregator getProfileAggregator();
void setProfileAggregator(ProfileAggregator aggregator);

}
