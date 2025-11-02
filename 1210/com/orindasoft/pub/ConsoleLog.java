package com.orindasoft.pub;

import com.orindasoft.pub.LogInterface;
import java.util.Date;

import java.text.SimpleDateFormat;

/**
* This is a lightweight implementation of
* LogInterface that uses standard output for information messages and standard error
* for everything else. Requests for modal messages and logged messages
* are accepted and then ignored.
*
* <p> See <a href=http://www.orindasoft.com/public/Librarytwo.php4#logor&pdsrc=GD3145 TARGET=_blank class=news>LogInterface</a>
*
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class ConsoleLog extends GenericLog implements LogInterface
{
  /**
  * Waiting for input message text
  */
  protected static final String PRESS_ENTER = "Press Enter to continue...";
  
  /**
  * Create an instance of ConsoleLog.
  */
  public ConsoleLog()
    {
    super();
    }

  /**
  * Format a message and write it to either Standard Output(INFO messages) or Standard Error (eEverything else)
  * @param String messageType should be one of the constants DEBUG, INFO, WARN, ERROR or SYSERR as defined in LogInterface.
  * @param String messageText The message
  * @param boolean isModal ignored as we are dumping messages to the console.
  * @param boolean isLogged ignored as we are dumping messages to the console.
  */
  protected synchronized void writeMessage(String messageType, String messageText, boolean isModal, boolean isLogged)
    {
    String outputMessage = formatMessage(messageType,messageText);

    if (messageType.equals(LogInterface.INFO))
      {
      System.out.println(outputMessage);
      }
    else
      {
      System.err.println(outputMessage);
      }

    // If this is a modal message print a message and wait for input...
    if (isModal)
      {
      IOUtils.getStringFromConsole(PRESS_ENTER, true);
      }

    }

  /**
  * This is required by <code>LogInterface</code> but is not used in the implementation.
  */
  public void setAutoFlush(boolean flushEveryMessage)
    {
    }
    
  /**
  * This is required by <code>LogInterface</code> but is not used in the implementation.
  */
  public void flush()
    {
    }

  /**
  * This is required by <code>LogInterface</code> but is not used in the implementation.
  */
  public String getCurrentLog()
    {
    return(this.getClass().getName());
    }
}



