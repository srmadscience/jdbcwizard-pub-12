package com.orindasoft.pub;

/**
* Thrown when a method which returns a native type such as <tt>int,long,double</tt>
* is called for a column whose value is <tt>null</tt>.
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @see ReadOnlyRowSet 
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSAttemptToGetNullException extends CSException
{

  /**
  * Which column we were looking at when this happened.
  */
  public int theColumnId = -1;

  /**
  * Default constructor
  */
  public CSAttemptToGetNullException()
  {
  super();
  }

  /**
  * Constructor which takes an message and a columnId
  */
  public CSAttemptToGetNullException(String theExceptionMessage
                                    ,int theColumnId)
  {
  super("Column " + theColumnId + ":" + theExceptionMessage);
  this.theColumnId = theColumnId;
  }
}





