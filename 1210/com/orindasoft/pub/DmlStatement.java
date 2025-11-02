package com.orindasoft.pub;

// We are working wih JDBC
import java.sql.*;

/**
* A parameterized SQL statement
* that continues to exist even if the connection it uses is withdrawn.
* <p>
* Under normal circumstances <a href="http://www.orindasoft.com/?adsrc=api" target="_blank class="manual">OrindaBuild</a> users
* will have no reason to use this class directly - the generated code will use it.
* <p>
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public abstract class DmlStatement extends StatementParameters2 implements OracleResourceUser
{
  /**
  * Text of SQL DML statement
  */
  String statementSqlText = null;

  /**
  * What kind of DML statement this is - SELECT, INSERT, UPDATE or DELETE, etc
  * @see 
  */
  int statementType;

  /**
  * Database connection
  */
  Connection theConnection = null;

  /**
  * Prepared form of Sql Statement
  */
  PreparedStatement thePreparedStatement = null;

  /**
  * Log Interface Object
  */
  LogInterface theLog = null;

  /**
  * Create a DML Statement object and give it a connection
  */
  public DmlStatement(String statementSqlText, LogInterface theLog, Connection theConnection)
    {
    this(statementSqlText,theLog);
    setConnection(theConnection);
    }

  /**
  * Create a DML Statement object.
  */
  public DmlStatement(String statementSqlText, LogInterface theLog)
    {
    super(statementSqlText,theLog);
    this.theLog = theLog;
    this.statementSqlText = new String(statementSqlText);
    statementType = SqlUtils.getStatementType(statementSqlText);
    }

  /**
  * Provide a connection
  * @param Connection theConnection
  */
  public void setConnection(Connection theConnection)
    {
    this.theConnection = theConnection;
    }


  /**
  * Complain if the connection does not exist.
  * @throws CSDBException No database connection was available when this method was called
  */
  protected void testConnection() throws CSDBException
    {
    // Complain if we are without a connection...
    if (theConnection == null)
      {
      throw new CSDBException(0,"No Connection Provided",statementSqlText,"Execute method called with no Connection present");
      }
    }

  /**
  * Prepare the statement if needed.
  * @return <code>true</code> if we had to prepare the statement
  * @return <code>false</code> if the statement was already prepared
  * @throws CSDBException We were unable to prepare the statement
  */
  protected boolean createPreparedStatement() throws CSDBException
    {
    boolean prepareDoneThisTime = false;

    if (thePreparedStatement == null)
      {
      prepareDoneThisTime = true;
      try
        {
        thePreparedStatement = theConnection.prepareStatement(statementSqlText);
        }
      catch (java.sql.SQLException e)
        {
        thePreparedStatement = null;
        throw new CSDBException(e.getErrorCode(),e.toString(),statementSqlText,"Unable to prepare this statement");
        }
      }
    return(prepareDoneThisTime);
    }

  /**
  * Release the current connection
  * <p>
  * The prepared Statement will be closed and nullified. 
  */
  public void freeConnection()
    {
    if (thePreparedStatement != null)
      {
      try
        {
        thePreparedStatement.close();
        thePreparedStatement = null;
        }
      catch (SQLException e)
        {
        theLog.error("Unable to close " + statementSqlText + " :" + e.getMessage());
        }
      }
    this.theConnection = null;
    }

  /**
  * Check if our connection is usable
  * @return <tt>true</tt> if our connecion is usable
  * @return <tt>false</tt> if our connecion is not usable
  */
  public boolean connectionIsUsable()
    {
    if (theConnection == null || theLog == null)
      {
      return(false);
      }
    return (true);
    }

  /**
  * Used to tell if the object is using Oracle resources.
  * @return <tt>true</tt> if the object holds a resource.
  * @return <tt>false</tt> if the object does not hold a resource.
  */
  public boolean hasResources()
  {
  if (theConnection != null)
    {
    return(true);
    }
  return(false);
  }

  /**
  * Used to tell an object to release its Oracle resources. This method never throws an exception. If
  * releasing the resource will create problems they should be dealt with by the implementing class, not
  * escalated to the calling class.
  * @return <tt>true</tt> if the objects held an open PreparedStatement, ResultSet or similer resource.
  */
  public boolean releaseResources()
  {
  freeConnection();
  return(true);
  }

  /**
  * Return underlying java.sql.Statement Object. This method exists so that users
  * can call the various methods such as 'setQueryTimeout' that are defined in
  * the java.sql.Statement interface. Do not use it to replace the Statement
  * object.
  * @return java.sql.Statement
  * @throws CSDBException If we had to try to create the Statement before we could return it and something went wrong.
  * @since 5.0.2267 Retuens Statement Object.
  */
  public java.sql.Statement getUnderlyingStatement() throws CSDBException
  {
  createPreparedStatement();
  return(thePreparedStatement);
  }

}



