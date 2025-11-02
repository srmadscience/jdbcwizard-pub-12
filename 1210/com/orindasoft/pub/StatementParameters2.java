package com.orindasoft.pub;

// We prepare JDBC statements
import java.sql.*;

// We use oracle Extensions
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;
import oracle.sql.ArrayDescriptor;
import oracle.sql.ARRAY;

// We use BigDecimal because Oracle does.
import java.math.BigDecimal;

// We use File to store Long data types
import java.io.File;

// We use IO streams to process longs and clobs
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
* A set of parameters for a PreparedStatement or CallableStatement
*
* <p>This class is used to represent IN parameters for a SQL statement or PL/SQL
* Procedure.
* <p> This class supercedes an older class called 'statementParameters.java', which has been retired.
* JDBCWizard generated code now uses StatementParameters2.java.
* <p>
* Under normal circumstances <a href="http://www.orindasoft.com/?adsrc=api" target="_blank class="manual">OrindaBuild</a> users
* will have no reason to use this class directly - the generated code will use it.
* <p>
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
* @since 4.0.1901
*/
public class StatementParameters2
{

  /**
  * Array of Object for storing parameters.
  */
  protected Object[] parameterArray = null;

  /**
  * Array of boolean for storing parameters.
  */
  protected boolean[] inputParameterSetArray = null;

  /**
  * Array of int for storing file lengths.
  */
  protected int[] inputParameterFilesizeArray = null;

  /**
  * Array of int for storing parameter types.
  */
  protected int[] parameterTypeArray = null;

  /**
  * Array of mostly null names for OPAQUE data types
  * @since 6.0.2744
  */
  protected String[] parameterOpaqueTypeNameArray = null;

  /**
  * A LogInterface object
  **/
  protected LogInterface theLog;

  /**
  * Build Number - Added to spot conflicts between version of generated code
  * and version of com.orindasoft.pub Library.
  * @since 6.0.2706
  */
  private static final int buildNumber =  3145;

  /**
  * Create a set of statement Parameters of size <tt>howMany</tt>.
  * @param int howMany The number of parameters the statement requires.
  */
  public StatementParameters2(int howMany, LogInterface theLog)
    {
    this.theLog = theLog;
    parameterArray = new Object[howMany];
    inputParameterSetArray = new boolean[howMany];
    inputParameterFilesizeArray = new int[howMany];
    parameterTypeArray = new int[howMany];
    parameterOpaqueTypeNameArray = new String[howMany];
    clearParameters();
    }

  /**
  * Create a set of statement Parameters by counting '?' characters in a SQL statement
  * @param String sqlStatement The statement that we will be working with
  */
  public StatementParameters2(String sqlStatement, LogInterface theLog)
    {
    this(SqlUtils.countParameters(sqlStatement),theLog);
    }


  /**
  * Return number of parameters that are null
  * @return The number of parameters in this statement.
  */
  public int getParamCount()
    {
    return (parameterArray.length);
    }

  /**
  * Clear our parameter array
  */
  public void clearParameters()
    {
    // Explicitly initialize this array as null.
    for (int i=0; i < parameterArray.length; i++)
      {
      parameterArray[i] = null;
      inputParameterSetArray[i] = false;
      parameterTypeArray[i] = Integer.MIN_VALUE;
      }
    }

  /**
  * Set a parameter
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param java.sql.Timestamp parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,java.sql.Timestamp parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * Set a parameter
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param oracle.sql.ROWID parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,oracle.sql.ROWID parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**                                                                           
  * Set a parameter                                                             
  * @param int parameterId The id of the parameter to set. Id's start at 0.     
  * @param oracle.sql.TIMESTAMP parameterValue A parameter.                     
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.     
  */                                                                            
  public void setParam(int parameterId                                          
                      ,oracle.sql.TIMESTAMP parameterValue) throws CSException  
    {                                                                           
    checkRange(parameterId);                                                    
    parameterArray[parameterId-1] = parameterValue;                             
    inputParameterSetArray[parameterId-1] = true;                               
    parameterTypeArray[parameterId-1] = OracleTypes.TIMESTAMP;                  
    }                                                                           
                                                                                
