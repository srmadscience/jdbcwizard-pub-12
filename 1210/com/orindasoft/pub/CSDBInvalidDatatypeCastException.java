package com.orindasoft.pub;

/**
* Thrown when an attempt is made to retrieve a value in a form that it can not be converted to.
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSDBInvalidDatatypeCastException extends CSException
{

  /**
  * The data type we were trying to convert from
  */
  public String theCastedDatatype = null;

  /**
  * The data type we were trying to convert to
  */
  public String theCasteeDatatype = null;

  /**
  * Default constructor
  */
  public CSDBInvalidDatatypeCastException()
  {
  super();
  }

  /**
  * Constructer that takes parameters.
  * @param String theExceptionMessage An application generated message
  * @param String theCastedDatatype The data type we were trying to convert from
  * @param String theCasteeDatatype The data type we were trying to convert to
  */
  public CSDBInvalidDatatypeCastException
    (String theExceptionMessage
    ,String theCastedDatatype
    ,String theCasteeDatatype)
  {
  super(theExceptionMessage);
  this.theCastedDatatype = new String(theCastedDatatype);
  this.theCasteeDatatype = new String(theCasteeDatatype);
  }
}



