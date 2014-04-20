package net.cqs.storage;

import java.util.HashMap;
import java.util.IdentityHashMap;

import net.cqs.storage.profiling.ProfileReport;

import com.sleepycat.je.Transaction;

final class BDBTransactionLog
{

final long startTime;
final Transaction transaction;
final HashMap<Long,ManagedEntry> entries = new HashMap<Long,ManagedEntry>();
final IdentityHashMap<Object,ManagedEntry> entryLookup = new IdentityHashMap<Object,ManagedEntry>();
final ProfileReport report;

public BDBTransactionLog(long startTime, Transaction transaction, String taskType)
{
	this.startTime = startTime;
	this.transaction = transaction;
	this.report = new ProfileReport(taskType, startTime);
}

void add(ManagedEntry entry)
{
	entries.put(entry.getId(), entry);
	entryLookup.put(entry.getValue(), entry);
	report.increaseObjectCount();
}

}
