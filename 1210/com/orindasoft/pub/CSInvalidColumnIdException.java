package com.orindasoft.pub;

/**
* Thrown when an attempt is made to retrieve a value from a column that does not exist.
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @see ReadOnlyRowSet
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSInvalidColumnIdException extends CSException
{
  int invalidColumnId = 0;
  
  /**
  * Default constructor
  */
  public CSInvalidColumnIdException()
  {
  super();
  }
  
  public CSInvalidColumnIdException(String theExceptionMessage
                                   ,int    theInvalidColumnId)
  {
  super(theExceptionMessage);
  this.invalidColumnId = theInvalidColumnId;
  }
}




