package com.orindasoft.pub;

/**
* Thrown when a SQLException is generated.
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSDBException extends CSException
{

  /**
  * The oracle Error code
  */
  public int theSqlCode = 0;

  /**
  * The Sql statement that caused this exception
  */
  String theSqlStatement = null;

  /**
  * An optional comment
  */
  String theApplicationComment = null;

  /**
  * Default constructor
  */
  public CSDBException()
  {
  super();
  }

  /**
  * Contructor with parameters
  * @param int theSqlCode the oracle error code
  * @param String theSqlErrorMessage The message text associated with this exception
  * @param String theSqlStatement The Sql statement that caused this exception
  * @param String theApplicationComment An optional comment
  */
  public CSDBException(int theSqlCode
                      ,String theSqlErrorMessage
                      ,String theSqlStatement
                      ,String theApplicationComment)
  {
  super(theSqlStatement + ":" + theSqlErrorMessage + ":" + theApplicationComment);
  this.theSqlCode = theSqlCode;
  this.theSqlStatement = theSqlStatement;
  this.theApplicationComment = theApplicationComment;
  }
}




