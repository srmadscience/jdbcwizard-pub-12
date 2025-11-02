package com.orindasoft.pub;

/**
* Thrown when an attempt is made to retrieve a value from an empty readOnlyRowset.
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @see ReadOnlyRowSet
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSNoDataInRowSetException extends CSException
{

  /**
  * Default constructor
  */
  public CSNoDataInRowSetException()
  {
  super();
  }

  /**
  * Constructor with parameters.
  * @param String theExceptionMessage An exception message
  */
  public CSNoDataInRowSetException(String theExceptionMessage)
    {
    super(theExceptionMessage);
    }
}



