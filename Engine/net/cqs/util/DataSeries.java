package net.cqs.util;

public interface DataSeries
{

int size();
long get(int index);
long min();
long max();
String toCSV();

}