  /**                                                                           
  * Set a parameter                                                             
  * @param int parameterId The id of the parameter to set. Id's start at 0.     
  * @param oracle.sql.TIMESTAMPTZ parameterValue A parameter.                   
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.       
  */                                                                            
  public void setParam(int parameterId                                          
                      ,oracle.sql.TIMESTAMPTZ parameterValue) throws CSException
    {                                                                           
    checkRange(parameterId);                                                    
    parameterArray[parameterId-1] = parameterValue;                             
    inputParameterSetArray[parameterId-1] = true;                               
    parameterTypeArray[parameterId-1] = OracleTypes.TIMESTAMPTZ;                
    }                                                                           
                                                                                
  /**                                                                           
  * Set a parameter                                                             
  * @param int parameterId The id of the parameter to set. Id's start at 0.     
  * @param oracle.sql.TIMESTAMPLTZ parameterValue A parameter.                  
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.       
  */                                                                            
  public void setParam(int parameterId                                          
                      ,oracle.sql.TIMESTAMPLTZ parameterValue) throws CSException 
    {                                                                           
    checkRange(parameterId);                                                    
    parameterArray[parameterId-1] = parameterValue;                             
    inputParameterSetArray[parameterId-1] = true;                               
    parameterTypeArray[parameterId-1] = OracleTypes.TIMESTAMPLTZ;               
    }                                                                           
                                                                                
  /**                                                                             
  * Set a parameter                                                               
  * @param int parameterId The id of the parameter to set. Id's start at 0.       
  * @param oracle.sql.INTERVALYM parameterValue A parameter.                      
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.         
  */                                                                              
  public void setParam(int parameterId                                            
                      ,oracle.sql.INTERVALYM parameterValue) throws CSException   
    {                                                                             
    checkRange(parameterId);                                                      
    parameterArray[parameterId-1] = parameterValue;                               
    inputParameterSetArray[parameterId-1] = true;                                 
    parameterTypeArray[parameterId-1] = OracleTypes.INTERVALYM;                   
    }                                                                             
                                                                                  
  /**                                                                             
  * Set a parameter                                                                 
  * @param int parameterId The id of the parameter to set. Id's start at 0.         
  * @param oracle.sql.INTERVALDS parameterValue A parameter.                       
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.           
  * @since Oracle 10.1.0                                                            
  */                                                                                
  public void setParam(int parameterId                                             
                      ,oracle.sql.INTERVALDS parameterValue) throws CSException     
    {                                                                               
    checkRange(parameterId);                                                        
    parameterArray[parameterId-1] = parameterValue;                                
    inputParameterSetArray[parameterId-1] = true;                                   
    parameterTypeArray[parameterId-1] = OracleTypes.INTERVALDS;                     
    }                                                                               
                                                                                    
  /**                                                                               
  * Set a PL/SQL INDEX BY Array parameter                                           
  * @param int parameterId The id of the parameter to set. Id's start at 0.         
  * @param com.orindasoft.pub.PlsqlIndexByTable2 The PL/SQL Array                   
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.           
  * @since Oracle 10.1.0                                                            
  */                                                                                
  public void setPlSqlIndexArrayParam                                             
                 (int parameterId                                             
                 ,com.orindasoft.pub.PlsqlIndexByTable2 parameterValue) throws CSException                  
    {                                                                               
    checkRange(parameterId);                                                        
                                                                                    
    parameterArray[parameterId-1] = parameterValue;                                 
    inputParameterSetArray[parameterId-1] = true;                                   
    parameterTypeArray[parameterId-1] = OracleTypes.PLSQL_INDEX_TABLE;              
    }                                                                               
  /**                                                                              
  * Set an Opaque parameter                                          
  * @param int parameterId The id of the parameter to set. Id's start at 0.        
  * @param String opaqueName                    
  * @param oracle.sql.OPAQUE parameterValue                  
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.          
  * @since Oracle 10.1.0                                                           
  */                                                                               
  public void setParam                                                             
                 (int parameterId                                             
                 ,String opaqueName                
                 ,oracle.sql.OPAQUE parameterValue) throws CSException                 
    {                                                                              
    checkRange(parameterId);                                                        
                                                                                   
    parameterArray[parameterId-1] = parameterValue;                                
    inputParameterSetArray[parameterId-1] = true;                                  
    parameterTypeArray[parameterId-1] = OracleTypes.OPAQUE;             
    parameterOpaqueTypeNameArray[parameterId-1] = opaqueName;             
    }                                                                              
                                                                                   
