 package com.orindasoft.pub;

/**
* Thrown when an attempt to turn a String into a number fails
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class CSNumberFormatException extends CSException
{

public CSNumberFormatException(String callLocation
                              ,String value)
  {
  super(callLocation +":"+value);
  }
}

