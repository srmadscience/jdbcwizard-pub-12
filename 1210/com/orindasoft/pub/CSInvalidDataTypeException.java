package com.orindasoft.pub;

/**
* Thrown when an we encounter a value whose data type is unsupported
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSInvalidDataTypeException extends CSException
{
  /**
  * Data type we we trying to convert from
  */
  String theCasterDatatype = "";

  /**
  * Data type we we trying to convert to
  */
  String theCasteeDatatype = "";

  /**
  * Default constructor
  */
  public CSInvalidDataTypeException()
  {
  super();
  }

  /**
  * Thrown when an we encounter a value whose data type is unsupported
  *
  */
  public CSInvalidDataTypeException(String theExceptionMessage
                                   ,String theCasterDatatype
                                   ,String theCasteeDatatype)
  {
  super(theExceptionMessage);
  this.theCasterDatatype = new String(theCasterDatatype);
  this.theCasteeDatatype = new String(theCasteeDatatype);
  }
}