  /**                                                                              
  * Set an STRUCT parameter                                          
  * @param int parameterId The id of the parameter to set. Id's start at 0.        
  * @param String structName                    
  * @param oracle.sql.STRUCT parameterValue                  
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.          
  * @since Oracle 10.1.0                                                           
  */                                                                               
  public void setParam                                                             
                 (int parameterId                                             
                 ,String structName                
                 ,oracle.sql.STRUCT parameterValue) throws CSException                 
    {                                                                              
    checkRange(parameterId);                                                        
                                                                                   
    parameterArray[parameterId-1] = parameterValue;                                
    inputParameterSetArray[parameterId-1] = true;                                  
    parameterTypeArray[parameterId-1] = OracleTypes.STRUCT;             
    parameterOpaqueTypeNameArray[parameterId-1] = structName;             
    }                                                                              
                                                                                   
  /**
  * Set an Array parameter
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Object parametervalue
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  * @since Oracle 9.0.1
  */
  public void setPlSqlTableArrayParam
                 (int parameterId
                 ,Object parameterValue) throws CSException
    {
    checkRange(parameterId);
                                                                                    
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    parameterTypeArray[parameterId-1] = OracleTypes.ARRAY;
    }


  /**
  * Set a parameter. This isn't normally used as java.sql.Date truncates the time
  * portion of a date.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param java.sql.Date parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,java.sql.Date parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * Set a parameter
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param java.util.Date parameterValue A java.util.date parameter. We ignore
  * nanoseconds. This is because DATE is stored with a precision of 1 second
  * but can be searched with nanosecond precison. If you don't always drop
  * the nanosecond portion you can create records which you can't find.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  * @since 4.0.2016 Support for java.util.Date added.
  */
  public void setParam(int parameterId
                      ,java.util.Date parameterValue) throws CSException
    {
    checkRange(parameterId);
    if (parameterValue == null)
      {
      parameterArray[parameterId-1] = null;
      }
    else
      {
      long theTime = parameterValue.getTime();
      // Remove subsecond portion of date - if this data is being stored
      // in a DATE column it will be discarded anyway.
      theTime = theTime - (theTime % 1000);
      parameterArray[parameterId-1] = new java.sql.Timestamp(theTime);
      }

    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set a <tt>boolean</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param boolean parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,boolean parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = new Boolean(parameterValue);
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>byte</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param byte parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,byte parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = new BigDecimal(parameterValue);
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>short</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param short parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,short parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = new BigDecimal(parameterValue);
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>int</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param int parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,int parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = new BigDecimal(parameterValue);
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>long</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param long parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,long parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = new BigDecimal(parameterValue);
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>float</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param float parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,float parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = new BigDecimal(parameterValue);
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>double</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param double parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,double parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = new BigDecimal(parameterValue);
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set a <tt>boolean</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Boolean parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,Boolean parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>byte</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Byte parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,Byte parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>short</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Short parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,Short parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>int</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Integer parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,Integer parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>long</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Long parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,Long parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>float</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Float parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,Float parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>double</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param Double parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,Double parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an <tt>double</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param java.math.BigDecimal parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,java.math.BigDecimal parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }


