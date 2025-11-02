package com.orindasoft.pub;

/**
* An interface that can be inplemented by objects that tend to use up cursors and other
* oracle resources. All the objects in a class that implement this interface can be added
* to a vector or a similer data structure. When the time comes to free Oracle resources the
* class can then iterate through the vector of <tt>OracleResourceUser</tt> and get them to free
* their resources.
* <p>
* <p> See <a href=http://www.orindasoft.com/public/Librarytwo.php4#oresor&pdsrc=GD3145 TARGET=_blank class=news>OracleResourceUser</a>
* <p>
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public interface OracleResourceUser
{
  /**
  * Used to tell if the object is using Oracle resources.
  * @return <tt>true</tt> if the objects holds an open PreparedStatement, ResultSet or similer resource.
  */
  public boolean hasResources();

  /**
  * Used to tell an object to release its Oracle resources. This method never throws an exception. If
  * releasing the resource will create problems they should be dealt with by the implementing class, not
  * escalated to the calling class.
  * @return <tt>true</tt> if the objects held an open PreparedStatement, ResultSet or similer resource.
  */
  public boolean releaseResources();
}




