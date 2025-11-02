package com.orindasoft.pub;

// We're working with JDBC
import java.sql.*;

// We use an Arraylist to store data
import java.util.ArrayList;

// We have to format numbers
import java.text.NumberFormat;

// We have to format dates
import java.text.SimpleDateFormat;

// Oracle always returns numbers as BigDecimal
import java.math.BigDecimal;

// We turn Longs and Clobs into files
import java.io.*;

/**
* Create a writableRowSet that is based on a ResultSet.
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* <p>
* Under normal circumstances <a href="http://www.orindasoft.com/?adsrc=api" target="_blank class="manual">OrindaBuild</a> users
* will have no reason to use this class directly - the generated code will use it.
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class WriteableRowSet extends ReadOnlyRowSet
{
  /**
  * Create a writableRowSet that is based on a ResultSet.
  *
  * @param ResultSet theResultSet
  * @param String theQuery
  * @param int maxRows
  * @param LogInterface theLog
  * @throws CSException
  */
 public WriteableRowSet(ResultSet theResultSet
                       ,String theQuery
                       ,int maxRows
                       ,LogInterface theLog) throws CSException
  {
  super(theResultSet,theQuery,maxRows,theLog);
  }

  /**
  * Create a ReadOnlyRowSet that is based on user defined data rather than a
  * ResultSet.
  *
  * This constructor is used to create a rowset based on a set of parameters for
  * a stored procedure call.
  * @param Object[] theData An array of Object that contains another array of
  * Object. The inner arrays are all of the the same length and map to a row of
  * columns.
  * @param String[] columnNames
  * @param String[] columnOracleDatatypeNames
  * @param int[] underlyingOracleDatatypes
  * @param int[] columnJavaDatatypes
  * @param LogInterface theLog
  * @param File downloadedFileDir A directory to keep files containing clobs
  * and blobs
  */
  public WriteableRowSet(Object[] theData
                        ,String[] columnNames
                        ,String[] columnOracleDatatypeNames
                        ,int[] underlyingOracleDatatypes
                        ,int[] columnJavaDatatypes
                        ,long[] columnLengths
                        ,int[] columnDecimalPlaces
                        ,LogInterface theLog
                        ,File downloadedFileDir
                        ,boolean keepFiles)
    {
    super(theData
         ,columnNames
         ,columnOracleDatatypeNames
         ,underlyingOracleDatatypes
         ,columnJavaDatatypes
         ,columnLengths
         ,columnDecimalPlaces
         ,theLog
         ,downloadedFileDir
         ,keepFiles);
    }

  /**
  *  Update current row as an array of Object
  * @return Object[] A one dimensional Object array containing the current row.
  * @throws CSNoDataInRowSetException if the rowset is empty.
  */
  public void setCurrentRow(Object[] currentRow) throws CSNoDataInRowSetException
    {
    checkRows();
    readOnlyRowSetData.set(currentRowNumber,currentRow);
    }

  /**
  *  Delete current row
  * @throws CSNoDataInRowSetException if the rowset is empty.
  */
  public void deleteCurrentRow() throws CSNoDataInRowSetException
    {
    checkRows();
    readOnlyRowSetData.remove(currentRowNumber);
    rowCount--;
    }

  /**
  * Add a new row.
  * We assume that newRow is an array of Object whose structure exactly matches the existing rows
  * @return Object[] A one dimensional Object array containing a new row.
  */
  public void addNewRow(Object[] newRow) throws CSNoDataInRowSetException
    {
    int originalRowNumber = getCurrentRowNumber();

    last();
    readOnlyRowSetData.add(newRow);
    readOnlyRowSetData.trimToSize();

    // Increment row counter
    rowCount++;

    setCurrentRowNumber(originalRowNumber);

    }

 /**
  * Set column <tt>columnId</tt> as a String. Numbers will be formatted. Dates will be formatted according
  * to theTimeStampFormat.
  * @param String theColumnName The name of the column
  * @param String theValue of column #columnId.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public void setString(String theColumnName, String theValue) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    setString(getColumnId(theColumnName), theValue);
    }

 /**
  * Set column <tt>columnId</tt> as a String. Numbers will be formatted. Dates will be formatted according
  * to theTimeStampFormat.
  * @param int columnId The number of the column.
  * @param String theValue value of column #columnId if it can be turned into a string.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public void setString(int columnId, String theValue) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // will throw CSUnsupportedDatatypeException if currentRow(columnId) can not be turned into a String
    tempRow[columnId] = setString(theValue
                                 ,underlyingOracleDatatypes[columnId]
                                 ,columnOracleDatatypeNames[columnId]
                                 ,columnNames[columnId]
                                 ,theTimestampFormat
                                 ,theNumberFormat);

    setCurrentRow(tempRow);

    }

 /**
  * Set column <tt>columnId</tt> as a String.
  * Numbers will be formatted. Dates will be formatted according
  * to theTimeStampFormat.
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param SimpleDateFormat theTimestampFormat Used if tempObject is a String.
  * @param NumberFormat theNumberFormat Used if tempObject is a Number.
  * @return String the Value of column #columnId if it can be turned into a string.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a String
  */
  private Object setString(String theValue
                          ,int oracleUnderlyingDatatype
                          ,String oracleDataType
                          ,String oracleColumnName
                          ,SimpleDateFormat theTimestampFormat
                          ,NumberFormat theNumberFormat) throws CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    String newString = null;

    if (theValue == null)
      {
      newString = null;
      }
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_TEXT_DATATYPE:
          {
          newString = theValue.trim();
          break;
          }
        case SqlUtils.ORACLE_BINARY_DATATYPE:
          {
          newString = new String(theValue.getBytes());
          break;
          }
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
        case SqlUtils.ORACLE_DATE_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_CLOB_DATATYPE:
        case SqlUtils.ORACLE_BLOB_DATATYPE:
        case SqlUtils.ORACLE_BFILE_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"String",oracleColumnName);
          break;
          }
        }
      }

    return(newString);
    }
}