  /**
  * A convenience method to set an <tt>File</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param File parameterValue A File containing Long Data.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,File parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }
 /**
  * A convenience method to set an <tt>byte[]</tt> parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param byte[] parameterValue An array of byte[].
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,byte[] parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set a ReadOnlyRowSet parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param File parameterValue A ReadOnlyRowSet.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,ReadOnlyRowSet parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * A convenience method to set an InputStream parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param InputStream parameterValue An InputStream containing Long Data.
  * @param int fileSize How many bytes are in the data stream.
  * @param int dataType The OracleType of the underlying object
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,java.io.InputStream parameterValue
                      ,int fileSize
                      ,int dataType) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    inputParameterFilesizeArray[parameterId-1] = fileSize;
    parameterTypeArray[parameterId-1] = dataType;
    }

 /**
  * A convenience method to set a CLOB parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param oracle.sql.CLOB parameterValue An oracle.sql.CLOB containing CLOB Data.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,oracle.sql.CLOB parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    parameterTypeArray[parameterId-1] = OracleTypes.CLOB;
    }

 /**
  * A convenience method to set a BFILE parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param oracle.sql.BFILE parameterValue An oracle.sql.BFILE containing BFILE Data.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,oracle.sql.BFILE parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    parameterTypeArray[parameterId-1] = OracleTypes.BFILE;
    }

 /**
  * A convenience method to set a BLOB parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param oracle.sql.BLOB parameterValue An oracle.sql.BLOB containing BLOB Data.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,oracle.sql.BLOB parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    parameterTypeArray[parameterId-1] = OracleTypes.BLOB;
    }

  /**
  * A convenience method to set a BufferedInputStream parameter.
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param BufferedInputStream parameterValue A Buffered InputStream containing Long Data.
  * @param int fileSize How many bytes are in the data stream.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,BufferedInputStream parameterValue
                      ,int fileSize
                      ,int dataType) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    inputParameterFilesizeArray[parameterId-1] = fileSize;
    parameterTypeArray[parameterId-1] = dataType;
    }

  /**
  * Set a parameter
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @param String parameterValue A parameter.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public void setParam(int parameterId
                      ,String parameterValue) throws CSException
    {
    checkRange(parameterId);
    parameterArray[parameterId-1] = parameterValue;
    inputParameterSetArray[parameterId-1] = true;
    }

  /**
  * Get a parameter
  * @param int parameterId The id of the parameter to set. Id's start at 0.
  * @throws CSException if <tt>parameterId</tt> is not a valid parameter.
  */
  public Object getParam(int parameterId) throws CSException
    {
    checkRange(parameterId);
    return (parameterArray[parameterId-1]);
    }

  /**
  * Get all parameters
  * @return An array of Object.
  */
  public Object[] getParameters()
    {
    return (parameterArray);
    }

  /**
  * Return a String that uniquely identifies a set of parameters.
  * @return A unique string that describes this set of parameters.
  */
  public String getSignature()
  {
  String newSignature = "<signature ";

  for (int i=0; i < parameterArray.length; i++)
    {
    if (parameterArray[i] == null)
      {
      newSignature = newSignature + "paramType" + i + "=\""
        + "null" + "\" " + "paramValue" + i + "=\"" + "null" + "\" ";
      }
    else
      {
      newSignature = newSignature + "paramType" + i + "=\""
        + parameterArray[i].getClass().getName() + "\" " + "paramValue" + i + "=\"" + parameterArray[i].toString() + "\" ";
      }
    }

  newSignature = newSignature + ">";

  return(newSignature);
  }

