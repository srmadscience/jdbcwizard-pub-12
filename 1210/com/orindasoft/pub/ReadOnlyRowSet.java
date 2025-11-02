package com.orindasoft.pub;

// We're working with JDBC
import java.sql.*;

// We use oracle specific extensions to handle BLOBs and CLOBS
import oracle.jdbc.OracleResultSet;

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
* A class for representing a ResultSet or set of stored procedure parameters.
* <p>
* Detailed information on this class is available <a href="http://www.orindasoft.com/public/Librarytwo.php4?siteloc=Librarytwo#reaor&pdsrc=GD3145 TARGET=_blank class=news>here</a>
* <p>
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class ReadOnlyRowSet
{
  /**
  * String format used when converting Timestamps to Strings
  */
  protected SimpleDateFormat theTimestampFormat
             = new SimpleDateFormat(LogInterface.DEFAULT_TIME_FORMAT_STRING);
  /**
  * A number formatter used by the <tt>getString</tt> methods
  */
  protected NumberFormat theNumberFormat = NumberFormat.getInstance();

  /**
  * How many columns are in this ReadOnlyRowSet
  */
  protected int colCount = 0;

  /**
  * How many rows are in this ReadOnlyRowSet
  */
  protected int rowCount = 0;

  /**
  * Which row we are currently on. The row number starts at 0. A value of
  * -1 indicates no rows.
  */
  protected int currentRowNumber = -1;

  /**
  * A flag indicating that we stopped adding rows because we hit maxRows, the limit specified
  * in the constructor
  */
  protected boolean hitRowLimit = false;

  /**
  * How much memory has to be available for us to continue retrieving rows from a
  * large query. The default value is 1M.. If free memory gets this low then it is probably
  * because the JVM can't allocate any more.
  */
  protected long minimumFreeMemoryBytes = 1048576;

  /**
  * A flag indicating that the JVm ran low on memory.
  * @since 6.0.2615 hitMemLimit no longer stops query being read.
  */
  protected boolean hitMemLimit = false;

  /**
  * An array containing the names of the columns that make up this ReadOnlyRowSet.
  * It is possible that the same name may appear more than once.
  */
  protected String[] columnNames = null;

  /**
  * An array containing the Oracle Data Type names of the columns that make up this ReadOnlyRowSet.
  */
  protected String[] columnOracleDatatypeNames = null;

  /**
  * An Array containing ints that map to core oracle datatypes in SqlUtil.
  * @see SqlUtils.getUnderlyingOracleDatatype()
  */
  protected int[] underlyingOracleDatatypes = null;

  /**
  * An array containing the Data Type ids of the columns that make up this
  * ReadOnlyRowSet. The ids match those specified in java.sql.Types
  * @see java.sql.Types
  */
  protected  int[] columnJavaDatatypes = null;

  /**
  * An array containing the lengths of the columns that make up this
  * ReadOnlyRowSet.
  *
  * We need to use a <tt>long</tt> as CLOBS and BLOBS can be 4GB in length.
  */
  protected  long[] columnLengths = null;

  /**
  * An array containing the number of decimal places of each column that makes
  * up this ReadOnlyRowSet
  */
  protected  int[] columnDecimalPlaces = null;

  /**
  * An ArrayList used for storing the data in the ReadOnlyRowSet.
  * Each entry contains an Object array that holds the data for each row.
  */
  protected ArrayList readOnlyRowSetData = new ArrayList();

  /**
  * An instance of the LogInterface logging mechanism
  * @see com.orindasoft.pub#LogInterface
  */
  protected LogInterface theLog = null;

  /**
  * The directory where longs, clobs, etc will be stored as files. It is
  * recommended that you have one per JVM at an absolute minimum. One per
  * session is better.
  */
  protected File downloadedFileDir = null;

  /**
  * When the data in this ReadOnlyRowSet will expire.
  *
  * This is used by the caching mechanism to determine when we the data
  * has reached its sell by date and needs to be refreshed.
  */
  protected java.util.Date expireDate = null;

  /**
  * The Prefix for downloaded files containing BLOB, CLOB and BFILE data
  */
  protected String tempFilePrefix = "OSOFT";

  /**
  * The Suffix for downloaded files containing BLOB, CLOB and BFILE data
  */
  protected String tempFileSuffix = ".tmp";

  /**
  * boolean flag that specifies whether downloaded files will automaticly be
  * deleted when the JVM exits or not
  */
  protected boolean keepFiles = true;

  /**
  * boolean flag that specifies whether lobs such as CLOBS, BLOBS and BFILES
  * will be kept as Oracle objects or downloaded into Files.
  * @since 2.0.1505
  */
  protected boolean keepLobs = false;

  /**
  * boolean flag that specifies whether lobs such as CLOBS, BLOBS and BFILES
  * and LONG columns
  * will be kept as byte arrays. This only works if keepFiles == false.
  * @since JDBCWizard 5.0.2314
  */
  protected boolean useByteArraysForLongsAndLOBS = false;

  /**
  * object Unloader
  */
  protected LongObjectLoader objectUnloader = null;

  /**
  * How many times the 'rowSetUsed' method has been called.
  * This is used by QueryStatements caching functionality
  */
  int timesUsed = 1;

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
  * @param String[] columnOracleDatatypes
  * @param int[] underlyingOracleDatatypes
  * @param int[] columnJavaDatatypes
  * @param LogInterface theLog
  * @param File downloadedFileDir A directory to keep files containing clobs
  * and blobs
  */
  public ReadOnlyRowSet(Object[] theData
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
    objectUnloader = new LongObjectLoader();
    this.downloadedFileDir = downloadedFileDir;
    this.theLog = theLog;
    this.columnNames = columnNames;
    this.columnOracleDatatypeNames = columnOracleDatatypeNames;
    this.underlyingOracleDatatypes = underlyingOracleDatatypes;
    this.columnJavaDatatypes = columnJavaDatatypes;
    this.columnLengths = columnLengths;
    this.columnDecimalPlaces = columnDecimalPlaces;
    this.keepFiles = keepFiles;

    for (int i=0; i < theData.length; i++)
      {
      readOnlyRowSetData.add(theData[i]);
      }

    colCount = columnNames.length;
    rowCount = theData.length;

    }

  /**
  * Create a ReadOnlyRowSet that is based on a ResultSet.
  *
  * @param ResultSet theResultSet
  * @param String theQuery
  * @param int maxRows
  * @param LogInterface theLog
  * @throws CSException
  */
  public ReadOnlyRowSet(ResultSet theResultSet
                       ,String theQuery
                       ,int maxRows
                       ,LogInterface theLog) throws CSException
    {
    this(theResultSet,theQuery,maxRows,theLog,IOUtils.getOsTempDir()
        ,true,"OSOFT",".tmp",false);
    }
    
  /**
  * Create a ReadOnlyRowSet that is based on a ResultSet.
  *
  * @param ResultSet theResultSet
  * @param String theQuery
  * @param int maxRows Maximum number of rows that will be retrieved. The JDBC driver will
  * stop returning rows after about 30,000.
  * @param LogInterface theLog
  * @param File A directory where downloaded CLOBS and BLOBS will be stored.
  * @param boolean keepFiles Whether generated files are kept or deleted when the JVM exits
  * @param String tempFilePrefix Prefix for generated temporary files.
  * @param String tempFileSuffix Suffix for generated temporary files.
  * @throws CSException
  */
  public ReadOnlyRowSet(ResultSet theResultSet
                       ,String theQuery
                       ,int maxRows
                       ,LogInterface theLog
                       ,File downloadedFileDir
                       ,boolean keepFiles
                       ,String tempFilePrefix
                       ,String tempFileSuffix) throws CSException
    {
    this          (theResultSet
                  ,theQuery
                  ,maxRows
                  ,theLog
                  ,downloadedFileDir
                  ,keepFiles
                  ,tempFilePrefix
                  ,tempFileSuffix
                  ,false);
    }

  /**
  * Create a ReadOnlyRowSet that is based on a ResultSet.
  *
  * @param ResultSet theResultSet
  * @param String theQuery
  * @param int maxRows Maximum number of rows that will be retrieved. The JDBC driver will
  * stop returning rows after about 30,000.
  * @param LogInterface theLog
  * @param File A directory where downloaded CLOBS and BLOBS will be stored.
  * @param boolean keepFiles Whether generated files are kept or deleted when the JVM exits
  * @param String tempFilePrefix Prefix for generated temporary files.
  * @param String tempFileSuffix Suffix for generated temporary files.
  * @param boolean keepLobs Whether lobs suchs as CLOBS and BLOBS are turned into Files on retrieval
  * @since 2.0.1505 'keepLobs' added.
  * @since 5.0.2314 default for 'useByteArraysForLongsAndLOBS' added.
  * @throws CSException
  */
  public ReadOnlyRowSet(ResultSet theResultSet
                       ,String theQuery
                       ,int maxRows
                       ,LogInterface theLog
                       ,File downloadedFileDir
                       ,boolean keepFiles
                       ,String tempFilePrefix
                       ,String tempFileSuffix
                       ,boolean keepLobs) throws CSException
    {
     this          (theResultSet
                  ,theQuery
                  ,maxRows
                  ,theLog
                  ,downloadedFileDir
                  ,keepFiles
                  ,tempFilePrefix
                  ,tempFileSuffix
                  ,keepLobs
                  ,false );
    }

  /**
  * Create a ReadOnlyRowSet that is based on a ResultSet.
  *
  * @param ResultSet theResultSet
  * @param String theQuery
  * @param int maxRows Maximum number of rows that will be retrieved. The JDBC driver will
  * stop returning rows after about 30,000.
  * @param LogInterface theLog
  * @param File A directory where downloaded CLOBS and BLOBS will be stored.
  * @param boolean keepFiles Whether generated files are kept or deleted when the JVM exits
  * @param String tempFilePrefix Prefix for generated temporary files.
  * @param String tempFileSuffix Suffix for generated temporary files.
  * @param boolean keepLobs Whether lobs suchs as CLOBS and BLOBS are turned into Files on retrieval
  * @since 2.0.1505 'keepLobs' added.
  * @since 5.0.2314 'useByteArraysForLongsAndLOBS' added.
  * @since 6.0.2615 hitMemLimit no longer stops query being read.
  * @throws CSException
  */
  public ReadOnlyRowSet(ResultSet theResultSet
                       ,String theQuery
                       ,int maxRows
                       ,LogInterface theLog
                       ,File downloadedFileDir
                       ,boolean keepFiles
                       ,String tempFilePrefix
                       ,String tempFileSuffix
                       ,boolean keepLobs
                       ,boolean useByteArraysForLongsAndLOBS) throws CSException
    {
    objectUnloader = new LongObjectLoader();

    if (theResultSet == null)
      {
      throw new CSDBNullObjectException();
      }

    this.theLog = theLog;
    this.downloadedFileDir = downloadedFileDir;
    this.keepFiles = keepFiles;
    this.keepLobs  = keepLobs;
    this.useByteArraysForLongsAndLOBS  = useByteArraysForLongsAndLOBS;

    if ( ! downloadedFileDir.exists())
      {
      if (downloadedFileDir.mkdirs())
        {
        theLog.info("Creating directory " + downloadedFileDir.getAbsolutePath(),false,true);
        }
      else
        {
        theLog.error("Unable to create directory " + downloadedFileDir.getAbsolutePath(),false,true);
        throw new CSException("Unable to create file download directory " + downloadedFileDir.getAbsolutePath());
        }
      }

    // An array used to load rows into ReadOnlyRowSetData.
    Object[] tempArray = null;

    try
      {
      // Get metadata so we can build data structures.
      ResultSetMetaData theMetaData = theResultSet.getMetaData();
      colCount = theMetaData.getColumnCount();
      columnNames = new String[colCount];
      columnOracleDatatypeNames = new String[colCount];
      columnJavaDatatypes = new int[colCount];
      underlyingOracleDatatypes = new int[colCount];
      columnLengths = new long[colCount];
      columnDecimalPlaces = new int[colCount];

      // Populate our metadata information fields.
      for (int i=0; i < colCount; i++)
        {
        columnNames[i] = theMetaData.getColumnName(i+1);
        // columnOracleDatatypeNames[i] will be null if the column is UROWID
        columnOracleDatatypeNames[i] = theMetaData.getColumnTypeName(i+1);
        columnJavaDatatypes[i] = theMetaData.getColumnType(i+1);
        underlyingOracleDatatypes[i] = SqlUtils.getUnderlyingOracleDatatype(columnOracleDatatypeNames[i]);

        try
          {
          // CLOBS and BLOBS can be up to 4GB in length. The default size of int is
          // 2GB. The getPrecision method returns an int if the precison (aka 'size') is
          // <= 2GB and throws a NumberFormatException containing the value otherwise.
          columnLengths[i] = theMetaData.getPrecision(i+1);
          }
        catch (java.lang.NumberFormatException e)
          {
          // Prior to Java 1.5 'e.getMessage()' will contain a
          // String that is an Integer representation of 4GB. e.g.:
          //      4294967295
          // In Java 1.5 it turns into the String:
          //      For input string: "4294967295"
          // This will break older versions of this Class.

          // Assume message has "'s and remove them.

          String message = e.getMessage().replace('"',' ');

          // Find position of last ':'.
          int colonPosition = message.lastIndexOf(':');
          if (colonPosition == -1)
            {
            colonPosition = 0;
            }

          // Create Long based on position of last ':' char or zero.
          Long tempLong = new Long(message.substring(colonPosition+1).trim());
          columnLengths[i] = tempLong.longValue();
          }

        // If an oracle column is defined as NUMBER without a precision it
        // will have a columLengths[i] of 0. The correct value is 38.
        if (columnLengths[i] == 0 && underlyingOracleDatatypes[i] == SqlUtils.ORACLE_NUMBER_DATATYPE)
          {
          columnLengths[i] = 38;
          }

        columnDecimalPlaces[i] = theMetaData.getScale(i+1);
        }

      // work our way thorugh the result set. Give up when we run out of
      // rows or hit maxRows.

      // From 6.0.2615: removed reference to 'hitMemLimit'...

      while (theResultSet.next() && rowCount < maxRows)
        {
        // Increment row counter
        rowCount++;

        // Initialize temporary array.
        tempArray = new Object[colCount];

        // Fill in tempArray by working our way along the current
        // row in the resultSet.
        for (int i=0; i < colCount; i++)
          {
          try
            {
            tempArray[i] = unloadObject(theResultSet,i,underlyingOracleDatatypes[i],this.keepFiles);
            }
          catch (Exception e)
            {
            theLog.syserror("Unable to handle row " + rowCount + ", column " + i);
            }
          }

        // Add temp array to temp array list.
        readOnlyRowSetData.add(tempArray);

        // If this is a big query we should keep track of free memory
        // and stop if we run dangerously short.
        if (rowCount > 1999 && (rowCount % 100) == 0)
          {

          if (ResourceWatcher.freeMemAsPct() <= ResourceWatcher.MIN_SAFE_MEMORY_PCT)
            {
            hitMemLimit = true;
            theLog.warning("JVM Ran low on allocated memory on row " + rowCount
                           + "; Free Memory = "
                           + ResourceWatcher.freeMemAsPct() +"%");
            }
          }


        }

      theLog.debug("After resultset"); //DEBUG
      // Set current row to start value if we successfully retrieved one or
      // more rows.
      first();

      try
        {
        if (theResultSet.next())
          {
          hitRowLimit = true;
          }
        
       } catch (SQLException e) { }


      theResultSet.close();
      readOnlyRowSetData.trimToSize();
      }
    catch (java.sql.SQLException e)
      {
      throw new CSException("Unable to retrieve data:" +  e.getMessage());
      }
    catch (Exception e)
      {
      throw new CSException("Unable to retrieve data:" +  e.getMessage());
      }
    }


  /**
  * Get the position of a named column in rows of the underlying result set.
  *
  * Return the Id of the first value in <tt>columnNames</tt> that
  * matches <tt>theColumnName</tt>. Note that there is nothing that prevents
  * a column name being used multiple times withing a result set, in which case the
  * id of the first matching column will be returned.
  * @param String theColumnName The name of the column whose id you are looking for.
  * @return An int which identifies the first occurance of <tt>columnName</tt>
  * @return -1 If no match is made.
  */
  public int getColumnId(String theColumnName)
    {
    int columnId = -1;

    for (int i=0; i < columnNames.length; i++)
      {
      if (theColumnName.equalsIgnoreCase(columnNames[i]))
        {
        columnId = i;
        break;
        }
      }

    return columnId;
    }
  /**
  * Get the name of a column.
  *
  * Return the Name of the value in <tt>columnNames</tt> that
  * for <tt>theColumnId</tt>.
  * @param int theColumnId The id of the column whose name you are looking for.
  * @return A String  which is the column name
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 1.
  */
  public String getColumnName(int theColumnId) throws CSInvalidColumnIdException
    {
    checkRange(theColumnId);
    theLog.debug("Column id " + theColumnId + ":" + columnNames[theColumnId] + " returned");
    return columnNames[theColumnId];
    }
    
  /**
  * Convert the object returned by the resultset into one we can use.
  *
  * @param ResultSet theResultSet We need this as we may have to cast it to <tt>OracleResultSet</tt>.
  * @param int columnId Which column we want to get.
  * @param int oracleUnderlyingDatatype The underlying oracle datatype. We pass it in to avoid having to find out what it is twice.
  * @param boolean keepFiles Whether any Files generated by retrieving Lobs should be kept when the program finishes. The default behaviour is to delete them.
  * @return null If the column is null or a zero-length Long field.
  * @return String If the column is a text string of some kind.
  * @return java.sql.Timestamp If the column is a Date field.
  * @return BigDecimal If the column is a number field.
  * @return File If the column is a valid Long object with a length > 0.
  * @return Exception If something went wrong, Usually with a BFILE retrieval.
  * @since 4.0.1794 DATE Columns are now explicitly casted to java.sql.TimeStamp.
  * @since 5.0.2314 A value of true for useByteArraysForLongsAndLOBS will lead to downloaded LONG and LOB columns being stored in byte arrays.
  */
  protected Object unloadObject(ResultSet theResultSet, int columnId, int oracleUnderlyingDatatype, boolean keepFiles)
    {
    Object newObject = null;

    objectUnloader.setKeepFiles(keepFiles);

    try
      {
      switch (oracleUnderlyingDatatype)
        {
        // Force DATE fields to TimeStamp for consistancy with older versions of OrindaBuild.
        case SqlUtils.ORACLE_DATE_DATATYPE:
          {
          newObject = theResultSet.getTimestamp(1+columnId);
          break;
          }
        case SqlUtils.ORACLE_TEXT_DATATYPE:
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
          {
           // We don't have to do anything special to this value.
           newObject = theResultSet.getObject(1+columnId);
           break;
           }
        case SqlUtils.ORACLE_BINARY_DATATYPE:
          {
          newObject = theResultSet.getBytes(1+columnId);
          break;
          }
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_CLOB_DATATYPE:
        case SqlUtils.ORACLE_BLOB_DATATYPE:
        case SqlUtils.ORACLE_BFILE_DATATYPE:
          {
          if (useByteArraysForLongsAndLOBS)
            {
            // A File that will be used to store the contents of the LONG object.
            byte[] newBytes = new byte[0];

            if (   columnOracleDatatypeNames[columnId].equals("LONG"))
              {
              newObject = objectUnloader.unloadBinaryStreamIntoByteArray(theResultSet.getAsciiStream(1+columnId),columnOracleDatatypeNames[columnId]);
              }
            else if (columnOracleDatatypeNames[columnId].equals("CLOB"))
              {
              java.sql.Clob tempClob = null;  		  
              tempClob = theResultSet.getClob(1+columnId);
             
              if (tempClob != null)
                {
                if (keepLobs)
                  {
                  newObject = tempClob;
                  }
                else
                  {
                  //newObject = objectUnloader.unloadBinaryStreamIntoByteArray(tempClob.binaryStreamValue(),columnOracleDatatypeNames[columnId]);
                  newObject = objectUnloader.unloadClobIntoCharArray(tempClob);
                  }
                }
              }
            else if (columnOracleDatatypeNames[columnId].equals("LONG RAW"))
              {
              newObject = objectUnloader.unloadBinaryStreamIntoByteArray(theResultSet.getBinaryStream(1+columnId),columnOracleDatatypeNames[columnId]);
              }
            else if (columnOracleDatatypeNames[columnId].equals("BLOB"))
              {
              oracle.sql.BLOB tempBlob = ((OracleResultSet)theResultSet).getBLOB(1+columnId);
              if (tempBlob != null)
                {
                if (keepLobs)
                  {
                  newObject = tempBlob;
                  }
                else
                  {
                  newObject = objectUnloader.unloadBinaryStreamIntoByteArray(tempBlob.binaryStreamValue(),columnOracleDatatypeNames[columnId]);
                  }
                }
              }
            else if (columnOracleDatatypeNames[columnId].equals("BFILE"))
              {
              oracle.sql.BFILE tempBFILE = ((OracleResultSet)theResultSet).getBFILE(1+columnId);
              if (tempBFILE != null)
                {
                if (keepLobs)
                  {
                  newObject = tempBFILE;
                  }
                else
                  {
                  newObject = objectUnloader.unloadBfileIntoByteArray(tempBFILE);
                  }
                }
              }
            else
              // We don't recognize the Oracle data type so we are not going to
              // turn it into an InputStream.
              {
              theLog.error("Oracle data type '" + columnOracleDatatypeNames[columnId] + "' is unrecognized");
              throw (new CSUnsupportedDatatypeException("Unable to turn "
                                                       + columnOracleDatatypeNames[columnId]
                                                       + " column into a File"
                                                       , columnOracleDatatypeNames[columnId]));
              }
            }
          else
            {
            // A File that will be used to store the contents of the LONG object.
            File newFile = File.createTempFile(tempFilePrefix + "_"+ rowCount + "_" + (columnId+1) +"_",tempFileSuffix
                                              ,downloadedFileDir);
            //
            if (   columnOracleDatatypeNames[columnId].equals("LONG"))
              {
              newObject = objectUnloader.unloadBinaryStream(newFile,theResultSet.getAsciiStream(1+columnId),columnOracleDatatypeNames[columnId]);
              }
            else if (columnOracleDatatypeNames[columnId].equals("CLOB"))
              {
              java.sql.Clob tempClob = null;	  		  
              tempClob = theResultSet.getClob(1+columnId);
                
              if (tempClob != null)
                {
                if (keepLobs)
                  {
                  newObject = tempClob;
                  }
                else
                  {
                  newObject = objectUnloader.unloadBinaryStream(newFile,tempClob.getAsciiStream(),columnOracleDatatypeNames[columnId]);
                  }
                }
              }
            else if (columnOracleDatatypeNames[columnId].equals("LONG RAW"))
              {
              newObject = objectUnloader.unloadBinaryStream(newFile,theResultSet.getBinaryStream(1+columnId),columnOracleDatatypeNames[columnId]);
              }
            else if (columnOracleDatatypeNames[columnId].equals("BLOB"))
              {
              oracle.sql.BLOB tempBlob = ((OracleResultSet)theResultSet).getBLOB(1+columnId);
              if (tempBlob != null)
                {
                if (keepLobs)
                  {
                  newObject = tempBlob;
                  }
                else
                  {
                  newObject = objectUnloader.unloadBinaryStream(newFile,tempBlob.binaryStreamValue(),columnOracleDatatypeNames[columnId]);
                  }
                }
              }
            else if (columnOracleDatatypeNames[columnId].equals("BFILE"))
              {
              oracle.sql.BFILE tempBFILE = ((OracleResultSet)theResultSet).getBFILE(1+columnId);
              if (tempBFILE != null)
                {
                if (keepLobs)
                  {
                  newObject = tempBFILE;
                  }
                else
                  {
                  newObject = objectUnloader.unloadBfile(newFile,tempBFILE);
                  }
                }
              }
            else
              // We don't recognize the Oracle data type so we are not going to
              // turn it into an InputStream.
              {
              theLog.error("Oracle data type '" + columnOracleDatatypeNames[columnId] + "' is unrecognized");
              throw (new CSUnsupportedDatatypeException("Unable to turn "
                                                       + columnOracleDatatypeNames[columnId]
                                                       + " column into a File"
                                                       , columnOracleDatatypeNames[columnId]));
              }
            }
          break;
          }
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:
          {                                                                                  
          newObject = ((oracle.jdbc.OracleResultSet)theResultSet).getTIMESTAMP(1+columnId);  
          break;                                                                             
          }                                                                                  
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:
          {                                                                                  
          newObject = ((oracle.jdbc.OracleResultSet)theResultSet).getTIMESTAMPTZ(1+columnId);
          break;                                                                             
          }                                                                                  
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:
          {                                                                                   
          newObject = ((oracle.jdbc.OracleResultSet)theResultSet).getTIMESTAMPLTZ(1+columnId);
          break;                                                                              
          }                                                                                   
        case SqlUtils.ORACLE_NULL_DATATYPE:
          {                                                                                   
          newObject = null;                                                                   
          break;                                                                              
          }                                                                                   
        case SqlUtils.ORACLE_ROWID_DATATYPE:
          {
           // We don't have to do anything special to this value.
           newObject = theResultSet.getObject(1+columnId);
           break;
           }
        case SqlUtils.ORACLE_INTERVAL_DAY_TO_SECOND_DATATYPE:
        case SqlUtils.ORACLE_INTERVAL_YEAR_TO_MONTH_DATATYPE:
          {                                                             
           // We don't have to do anything special to this value.       
           newObject = theResultSet.getObject(1+columnId);              
           break;                                                       
           }                                                            
        case SqlUtils.ORACLE_OTHER_DATATYPE:                                     
          {                                                             
            // Some 'other' types we like...    
            // Note that we fall thrpugh tpo default: if we dont match       
        	  if (   columnOracleDatatypeNames[columnId].equals("SYS.XMLTYPE")
        		  || columnOracleDatatypeNames[columnId].endsWith(".SDO_GEOMETRY"))
        	  {
              newObject = theResultSet.getObject(1+columnId);              
              break;                                                       
              }    
          }
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        default:
          {
          newObject = new CSUnsupportedDatatypeException("Datatype "
                                                        + columnOracleDatatypeNames[columnId]
                                                        + " is not supported"
                                                        ,columnOracleDatatypeNames[columnId]);
          break;
          }
        }
      }
    catch (Exception e)
        {
        // Rather than log the exception to a file we return it instead of the file
        // so the application still has control of the situation and decide what to do.
        // This is useful when dealing with errant BFILEs - the application can forward the
        // message to some other business process whose job is to fix BFILE probelems.
        newObject = e;
        }

    // newObject will be one of the following:
    // * null
    // * A java data type such as String, Date or BigDecimal.
    // * A File object in the case of Long datatypes
    // * An Exception if something went wrong.
    return(newObject);
    }

  /**
  * Return the current row number
  * @return int the current row number.
  */
  public int getCurrentRowNumber()
    {
    return(currentRowNumber);
    }

  /**
  * Return the number of rows
  * @return int the number of rows
  */
  public int size()
    {
    return(rowCount);
    }

  /**
  * Return the number of columns
  * @return int the number of columns
  */
  public int width()
    {
    return(colCount);
    }

  /**
  * Identify when we had to stop adding rows to the ResultSet because we reached the
  * limit on the number of rows that are to be stored.
  * @return boolean <tt>true</tt> if we reached <tt>maxRows</tt> when we were created.
  * @return boolean <tt>false</tt> if we did not reach <tt>maxRows</tt> when we were created.
  */
  public boolean hitRowLimit()
    {
    return(hitRowLimit);
    }

  /**
  * Identify when we had to stop adding rows to the ResultSet because we
  * ran out of memory.
  * @return boolean <tt>true</tt> if we reached <tt>minimumFreeMemoryBytes</tt> when we were created.
  * @return boolean <tt>false</tt> if we did not reach <tt>minimumFreeMemoryBytes</tt> when we were created.
  * @since 6.0.2615 hitMemLimit no longer stops query being read.
  */
  public boolean hitMemLimit()
    {
    return(hitMemLimit);
    }

  /**
  * Move to a specific row of the RowSet.
  * @param int newCurrentRowNumber the new row number.
  * @return <tt>true</tt> if newCurrentRowNumber is a valid row
  * @return <tt>false</tt> if newCurrentRowNumber is less than zero or
  * higher than <tt>size() - 1</tt>.
  */
  public boolean setCurrentRowNumber(int newCurrentRowNumber)
    {
    boolean returnCode = true;

    if (rowCount < 0)
      {
      // return false because there are no rows....
      returnCode = false;
      }
    else if (newCurrentRowNumber < 0)
      {
      // Return false because we were asked to set a negative row number.
      currentRowNumber = 0;
      returnCode = false;
      }
    else if (newCurrentRowNumber >= rowCount)
      {
      // return false because we were asked to set row number to a value greater than
      // the number of rows
      currentRowNumber = rowCount - 1;
      returnCode = false;
      }
    else
      {
      // Change row number and return true.
      currentRowNumber = newCurrentRowNumber;
      returnCode = true;
      }

    return(returnCode);
    }

  /**
  * Move to next row of the RowSet.
  * @return <tt>true</tt> if the RowSet has 1 or more rows
  * and we weren't already on the last row
  */
  public boolean nextRow()
    {
    return(setCurrentRowNumber(currentRowNumber + 1));
    }

  /**
  * Move to previous row of the RowSet.
  * @return <tt>true</tt> if the RowSet has 1 or more rows
  * and we weren't already on the first row
  */
  public boolean prevRow()
    {
    return(setCurrentRowNumber(currentRowNumber - 1));
    }

  /**
  * Move to first row of the RowSet.
  * @return <tt>true</tt> if the RowSet has 1 or more rows
  */
  public boolean first()
    {
    return(setCurrentRowNumber(0));
    }

  /**
  * Move to last row of the RowSet.
  * @return <tt>true</tt> if the RowSet has 1 or more rows
  */
  public boolean last()
    {
    return(setCurrentRowNumber(rowCount - 1));
    }

  /**
  * Get column <tt>columnId</tt> as a java.sql.Date
  * @param String theColumnName The name of the column
  * @return java.sql.Date the Value of column #columnId if it can be turned into a Date.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 1.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId can not be turned into a date
  */
  public java.sql.Date getDate(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return (getDate(getColumnId(theColumnName)));
    }

  /**
  * Get column <tt>columnId</tt> as a java.sql.Date
  * @param int columnId the ID of the column
  * @return java.sql.Date the Value of column #columnId if it can be turned into a Date.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 1.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId can not be turned into a date
  */
  public java.sql.Date getDate(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.sql.Date tempDate = null;

    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Will throw CSDBInvalidDatatypeCastException if the value of temprow[columnId] can not
    // be turned into a date
    return(getDate(underlyingOracleDatatypes[columnId]
                  ,columnOracleDatatypeNames[columnId]
                  ,columnNames[columnId]
                  ,tempRow[columnId]
                  ,theTimestampFormat));
    }
  /**
  * Get Object <tt>tempObject</tt> as a java.sql.Date
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempObject An Object that presumably contains a java.sql.Date
  * @param SimpleDateFormat theTimestampFormat Used if tempObject is a String.
  * @return java.sql.Timestamp the Value of column #columnId if it can be turned into a Date.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a Date.
  * This will happen every time if the underlying datatype is fundamentally un-datelike (e.g. a BLOB)
  * or if the underlying data type can in theory be turned into a date depending on the data itself
  * (e.g. VARCHAR2) but not in this case.
  */
  private java.sql.Date getDate
       (int oracleUnderlyingDatatype
       ,String oracleDataType
       ,String oracleColumnName
       ,Object tempObject
       ,SimpleDateFormat theTimestampFormat) throws CSDBInvalidDatatypeCastException
    {
    java.sql.Date tempDate = null;

    if (tempObject == null)
      {
      tempDate = null;
      }
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_DATE_DATATYPE:
          {
          tempDate = new java.sql.Date(((java.sql.Date)tempObject).getTime());
          break;
          }
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:
          {                                                                 
          tempDate = convertTIMESTAMPToDate(tempObject, theTimestampFormat); 
          break;                                                             
          }                                                                  
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:
          {                                                                   
          tempDate = convertTIMESTAMPTZToDate(tempObject, theTimestampFormat); 
          break;                                                               
          }                                                                    
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:
          {                                                                    
          tempDate = convertTIMESTAMPLTZToDate(tempObject, theTimestampFormat);
          break;                                                               
          }                                                                    
        case SqlUtils.ORACLE_TEXT_DATATYPE:
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_BINARY_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"java.sql.Date",oracleColumnName);
          break;
          }
        }
      }

    return(tempDate);

    }
  /**
  * Get column <tt>columnId</tt> as a java.sql.Timestamp
  * @param String theColumnName The name of the column
  * @return java.util.Date the Value of column #columnId if it can be turned into a Date.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 1.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId can not be turned into a date
  */
  public java.sql.Timestamp getTimestamp(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return (getTimestamp(getColumnId(theColumnName)));
    }
  /**
  * Get column <tt>columnId</tt> as a java.sql.Timestamp
  * @param int theColumnId The id of the column
  * @return java.util.Date the Value of column #columnId if it can be turned into a Date.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 1.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId can not be turned into a date
  */
  public java.sql.Timestamp getTimestamp(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.sql.Timestamp tempDate = null;

    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Will throw CSDBInvalidDatatypeCastException if the value of temprow[columnId] can not
    // be turned into a date
    return(getTimestamp(underlyingOracleDatatypes[columnId]
                       ,columnOracleDatatypeNames[columnId]
                       ,columnNames[columnId]
                       ,tempRow[columnId]
                       ,theTimestampFormat));
    }
  /**
  * Get Object <tt>tempObject</tt> as a java.sql.Timestamp
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempObject
  * @param SimpleDateFormat theTimestampFormat Used if tempObject is a String.
  * @return java.sql.Timestamp the Value of column #columnId if it can be turned into a Date.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a Date.
  * This will happen every time if the underlying datatype is fundamentally un-datelike (e.g. a BLOB)
  * or if the underlying data type can in theory be turned into a date depending on the data itself
  * (e.g. VARCHAR2) but not in this case.
  */
  private java.sql.Timestamp getTimestamp
       (int oracleUnderlyingDatatype
       ,String oracleDataType
       ,String oracleColumnName
       ,Object tempObject
       ,SimpleDateFormat theTimestampFormat) throws CSDBInvalidDatatypeCastException
    {
    java.sql.Timestamp tempDate = null;

    if (tempObject == null)
      {
      tempDate = null;
      }
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_DATE_DATATYPE:
          {
          tempDate = new java.sql.Timestamp(((java.sql.Timestamp)tempObject).getTime());
          break;
          }
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:
          {                                                                          
          tempDate = convertTIMESTAMPToTimestamp(tempObject,theTimestampFormat);     
          break;                                                                     
          }                                                                          
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:
          {                                                                          
          tempDate = convertTIMESTAMPTZToTimestamp(tempObject,theTimestampFormat);   
          break;                                                                     
          }                                                                          
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:
          {                                                                           
          tempDate = convertTIMESTAMPLTZToTimestamp(tempObject,theTimestampFormat);  
          break;                                                                      
          }                                                                          
        case SqlUtils.ORACLE_TEXT_DATATYPE:
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_BINARY_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"java.sql.Timestamp",oracleColumnName);
          break;
          }
        }
      }

    return(tempDate);

    }

  /**
  * Return <tt>true</tt> if the column is <tt>null</tt>.
  *
  * @param String theColumnName The name of the column
  * @throws CSInvalidColumnIdException If the columnId is not that of a valid column
  * @throws CSNoDataInRowSetException If there are no rows.
  * @return <tt>true</tt> is this column is <tt>null</tt>
  * @return <tt>false</tt> is this column is not <tt>null</tt>
  */
  public boolean isNull(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException
    {
    return(isNull(getColumnId(theColumnName)));
    }

  /**
  * Return <tt>true</tt> if the column is <tt>null</tt>.
  *
  * @param int columnId The column Id
  * @throws CSInvalidColumnIdException If the columnId is not that of a valid column
  * @throws CSNoDataInRowSetException If there are no rows.
  * @return <tt>true</tt> is this column is <tt>null</tt>
  * @return <tt>false</tt> is this column is not <tt>null</tt>
  */
  public boolean isNull(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException
    {
    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Since we know we have 1 or more rows get the current one.
    Object[] tempRow = getCurrentRow();

    if (tempRow[columnId] == null)
      {
      return(true);
      }

    return(false);
    }

 /**
  * Get column <tt>columnId</tt> as a Boolean Object
  *
  * @param String theColumnName The name of the column
  * @return <tt>true</tt> If the underlying column is a number with a value of zero.
  * @return <tt>false</tt> If the underlying column is a number with a value of zero.
  * @return <tt>true</tt> If the underlying column is a String with a value of "Y","y","T" or "t".
  * @return <tt>false</tt> If the underlying column is a number with a value of "N","n","F","f",""
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Boolean
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Boolean getBooleanObj(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return (getBooleanObj(getColumnId(theColumnName)));
    }
 /**
  * Get column <tt>columnId</tt> as a Boolean Object
  * @param int columnId The number of the column.
  * @return <tt>true</tt> If the underlying column is a number with a value of zero.
  * @return <tt>false</tt> If the underlying column is a number with a value of zero.
  * @return <tt>true</tt> If the underlying column is a String with a value of "Y","y","T" or "t".
  * @return <tt>false</tt> If the underlying column is a number with a value of "N","n","F","f",""
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Boolean
  * or the column is a number that is too big too fit into a Boolean.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Boolean getBooleanObj(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // will throw CSUnsupportedDatatypeException if currentRow(columnId) can not be turned into a String
    return(getBooleanObj(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]));

    }

 /**
  * Get column <tt>columnId</tt> as a Boolean.
  *
  * This method handles boolean data returned by stored procedure calls and
  * makes a reasonable attempt to convert non-boolean data to boolean. A List of positive and
  * negative values is used to decide whether to return <tt>true</tt> or <tt>false</tt>. If the
  * String is zero length then <tt>null</tt> is returned. In the case of numbers <tt>0</tt> means <tt>false</tt>
  * and any other value means <tt>true</tt>
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue An Object that presumably contains a java.sql.Date
  * @return <tt>true</tt> If the underlying column is a number with a value other than zero
  * @return <tt>false</tt> If the underlying column is a number with a value of zero.
  * @return <tt>true</tt> If the underlying column is a String with a value of "Y","y","T" or "t".
  * @return <tt>false</tt> If the underlying column is a number with a value of "N","n","F","f"
  * @return <tt>null</tt> If the underlying column is <tt>null</tt> or a zero length string.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a String
  */
  private Boolean getBooleanObj(int oracleUnderlyingDatatype
                               ,String oracleDataType
                               ,String oracleColumnName
                               ,Object tempValue) throws CSDBInvalidDatatypeCastException
    {
    /**
    * List of ways of saying yes
    */
    final String[] positiveValues = {"Y","T","1","YES","TRUE"};

    /**
    * List of ways of saying No
    */
    final String[] negativeValues = {"N","F","0","NO","FALSE"};

    Boolean newBoolean = new Boolean(false);

    if (tempValue == null)
      {
      newBoolean = null;
      }
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
          {
          newBoolean = new Boolean(((Boolean)tempValue).booleanValue());
          }
        case SqlUtils.ORACLE_TEXT_DATATYPE:
          {
          String newString = tempValue.toString().trim();
          if (newString.length() == 0)
            {
            newBoolean = new Boolean(false);
            }
          else
            {
            // Loop through lists of values and see if we can find a match
            // Note that this loop assumes the lists are equal in length
            for (int i=0; i < positiveValues.length; i++)
              {
              if (newString.equalsIgnoreCase(positiveValues[i]))
                {
                newBoolean = new Boolean(true);
                break;
                }
              else if (newString.equalsIgnoreCase(negativeValues[i]))
                {
                newBoolean = new Boolean(false);
                break;
                }
              }
            }

          break;
          }
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
          {
          BigDecimal newBigDecimal = (BigDecimal)tempValue;

          if (newBigDecimal.doubleValue() == 0)
            {
            newBoolean = new Boolean(false);
            }
          else
            {
            newBoolean = new Boolean(true);
            }

          break;
          }
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:
        case SqlUtils.ORACLE_DATE_DATATYPE:
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_BINARY_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"Boolean",oracleColumnName);
          break;
          }
        }
      }

    return (newBoolean);
    }
 /**                                                               
  * Get column <tt>columnId</tt> as a oracle.sql.TIMESTAMP Object  
  *                                                                
  * @param String theColumnName The name of the column             
  * @return an oracle.sql.TIMESTAMP.                               
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle. 
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a TIMESTAMP  
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.    
  */                                                                           
  public oracle.sql.TIMESTAMP getTIMESTAMP(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException 
    {                                                                                                                                                           
    return (getTIMESTAMP(getColumnId(theColumnName)));                                                                                                         
    }                                                                                                                                                          
 /**                                                                                                                                                           
  * Get column <tt>columnId</tt> as a TIMESTAMP Object                                                                                                         
  * @param int columnId The number of the column.                                                                                                              
  * @return an oracle.sql.TIMESTAMP.                                                                                                                           
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.                                                                   
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a TIMESTAMP                                            
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.                                                                                     
  */                                                                                                                                                           
  public oracle.sql.TIMESTAMP getTIMESTAMP(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException        
    {                                                                                                                                                          
    Object[] tempRow = getCurrentRow();                                                                                                                       
                                                                                                                                                           
    // Will throw CSNoDataInRowSetException if no rows exist.                                                                                            
    checkRows();                                                                                                                                        
                                                                                                                                                       
    // Will throw CSInvalidColumnIdException if columnId is out of range.                                                                             
    checkRange(columnId);                                                                                                                            
                                                                                                                                                    
    // will throw CSUnsupportedDatatypeException if currentRow(columnId) can not be turned into a String                                           
    return(getTIMESTAMP (underlyingOracleDatatypes[columnId]                                                                                      
                        ,columnOracleDatatypeNames[columnId]                                                                                    
                        ,columnNames[columnId]                                                                                                 
                        ,tempRow[columnId]));                                                                                                 
                                                                                                                                             
    }                                                                                                                                       
                                                                                                                                           
 /**                                                                                                                                     
  * Get column <tt>columnId</tt> as a TIMESTAMP.                                                                                        
  *                                                                                                                          
  * This method handles TIMESTAMP data returned by stored procedure calls                                              
  * @since Oracle 9i                                                                   
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.                                    
  * @param String oracleDataType The oracle data type of the column                                                          
  * @param String oracleColumnName The name of the column.                                                                   
  * @param Object tempValue An Object that presumably contains a java.sql.Date                                               
  * @return an oracle.sql.TIMESTAMP.                                                                                         
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a TIMESTAMP          
  */                                                                                                                         
  private oracle.sql.TIMESTAMP getTIMESTAMP(int oracleUnderlyingDatatype                                                     
                                           ,String oracleDataType                                                            
                                           ,String oracleColumnName                                                          
                                           ,Object tempValue) throws CSDBInvalidDatatypeCastException                       
    {                                                                           
    oracle.sql.TIMESTAMP newTIMESTAMP = null;                                   
                                                                                
    if (tempValue != null)                                                      
      {                                                                         
      switch (oracleUnderlyingDatatype)                                         
        {                                                                       
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:                                
          {                                                                     
          newTIMESTAMP = (oracle.sql.TIMESTAMP)tempValue;                       
          break;                                                                
          }                                                                     
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:                             
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:                              
        case SqlUtils.ORACLE_TEXT_DATATYPE:                                     
        case SqlUtils.ORACLE_NUMBER_DATATYPE:                                   
        case SqlUtils.ORACLE_DATE_DATATYPE:                                     
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:                                 
        case SqlUtils.ORACLE_BINARY_DATATYPE:                                   
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:                              
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:                                
        case SqlUtils.ORINDASOFT_READONLYROWSET:                                
        case SqlUtils.ORACLE_OBJECT_DATATYPE:                                   
        case SqlUtils.ORACLE_TABLE_DATATYPE:                                    
        case SqlUtils.ORACLE_VARRAY_DATATYPE:                                   
        default:                                                                
          {                                                                                 
          createInvalidDatatypeCastException(oracleDataType,"TIMESTAMP",oracleColumnName);  
          break;                                                                            
          }                                                                                 
        }                                                                                   
      }                                                                                     
                                                                                            
    return (newTIMESTAMP);                                                                  
    }                                                                                                                                                        
                                                                                                                                                             
 /**                                                                                                                                                         
  * Get column <tt>columnId</tt> as a oracle.sql.TIMESTAMPTZ Object                                                                                          
  *                                                                                                                                                          
  * @param String theColumnName The name of the column                                                                                                       
  * @return an oracle.sql.TIMESTAMPTZ.                                                                                                                       
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.                                                                 
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a TIMESTAMPTZ                                        
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.                                                                                   
  */                                                                                                                                                         
  public oracle.sql.TIMESTAMPTZ getTIMESTAMPTZ(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException   
    {                                                                                                                                                        
    return (getTIMESTAMPTZ(getColumnId(theColumnName)));                                                                                                     
    }                                                                                                                                                        
                                                                                                                                                             
 /**                                                                                                                                                         
  * Get column <tt>columnId</tt> as a TIMESTAMPTZ Object                                                                                                     
  * @param int columnId The number of the column.                                                                                                            
  * @return an oracle.sql.TIMESTAMPTZ.                                                                                                                       
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.                                                                 
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a TIMESTAMPTZ                                        
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.                                                                                   
  */                                                                                                                                                         
  public oracle.sql.TIMESTAMPTZ getTIMESTAMPTZ(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException  
    {                                                                                                                               
    Object[] tempRow = getCurrentRow();                                                                                             
                                                                                                                                    
    // Will throw CSNoDataInRowSetException if no rows exist.                                                                       
    checkRows();                                                                                                                    
                                                                                                                                    
    // Will throw CSInvalidColumnIdException if columnId is out of range.                                                           
    checkRange(columnId);                                                                                                           
                                                                                                                                    
    // will throw CSUnsupportedDatatypeException if currentRow(columnId) can not be turned into a String                            
    return(getTIMESTAMPTZ (underlyingOracleDatatypes[columnId]                                                                      
                        ,columnOracleDatatypeNames[columnId]                                                                        
                        ,columnNames[columnId]                                                                                      
                        ,tempRow[columnId]));                                                                                       
                                                                                                                                    
    }                                                                                                                               
 /**                                                                                                                                
  * Get column <tt>columnId</tt> as a TIMESTAMPTZ.                                                                                  
  *                                                                                                                                 
  * This method handles TIMESTAMPTZ data returned by stored procedure calls                                                         
  * @since Oracle 9i                                                                   
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.                                           
  * @param String oracleDataType The oracle data type of the column                                                                 
  * @param String oracleColumnName The name of the column.                                                                          
  * @param Object tempValue An Object that presumably contains a oracle.sql.TIMESTAMPLTZ                                            
  * @return an oracle.sql.TIMESTAMPTZ.                                                                                              
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a String                    
  */                                                                                                                                
  private oracle.sql.TIMESTAMPTZ getTIMESTAMPTZ(int oracleUnderlyingDatatype                                                        
                                           ,String oracleDataType                                                                   
                                           ,String oracleColumnName                                                                 
                                           ,Object tempValue) throws CSDBInvalidDatatypeCastException                               
    {                                                                                                                               
    oracle.sql.TIMESTAMPTZ newTIMESTAMPTZ = null;                                                                                   
                                                                                                                                    
    if (tempValue != null)                                                                                                          
      {                                                                                                                             
      switch (oracleUnderlyingDatatype)                                                                                             
        {                                                                                                                           
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:                                                                                  
          {                                                                                                                         
          newTIMESTAMPTZ = (oracle.sql.TIMESTAMPTZ)tempValue;                                                                       
          break;                                                                                                                    
          }                                                                                                                         
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:                                                                                    
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:                                                                                 
        case SqlUtils.ORACLE_TEXT_DATATYPE:                                                                                         
        case SqlUtils.ORACLE_NUMBER_DATATYPE:                                                                                       
        case SqlUtils.ORACLE_DATE_DATATYPE:                                                                                         
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:                                                                                     
        case SqlUtils.ORACLE_BINARY_DATATYPE:                                                                                       
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:                                                                                  
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:                                                                                    
        case SqlUtils.ORINDASOFT_READONLYROWSET:                                                                                    
        case SqlUtils.ORACLE_OBJECT_DATATYPE:                                                                                       
        case SqlUtils.ORACLE_TABLE_DATATYPE:                                                                                        
        case SqlUtils.ORACLE_VARRAY_DATATYPE:                                                                                       
        default:                                                                                                                    
          {                                                                                                                         
          createInvalidDatatypeCastException(oracleDataType,"TIMESTAMPTZ",oracleColumnName);                                        
          break;                                                                                                                    
          }                                                                                                                         
        }                                                                                                                           
      }                                                                                                                             
                                                                                                                                    
    return (newTIMESTAMPTZ);                                                                                                        
    }                                                                                                                               
                                                                                                                                    
  /**                                                                                                                               
  * Get column <tt>columnId</tt> as a oracle.sql.TIMESTAMPLTZ Object                                                                
  *                                                                                                                                 
  * @param String theColumnName The name of the column                                                                              
  * @return an oracle.sql.TIMESTAMPLTZ.                                                                                             
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.                                        
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a TIMESTAMPLTZ              
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.                                                          
  */                                                                                                                                
  public oracle.sql.TIMESTAMPLTZ getTIMESTAMPLTZ(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException,
 CSDBInvalidDatatypeCastException {                                                                                                 
    return (getTIMESTAMPLTZ(getColumnId(theColumnName)));                                                                           
    }                                                                                                                               
 /**                                                                                                                                
  * Get column <tt>columnId</tt> as a TIMESTAMPLTZ Object                                                                           
  * @param int columnId The number of the column.                                                                                   
  * @return an oracle.sql.TIMESTAMPLTZ.                                                                                             
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.                                        
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a TIMESTAMPLTZ              
  * or the column is a number that is too big too fit into a TIMESTAMPLTZ.                                                          
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.                                                          
  */                                                                                                                                
  public oracle.sql.TIMESTAMPLTZ getTIMESTAMPLTZ(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException   
    {                                                                                                                                                           
    Object[] tempRow = getCurrentRow();                                                                                             
                                                                                                                                    
    // Will throw CSNoDataInRowSetException if no rows exist.                                                                       
    checkRows();                                                                                                                    
                                                                                                                                    
    // Will throw CSInvalidColumnIdException if columnId is out of range.                                                           
    checkRange(columnId);                                                                                                           
                                                                                                                                    
    // will throw CSUnsupportedDatatypeException if currentRow(columnId) can not be turned into a String                            
    return(getTIMESTAMPLTZ (underlyingOracleDatatypes[columnId]                                                                     
                        ,columnOracleDatatypeNames[columnId]                                                                        
                        ,columnNames[columnId]                                                                                      
                        ,tempRow[columnId]));                                                                                       
                                                                                                                                    
    }                                                                                                                               
 /**                                                                                                                                
  * Get column <tt>columnId</tt> as a TIMESTAMPLTZ.                                                                                 
  *                                                                                                                                 
  * This method handles TIMESTAMPLTZ data returned by stored procedure calls                                                        
  * @since Oracle 9i                                                                   
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.                                           
  * @param String oracleDataType The oracle data type of the column                                                                 
  * @param String oracleColumnName The name of the column.                                                                          
  * @param Object tempValue An Object that presumably contains a oracle.sql.TIMESTAMPLTZ                                            
  * @return an oracle.sql.TIMESTAMPLTZ.                                                                                             
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a oracle.sql.TIMESTAMPLTZ   
  */                                                                                                                                
  private oracle.sql.TIMESTAMPLTZ getTIMESTAMPLTZ(int oracleUnderlyingDatatype                                                      
                                           ,String oracleDataType                                                                   
                                           ,String oracleColumnName                                                                 
                                           ,Object tempValue) throws CSDBInvalidDatatypeCastException                               
    {                                                                                                                               
    oracle.sql.TIMESTAMPLTZ newTIMESTAMPLTZ = null;                                               
                                                                                                  
    if (tempValue == null)                                                                        
      {                                                                                           
      newTIMESTAMPLTZ = null;                                                                     
      }                                                                                           
    else                                                                                          
      {                                                                                           
      switch (oracleUnderlyingDatatype)                                                           
        {                                                                                         
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:                                               
          {                                                                                       
          newTIMESTAMPLTZ = (oracle.sql.TIMESTAMPLTZ)tempValue;                                   
          break;                                                                                  
          }                                                                                       
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:                                                  
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:                                                
        case SqlUtils.ORACLE_TEXT_DATATYPE:                                                       
        case SqlUtils.ORACLE_NUMBER_DATATYPE:                                                     
        case SqlUtils.ORACLE_DATE_DATATYPE:                                                       
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:                                                   
        case SqlUtils.ORACLE_BINARY_DATATYPE:                                                     
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:                                                
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:                                                  
        case SqlUtils.ORINDASOFT_READONLYROWSET:                                                  
        case SqlUtils.ORACLE_OBJECT_DATATYPE:                                                     
        case SqlUtils.ORACLE_TABLE_DATATYPE:                                                      
        case SqlUtils.ORACLE_VARRAY_DATATYPE:                                                     
        default:                                                                                  
          {                                                                                       
          createInvalidDatatypeCastException(oracleDataType,"TIMESTAMPLTZ",oracleColumnName);     
          break;                                                                                  
          }                                                                                       
        }                                                                                         
      }                                                                                           
                                                                                                  
    return (newTIMESTAMPLTZ);                                                                     
    }                                                                                             
                                                                                                  
 /**
  * Get column <tt>columnId</tt> as a boolean.
  * @param String theColumnName The name of the column
  * @return <tt>true</tt> If the underlying column is a number with a value of zero.
  * @return <tt>false</tt> If the underlying column is a number with a value of zero.
  * @return <tt>true</tt> If the underlying column is a String with a value of "Y","y","T" or "t".
  * @return <tt>false</tt> If the underlying column is a number with a value of "N","n","F","f",""
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a boolean
  * or the column is a number that is too big too fit into a boolean.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public boolean getBoolean(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    return(getBoolean(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a boolean.
  * @param int columnId The number of the column.
  * @return <tt>true</tt> If the underlying column is a number with a value of zero.
  * @return <tt>false</tt> If the underlying column is a number with a value of zero.
  * @return <tt>true</tt> If the underlying column is a String with a value of "Y","y","T" or "t".
  * @return <tt>false</tt> If the underlying column is a number with a value of "N","n","F","f",""
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a boolean
  * or the column is a number that is too big too fit into a boolean.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public boolean getBoolean(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Check to make sure this isnt a null value
    if (tempRow[columnId] == null)
      {
      throw new CSAttemptToGetNullException("Attempt made to return 'null' in a method that returns 'boolean', 'boolean' can never be null.",columnId);
      }

    // will throw CSUnsupportedDatatypeException if currentRow(columnId) can not be turned into a String
    return(getBooleanObj(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]).booleanValue());
    }
 /**
  * Get column <tt>columnId</tt> as a Byte Object
  * @param String theColumnName The name of the column
  * @return Byte the Value of column #columnId if it can be turned into a Byte.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Byte
  * or the column is a number that is too big too fit into a Byte.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Byte getByteObj(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return (getByteObj(getColumnId(theColumnName)));
    }
 /**
  * Get column <tt>columnId</tt> as a Byte Object
  * @param int columnId The number of the column.
  * @return Byte the Value of column #columnId if it can be turned into a Byte.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Byte
  * or the column is a number that is too big too fit into a Byte.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Byte getByteObj(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.math.BigDecimal tempNumber = null;
    Byte tempByte;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    // See if it is null.
    if (tempNumber == null)
      {
      tempByte = null;
      }
    else
      {
      // Now see if it can be turned into an Integer
      // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
      // be safely turned into an int.
      if (   tempNumber.doubleValue() > Byte.MAX_VALUE
          || tempNumber.doubleValue() < Byte.MIN_VALUE)
        {
        throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                               + tempNumber.doubleValue() + " to a Byte.","java.math.BigDecimal","Byte");
        }

      // If tempNumber is > Integer.MAX_VALUE we get a large negative number here.
      tempByte = new Byte(tempNumber.byteValue());
      }


    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempByte);
    }
 /**
  * Get column <tt>columnId</tt> as a byte.
  * @param String theColumnName The name of the column
  * @return byte the Value of column #columnId if it can be turned into a byte.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a byte
  * or the column is a number that is too big too fit into a byte.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public byte getByte(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    return(getByte(getColumnId(theColumnName)));
    }
    
 /**
  * Get column <tt>columnId</tt> as a byte.
  * @param int columnId The number of the column.
  * @return byte the Value of column #columnId if it can be turned into a byte.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a byte
  * or the column is a number that is too big too fit into a byte.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public byte getByte(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    java.math.BigDecimal tempNumber = null;
    byte tempByte;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    if (tempNumber == null)
      {
      throw new CSAttemptToGetNullException("Attempt made to return 'null' in a method that returns 'byte', 'byte' can never be null.",columnId);
      }

    // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
    // be safely turned into a Byte
    if (   tempNumber.doubleValue() > Byte.MAX_VALUE
        || tempNumber.doubleValue() < Byte.MIN_VALUE)
      {
      throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                             + tempNumber.doubleValue() + " to a byte.","java.math.BigDecimal","byte");
      }

    // If tempNumber is > Byte.MAX_VALUE we get a large negative number here.
    tempByte = tempNumber.byteValue();

    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempByte);
    }
 /**
  * Get column <tt>columnId</tt> as a Short Object
  * @param String theColumnName The name of the column
  * @return Short the Value of column #columnId if it can be turned into a Short.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Short
  * or the column is a number that is too big too fit into a Short.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Short getShortObj(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getShortObj(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a Short Object
  * @param int columnId The number of the column.
  * @return Short the Value of column #columnId if it can be turned into a Short.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Short
  * or the column is a number that is too big too fit into a Short.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Short getShortObj(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.math.BigDecimal tempNumber = null;
    Short tempShort;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    // See if it is null.
    if (tempNumber == null)
      {
      tempShort = null;
      }
    else
      {
      // Now see if it can be turned into an Integer
      // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
      // be safely turned into an int.
      if (   tempNumber.doubleValue() > Short.MAX_VALUE
          || tempNumber.doubleValue() < Short.MIN_VALUE)
        {
        throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                               + tempNumber.doubleValue() + " to a Short.","java.math.BigDecimal","Short");
        }

      // If tempNumber is > Integer.MAX_VALUE we get a large negative number here.
      tempShort = new Short(tempNumber.shortValue());
      }


    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempShort);
    }
 /**
  * Get column <tt>columnId</tt> as a short.
  * @param String theColumnName The name of the column
  * @return short the Value of column #columnId if it can be turned into a short.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a short
  * or the column is a number that is too big too fit into a short.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public short getShort(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    return(getShort(getColumnId(theColumnName)));
    }
    
 /**
  * Get column <tt>columnId</tt> as a short.
  * @param int columnId The number of the column.
  * @return short the Value of column #columnId if it can be turned into a short.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a short
  * or the column is a number that is too big too fit into a short.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public short getShort(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    java.math.BigDecimal tempNumber = null;
    short tempShort;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    if (tempNumber == null)
      {
      throw new CSAttemptToGetNullException("Attempt made to return 'null' in a method that returns 'short', 'short' can never be null.",columnId);
      }

    // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
    // be safely turned into a short
    if (   tempNumber.doubleValue() > Short.MAX_VALUE
        || tempNumber.doubleValue() < Short.MIN_VALUE)
      {
      throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                             + tempNumber.doubleValue() + " to a short.","java.math.BigDecimal","short");
      }

    // If tempNumber is > Short.MAX_VALUE we get a large negative number here.
    tempShort = tempNumber.shortValue();

    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempShort);
    }
 /**
  * Get column <tt>columnId</tt> as a Integer Object
  * @param String theColumnName The name of the column
  * @return Integer the Value of column #columnId if it can be turned into a Integer.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Integer
  * or the column is a number that is too big too fit into a Integer.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Integer getIntegerObj(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getIntegerObj(getColumnId(theColumnName)));
    }
 /**
  * Get column <tt>columnId</tt> as a Integer Object
  * @param int columnId The number of the column.
  * @return Integer the Value of column #columnId if it can be turned into a Integer.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Integer
  * or the column is a number that is too big too fit into a Integer.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Integer getIntegerObj(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.math.BigDecimal tempNumber = null;
    Integer tempInteger;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    // See if it is null.
    if (tempNumber == null)
      {
      tempInteger = null;
      }
    else
      {
      // Now see if it can be turned into an Integer
      // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
      // be safely turned into an int.
      if (   tempNumber.doubleValue() > Integer.MAX_VALUE
          || tempNumber.doubleValue() < Integer.MIN_VALUE)
        {
        throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                               + tempNumber.doubleValue() + " to an Integer.","java.math.BigDecimal","Integer");
        }

      // If tempNumber is > Integer.MAX_VALUE we get a large negative number here.
      tempInteger = new Integer(tempNumber.intValue());
      }


    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempInteger);
    }
 /**
  * Get column <tt>columnId</tt> as an int.
  * @param String theColumnName The name of the column
  * @return int the Value of column #columnId if it can be turned into a int.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a int
  * or the column is a number that is too big too fit into a int.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public int getInt(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    return(getInt(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as an int.
  * @param int columnId The number of the column.
  * @return int the Value of column #columnId if it can be turned into a int.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a int
  * or the column is a number that is too big too fit into a int.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public int getInt(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    java.math.BigDecimal tempNumber = null;
    int tempInt;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    if (tempNumber == null)
      {
      throw new CSAttemptToGetNullException("Attempt made to return 'null' in a method that returns 'int', 'int' can never be null.",columnId);
      }

    // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
    // be safely turned into an int.
    if (   tempNumber.doubleValue() > Integer.MAX_VALUE
        || tempNumber.doubleValue() < Integer.MIN_VALUE)
      {
      throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                             + tempNumber.doubleValue() + " to an int.","java.math.BigDecimal","int");
      }

    // If tempNumber is > Integer.MAX_VALUE we get a large negative number here.
    tempInt = tempNumber.intValue();

    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempInt);
    }
  /**
  * Get column <tt>columnId</tt> as a Long Object
  * @param String theColumnName The name of the column
  * @return Long the Value of column #columnId if it can be turned into a Long.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Long
  * or the column is a number that is too big too fit into a Long.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Long getLongObj(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getLongObj(getColumnId(theColumnName)));
    }

  /**
  * Get column <tt>columnId</tt> as a Long Object
  * @param int columnId The number of the column.
  * @return Long the Value of column #columnId if it can be turned into a Long.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Long
  * or the column is a number that is too big too fit into a Long.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Long getLongObj(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.math.BigDecimal tempNumber = null;
    Long tempLong;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    // See if it is null.
    if (tempNumber == null)
      {
      tempLong = null;
      }
    else
      {
      // Now see if it can be turned into an Integer
      // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
      // be safely turned into an int.
      if (   tempNumber.doubleValue() > Long.MAX_VALUE
          || tempNumber.doubleValue() < Long.MIN_VALUE)
        {
        throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                               + tempNumber.doubleValue() + " to a Long.","java.math.BigDecimal","Long");
        }

      // If tempNumber is > Long.MAX_VALUE we get a large negative number here.
      tempLong = new Long(tempNumber.longValue());
      }


    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempLong);
    }
 /**
  * Get column <tt>columnId</tt> as a long.
  * @param String theColumnName The name of the column
  * @return long the Value of column #columnId if it can be turned into a long.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a long
  * or the column is a number that is too big too fit into a long.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public long getLong(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    return(getLong(getColumnId(theColumnName)));
    }
    
 /**
  * Get column <tt>columnId</tt> as a long.
  * @param int columnId The number of the column.
  * @return long the Value of column #columnId if it can be turned into a long.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a long
  * or the column is a number that is too big too fit into a long.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public long getLong(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    java.math.BigDecimal tempNumber = null;
    long tempLong;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    if (tempNumber == null)
      {
      throw new CSAttemptToGetNullException("Attempt made to return 'null' in a method that returns 'long', 'long' can never be null.",columnId);
      }

    // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
    // be safely turned into an int.
    if (   tempNumber.doubleValue() > Long.MAX_VALUE
        || tempNumber.doubleValue() < Long.MIN_VALUE)
      {
      throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                             + tempNumber.doubleValue() + " to a long.","java.math.BigDecimal","long");
      }

    // If tempNumber is > Integer.MAX_VALUE we get a large negative number here.
    tempLong = tempNumber.longValue();

    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempLong);
    }
 /**
  * Get column <tt>columnId</tt> as a Float Object
  * @param String theColumnName The name of the column
  * @return Float the Value of column #columnId if it can be turned into a Float.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Float
  * or the column is a number that is too big too fit into a Float.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Float getFloatObj(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getFloatObj(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a Float Object
  * @param int columnId The number of the column.
  * @return Float the Value of column #columnId if it can be turned into a Float.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Float
  * or the column is a number that is too big too fit into a Float.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Float getFloatObj(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.math.BigDecimal tempNumber = null;
    Float tempFloat;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    // See if it is null.
    if (tempNumber == null)
      {
      tempFloat = null;
      }
    else
      {
      // Now see if it can be turned into an Integer
      // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
      // be safely turned into an int.
      if (   tempNumber.doubleValue() > Float.MAX_VALUE
          || tempNumber.doubleValue() < Float.MIN_VALUE)
        {
        throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                               + tempNumber.doubleValue() + " to a Float.","java.math.BigDecimal","Float");
        }

      // If tempNumber is > Float.MAX_VALUE we get a large negative number here.
      tempFloat = new Float(tempNumber.floatValue());
      }


    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempFloat);
    }
 /**
  * Get column <tt>columnId</tt> as a float.
  * @param String theColumnName The name of the column
  * @return float the Value of column #columnId if it can be turned into a float.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a float
  * or the column is a number that is too big too fit into a float.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public float getFloat(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    return(getFloat(getColumnId(theColumnName)));
    }
    
 /**
  * Get column <tt>columnId</tt> as a float.
  * @param int columnId The number of the column.
  * @return float the Value of column #columnId if it can be turned into a float.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a float
  * or the column is a number that is too big too fit into a float.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public float getFloat(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    java.math.BigDecimal tempNumber = null;
    float tempFloat;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    if (tempNumber == null)
      {
      throw new CSAttemptToGetNullException("Attempt made to return 'null' in a method that returns 'float', 'float' can never be null.",columnId);
      }

    // java will *not* throw an exception if the BigDecimal we got from the Oracle DB can not
    // be safely turned into a float
    if (   tempNumber.doubleValue() > Float.MAX_VALUE
        || tempNumber.doubleValue() < Float.MIN_VALUE)
      {
      throw new CSDBInvalidDatatypeCastException("Attempt made to cast a 'java.math.BigDecimal' with a value of "
                                             + tempNumber.doubleValue() + " to a float.","java.math.BigDecimal","float");
      }

    // If tempNumber is too big we get garbage here.
    tempFloat = tempNumber.floatValue();

    // If we get to this line we must have a non-null value that was turned into a float
    return(tempFloat);
    }
 /**
  * Get column <tt>columnId</tt> as a Double Object
  * @param String theColumnName The name of the column
  * @return Double the Value of column #columnId if it can be turned into a Double.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Double
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Double getDoubleObj(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return (getDoubleObj(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a Double Object
  * @param int columnId The number of the column.
  * @return Double the Value of column #columnId if it can be turned into a Double.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a Double
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public Double getDoubleObj(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    java.math.BigDecimal tempNumber = null;
    Double tempDouble;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    // See if it is null.
    if (tempNumber == null)
      {
      tempDouble = null;
      }
    else
      {
      // If tempNumber is > Double.MAX_VALUE we get a large negative number here.
      tempDouble = new Double(tempNumber.doubleValue());
      }


    // If we get to this line we must have a non-null value that was turned into an int.
    return(tempDouble);
    }

 /**
  * Get column <tt>columnId</tt> as a double.
  * @param String theColumnName The name of the column
  * @return double the Value of column #columnId if it can be turned into a double.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a double
  * or the column is a number that is too big too fit into a double.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public double getDouble(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    return (getDouble(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a double.
  * @param int columnId The number of the column.
  * @return double the Value of column #columnId if it can be turned into a double.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a double
  * or the column is a number that is too big too fit into a double.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSAttemptToGetNullException if the value of <tt>columnId</tt> is <tt>null</tt>
  */
  public double getDouble(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSAttemptToGetNullException
    {
    java.math.BigDecimal tempNumber = null;
    double tempDouble;

    // First see if this column is a valid number.
    tempNumber = getBigDecimal(columnId);

    if (tempNumber == null)
      {
      throw new CSAttemptToGetNullException("Attempt made to return 'null' in a method that returns 'float', 'float' can never be null.",columnId);
      }


    // If tempNumber is too big we get garbage here.
    tempDouble = tempNumber.doubleValue();

    // If we get to this line we must have a non-null value that was turned into a float
    return(tempDouble);
    }
  /**
  * Get column <tt>columnId</tt> as a java.math.BigDecimal Object
  * @param String theColumnName The name of the column
  * @return java.math.BigDecimal the Value of column #columnId if it can be turned into a java.math.BigDecimal.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a java.math.BigDecimal
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public java.math.BigDecimal getBigDecimal(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getBigDecimal(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a java.math.BigDecimal Object
  * @param int columnId The number of the column.
  * @return java.math.BigDecimal the Value of column #columnId if it can be turned into a java.math.BigDecimal.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a java.math.BigDecimal
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public java.math.BigDecimal getBigDecimal(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Since we know we have 1 or more rows get the current one.
    Object[] tempRow = getCurrentRow();

    // Will throw CSUnsupportedDatatypeException if we don't handle the datatype of the underlying column and
    // CSDBInvalidDatatypeCastException if thr underlying field can not nbe turned into a BigDecimal
    return(getBigDecimal(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]
                        ,theNumberFormat));

    }

  /**
  * Get column <tt>columnId</tt> as a java.math.BigDecimal
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue
  * @param NumberFormat theNumberFormat used if the underlying column is a String that needs to be turned into a number.
  * @return java.lang.BigDecimal the Value of column #columnId if it can be turned into a BigDecimal.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a BigDecimal.
  */
  private java.math.BigDecimal getBigDecimal
     (int oracleUnderlyingDatatype
     ,String oracleDataType
     ,String oracleColumnName
     ,Object tempValue
     ,NumberFormat theNumberFormat) throws CSDBInvalidDatatypeCastException
  {
  BigDecimal newNumber = null;

  if (tempValue == null)
    {
    newNumber = null;
    }
  else
    {
    switch (oracleUnderlyingDatatype)
      {
      case SqlUtils.ORACLE_NUMBER_DATATYPE:
        {
         if (tempValue instanceof java.lang.Integer)
           {
           newNumber = new BigDecimal (((java.lang.Integer)tempValue).intValue());
           }
         else
           {
           newNumber = (BigDecimal)tempValue;
           }

        break;
        }
      case SqlUtils.ORACLE_TEXT_DATATYPE:
      case SqlUtils.ORACLE_DATE_DATATYPE:  
      case SqlUtils.ORACLE_BINARY_DATATYPE:
      case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
      case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
      case SqlUtils.ORACLE_OBJECT_DATATYPE:
      case SqlUtils.ORACLE_TABLE_DATATYPE:
      case SqlUtils.ORACLE_VARRAY_DATATYPE:
      case SqlUtils.ORINDASOFT_READONLYROWSET:
      case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
      case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
      default:
        {
        createInvalidDatatypeCastException(oracleDataType,"java.math.BigDecimal",oracleColumnName);
        break;
        }
      }
    }


   return(newNumber);
   }


 /**
  * Get column <tt>columnId</tt> as a String. Numbers will be formatted. Dates will be formatted according
  * to theTimeStampFormat.
  * @param String theColumnName The name of the column
  * @return String the Value of column #columnId if it can be turned into a string.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public String getString(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    return(getString(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a String. Numbers will be formatted. Dates will be formatted according
  * to theTimeStampFormat.
  * @param int columnId The number of the column.
  * @return String the Value of column #columnId if it can be turned into a string.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public String getString(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    String newString = null;

    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // will throw CSUnsupportedDatatypeException if currentRow(columnId) can not be turned into a String
    return(getString(underlyingOracleDatatypes[columnId]
                    ,columnOracleDatatypeNames[columnId]
                    ,columnNames[columnId]
                    ,tempRow[columnId]
                    ,theTimestampFormat
                    ,theNumberFormat));
    }

 /**
  * Get column <tt>columnId</tt> as a String.
  * Numbers will be formatted. Dates will be formatted according
  * to theTimeStampFormat.
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue An Object that presumably contains a java.sql.Date
  * @param SimpleDateFormat theTimestampFormat Used if tempObject is a String.
  * @param NumberFormat theNumberFormat Used if tempObject is a Number.
  * @return String the Value of column #columnId if it can be turned into a string.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a String
  */
  private String getString(int oracleUnderlyingDatatype
                          ,String oracleDataType
                          ,String oracleColumnName
                          ,Object tempValue
                          ,SimpleDateFormat theTimestampFormat
                          ,NumberFormat theNumberFormat) throws CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    String newString = null;

    if (tempValue == null)
      {
      newString = null;
      }
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_TEXT_DATATYPE:
          {
          newString = tempValue.toString().trim();
          break;
          }
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
          {
          newString = convertNumberToString(tempValue,theNumberFormat);
          break;
          }
        case SqlUtils.ORACLE_DATE_DATATYPE:
          {
          newString = convertTimestampToString(tempValue, theTimestampFormat);
          break;
          }
        case SqlUtils.ORACLE_TIMESTAMP_DATATYPE:
          {                                                                     
          newString = convertTIMESTAMPToString(tempValue, theTimestampFormat);  
          break;                                                                
          }                                                                     
        case SqlUtils.ORACLE_TIMESTAMPTZ_DATATYPE:
          {                                                                     
          newString = convertTIMESTAMPTZToString(tempValue, theTimestampFormat);
          break;                                                                
          }                                                                     
        case SqlUtils.ORACLE_TIMESTAMPLTZ_DATATYPE:
          {                                                                     
          newString = convertTIMESTAMPLTZToString(tempValue, theTimestampFormat);
          break;                                                                 
          }                                                                     
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_BLOB_DATATYPE:
        case SqlUtils.ORACLE_BFILE_DATATYPE:
        case SqlUtils.ORACLE_CLOB_DATATYPE:
          {
          if (tempValue instanceof File)
            {
            newString = ((File)tempValue).getAbsolutePath();
            }
          else if (tempValue instanceof char[])
            {
            newString = new String((char[])tempValue);
            }
          else
            {
            newString = tempValue.toString();
            }
          break;
          }
        case SqlUtils.ORACLE_INTERVAL_DAY_TO_SECOND_DATATYPE:
          {                                                      
          newString = tempValue.toString().trim();               
          break;                                                  
          }                                                       
        case SqlUtils.ORACLE_INTERVAL_YEAR_TO_MONTH_DATATYPE:
          {                                                      
          newString = tempValue.toString().trim();               
          break;                                                  
          }                                                       
        case SqlUtils.ORACLE_ROWID_DATATYPE:
          {                                                              
          newString = ((oracle.sql.ROWID)tempValue).stringValue();       
          break;                                                          
          }                                                               
        case SqlUtils.ORACLE_BINARY_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        case SqlUtils.ORACLE_XMLTYPE_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"String",oracleColumnName);
          break;
          }
        }
      }

    return (newString);
    }
 /**
  * Get column <tt>columnId</tt> as a char[] Object
  * @param String theColumnName The name of the column
  * @return char[] the Value of column #columnId if it can be turned into a char[].
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a char[]
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since v6.0.2770 
  */
  public char[] getCharArray(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    return(getCharArray(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a char[] Object
  * @param int columnId The number of the column.
  * @return char[] the Value of column #columnId if it can be turned into a char[].
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a char[]
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since v6.0.2770 
  */
  public char[] getCharArray(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    char[] tempNumber = null;

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Since we know we have 1 or more rows get the current one.
    Object[] tempRow = getCurrentRow();

    // Will throw CSUnsupportedDatatypeException if we don't handle the datatype of the underlying column and
    // CSDBInvalidDatatypeCastException if thr underlying field can not nbe turned into a CharArray
    return(getCharArray(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]));

    }

 /**
  * Get column <tt>columnId</tt> as an array of char
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue An Object that presumably contains a java.sql.Date
  * @param SimpleDateFormat theTimestampFormat Used if tempObject is a String.
  * @param NumberFormat theNumberFormat Used if tempObject is a Number.
  * @return char[] the Value of column #columnId if it can be turned into an array of char.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a String
  * @since v6.0.2770 
  */
  private char[] getCharArray(int oracleUnderlyingDatatype
                             ,String oracleDataType
                             ,String oracleColumnName
                             ,Object tempValue) throws CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    char[] newCharArray = null;

    if (tempValue == null)
      {
      newCharArray = null;
      }
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_BINARY_DATATYPE:
          {
          newCharArray = (char[])tempValue;
          break;
          }
        case SqlUtils.ORACLE_CLOB_DATATYPE:
         {
          if (tempValue == null)
            {
            newCharArray = new char[0];
            }
          else  if (tempValue instanceof char[])
            {
            newCharArray = (char[])tempValue;
            }
          else
            {
            createInvalidDatatypeCastException(oracleDataType,"char[]",oracleColumnName);
            }
          break;
          }
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
        case SqlUtils.ORACLE_DATE_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        case SqlUtils.ORACLE_BLOB_DATATYPE:
        case SqlUtils.ORACLE_BFILE_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"char[]",oracleColumnName);
          break;
          }
        }
      }

    return (newCharArray);
    }
  /**
  * Get column <tt>columnId</tt> as a byte[] Object
  * @param String theColumnName The name of the column
  * @return byte[] the Value of column #columnId if it can be turned into a byte[].
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a byte[]
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public byte[] getByteArray(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    return(getByteArray(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as a byte[] Object
  * @param int columnId The number of the column.
  * @return byte[] the Value of column #columnId if it can be turned into a byte[].
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a byte[]
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public byte[] getByteArray(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    byte[] tempNumber = null;

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Since we know we have 1 or more rows get the current one.
    Object[] tempRow = getCurrentRow();

    // Will throw CSUnsupportedDatatypeException if we don't handle the datatype of the underlying column and
    // CSDBInvalidDatatypeCastException if thr underlying field can not nbe turned into a ByteArray
    return(getByteArray(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]));

    }

 /**
  * Get column <tt>columnId</tt> as an array of byte
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue An Object that presumably contains a java.sql.Date
  * @param SimpleDateFormat theTimestampFormat Used if tempObject is a String.
  * @param NumberFormat theNumberFormat Used if tempObject is a Number.
  * @return byte[] the Value of column #columnId if it can be turned into an array of byte.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a String
  * @since 5.0.2314 Added support for useByteArraysForLongsAndLOBS
  */
  private byte[] getByteArray(int oracleUnderlyingDatatype
                             ,String oracleDataType
                             ,String oracleColumnName
                             ,Object tempValue) throws CSDBInvalidDatatypeCastException, CSUnsupportedDatatypeException
    {
    byte[] newByteArray = null;

    if (tempValue == null)
      {
      newByteArray = null;
      }
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_BINARY_DATATYPE:
          {
          newByteArray = (byte[])tempValue;
          break;
          }
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_BLOB_DATATYPE:
        case SqlUtils.ORACLE_BFILE_DATATYPE:
          {
          if (tempValue == null)
            {
            newByteArray = new byte[0];
            }
          else  if (tempValue instanceof byte[])
            {
            newByteArray = (byte[])tempValue;
            }
          else
            {
            createInvalidDatatypeCastException(oracleDataType,"byte[]",oracleColumnName);
            }
          break;
          }
        case SqlUtils.ORACLE_CLOB_DATATYPE:
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
        case SqlUtils.ORACLE_DATE_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"byte[]",oracleColumnName);
          break;
          }
        }
      }

    return (newByteArray);
    }

 /**
  * Get column <tt>columnId</tt> as an Object.
  * @param String the name of the column.
  * @return Object the Value of column #columnId if it can be turned into an Object.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public Object getObject(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    return(getObject(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as an Object.
  * @param int columnId The number of the column.
  * @return Object the Value of column #columnId if it can be turned into an Object.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public Object getObject(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    return(tempRow[columnId]);
    }
/**
  * Get column <tt>columnId</tt> as an oracle.sql.OPAQUE.
  * @param String the name of the column.
  * @return oracle.sql.OPAQUE the Value of column #columnId if it can be turned into an oracle.sql.OPAQUE.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public oracle.sql.OPAQUE getOpaque(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    return(getOpaque(getColumnId(theColumnName)));
    }

 /**
  * Get column <tt>columnId</tt> as an oracle.sql.OPAQUE.
  * @param int columnId The number of the column.
  * @return oracle.sql.OPAQUE the Value of column #columnId if it can be turned into an oracle.sql.OPAQUE.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 0.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSUnsupportedDatatypeException if columnId is not of a data type that we support.
  */
   public oracle.sql.OPAQUE getOpaque(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException,CSDBInvalidDatatypeCastException,CSUnsupportedDatatypeException
    {
    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    return((oracle.sql.OPAQUE)tempRow[columnId]);
    }

  /**
  * Return the entire row in the form of a delimited string
  * @param String delimiter a delimiter such as ":"
  * @param String unprintableFieldMessage a label such as "unprintable"
  * @return The row in the form of a text String.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  */
  public String getRowAsString(String delimiter, String unprintableFieldMessage) throws CSNoDataInRowSetException
    {
    Object[] tempRow = getCurrentRow();
    String newString = "";

    try
      {
      checkRows();

      for (int i=0; i < tempRow.length; i++)
        {
        try
          {
          newString = newString + getString(underlyingOracleDatatypes[i]
                                           ,columnOracleDatatypeNames[i]
                                           ,columnNames[i]
                                           ,tempRow[i]
                                           ,theTimestampFormat
                                           ,theNumberFormat);
          }
        catch (CSDBInvalidDatatypeCastException e)
          {
          newString = newString + unprintableFieldMessage;
          }
        catch (CSUnsupportedDatatypeException e)
          {
          newString = newString + unprintableFieldMessage;
          }

        // Add delimiter if appropriate
        if (i < (tempRow.length +1)  && tempRow.length > 1)
          {
          newString = newString + delimiter;
          }
        }
      }
    catch (CSNoDataInRowSetException e)
      {
      newString = "";
      }

    return(newString);
    }
  /**
  * Return the row's column names in the form of a delimited string
  * @param String delimiter a delimiter such as ":"
  */
  public String getColumnNamesAsString(String delimiter)
    {
    String newString = "";

    for (int i=0; i < columnNames.length; i++)
      {
      newString = newString + columnNames[i];
      // Add delimiter if appropriate
      if (i < (columnNames.length +1)  && columnNames.length > 1)
        {
        newString = newString + delimiter;
        }
      }

    return(newString);
    }
    
  /**
  * Return the row's column names in the form of a  string Array
  * @return String[]
  */
  public String[] getColumnNamesAsStringArray()
    {
    return(columnNames);
    }


  /**
  * Return the Oracle data types of the the columns as an array of String
  * @return String[]
  * @since JDBCWizard V5.0.2177
  */
  public String[] getColumnOracleDatatypeNames()
    {
    return(columnOracleDatatypeNames);
    }
    
  /**
  * Make sure that columnId is a valid column.
  * @param int columnId The Id of a supposedly valid column
  * @throws CSInvalidColumnIdException in advance to prevent ArrayIndexOutOfBoundsException later.
  */
  protected void checkRange(int columnId) throws CSInvalidColumnIdException
  {
  if (columnId < 0 || columnId >= colCount)
    {
    throw new CSInvalidColumnIdException("Column id of " + columnId + " is invalid", columnId);
    }
  }

  /**
  * Make sure that theColumnName is a valid column.
  * @param String theColumnName The name of a supposedly valid column
  * @return <tt>true</tt> if we can match this column name to a column
  * @return <tt>true</tt> otherwise
  */
  public boolean checkColumnName(String theColumnName)
  {
  if (getColumnId(theColumnName) > -1)
    {
    return(true);
    }
  return(false);
  }

  /**
  * Make sure that we have rows.
  * @throws CSNoDataInRowSetException if we don't have any rows.
  */
  protected void checkRows() throws CSNoDataInRowSetException
  {
  if (rowCount <= 0)
    {
    throw new CSNoDataInRowSetException("There are no rows in this RowSet");
    }
  }

  /**
  * Change the way Timestamps are converted to Strings
  * @param String newTimestampFormat
  */
  public void setTimestampFormat(String newTimestampFormat)
    {
    theTimestampFormat = new SimpleDateFormat(newTimestampFormat);
    }

  /**
  * Change the way Numbers are converted to Strings
  * @param NumberFormat newNumberFormat
  */
  public void setNumberFormat(NumberFormat newNumberFormat)
    {
    theNumberFormat = newNumberFormat;
    }

  /**
  *  Return current row as an array of Object
  * @return Object[] A one dimensional Object array containing the current row.
  * @throws CSNoDataInRowSetException if the rowset is empty.
  */
  public Object[] getCurrentRow() throws CSNoDataInRowSetException
    {
    checkRows();
    return((Object[])readOnlyRowSetData.get(currentRowNumber));
    }

  /**
  * Utility method that will try to turn theDateObject into a Date object and then a String.
  * @param Object An object which is supposed to be an instance of java.sql.Date or java.sql.Timestamp
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING
  * @see java.sql.Date
  * @see java.sql.Timestamp
  * @see oracle.sql.TIMESTAMP
  * @return String A date formatted into a string.
  */
  protected String convertTimestampToString(Object theDateObject, SimpleDateFormat theTimestampFormat)
    {
    String newString = "";

    if (theDateObject == null)
      {
      newString = "";
      }
    else
      {
      if (theDateObject instanceof java.sql.Timestamp)
        {
        newString = theTimestampFormat.format((Timestamp)theDateObject);
        }
      else
        {
        newString = theTimestampFormat.format((Date)theDateObject);
        }
      }

    return(newString);
    }

  /**                                                                                                               
  * Utility method that will try to turn theTIMESTAMPObject into a oracle.sql.TIMESTAMP object and then a String.   
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMP                             
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                                   
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                                 
  * @see java.sql.Date                                                                                              
  * @see java.sql.Timestamp                                                                                         
  * @see oracle.sql.TIMESTAMP                                                                                       
  * @return String A date formated into a string.                                                                   
  */                                                                                                                
  protected String convertTIMESTAMPToString(Object theTIMESTAMPObject, SimpleDateFormat theTimestampFormat)         
    {                                                                                                               
    String newString = "";                                                                                          
                                                                                                                    
    if (theTIMESTAMPObject == null)                                                                                 
      {                                                                                                             
      newString = "";                                                                                               
      }                                                                                                             
    else                                                                                                            
      {                                                                                                             
       try                                                                                                          
         {                                                                                                          
         // This will throw a nullpointer exception if tempValue is a valid but empty oracle.sql.TIMESTAMP          
         Timestamp tempStamp = ((oracle.sql.TIMESTAMP)theTIMESTAMPObject).timestampValue();                         
         newString = convertTimestampToString(tempStamp, theTimestampFormat);                                       
         }                                                                                                          
       catch (java.lang.NullPointerException e)                                                                     
         {                                                                                                          
         // A null timestamp column shows up as an empty timestamp object.....                                      
         newString = "";                                                                                            
         }                                                                                                          
       catch (SQLException e)                                                                                       
         {                                                                                                          
         theLog.error(e);                                                                                           
         newString = "";                                                                                            
         }                                                                                                          
      }                                                                                                             
                                                                                                                    
    return(newString);                                                                                              
    }                                                                                                               
                                                                                                                    
  /**                                                                                                               
  * Utility method that will try to turn theTIMESTAMPObject into a Date object.                                     
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMP                             
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                                   
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                                 
  * @see java.sql.Date                                                                                              
  * @see java.sql.Timestamp                                                                                         
  * @see oracle.sql.TIMESTAMP                                                                                       
  * @return Date A date.                                                                                            
  */                                                                                                                
  protected java.sql.Date convertTIMESTAMPToDate(Object theTIMESTAMPObject, SimpleDateFormat theTimestampFormat)    
    {                                                                                                               
    Date newDate = null;                                                                                           
                                                                                                                   
    if (theTIMESTAMPObject != null)                                                                               
      {                                                                                                           
      Timestamp tempTimestamp = convertTIMESTAMPToTimestamp(theTIMESTAMPObject, theTimestampFormat);             
                                                                                                                
      if (tempTimestamp != null)                                                                                
        {                                                                                                      
        newDate = new java.sql.Date(tempTimestamp.getTime());                                                  
        }                                                                                                     
                                                                                                              
      }                                                                                                       
                                                                                                              
    return(newDate);                                                                                          
    }                                                                                                         
                                                                                                              
  /**                                                                                                         
  * Utility method that will try to turn theTIMESTAMPObject into a Date object.                               
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMP                       
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                             
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                           
  * @see java.sql.Date                                                                                        
  * @see java.sql.Timestamp                                                                                   
  * @see oracle.sql.TIMESTAMP                                                                                 
  * @return Date A date.                                                                                      
  */                                                                                                          
  protected java.sql.Timestamp convertTIMESTAMPToTimestamp(Object theTIMESTAMPObject, SimpleDateFormat theTimestampFormat) 
    {                                                                                                         
    Timestamp newDate = null;                                                                                 
                                                                                                              
    if (theTIMESTAMPObject == null)                                                                           
      {                                                                                                       
      newDate = null;                                                                                         
      }                                                                                                       
    else                                                                                                      
      {                                                                                                       
       try                                                                                                    
         {                                                                                                    
         // This will throw a nullpointer exception if tempValue is a valid but empty oracle.sql.TIMESTAMP    
         newDate = ((oracle.sql.TIMESTAMP)theTIMESTAMPObject).timestampValue();                               
         }                                                                                                    
       catch (java.lang.NullPointerException e)                                                               
         {                                                                                                    
         // A null timestamp column shows up as an empty timestamp object.....                                
         newDate = null;                                                                                      
         }                                                                                                    
       catch (SQLException e)                                                                                 
         {                                                                                                    
         theLog.error(e);                                                                                     
         newDate = null;                                                                                      
         }                                                                                                    
      }                                                                                                       
                                                                                                              
    return(newDate);                                                                                          
    }                                                                                                         
                                                                                                              
  /**                                                                                                         
  * Utility method that will try to turn theTIMESTAMPObject into a Date object.                               
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMP                       
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                             
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                           
  * @see java.sql.Date                                                                                        
  * @see java.sql.Timestamp                                                                                   
  * @see oracle.sql.TIMESTAMP                                                                                 
  * @return Date A date.                                                                                      
  */                                                                                                          
  protected java.sql.Timestamp convertTIMESTAMPTZToTimestamp(Object theTIMESTAMPTZObject, SimpleDateFormat theTimestampFormat) 
    {                                                                                                         
    Timestamp newTimestamp = null;                                                                            
                                                                                                              
    if (theTIMESTAMPTZObject == null)                                                                         
      {                                                                                                       
      newTimestamp = null;                                                                                    
      }                                                                                                       
    else                                                                                                      
      {                                                                                                       
       try                                                                                                    
         {                                                                                                    
         if (((oracle.sql.TIMESTAMPTZ)theTIMESTAMPTZObject).getLength() > 0)                                   
           {                                                                                                     
           oracle.sql.DATE tempDate = new oracle.sql.DATE(((oracle.sql.TIMESTAMPTZ)theTIMESTAMPTZObject).getBytes()); 
           newTimestamp = tempDate.timestampValue();                                                                
           }                                                                                                      
         }                                                                                                      
       catch (java.lang.NullPointerException e)                                                                
         {                                                                                                    
         // A null timestamp column shows up as an empty timestamp object.....                               
         newTimestamp = null;                                                                               
         }                                                                                                 
      }                                                                                                   
                                                                                                         
    return(newTimestamp);                                                                               
    }                                                                                                  
                                                                                                      
  /**                                                                                      
  * Utility method that will try to turn theTIMESTAMPObject into a Date object.            
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMP    
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.          
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                       
  * @see java.sql.Date                                                                     
  * @see java.sql.Timestamp                                                                
  * @see oracle.sql.TIMESTAMP                                                              
  * @return Date A date.                                                                   
  */                                                                                        
  protected java.sql.Timestamp convertTIMESTAMPLTZToTimestamp(Object theTIMESTAMPLTZObject, SimpleDateFormat theTimestampFormat) 
    {                                                                                        
    Timestamp newTimestamp = null;                                                           
                                                                                             
    if (theTIMESTAMPLTZObject == null)                                                       
      {                                                                                      
      newTimestamp = null;                                                                   
      }                                                                                      
    else                                                                                     
      {                                                                                      
       try                                                                                   
         {                                                                                   
         if (((oracle.sql.TIMESTAMPLTZ)theTIMESTAMPLTZObject).getLength() > 0)               
           {                                                                                 
           oracle.sql.DATE tempDate = new oracle.sql.DATE(((oracle.sql.TIMESTAMPLTZ)theTIMESTAMPLTZObject).getBytes()); 
           newTimestamp = tempDate.timestampValue();                                         
           }                                                                                  
         }                                                                                    
       catch (java.lang.NullPointerException e)                                               
         {                                                                                    
         // A null timestamp column shows up as an empty timestamp object.....                
         newTimestamp = null;                                                                                           
         }                                                                                                              
      }                                                                                                                 
                                                                                                                        
    return(newTimestamp);                                                                                               
    }                                                                                                                   
                                                                                                                        
  /**                                                                                                                    
  * Utility method that will try to turn theTIMESTAMPObject into a Date object.                                          
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMP                                  
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                                        
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                                      
  * @see java.sql.Date                                                                                                   
  * @see java.sql.Timestamp                                                                                              
  * @see oracle.sql.TIMESTAMP                                                                                            
  * @return Date A date.                                                                                                 
  */                                                                                                                     
  protected java.sql.Date convertTIMESTAMPLTZToDate(Object theTIMESTAMPLTZObject, SimpleDateFormat theTimestampFormat)   
    {                                                                                                                    
    java.sql.Date newDate = null;                                                                                        
                                                                                                                         
    if (theTIMESTAMPLTZObject == null)                                                                                   
      {                                                                                                                  
      newDate = null;                                                                                                    
      }                                                                                                                  
    else                                                                                                                 
      {                                                                                                                  
       try                                                                                                               
         {                                                                                                               
         if (((oracle.sql.TIMESTAMPLTZ)theTIMESTAMPLTZObject).getLength() > 0)                                           
           {                                                                                                             
           Timestamp tempTimeStamp = convertTIMESTAMPLTZToTimestamp(theTIMESTAMPLTZObject, theTimestampFormat);          
           newDate = new java.sql.Date(tempTimeStamp.getTime());                                                         
           }                                                                                                             
         }                                                                                                               
       catch (java.lang.NullPointerException e)                                                                          
         {                                                                                                               
         // A null timestamp column shows up as an empty timestamp object.....                                           
         newDate = null;                                                                                                 
         }                                                                                                               
      }                                                                                                                  
                                                                                                                         
    return(newDate);                                                                                                     
    }                                                                                                                    
                                                                                                                         
  /**                                                                                                                    
  * Utility method that will try to turn theTIMESTAMPTZObject into a oracle.sql.TIMESTAMPTZ object and then a String.    
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMPTZ                                
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                                        
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                                      
  * @see java.sql.Date                                                                                                   
  * @see java.sql.Timestamp                                                                                              
  * @see oracle.sql.TIMESTAMPTZ                                                                                          
  * @return String A date formated into a string.                                                                        
  */                                                                                                                     
  protected String convertTIMESTAMPTZToString(Object theTIMESTAMPTZObject, SimpleDateFormat theTimestampFormat)          
    {                                                                                                                    
    String newString = "";                                                                                               
                                                                                                                         
    if (theTIMESTAMPTZObject == null)                                                                                    
      {                                                                                                                  
      newString = "";                                                                                                    
      }                                                                                                                  
    else                                                                                                                 
      {                                                                                                                  
       try                                                                                                               
         {                                                                                                               
         // A Null TIMESTAMPTZ column still returns a TIMESTAMPTZ object. getLength() tells us                           
         // whether it's null or not.                                                                                    
         if (((oracle.sql.TIMESTAMPTZ)theTIMESTAMPTZObject).getLength() > 0)                                             
           {                                                                                                             
           oracle.sql.DATE tempDate = new oracle.sql.DATE(((oracle.sql.TIMESTAMPTZ)theTIMESTAMPTZObject).getBytes());    
           newString = convertTimestampToString(tempDate.timestampValue(), theTimestampFormat);                          
           }                                                                                                             
         }                                                                                                               
       catch (java.lang.NullPointerException e)                                                                          
         {                                                                                                               
         // A null timestamp column shows up as an empty timestamp object.....                                           
         newString = "";                                                                                                 
         }                                                                                                               
      }                                                                                                                  
                                                                                                                         
    return(newString);                                                                                                   
    }                                                                                                                    
  /**                                                                                                                    
  * Utility method that will try to turn theTIMESTAMPTZObject into a oracle.sql.TIMESTAMPTZ object and then a String.    
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMPTZ                                
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                                        
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                                      
  * @see java.sql.Date                                                                                                   
  * @see java.sql.Timestamp                                                                                              
  * @see oracle.sql.TIMESTAMPTZ                                                                                          
  * @return String A date formated into a string.                                                                        
  */                                                                                                                     
  protected java.sql.Date convertTIMESTAMPTZToDate(Object theTIMESTAMPTZObject, SimpleDateFormat theTimestampFormat)     
    {                                                                                                                    
    java.sql.Date newDate = null;                                                                                        
                                                                                                                         
    if (theTIMESTAMPTZObject == null)                                                                                    
      {                                                                                                                  
      newDate = null;                                                                                                    
      }                                                                                                                  
    else                                                                                                                 
      {                                                                                                                  
       try                                                                                                               
         {                                                                                                               
         // A Null TIMESTAMPTZ column still returns a TIMESTAMPTZ object. getLength() tells us                           
         // whether it's null or not.                                                                                    
         if (((oracle.sql.TIMESTAMPTZ)theTIMESTAMPTZObject).getLength() > 0)                                             
           {                                                                                                             
           Timestamp tempTimestamp = convertTIMESTAMPTZToTimestamp(theTIMESTAMPTZObject, theTimestampFormat);            
           newDate = new java.sql.Date(tempTimestamp.getTime());                                                         
           }                                                                                                             
         }                                                                                                               
       catch (java.lang.NullPointerException e)                                                                          
         {                                                                                                               
         // A null timestamp column shows up as an empty timestamp object.....                                           
         newDate = null;                                                                                                 
         }                                                                                                               
      }                                                                                                                  
                                                                                                                         
    return(newDate);                                                                                                     
    }                                                                                                                    
                                                                                                                         
  /**                                                                                                                    
  * Utility method that will try to turn theTIMESTAMPLTZObject into a oracle.sql.TIMESTAMPLTZ object and then a String.  
  * @param Object An object which is supposed to be an instance of oracle.sql.TIMESTAMPLTZ                               
  * @param SimpleDateFormat theTimestampFormat The date/time format we are using.                                        
  * @see com.orindasoft.pub.LogInterface.DEFAULT_TIME_FORMAT_STRING                                                      
  * @see java.sql.Date                                                                                                   
  * @see java.sql.Timestamp                                                                                              
  * @see oracle.sql.TIMESTAMPLTZ                                                                                         
  * @return String A date formated into a string.                                                                        
  */                                                                                                                     
  protected String convertTIMESTAMPLTZToString(Object theTIMESTAMPLTZObject, SimpleDateFormat theTimestampFormat)        
    {                                                                                                                    
    String newString = "";                                                                                               
                                                                                                                         
    if (theTIMESTAMPLTZObject == null)                                                                                   
      {                                                                                                                  
      newString = "";                                                                                                    
      }                                                                                                                  
    else                                                                                                                 
      {                                                                                                                  
       try                                                                                                               
         {                                                                                                               
         if (((oracle.sql.TIMESTAMPLTZ)theTIMESTAMPLTZObject).getLength() > 0)                                           
           {                                                                                                             
           oracle.sql.DATE tempDate = new oracle.sql.DATE(((oracle.sql.TIMESTAMPLTZ)theTIMESTAMPLTZObject).getBytes());  
           newString = convertTimestampToString(tempDate.timestampValue(), theTimestampFormat);                          
           }                                                                                                             
         }                                                                                                               
       catch (java.lang.NullPointerException e)                                                                          
         {                                                                                                               
         // A null timestamp column shows up as an empty timestamp object.....                                           
         newString = "";                                                                                                 
         }                                                                                                               
      }                                                                                                                  
                                                                                                                         
    return(newString);                                                                                                   
    }                                                                                                                    

  /**
  * Utility method that will try to turn theNumberObject into a String.
  *
  * This method used the current Number Format to convert a number field to A String.
  * @param Object An object which is supposed to be an instance of java.sql.Date or java.sql.Timestamp
  * @param NumberFormat theNumberFormat A Valid number format
  * @return String A Number formatted into a string.
  * @throws CSDBInvalidDatatypeCastException
  * @see java.util.NumberFormat
  */
  protected String convertNumberToString(Object theNumberObject
                                      ,NumberFormat theNumberFormat) throws CSDBInvalidDatatypeCastException
    {
    String newString = null;

    if (theNumberObject == null)
      {
      newString = "";
      }
    else
      {
      try
        {
        newString = theNumberFormat.format(theNumberObject);
        }
      catch (Exception e)
        {
        newString = theNumberObject.toString();
        }
      }
    return(newString);
    }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.BFILE Object
  * This method will only work if 'keepLobs' was set to true.
  * @param String theColumnName The name of the column
  * @return oracle.sql.BFILE the Value of column #columnId if it can be turned into a oracle.sql.BFILE.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a oracle.sql.BFILE
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since 2.0.1505
  */
  public oracle.sql.BFILE getBFILE(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getBFILE(getColumnId(theColumnName)));
    }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.BFILE Object
  * This method will only work if 'keepLobs' was set to true.
  * @param int columnId The number of the column.
  * @return oracle.sql.BFILE the Value of column #columnId if it can be turned into a oracle.sql.BFILE.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a oracle.sql.BFILE
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since 2.0.1505
  */
  public oracle.sql.BFILE getBFILE(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Since we know we have 1 or more rows get the current one.
    Object[] tempRow = getCurrentRow();

    // Will throw CSUnsupportedDatatypeException if we don't handle the datatype of the underlying column and
    // CSDBInvalidDatatypeCastException if thr underlying field can not nbe turned into a BigDecimal
    return(getBFILE(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]
                        ,theNumberFormat));

    }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.BFILE
  * This method will only work if 'keepLobs' was set to true.
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue
  * @param NumberFormat theNumberFormat used if the underlying column is a String that needs to be turned into a number.
  * @return java.lang.BigDecimal the Value of column #columnId if it can be turned into a BigDecimal.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a BigDecimal.
  * @since 2.0.1505
  */
  private oracle.sql.BFILE getBFILE
     (int oracleUnderlyingDatatype
     ,String oracleDataType
     ,String oracleColumnName
     ,Object tempValue
     ,NumberFormat theNumberFormat) throws CSDBInvalidDatatypeCastException
  {
  oracle.sql.BFILE newLob = null;

  if (tempValue == null)
    {
    newLob = null;
    }
  else
    {
    switch (oracleUnderlyingDatatype)
      {
      case SqlUtils.ORACLE_BFILE_DATATYPE:
        {
        if (tempValue instanceof oracle.sql.BFILE)
          {
          newLob = (oracle.sql.BFILE)tempValue;
          }
        else
          {
          createInvalidDatatypeCastException(oracleDataType,"oracle.sql.BFILE",oracleColumnName);
          }
        break;
        }
      default:
        {
        createInvalidDatatypeCastException(oracleDataType,"oracle.sql.BFILE",oracleColumnName);
        break;
        }
      }
    }


  return(newLob);
  }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.BLOB Object
  * This method will only work if 'keepLobs' was set to true.
  * @param String theColumnName The name of the column
  * @return oracle.sql.BLOB the Value of column #columnId if it can be turned into a oracle.sql.BLOB.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a oracle.sql.BLOB
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since 2.0.1505
  */
  public oracle.sql.BLOB getBLOB(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getBLOB(getColumnId(theColumnName)));
    }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.BLOB Object
  * This method will only work if 'keepLobs' was set to true.
  * @param int columnId The number of the column.
  * @return oracle.sql.BLOB the Value of column #columnId if it can be turned into a oracle.sql.BLOB.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a oracle.sql.BLOB
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since 2.0.1505
  */
  public oracle.sql.BLOB getBLOB(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Since we know we have 1 or more rows get the current one.
    Object[] tempRow = getCurrentRow();

    // Will throw CSUnsupportedDatatypeException if we don't handle the datatype of the underlying column and
    // CSDBInvalidDatatypeCastException if thr underlying field can not nbe turned into a BigDecimal
    return(getBLOB(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]
                        ,theNumberFormat));

    }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.BLOB
  * This method will only work if 'keepLobs' was set to true.
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue
  * @param NumberFormat theNumberFormat used if the underlying column is a String that needs to be turned into a number.
  * @return java.lang.BigDecimal the Value of column #columnId if it can be turned into a BigDecimal.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a BigDecimal.
  * @since 2.0.1505
  */
  private oracle.sql.BLOB getBLOB
     (int oracleUnderlyingDatatype
     ,String oracleDataType
     ,String oracleColumnName
     ,Object tempValue
     ,NumberFormat theNumberFormat) throws CSDBInvalidDatatypeCastException
  {
  oracle.sql.BLOB newLob = null;

  if (tempValue == null)
    {
    newLob = null;
    }
  else
    {
    switch (oracleUnderlyingDatatype)
      {
      case SqlUtils.ORACLE_BLOB_DATATYPE:
        {
        if (tempValue instanceof oracle.sql.BLOB)
          {
          newLob = (oracle.sql.BLOB)tempValue;
          }
        else
          {
          createInvalidDatatypeCastException(oracleDataType,"oracle.sql.BLOB",oracleColumnName);
          }
        break;
        }
      default:
        {
        createInvalidDatatypeCastException(oracleDataType,"oracle.sql.BLOB",oracleColumnName);
        break;
        }
      }
    }


  return(newLob);
  }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.CLOB Object
  * This method will only work if 'keepLobs' was set to true.
  * @param String theColumnName The name of the column
  * @return oracle.sql.CLOB the Value of column #columnId if it can be turned into a oracle.sql.CLOB.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a oracle.sql.CLOB
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since 2.0.1505
  */
  public oracle.sql.CLOB getCLOB(String theColumnName) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getCLOB(getColumnId(theColumnName)));
    }

  /**
  * Get column <tt>columnId</tt> as a oracle.sql.CLOB Object
  * This method will only work if 'keepLobs' was set to true.
  * @param int columnId The number of the column.
  * @return oracle.sql.CLOB the Value of column #columnId if it can be turned into a oracle.sql.CLOB.
  * @throws CSInvalidColumnIdException if columnId is not of a data type that we can handle.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be converted to a oracle.sql.CLOB
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @since 2.0.1505
  */
  public oracle.sql.CLOB getCLOB(int columnId) throws CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Since we know we have 1 or more rows get the current one.
    Object[] tempRow = getCurrentRow();

    // Will throw CSUnsupportedDatatypeException if we don't handle the datatype of the underlying column and
    // CSDBInvalidDatatypeCastException if thr underlying field can not nbe turned into a BigDecimal
    return(getCLOB(underlyingOracleDatatypes[columnId]
                        ,columnOracleDatatypeNames[columnId]
                        ,columnNames[columnId]
                        ,tempRow[columnId]
                        ,theNumberFormat));

    }
  /**
  * Get column <tt>columnId</tt> as a oracle.sql.CLOB
  * This method will only work if 'keepLobs' was set to true.
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempValue
  * @param NumberFormat theNumberFormat used if the underlying column is a String that needs to be turned into a number.
  * @return java.lang.BigDecimal the Value of column #columnId if it can be turned into a BigDecimal.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a BigDecimal.
  * @since 2.0.1505
  */
  private oracle.sql.CLOB getCLOB
     (int oracleUnderlyingDatatype
     ,String oracleDataType
     ,String oracleColumnName
     ,Object tempValue
     ,NumberFormat theNumberFormat) throws CSDBInvalidDatatypeCastException
  {
  oracle.sql.CLOB newLob = null;

  if (tempValue == null)
    {
    newLob = null;
    }
  else
    {
    switch (oracleUnderlyingDatatype)
      {
      case SqlUtils.ORACLE_CLOB_DATATYPE:
        {
        if (tempValue instanceof oracle.sql.CLOB)
          {
          newLob = (oracle.sql.CLOB)tempValue;
          }
        else
          {
          createInvalidDatatypeCastException(oracleDataType,"oracle.sql.CLOB",oracleColumnName);
          }
        break;
        }
      default:
        {
        createInvalidDatatypeCastException(oracleDataType,"oracle.sql.CLOB",oracleColumnName);
        break;
        }
      }
    }


   return(newLob);
   }

 /**
  * Get a long text or binary column <tt>columnId</tt> as a <tt>File</tt>
  *
  * BLOBS, CLOBS and LONGS are downloaded to File's if in a ResultSet used to create a ReadOnlyRowSet. This
  * method allows the calling application to obtain a File Object which points to the temporary file containing
  * the long object in question. If the parameter <tt>tempFileName</tt> is that of a valid file that does not exist then
  * the contents of the temporary file will be coppied to a new file called <tt>tempFileName</tt>, which will then be returned instead.
  * @param String theColumnName The name of the column
  * @param File tempFile The File you want the data stored in. If this parameter is <tt>null</tt> you get the
  * temporary file that was created when we unloaded the result set.
  * @param Object[] tempRow The current Row.
  * @return String the Value of column #columnId if it can be turned into a string.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 1.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSException if we hit a problem retrieving the Long field. If this happens we store an
  * <tt>Exception</tt> rather than a <tt>File</tt>. This avoids throwing Exceptions when the object
  * is being constructed and allows the application to survive if thr failure to retrieve a Long Field
  * which may or may not be used.
   */
  public java.io.File getFile (String theColumnName, java.io.File outputFile) throws  CSException, CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    return(getFile(getColumnId(theColumnName),outputFile));
    }

 /**
  * Get a long text or binary column <tt>columnId</tt> as a <tt>File</tt>
  *
  * BLOBS, CLOBS and LONGS are downloaded to File's if in a ResultSet used to create a ReadOnlyRowSet. This
  * method allows the calling application to obtain a File Object which points to the temporary file containing
  * the long object in question. If the parameter <tt>tempFileName</tt> is that of a valid file that does not exist then
  * the contents of the temporary file will be coppied to a new file called <tt>tempFileName</tt>, which will then be returned instead.
  * @param int columnId The number of the column.
  * @param File tempFile The File you want the data stored in. If this parameter is <tt>null</tt> you get the
  * temporary file that was created when we unloaded the result set.
  * @param Object[] tempRow The current Row.
  * @return String the Value of column #columnId if it can be turned into a string.
  * @throws CSInvalidColumnIdException if columnId is not the id of a valid column. Column numbering starts at 1.
  * @throws CSNoDataInRowSetException if there are no rows in this rowset.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a String.
  * @throws CSException if we hit a problem retrieving the Long field. If this happens we store an
  * <tt>Exception</tt> rather than a <tt>File</tt>. This avoids throwing Exceptions when the object
  * is being constructed and allows the application to survive if thr failure to retrieve a Long Field
  * which may or may not be used.
   */
  public java.io.File getFile (int columnId, java.io.File outputFile) throws  CSException, CSInvalidColumnIdException, CSNoDataInRowSetException, CSDBInvalidDatatypeCastException
    {
    Object[] tempRow = getCurrentRow();

    // Will throw CSNoDataInRowSetException if no rows exist.
    checkRows();

    // Will throw CSInvalidColumnIdException if columnId is out of range.
    checkRange(columnId);

    // Will throw CSUnsupportedDatatypeException if the value of temprow[columnId] can not
    // be turned into a date
    return(getFile(underlyingOracleDatatypes[columnId]
                              ,columnOracleDatatypeNames[columnId]
                              ,columnNames[columnId]
                              ,tempRow[columnId]
                              ,outputFile));
    }

 /**
  * Get a long text or binary column <tt>columnId</tt> as a <tt>File</tt>
  *
  * BLOBS, CLOBS and LONGS are downloaded to File's if in a ResultSet used to create a ReadOnlyRowSet. This
  * method allows the calling application to obtain a File Object which points to the temporary file containing
  * the long object in question. If the parameter <tt>tempFileName</tt> is that of a valid file that does not exist then
  * the contents of the temporary file will be coppied to a new file called <tt>tempFileName</tt>, which will then be returned instead.
  * @param int oracleUnderlyingDatatype Identifies what kind of data we are dealing with.
  * @param String oracleDataType The oracle data type of the column
  * @param String oracleColumnName The name of the column.
  * @param Object tempObject An Object that presumably contains a File
  * @param File tempFile An optional File you want the data stored in.
  * @return java.sql.Timestamp the Value of column #columnId if it can be turned into a Date.
  * @throws CSDBInvalidDatatypeCastException if columnId is not of a data type that can be turned into a Date.
  * @throws CSException if we hit a problem retrieving the Long field. If this happens we store an
  * <tt>Exception</tt> rather than a <tt>File</tt>. This avoids throwing Exceptions when the object
  * is being constructed and allows the application to survive if thr failure to retrieve a Long Field
  * which may or may not be used.
  * @since 5.0.2314 Support for useByteArraysForLongsAndLOBS
  */
  private java.io.File getFile
     (int oracleUnderlyingDatatype
     ,String oracleDataType
     ,String oracleColumnName
     ,Object tempObject
     ,File outputFile) throws  CSDBInvalidDatatypeCastException, CSException
    {
    File newFile = null;

    // If tempObject == null the column was null or the LOB was zero length.
    if (tempObject == null)
      {
      newFile = null;
      }
    // If tempObject is an Exception we hit a problem retrieving the LOB so we stored
    // the Exception instead.
    else if(tempObject instanceof Exception)
      {
      throw (new CSException(((Exception)tempObject).getMessage()));
      }
    // If tempObject is a lob of some kind 'keepLobs' was set and we have a lob
    // instead of a file...
    else if (    ! (tempObject instanceof File)
            &&  ! (tempObject instanceof byte[])
            &&  ! (tempObject instanceof char[]))
      {
      throw (new CSException("Column is a " + tempObject.getClass().getName() +": use getCLOB, getBLOB or getBFILE"));
      }
    // If it's not null and it's not an Exception see if its a File...
    else
      {
      switch (oracleUnderlyingDatatype)
        {
        case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
        case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
        case SqlUtils.ORACLE_CLOB_DATATYPE:
        case SqlUtils.ORACLE_BLOB_DATATYPE:
        case SqlUtils.ORACLE_BFILE_DATATYPE:
          {
          if (tempObject == null)
            {
            IOUtils.loadByteArrayIntoFile(new byte[0], outputFile, theLog);
            }
          else  if (tempObject instanceof byte[])
            {
            IOUtils.loadByteArrayIntoFile((byte[])tempObject, outputFile, theLog);
            }
          else  if (tempObject instanceof char[])
          {
          IOUtils.loadCharArrayIntoFile((char[])tempObject, outputFile, theLog);
          }
          else  if (tempObject instanceof java.io.File)
            {
            // tempObject must be a File....
            newFile = (java.io.File)tempObject;

            // If we have been given a file for the output copy tempFile to it...
            if (outputFile != null)
              {
              IOUtils.copyFile(newFile, outputFile);
              newFile = outputFile;
              }
            }
          else
            {
            createInvalidDatatypeCastException(oracleDataType,"byte[]",oracleColumnName);
            }

          break;
          }
        case SqlUtils.ORACLE_DATE_DATATYPE:
        case SqlUtils.ORACLE_TEXT_DATATYPE:
        case SqlUtils.ORACLE_NUMBER_DATATYPE:
        case SqlUtils.ORINDASOFT_READONLYROWSET:
        case SqlUtils.ORACLE_BOOLEAN_DATATYPE:
        case SqlUtils.ORACLE_REFCURSOR_DATATYPE:
        case SqlUtils.ORACLE_OBJECT_DATATYPE:
        case SqlUtils.ORACLE_TABLE_DATATYPE:
        case SqlUtils.ORACLE_VARRAY_DATATYPE:
        case SqlUtils.ORACLE_BINARY_DATATYPE:
        default:
          {
          createInvalidDatatypeCastException(oracleDataType,"File",oracleColumnName);
          break;
          }
        }
      }

    return(newFile);
    }

  protected void createInvalidDatatypeCastException(String theCastedoracleDatatype
                                                        ,String theCasteeJavaDatatype
                                                        ,String oracleColumnName) throws CSDBInvalidDatatypeCastException
    {
    throw new CSDBInvalidDatatypeCastException
                        ("ReadOnlyRowSet does not know how to turn column " + oracleColumnName
                        + "(" + theCastedoracleDatatype + ") into " + theCasteeJavaDatatype
                        ,theCastedoracleDatatype
                        ,theCasteeJavaDatatype);
    }

  /**
  * Set how long the result will remain up to date.
  * @param long theExpireDate An arbitrary expiration date expressed in 1000'ths of a second from now
  */
  public void setExpireDate(long theExpireDate)
    {
    expireDate = new Date(System.currentTimeMillis() + theExpireDate);
    }

  /**
  * Set how long the result will remain up to date.
  * @param java.util.Date theExpireDate An arbitrary expiration date
  */
  public java.util.Date getExpireDate()
    {
    return(expireDate);
    }

  /**
  * Set how long the result will remain up to date.
  * @param java.util.Date theExpireDate An arbitrary expiration date
  */
  public boolean hasExpired()
    {
    // If expireDate was never set then we never expire.
    if (expireDate == null)
      {
      return(false);
      }
    // If it was set and is in the past return true
    else if (System.currentTimeMillis() > expireDate.getTime())
      {
      return(true);
      }

    // expireDate is set but is in the future.
    return(false);
    }

  /**
  * Set the suffix used for generating temporary files
  */
  public void setTempFileSuffix(String tempFileSuffix)
    {
    this.tempFileSuffix = tempFileSuffix;
    }

  /**
  * Get the prefix used for generating temporary files
  */
  public String getTempFileSuffix()
    {
    return(tempFileSuffix);
    }

  /**
  * How many times this row set has been used
  * This is used by QueryStatements caching functionality
  */

  int getTimesUsed()
    {
    return(timesUsed);
    }

  /**
  * Increment ow many times this row set has been used
  * This is used by QueryStatements caching functionality
  */
  void incrementTimesUsed()
    {
    if (timesUsed < (Integer.MAX_VALUE))
      {
      timesUsed++;
      }
    }

  public WriteableRowSet getWriteableRowSet()
    {
    WriteableRowSet tempWriteableRowSet
      = new WriteableRowSet(readOnlyRowSetData.toArray()
                           ,columnNames
                           ,columnOracleDatatypeNames
                           ,underlyingOracleDatatypes
                           ,columnJavaDatatypes
                           ,columnLengths
                           ,columnDecimalPlaces
                           ,theLog
                           ,downloadedFileDir
                           ,keepFiles);

    return(tempWriteableRowSet);

    }
  /**
  * Return a String representation of the start of the row set
  * @return A newline and colon deliminated String containing the first 30,000
  * characters of the ReadOnlyRowSet.
  */
  public String toString()
    {
    final int maxLength = 30000;
    String thisRowSet = this.getColumnNamesAsString(":");
    int originalRow = this.getCurrentRowNumber();

     try
      {
      for (int i=0; i < this.size() && thisRowSet.length() < maxLength; i++)
        {
        this.setCurrentRowNumber(i);
        thisRowSet = thisRowSet + "\n" + this.getRowAsString(":","?");
        }
      }
    catch(CSNoDataInRowSetException e)
      {
      }

    if (originalRow != this.getCurrentRowNumber())
      {
      this.setCurrentRowNumber(originalRow);
      }
      
    return(thisRowSet);

    }
  /**
  * Delete all files in this ReadOnlyRowSet
  * This is a cleanup method that you can use when the underlying cursor
  * contained one or more LOB columns and you need to explicitly remove the
  * resulting files.
  * @return int a count of the files deleted
  */
  public int deleteGeneratedFiles()
    {
    int deleteCount = 0;
    int originalRow = this.getCurrentRowNumber();

    try
      {
      for (int rows=0; rows < this.size(); rows++)
        {
        this.setCurrentRowNumber(rows);
        Object[] tempRow = getCurrentRow();

        for (int cols=0; cols < underlyingOracleDatatypes.length; cols++)
          {
          switch (underlyingOracleDatatypes[cols])
            {
            case SqlUtils.ORACLE_LONGTEXT_DATATYPE:
            case SqlUtils.ORACLE_LONG_BINARY_DATATYPE:
            case SqlUtils.ORACLE_CLOB_DATATYPE:
            case SqlUtils.ORACLE_BLOB_DATATYPE:
            case SqlUtils.ORACLE_BFILE_DATATYPE:
              {
              Object tempObject = tempRow[cols];

              // Because file io errors are stored as Exceptions we
              // have to check to see if we really do have a file
              if (   tempObject != null
                  && tempObject instanceof java.io.File)
                {
                File tempFile = (File)tempObject;

                if (tempFile.exists())
                  {
                  tempFile.delete();
                  deleteCount++;
                  }
                }
              }
            }

          }
        }
      }
    catch(CSNoDataInRowSetException e)
      {
      }

    if (originalRow != this.getCurrentRowNumber())
      {
      this.setCurrentRowNumber(originalRow);
      }

    return(deleteCount);

    }
}




