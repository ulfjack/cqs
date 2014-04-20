package net.cqs.storage.memory;

import java.util.HashMap;
import java.util.IdentityHashMap;

import net.cqs.storage.profiling.ProfileReport;

final class MemoryTransactionLog
{

final long startTime;
final HashMap<Long,ManagedEntry> entries = new HashMap<Long,ManagedEntry>();
final IdentityHashMap<Object,ManagedEntry> entryLookup = new IdentityHashMap<Object,ManagedEntry>();
final HashMap<String,ManagedBinding> bindings = new HashMap<String,ManagedBinding>();
final ProfileReport report;

public MemoryTransactionLog(long startTime, String taskType)
{
	this.startTime = startTime;
	this.report = new ProfileReport(taskType, startTime);
}

void add(ManagedEntry entry)
{
	entries.put(entry.getId(), entry);
	entryLookup.put(entry.getValue(), entry);
	report.increaseObjectCount();
}

}