  /**
  * Bind these parameter to a Prepared Statement.
  *
  * @param PreparedStatement thePreparedStatement The prepared statement you
  * want these parameters bound to.
  * @since Oracle 9i Support for TIMESTAMP, TIMESTAMP_TZ, TIMESTAMP_LTZ   
  * @since 2.0.1098 Support for Boolean parameters
  * @since 4.0.1789 Support for PL/SQL Index By Tables      
  * @since 4.0.1847 Support for PL/SQL Tables
  *
  */
  public void bindParameters(PreparedStatement thePreparedStatement) throws CSException
    {
    try                                                                                                                                                        //NOTIN1120  //NOTIN1120
      {                                                                                                                                                         
      final String hasExpired = "Demo Has Expired: Please buy a full copy from www.orindasoft.com";                                                         
      final int BORN = 2452710;                                                                                                                            
      final int DEFAULT_RA = Integer.MAX_VALUE - 12;                                                                                                     
                                                                                                                               
      int runtimeAuthority = DEFAULT_RA;                                                                                                            
      runtimeAuthority = runtimeAuthority + 11;                                                                                                     
                                                                                                                                                    
      java.util.Random r2 = new java.util.Random();                                                                                                 
      int bar = r2.nextInt(1000);                                                                                                           
      if (bar != 10)                                                                                                                                 
        {                                                                                                                                               
        throw new CSException("OK");                                                                                                            
        }                                                                                                                                            
                                                                                                                                                    
      // Find out when what the date really is....                                                                                                   
      PreparedStatement expireQry = thePreparedStatement.getConnection().prepareStatement("SELECT /* JDBCWizard Demo Library. Please buy a full copy of JDBCWizard from www.orindasoft.com */ to_number(to_char(sysdate,'J')) FROM DUAL");                                                  
      ResultSet qryResult = expireQry.executeQuery();                                                                                                
      qryResult.next();                                                                                                                                
      int sysdateJ = qryResult.getInt(1);                                                                                                             
      qryResult.close();                                                                                                                               
      qryResult = null;                                                                                                                               
      expireQry.close();                                                                                                                               
      expireQry = null;                                                                                                                               
                                                                                                                                                       
      sysdateJ = sysdateJ - BORN;                                                                                                                       
      int diff = runtimeAuthority - sysdateJ;                                                                                                           
                                                                                                                                                         
      if (runtimeAuthority == 81)                                                                                                                          
        {                                                                                                                                            
        }                                                                                                                                            
      else if (diff <= 0)                                                                                                                                 
        {                                                                                                                                               
        throw new CSException(hasExpired);                                                                                                            
        }                                                                                                                                            
      else if (diff < 10)                                                                                                                            
        {                                                                                                                                             
        theLog.warning("Generated code will expire " + (diff) +  " days from now. Please buy a full copy from www.orindasoft.com");                     
        }                                                                                                                                             
      else if (diff > 10)                                                                                                                           
        {                                                                                                                                            
        }                                                                                                                                             
      else                                                                                                                                            
        {                                                                                                                                              
        theLog.info("Generated code will expire " + (diff) +  " days from now. Please buy a full copy from www.orindasoft.com");                    
        }                                                                                                                                             
                                                                                                                                                      
      }                                                                                                                                                
    catch (CSException e)                                                                                                                              
      {                                                                                                                                                 
      if (e.getMessage().startsWith("OK"))                                                                                                
        {                                                                                                                                               
        }                                                                                                                                             
      else if (e.getMessage().startsWith("Demo Has Expired"))                                                                                                
        {                                                                                                                                               
        System.err.println(e.getMessage());                                                                                                             
        theLog.syserror(e,true,true);                                                                                                                           
        theLog.flush();                                                                                                                             
                                                                                                                                                  
        long endTime = System.currentTimeMillis() + 10000;                                                                                             
        while (System.currentTimeMillis() < endTime)                                                                                                  
          {                                                                                                                                          
          java.util.Random r = new java.util.Random();                                                                                                 
          int foo = r.nextInt() + r.nextInt();                                                                                                          
          }                                                                                                                                                 
        System.exit(1);                                                                                                                                      
        //throw (e);                                                                                                                                   
        }                                                                                                                                             
      }                                                                                                                                              
    catch (Exception e)                                                                                                                               
      {                                                                                                                                               
        throw (new CSException(e.getMessage()));                                                                                                      
      }                                                                                                                                                 
                                                                                                                                                       
    if (parameterArray.length > 0)
      {
      checkSet();

      // Bind Parameters. A CSDBException will be thrown if something goes wrong.
      for (int i=0; i < parameterArray.length; i++)
        {
        if (inputParameterSetArray[i])
          {
          try
            {
            if (   parameterArray[i]  == null)
              {
              if (parameterTypeArray[i] != Integer.MIN_VALUE)
                {
               	if ( parameterTypeArray[i] == OracleTypes.OPAQUE)
                  {
              	  ((OraclePreparedStatement)thePreparedStatement).setNull(i+1
              			  , OracleTypes.OPAQUE
              			  , parameterOpaqueTypeNameArray[i]);
                  }
               	else if ( parameterTypeArray[i] == OracleTypes.STRUCT)
                  {
              	  ((OraclePreparedStatement)thePreparedStatement).setNull(i+1
              			  , OracleTypes.STRUCT
              			  , parameterOpaqueTypeNameArray[i]);
                  }
                else
                  {
            	    thePreparedStatement.setNull(i+1, parameterTypeArray[i]);
                  }
                }
              else
                {
                thePreparedStatement.setNull(i+1, Types.VARCHAR);
                }
              }
            else if (parameterArray[i] instanceof String)
              {
              thePreparedStatement.setString(i+1, (String)parameterArray[i]);
              }
            else if (   parameterArray[i] instanceof java.sql.Timestamp)
              {
              thePreparedStatement.setTimestamp(i+1, (java.sql.Timestamp)parameterArray[i]);
              }
            else if (parameterArray[i] instanceof java.sql.Date)
              {
              thePreparedStatement.setDate(i+1, (java.sql.Date)parameterArray[i]);
              }
            else if (parameterArray[i] instanceof java.util.Date)
              {
              thePreparedStatement.setTimestamp(i+1,  new java.sql.Timestamp (((java.util.Date)parameterArray[i]).getTime()));
              }
            else if (parameterArray[i] instanceof BigDecimal)
              {
              thePreparedStatement.setBigDecimal(i+1, (BigDecimal)parameterArray[i]);
              }
            else if (parameterArray[i] instanceof oracle.sql.ROWID)
              {
              ((OraclePreparedStatement)thePreparedStatement).setROWID(i+1, (oracle.sql.ROWID)parameterArray[i]);
              }
            else if (parameterArray[i] instanceof oracle.sql.TIMESTAMP) 
              {                                                         
              ((OraclePreparedStatement)thePreparedStatement).setTIMESTAMP(i+1, (oracle.sql.TIMESTAMP)parameterArray[i]);      
              }                                                                                                                
            else if (parameterArray[i] instanceof oracle.sql.TIMESTAMPTZ)                                                      
              {                                                                                                                
              ((OraclePreparedStatement)thePreparedStatement).setTIMESTAMPTZ(i+1, (oracle.sql.TIMESTAMPTZ)parameterArray[i]);  
              }                                                                                                                
            else if (parameterArray[i] instanceof oracle.sql.TIMESTAMPLTZ)                                                     
              {                                                                                                                
              ((OraclePreparedStatement)thePreparedStatement).setTIMESTAMPLTZ(i+1, (oracle.sql.TIMESTAMPLTZ)parameterArray[i]);
              }                                                                                                                
            else if (parameterArray[i] instanceof oracle.sql.INTERVALYM)                                                         
              {                                                                                                                  
              ((OraclePreparedStatement)thePreparedStatement).setINTERVALYM(i+1, (oracle.sql.INTERVALYM)parameterArray[i]);      
              }                                                                                                                  
            else if (parameterArray[i] instanceof com.orindasoft.pub.PlsqlIndexByTable2)                                            
              {                                                                                                                    
              com.orindasoft.pub.PlsqlIndexByTable2 theTable = (com.orindasoft.pub.PlsqlIndexByTable2)parameterArray[i];             
                                                                                                                                   
              ((OraclePreparedStatement)thePreparedStatement).setPlsqlIndexTable(i+1                                               
                 ,theTable.getArray()  // Array Elements                                                                            
                 ,theTable.getElementMaxCount()  // Max number of elements (for updates)                                                   
                 ,theTable.getArrayLength()  // Current number of elements                                                       
                 ,theTable.getRealDataTypeCode()   // Oracle Data type code                                                               
                 ,theTable.getElementMaxLength()); // max length of an element                                                          
              }                                                                                                                     
            else if (parameterArray[i] instanceof com.orindasoft.pub.PlsqlArray)
              {
              Connection theConnection = thePreparedStatement.getConnection();

              com.orindasoft.pub.PlsqlArray theTable = (com.orindasoft.pub.PlsqlArray)parameterArray[i];

              ArrayDescriptor aDesc =
                  ArrayDescriptor.createDescriptor(theTable.getArrayName(), theConnection);
              ARRAY theArray = new ARRAY(aDesc, theConnection, theTable.getCurrentValuesAsObject(theConnection));

              ((OraclePreparedStatement)thePreparedStatement).setObject(i+1, theArray);
              }
            else if (parameterArray[i] instanceof oracle.sql.INTERVALDS)                                                         
              {                                                                                                                  
              ((OraclePreparedStatement)thePreparedStatement).setINTERVALDS(i+1, (oracle.sql.INTERVALDS)parameterArray[i]);      
              }                                                                                                                  
             else if (parameterArray[i] instanceof oracle.sql.OPAQUE)                                                         
              {                                                                                                                  
              ((OraclePreparedStatement)thePreparedStatement).setOPAQUE(i+1, (oracle.sql.OPAQUE)parameterArray[i]);      
              }                                                                                                                  
            else if (parameterArray[i] instanceof oracle.sql.STRUCT)                                                         
              {                                                                                                                  
              ((OraclePreparedStatement)thePreparedStatement).setSTRUCT(i+1, (oracle.sql.STRUCT)parameterArray[i]);      
              }                                                                                                                  
            else if (parameterArray[i] instanceof ReadOnlyRowSet)
              {
              // Added Build 2701: Do nothing if asked to bind a R.O.R.S
              thePreparedStatement.setNull(i+1, Types.VARCHAR);
              }
            else if (  parameterArray[i] instanceof java.io.InputStream
                    || parameterArray[i] instanceof java.io.BufferedInputStream
                    || parameterArray[i] instanceof java.io.ByteArrayInputStream)
              {
              if (parameterTypeArray[i] == OracleTypes.LONGVARCHAR)
                {
                thePreparedStatement.setAsciiStream(i+1, (java.io.InputStream)parameterArray[i],inputParameterFilesizeArray[i]);
                }
              else if (parameterTypeArray[i] == OracleTypes.LONGVARBINARY)
                {
                thePreparedStatement.setBinaryStream(i+1, (java.io.InputStream)parameterArray[i],inputParameterFilesizeArray[i]);
                }
              else
                {
                throw (new Exception("Don't know how to bind parameters of type " + parameterArray[i].getClass().getName()));
                }
              }
            else if (parameterArray[i] instanceof byte[])
              {
              thePreparedStatement.setBytes(i+1, (byte[])parameterArray[i]);
              }
            else if (parameterArray[i] instanceof oracle.sql.CLOB)
              {
              ((OraclePreparedStatement)thePreparedStatement).setCLOB(i+1, (oracle.sql.CLOB)parameterArray[i]);
              }
            else if (parameterArray[i] instanceof oracle.sql.BLOB)
              {
              ((OraclePreparedStatement)thePreparedStatement).setBLOB(i+1, (oracle.sql.BLOB)parameterArray[i]);
              }
            else if (parameterArray[i] instanceof oracle.sql.BFILE)
              {
              ((OraclePreparedStatement)thePreparedStatement).setBFILE(i+1, (oracle.sql.BFILE)parameterArray[i]);
              }
            else if (parameterArray[i] instanceof Boolean)
              {
              // Convert to a number and then set
              double booleanAsDouble = 0;
              if (((Boolean)parameterArray[i]).booleanValue())
                {
                booleanAsDouble = 1;
                }
              else
                {
                booleanAsDouble = -1;
                }
              thePreparedStatement.setBigDecimal(i+1, new java.math.BigDecimal(booleanAsDouble));
              }
            else
              {
              throw (new Exception("StatementParameters2: Don't know how to bind parameters of type " + parameterArray[i].getClass().getName()));
              }
            }
          catch (SQLException e)
            {
            thePreparedStatement = null;
            if (e.getErrorCode() == SqlUtils.INVALID_NAME_PATTERN)
              {
              throw new CSException("StatementParameters2:  Error while trying to set parameter "
            		             + (i+1)
            		             + ":ORA-" + SqlUtils.INVALID_NAME_PATTERN + ". This can happen if you are using PL/SQL Package arrays and "
            		             + "haven't created the extra objects required by JDBCWizard. "
            		             + "Try running the 'extraObjects.sql' script or calling the "
            		             + "'createExtraTypeObjects()' method in your service class "
            		             + ". Message Detail: "
            		             + e.getMessage()
            		             );
              }
            else if (e.getErrorCode() == SqlUtils.FAILED_TO_CONVERT_INTERNAL)
              {
              throw new CSException("StatementParameters2:  Error while trying to set parameter "
            		             + (i+1)
            		             + ":ORA-" + SqlUtils.FAILED_TO_CONVERT_INTERNAL + ". "
            		             + "The most common cause for this is a DB/JDBC Driver version mismatch."
            		             + ". Message Detail: "
            		             + e.getMessage()
            		             );
              }


            throw new CSException("StatementParameters2: Error while trying to set parameter " + (i+1) + ": ORA-" + e.getErrorCode() + " " + e.getClass().getName() + ":" + e.getMessage());
            }
          catch (Exception e)
            {
            thePreparedStatement = null;
            throw new CSException("StatementParameters2:  Error while trying to set parameter " + (i+1) + ":" + e.getClass().getName() + ":" + e.getMessage());
            }
          } // end of if set
        }
      }
    }
  /**
  * Complain if not all parameters set...
  * @since 2.0.1504 - method is now public instead of protected
  */
  public void checkSet() throws CSException
    {
    for (int i=0; i < parameterArray.length; i++)
      {
      if (   (! inputParameterSetArray[i]))
        {
        throw new CSException("Parameter " + (i+1) + " not set");
        }
      }
    }

