package com.orindasoft.pub;

/**
* An interface for tracking and accessing basic performance information.
*
* Any class that implments this needs to keep track of the following:
* <p> Parses - Turning a SQL statement from a string into something the database server can use
* <p> Executions - Executing the statement and either retrieving or changing data
* <p> Releases - Handing back the database connection to the calling class.
* <p> Errors - Error conditions such as prepared statements being unusable.
* <p>
* <p> See <a href=http://www.orindasoft.com/public/Librarytwo.php4#staor&pdsrc=GD3145 TARGET=_blank class=news>StatsInterface</a>
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public interface StatsInterface
{

/**
* Reset all counters
* Set the counters for Parses, Executions and Releases to zero.
*/
void resetStatsCounters();

/**
* Get Parses
* @return long The number of times this statement was parsed.
*/
long getParses();

/**
* Get Parse time
* @return long The length of time this statement has spent parsing in milliseconds
*/
long getParseTime();

/**
* Get executions
* @return long The number of times this statement was executed.
*/
long getExecutions();

/**
* Get execution time
* @return long The length of time this statement has spent executing in milliseconds
*/
long getExecutionTime();

/**
* Get Retrieval time
* @return long The length of time this statement has spent executing in milliseconds
*/
long getRetrievalTime();

/**
* Get Releases
* @return long The number of times this statement was released.
*/
long getReleases();

/**
* Get Errors
* @return long The number of times an error was generated.
*/
long getErrors();

}




