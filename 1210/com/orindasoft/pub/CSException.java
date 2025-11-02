package com.orindasoft.pub;

/**
* An extension of Exception used by this package.
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSException extends Exception
{

  /**
  * Default constructor
  */
  public CSException()
  {
  super();
  }
  
  /**
  * Default constructor that takes a String
  */
  public CSException(String theExceptionMessage)
  {
  super(theExceptionMessage);
  }
}