  /**
  * Complain if param id is too high
  */
  protected void checkRange(int paramId) throws CSException
    {
    // Complain if we are setting parameters when there are none to set.
    if (parameterArray.length  == 0)
      {
      throw new CSException("Attempt to set parameter number '" + paramId + "' even though statement doesn't take parameters.");
      }
    // Complain if paramId is out of range.
    else if (parameterArray.length  == 1 && paramId != 1 )
      {
      throw new CSException("Attempt to set a non-existent parameter number " + paramId
                            + "; Only legal value is '1'");
      }
    else if (paramId < 1 || paramId > parameterArray.length)
      {
      throw new CSException("Attempt to set a non-existent parameter number " + paramId
                            + "; legal range is '1' to '" + parameterArray.length + "'");
      }
    }

   /**
   * Return version of this class
   * This menthod is used by generated code to make sure that it's using the
   * right version of com.orindasoft.pub.
   *
   * @param int Build , e.g. "2705"
   * @throws CSException if builds not the same
   * @since 6.0.2706
   **/
   public static void checkBuild(int pBuild) throws CSException
     {
     if (pBuild != buildNumber)
       {
       throw new CSException("Mismatch between build numbers of generated code ("
                            + pBuild
                            +") and com.orindasoft.pub Library (3145)");
       }
     }
  }






