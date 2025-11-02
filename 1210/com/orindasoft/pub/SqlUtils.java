package com.orindasoft.pub;

import java.sql.*;
import oracle.jdbc.OracleTypes;

/**
* A set of useful static methods for working with SQL.
* <p>
* This class changes for different versions of Oracle.
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class SqlUtils
{

  /**
  * Constant for identifying statements as being queries
  */
  public static final int SELECT = 0;

  /**
  * Constant for identifying statements as being inserts
  */
  public static final int INSERT = 1;

  /**
  * Constant for identifying statements as being updates
  */
  public static final int UPDATE = 2;

  /**
  * Constant for identifying statements as being deletes
  */
  public static final int DELETE = 3;

  /**
  * Constant for identifying statements as being DDL statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int DDL = 5;

  /**
  * Constant for identifying statements as being Lock statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int LOCK = 6;

  /**
  * Constant for identifying statements as being Merge statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int MERGE = 7;

  /**
  * Constant for identifying statements as being Lock statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int XPLAN = 8;

  /**
  * Constant for identifying statements as being Lock statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int SAVEPOINT = 9;

  /**
  * Constant for identifying statements as being Lock statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int SET_CONSTRAINTS = 10;

  /**
  * Constant for identifying statements as being Lock statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int SET_TRANSACTION = 11;

  /**
  * Constant for identifying statements as being Lock statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int SET_ROLE = 12;

  /**
  * Constant for identifying statements as being PL/SQL statements
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static final int PLSQL  = 14;

  /**
  * Constant for identifying statements as being unidentifiable
  */
  public static final int UNKNOWN = 4;

  /**
  * Array containing words which are not legal identifers...
  */
  public static final String[] RESERVED_WORDS =
     {"ACCESS"
     ,"ADD"
     ,"ALL"
     ,"ALTER"
     ,"AND"
     ,"ANY"
     ,"AS"
     ,"ASC"
     ,"AUDIT"
     ,"BETWEEN"
     ,"BY"
     ,"CHAR"
     ,"CHECK"
     ,"CLUSTER"
     ,"COLUMN"
     ,"COMMENT"
     ,"COMPRESS"
     ,"CONNECT"
     ,"CREATE"
     ,"CURRENT"
     ,"DATE"
     ,"DECIMAL"
     ,"DEFAULT"
     ,"DELETE"
     ,"DESC"
     ,"DISTINCT"
     ,"DROP"
     ,"ELSE"
     ,"EXCLUSIVE"
     ,"EXISTS"
     ,"FILE"
     ,"FLOAT"
     ,"FOR"
     ,"FROM"
     ,"GRANT"
     ,"GROUP"
     ,"HAVING"
     ,"IDENTIFIED"
     ,"IMMEDIATE"
     ,"IN"
     ,"INCREMENT"
     ,"INDEX"
     ,"INITIAL"
     ,"INSERT"
     ,"INTEGER"
     ,"INTERSECT"
     ,"INTO"
     ,"IS"
     ,"LEVEL"
     ,"LIKE"
     ,"LOCK"
     ,"LONG"
     ,"MAXEXTENTS"
     ,"MINUS"
     ,"MLSLABEL"
     ,"MODE"
     ,"MODIFY"
     ,"NOAUDIT"
     ,"NOCOMPRESS"
     ,"NOT"
     ,"NOWAIT"
     ,"NULL"
     ,"NUMBER"
     ,"OF"
     ,"OFFLINE"
     ,"ON"
     ,"ONLINE"
     ,"OPTION"
     ,"OR"
     ,"ORDER"
     ,"PCTFREE"
     ,"PRIOR"
     ,"PRIVILEGES"
     ,"PUBLIC"
     ,"RAW"
     ,"RENAME"
     ,"RESOURCE"
     ,"REVOKE"
     ,"ROW"
     ,"ROWID"
     ,"ROWNUM"
     ,"ROWS"
     ,"SELECT"
     ,"SESSION"
     ,"SET"
     ,"SHARE"
     ,"SIZE"
     ,"SMALLINT"
     ,"START"
     ,"SUCCESSFUL"
     ,"SYNONYM"
     ,"SYSDATE"
     ,"TABLE"
     ,"THEN"
     ,"TO"
     ,"TRIGGER"
     ,"UID"
     ,"UNION"
     ,"UNIQUE"
     ,"UPDATE"
     ,"USER"
     ,"VALIDATE"
     ,"VALUES"
     ,"VARCHAR"
     ,"VARCHAR2"
     ,"VIEW"
     ,"WHENEVER"
     ,"WHERE"
     ,"WITH"};
     
  /**
  * Constant for identifiying oracle Text datatypes
  */
  public static final int ORACLE_TEXT_DATATYPE = 0;

  /**
  * Constant for identifiying oracle Number datatypes
  */
  public static final int ORACLE_NUMBER_DATATYPE = 1;

  /**
  * Constant for identifiying oracle Date datatypes
  */
  public static final int ORACLE_DATE_DATATYPE = 2;

  /**
  * Constant for identifiying oracle Long Text datatypes
  */
  public static final int ORACLE_LONGTEXT_DATATYPE = 3;

  /**
  * Constant for identifiying oracle Long Binary datatypes
  */
  public static final int ORACLE_LONG_BINARY_DATATYPE = 4;

  /**
  * Constant for identifiying oracle Binary datatypes
  */
  public static final int ORACLE_BINARY_DATATYPE = 11;

  /**
  * Constant for identifiying oracle CLOB datatypes
  */
  public static final int ORACLE_CLOB_DATATYPE = 15;

  /**
  * Constant for identifiying oracle BLOB datatypes
  */
  public static final int ORACLE_BLOB_DATATYPE = 16;

  /**
  * Constant for identifiying oracle BFILE datatypes
  */
  public static final int ORACLE_BFILE_DATATYPE = 17;

  /**
  * Constant for identifiying oracle ref cursors
  */
  public static final int ORACLE_REFCURSOR_DATATYPE = 6;

  /**
  * Constant for identifiying PL/SQL Boolean
  */
  public static final int ORACLE_BOOLEAN_DATATYPE = 7;

  /**
  * Constant for identifiying PL/SQL Boolean
  */
  public static final int  ORINDASOFT_READONLYROWSET = 8;

  /**
  * Constant for identifiying ROWID
  */
  public static final int  ORACLE_ROWID_DATATYPE = 9;

  /**
  * Constant for identifiying UROWID
  */
  public static final int  ORACLE_UROWID_DATATYPE = 10;

  /**
  * Constant for identifiying TIMESTAMP
  */
  public static final int  ORACLE_TIMESTAMP_DATATYPE = 12;

  /**
  * Constant for identifiying TIMESTAMPTZ
  */
  public static final int  ORACLE_TIMESTAMPTZ_DATATYPE = 13;

  /**
  * Constant for identifiying TIMESTAMPLTZ
  */
  public static final int  ORACLE_TIMESTAMPLTZ_DATATYPE = 14;

  /**
  * Constant for identifiying oracle Collection's TABLE
  */
  public static final int ORACLE_TABLE_DATATYPE = 18;

  /**
  * Constant for identifiying oracle Collection's VARRAY
  */
  public static final int ORACLE_VARRAY_DATATYPE = 19;

  /**
  * Constant for identifiying Oracle OBJECT Datatype
  */
  public static final int ORACLE_OBJECT_DATATYPE = 20;

  /**
  * Constant for identifiying PL/SQL Rowtype Datatype
  */
  public static final int ORACLE_ROWTYPE_DATATYPE = 21;

  /**
  * Constant for identifiying INTERVAL YEAR TO MONTH Datatype
  */
  public static final int ORACLE_INTERVAL_YEAR_TO_MONTH_DATATYPE = 22;

  /**
  * Constant for identifiying INTERVAL DAY TO SECOND Datatype
  * @since Oracle 10.1.0
  */
  public static final int ORACLE_INTERVAL_DAY_TO_SECOND_DATATYPE = 23;

  /**
  * Constant for identifiying PL/SQL Index By tables
  * @since Oracle 10.1.0
  */
  public static final int ORACLE_PLSQL_INDEXBY_DATATYPE = 24;

  /**
  * Constant for identifiying PL/SQL Index By tables
  * @since Oracle 10.1.0
  */
  public static final int ORACLE_PLSQL_INDEXBY_ROWTYPE_DATATYPE = 25;

  /**
  * Constant for identifiying XMLType
  * @since Oracle 10.2.0
  */
  public static final int ORACLE_XMLTYPE_DATATYPE = 26;

  /**
  * Constant for identifiying ORACLE_SDO_GEOMETRY_DATATYPE
  * @since Oracle 10.2.0
  */
  public static final int ORACLE_SDO_GEOMETRY_DATATYPE = 27;

  /**
  * Constant for identifiying unrecognized oracle datatypes
  */
  public static final int ORACLE_OTHER_DATATYPE = 99;

  /**
  * Constant for identifiying null datatypes
  */
  public static final int ORACLE_NULL_DATATYPE = 100;

  /**
  * Constant for ORA-4043 - Object does not exist
  */
  public static final int OBJECT_DOES_NOT_EXIST = 4043;

  /**
  * Constant for ORA-17074 message - invalid name pattern
  **/
  public static final int INVALID_NAME_PATTERN = 17074;

  /**
  * Constant for ORA-17059 message - invalid name pattern
  * The most common cause for this is a DB/Driver version mismatch
  **/
  public static final int FAILED_TO_CONVERT_INTERNAL = 17059;

  /**
  * Constant for ORA-2303 message - cannot drop or replace a type with type or table dependents
  **/
  public static final int TYPE_HAS_DEPENDENTS = 2303;

  /**
  * A set of useful static methods for working with SQL.
  */
  public SqlUtils()
  {
  }

  /**
  * Classify a SQL statement as a SELECT, INSERT, UPDATE, etc.
  * <p>
  * This method has a <a href="http://www.orindasoft.com/public/Supporttwo.php4#SqlUtils.getStatementType()_is_confused_by_leading_comments_and_spaces" target=_blank class=news>known bug</a> 
  * @param theStatement a SQL Statement
  * @return an int that will be a SqlUtils constant such as SqlUtils.SELECT, SqlUtils.UPDATE, etc.
  * @since 2.0.1527 Support for Lock, Merge and DDL statements
  */
  public static int getStatementType(String theStatement)
    {
    int statementType = SqlUtils.UNKNOWN;
    String tempStatement = theStatement.toUpperCase();

    if (tempStatement.startsWith("SELECT"))
      {
      statementType = SqlUtils.SELECT;
      }
    else if (tempStatement.startsWith("INSERT"))
      {
      statementType = SqlUtils.INSERT;
      }
    else if (tempStatement.startsWith("UPDATE"))
      {
      statementType = SqlUtils.UPDATE;
      }
    else if (tempStatement.startsWith("DELETE"))
      {
      statementType = SqlUtils.DELETE;
      }
    else if (tempStatement.startsWith("LOCK"))
      {
      statementType = SqlUtils.LOCK;
      }
    else if (tempStatement.startsWith("MERGE"))
      {
      statementType = SqlUtils.MERGE;
      }
    else if (tempStatement.startsWith("EXPLAIN PLAN"))
      {
      statementType = SqlUtils.XPLAN;
      }
    else if (tempStatement.startsWith("SAVEPOINT"))
      {
      statementType = SqlUtils.SAVEPOINT;
      }
    else if (tempStatement.startsWith("SET CONSTRAINTS"))
      {
      statementType = SqlUtils.SET_CONSTRAINTS;
      }
    else if (tempStatement.startsWith("SET TRANSACTION"))
      {
      statementType = SqlUtils.SET_TRANSACTION;
      }
    else if (tempStatement.startsWith("SET ROLE"))
      {
      statementType = SqlUtils.SET_ROLE;
      }
    else if (   tempStatement.startsWith("CALL")
             || tempStatement.startsWith("DECLARE")
             || tempStatement.startsWith("BEGIN")
            )
      {
      statementType = SqlUtils.PLSQL;
      }
    else if (   tempStatement.startsWith("ALTER")
             || tempStatement.startsWith("ANALYZE")
             || tempStatement.startsWith("ASSOCIATE")
             || tempStatement.startsWith("AUDIT")
             || tempStatement.startsWith("COMMENT")
             || tempStatement.startsWith("COMMIT")
             || tempStatement.startsWith("CREATE")
             || tempStatement.startsWith("DISASSOCIATE")
             || tempStatement.startsWith("DROP")
             || tempStatement.startsWith("GRANT")
             || tempStatement.startsWith("NOAUDIT")
             || tempStatement.startsWith("RENAME")
             || tempStatement.startsWith("REVOKE")
             || tempStatement.startsWith("ROLLBACK")
             || tempStatement.startsWith("TRUNCATE")
            )
      {
      statementType = SqlUtils.DDL;
      }

    return(statementType);

    }

  /**
  * Count parameters in a SQL Statement
  * This method has a <a href="http://www.orindasoft.com/public/Supporttwo.php4#SqlUtils.countParameters()_returns_incorrect_value_if_comment_contains_'?'" target=_blank class=news>known bug</a> 
  * @param String aSqlStatement a SQL Statement
  * @return an int The number of JDBC parameters in this SQL statement
  */
  public static int countParameters(String aSqlStatement)
    {
    char[] statementArray = aSqlStatement.toCharArray();
    boolean inQuote = false;
    int paramCounter = 0;

    for (int i=0; i < statementArray.length; i++)
      {
      if  (statementArray[i] == '\'')
        {
        if (inQuote)
          {
          inQuote = false;
          }
        else
          {
          inQuote = true;
          }
        }

      if ( (!inQuote) && statementArray[i] == '?')
        {
        paramCounter++;
        }
      }

    return(paramCounter);
    }
  /**
  * Return an <tt>int</tt> that represents the underlying oracle data type.
  * This method takes an oracle data type and classifies it as Text, Number, Date
  * Long Text or Other.
  * @param String An oracle data type
  * @return int A SqlUtils constant that represents the underlying oracle data type.
  */
  public static int getUnderlyingOracleDatatype(String theColumnDataType)
    {
    int returnCode = ORACLE_OTHER_DATATYPE;
    if (theColumnDataType == null)
      {
      returnCode = ORACLE_NULL_DATATYPE;
      }
    else if (   theColumnDataType.equals("VARCHAR2")
             || theColumnDataType.equals("VARCHAR")
             || theColumnDataType.equals("CHAR")
             || theColumnDataType.equals("CHARACTER")
             || theColumnDataType.equals("STRING")
             )
      {
      returnCode = ORACLE_TEXT_DATATYPE;
      }
    else if (   theColumnDataType.equals("ROWID"))
      {
      returnCode = ORACLE_ROWID_DATATYPE;
      }
    else if (theColumnDataType.equals("UROWID"))
      {
      returnCode = ORACLE_UROWID_DATATYPE;
      }
    else if (   theColumnDataType.equals("DATE"))
      {
      returnCode = ORACLE_DATE_DATATYPE;
      }
    else if (   theColumnDataType.equals("NUMBER")
             || theColumnDataType.equals("FLOAT")
             || theColumnDataType.equals("NATURAL")
             || theColumnDataType.equals("NATURALN")
             || theColumnDataType.equals("POSITIVE")
             || theColumnDataType.equals("POSITIVEN")
             || theColumnDataType.equals("SIGNTYPE")
             || theColumnDataType.equals("BINARY_INTEGER")
             || theColumnDataType.equals("DEC")
             || theColumnDataType.equals("DECIMAL")
             || theColumnDataType.equals("DOUBLE PRECISION")
             || theColumnDataType.equals("FLOAT")
             || theColumnDataType.equals("INTEGER")
             || theColumnDataType.equals("INT")
             || theColumnDataType.equals("NUMERIC")
             || theColumnDataType.equals("REAL")
             || theColumnDataType.equals("SMALLINT")
             || theColumnDataType.equals("PLS_INTEGER")
             || theColumnDataType.equals("DECFLOAT")//DB2
             )
      {
      returnCode = ORACLE_NUMBER_DATATYPE;
      }
    else if (   theColumnDataType.equals("LONG"))
      {
      returnCode = ORACLE_LONGTEXT_DATATYPE;
      }
    else if (theColumnDataType.equals("CLOB"))
      {
      returnCode = ORACLE_CLOB_DATATYPE;
      }
    else if (theColumnDataType.equals("com.orindasoft.pub.ReadOnlyRowSet"))
      {
      returnCode = ORINDASOFT_READONLYROWSET;
      }
    else if (theColumnDataType.equals("ORACLE COLLECTION"))
      {
      returnCode = ORACLE_TABLE_DATATYPE;
      }
    else if (theColumnDataType.equals("TABLE"))
      {
      returnCode = ORACLE_TABLE_DATATYPE;
      }
    else if (theColumnDataType.equals("VARRAY"))
      {
      returnCode = ORACLE_VARRAY_DATATYPE;
      }
    else if (theColumnDataType.equals("OBJECT"))
      {
      returnCode = ORACLE_OBJECT_DATATYPE;
      }
    else if (theColumnDataType.equals("PL/SQL BOOLEAN"))
      {
      returnCode = ORACLE_BOOLEAN_DATATYPE;
      }
    else if (theColumnDataType.equals("PL/SQL RECORD"))
      {
      returnCode = ORACLE_ROWTYPE_DATATYPE;
      }
    else if (theColumnDataType.equals("LONG RAW"))
      {
      returnCode = ORACLE_LONG_BINARY_DATATYPE;
      }
    else if (theColumnDataType.equals("BLOB"))
      {
      returnCode = ORACLE_BLOB_DATATYPE;
      }
    else if (theColumnDataType.equals("BFILE"))
      {
      returnCode = ORACLE_BFILE_DATATYPE;
      }
    else if ( theColumnDataType.equals("RAW"))
      {
      returnCode = ORACLE_BINARY_DATATYPE;
      }
    else if (   theColumnDataType.equals("REF CURSOR"))
      {
      returnCode = ORINDASOFT_READONLYROWSET;
      }
    else if (    theColumnDataType.equals("INTERVAL YEAR TO MONTH")
              || theColumnDataType.equals("INTERVALYM")
              || (   theColumnDataType.startsWith("INTERVAL YEAR")
                  && theColumnDataType.endsWith("TO MONTH")))
      {
      returnCode = ORACLE_INTERVAL_YEAR_TO_MONTH_DATATYPE;
      }
    else if (    theColumnDataType.equals("INTERVAL DAY TO SECOND")     
              || theColumnDataType.equals("INTERVALDS")                 
              || (   theColumnDataType.startsWith("INTERVAL DAY")       
                  && theColumnDataType.indexOf("TO SECOND") > -1))      
      {                                                                  
      returnCode = ORACLE_INTERVAL_DAY_TO_SECOND_DATATYPE;              
      }                                                                  
    else if (    theColumnDataType.equals("TIMESTAMP WITH LOCAL TIME ZONE") // Datatype according to ALL_SOURCE
              || theColumnDataType.equals("TIMESTAMPLTZ") // Datatype according to ResultSet
              || (   theColumnDataType.startsWith("TIMESTAMP") // Allow for "TIMESTAMP(6) WITH LOCAL TIME ZONE"
                  && theColumnDataType.endsWith("LOCAL TIME ZONE")))
      {
      returnCode = ORACLE_TIMESTAMPLTZ_DATATYPE;
      }
    else if (    theColumnDataType.equals("TIMESTAMP WITH TIME ZONE")  // Datatype according to ALL_SOURCE
              || theColumnDataType.equals("TIMESTAMPTZ")   // Datatype according to ResultSet
              || (   theColumnDataType.startsWith("TIMESTAMP") // Allow for "TIMESTAMP(6) WITH LOCAL TIME ZONE"
                  && theColumnDataType.endsWith("TIME ZONE")))
      {
      returnCode = ORACLE_TIMESTAMPTZ_DATATYPE;
      }
    else if (   theColumnDataType.startsWith("TIMESTAMP"))
      {
      returnCode = ORACLE_TIMESTAMP_DATATYPE;
      }
    else if (   theColumnDataType.startsWith("PL/SQL TABLE"))      
      {                                                            
      returnCode = ORACLE_PLSQL_INDEXBY_DATATYPE;                  
      }                                                             
     // SYS.XMLTYPE is used as well as XMLTYPE                    
    else if (   theColumnDataType.equals("SYS.XMLTYPE"))              
      {                                                           
      returnCode = ORACLE_XMLTYPE_DATATYPE;                         
      }                                                           
    else if (   theColumnDataType.equals("XMLTYPE"))               
      {                                                           
      returnCode = ORACLE_XMLTYPE_DATATYPE;                         
      }                                                           
    else if (   theColumnDataType.equals("SDO_GEOMETRY"))               
      {                                                           
      returnCode = ORACLE_SDO_GEOMETRY_DATATYPE;                         
      }                                                           

    return(returnCode);
    }
                                                                         
  /**                                                                     
  * Create and return a BFILE locator.                                    
  * This static method creates an oracle.sql.BFILE object                 
  * by accessing the database. Note that a 'valid' BFILE will be           
  * returned even if theOracleDirectory does not exist or theFileName           
  * does not refer to an existing file.                                     
  * @param String theOracleDirectory                                       
  * @param String theFileName                                              
  * @param Connection a database Connection                                 
  * @param com.orindasoft.pub.LogInterface a logging mechanism              
  * @param boolean debugMessages Create debug messages in log               
  * @return oracle.sql.BFILE A BFILE which may or may not be usable.         
  * @throws CSException If we can't create a BFILE.                          
  * @since Oracle 8.1.7 / JDBCWizard 4.0.2108                           
  */                                                                          
  public static oracle.sql.BFILE createBfileLocator(String theOracleDirectory    
                                                   ,String theFileName                
                                                   ,java.sql.Connection theConnection    
                                                   ,LogInterface theLog                    
                                                   ,boolean debugMessages) throws CSException    
    {                                                                                            
    oracle.sql.BFILE newBfile = null;                                                             
                                                                                                   
    try                                                                                             
      {                                                                                              
      if (debugMessages)                                                                              
        {                                                                                              
        theLog.debug("IOUtils.createBfileLocator: Attempting to create BFILE locator for Directory/File "   
                    + theOracleDirectory                                                                     
                    + "/"                                                                                    
                    + theFileName );                                                                          
        }                                                                                                     
                                                                                                                    
      PreparedStatement getBfileStatement = theConnection.prepareStatement("SELECT /* JDBCWizard */ bfilename(?,?) FROM DUAL");    
      getBfileStatement.setString(1,theOracleDirectory);                                                        
      getBfileStatement.setString(2,theFileName);                                                                
      ResultSet theResult = getBfileStatement.executeQuery();                                                    
      theResult.next();                                                                                          
      newBfile = (oracle.sql.BFILE)theResult.getObject(1);                                                        
      theResult.close();                                                                                          
      getBfileStatement.close();                                                                                  
                                                                                                                   
      if (debugMessages)                                                                                           
        {                                                                                                          
        theLog.debug("IOUtils.createBfileLocator: Created BFILE locator for Directory/File "                       
                    + theOracleDirectory                                                                            
                    + "/"                                                                                           
                    + theFileName );                                                                                 
        }                                                                                                           
      }                                                                                                             
    catch (SQLException e)                                                                                          
      {                                                                                                             
      theLog.error("IOUtils.createBfileLocator: " + e.getMessage());                                                 
      throw new CSException("Unable to create oracle.sql.BFILE object :" + e.getMessage());                         
      }                                                                                                             
                                                                                                                    
    return(newBfile);                                                                                               
    }                                                                                                                 
}




