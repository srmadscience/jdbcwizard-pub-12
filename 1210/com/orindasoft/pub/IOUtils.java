package com.orindasoft.pub;

import java.io.*;

/**
* A set of useful static methods for working with files.
* 
* <br>(c) Copyright 2003 - 2015 Orinda Software Ltd<p>
*
* @version 6.0
* @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>
*/
public class IOUtils
{

  /**
  * Default block size for IO operations
  */
  public static final int IO_BUFFER_SIZE = 4096;

  /**
  * IOUtils is a set of static methods for working with files.
  */
  public IOUtils()
  {
  }

  /**
  * Make sure directory <code>directoryName</code> exists.
  * @param String directoryName
  * @return A file object contaning the directory in question.
  * @throws CSIOException if the file isn't a writable, readable, directory.
  */
  public static File confirmDirectory(String directoryName) throws CSIOException
    {
    File tempFile;
    try
      {
      tempFile = new File (directoryName);

      if ( ! tempFile.exists() )
        {
        tempFile.mkdirs();
        }

      return(tempFile);
      }
    catch(Exception e)
      {
      throw new CSIOException(e.getMessage());
      }
    }

  /**
  * Get the user to enter a String
  * @param String theMessage A message to be sent to Standard Output
  * @param boolean noNewLine Whether a newline character is printed after <code>theMessage</code>.
  * @return String whatever the user typed prior to presssing 'Enter'.
  */
  public static String getStringFromConsole(String theMessage, boolean noNewline)
  {
  String inputString = "";
  BufferedReader theReader = new BufferedReader(new InputStreamReader(System.in));

  if (noNewline)
    {
    System.out.print(theMessage);
    }
  else
    {
    System.out.println(theMessage);
    }

  try
    {
    inputString = theReader.readLine();
    }
  catch (Exception e)
    {
    inputString = "";
    }

  System.out.println("");

  return(inputString);
  }

  /**
  * Return an OS-Specific temporary directory File
  */
  public static File getOsTempDir()
    {
    File tempDir = null;
    String osName = System.getProperty("os.name").toUpperCase();

    // See if we are running a flavour of Unix.
    if (   osName.lastIndexOf("UNIX") > -1
        || osName.lastIndexOf("LINUX") > -1
        || osName.lastIndexOf("SUNOS") > -1)
      {
      tempDir = new File("/tmp");
      }
    // Default to a subdirectory of our working directory.
    else
      {
      tempDir = new File(System.getProperty("user.home") + File.separator + "Temp");
      }
      
    return(tempDir);
    }

  /**
  * Copies a file.
  * @param File oldFile the file you want copied
  * @param File newFile the file you want it copied to
  */
  public static void copyFile(File oldFile, File newFile) throws CSException
    {
    byte[] buff = new byte[IO_BUFFER_SIZE];
    int bytesRead;

    try
      {
      BufferedInputStream  source      = new BufferedInputStream(new FileInputStream(oldFile),IO_BUFFER_SIZE );
      BufferedOutputStream destination = new BufferedOutputStream(new FileOutputStream(newFile), IO_BUFFER_SIZE);

      while(true)
        {
        bytesRead = source.read(buff);

        if (bytesRead == -1)
          {
          break;
          }

        destination.write(buff, 0, bytesRead);
        }

      source.close();
      destination.flush();
      destination.close();

      }
    catch(IOException error)
      {
      throw new CSException(error.getMessage());
      }

  }

/**
* Loads a file into a byte array. Note that this will not work with really big files.
* Non-existant or zero length files are returned as a zero length array. This routine
* has been tested with files up to 1MB in size.
* @param File inFile the file you want copied
* @return byte[] a byte array
* @since JDBCWizard 4.0.2108
*/
  public static byte[] loadFileIntoByteArray(File inFile) throws CSException
    {
    byte[] buff;

    if (inFile == null || (! inFile.exists()) || inFile.length() == 0)
      {
      // Return zero length array.
      buff = new byte[0];
      return(buff);
      }

    try
      {
      // Note that a file can be Long.MAX_VALUE but that
      // our byte array can only be Integer.MAX_VALUE in size
      buff = new byte[(int)inFile.length()];
      }
    catch (Exception e)
      {
      throw new CSException("loadFileIntoByteArray: File " + inFile.getAbsolutePath()
                           + " is too big to be turned into a byte array. Size is "
                           + inFile.length());
      }

    try
      {
      int bytesRead;
      BufferedInputStream  source = new BufferedInputStream(new FileInputStream(inFile),IO_BUFFER_SIZE );
      bytesRead = source.read(buff,0,(int)inFile.length());
      source.close();

      if (bytesRead != inFile.length())
        {
        throw new CSException("loadFileIntoByteArray: File " + inFile.getAbsolutePath()
                             + " could not be turned into a byte array of same size. File size is "
                             + inFile.length() + ". Only " + bytesRead + " bytes were retrieved");
        }

      }
    catch(IOException error)
      {
      throw new CSException("loadFileIntoByteArray: Error while reading: " + error.getMessage());
      }

    return(buff);
  }

/**
* An extremely crude implemtation of 'grep'
*/
public static boolean grep(String searchString, File searchFile)
  {
  boolean retCode = false;

  //
  // Set up input file stream...
  //
  try
    {
    FileInputStream aFileInputStream = new FileInputStream(searchFile);
    BufferedReader  aReader          = new BufferedReader(new InputStreamReader(aFileInputStream));

    //
    // Read each line in the input file...
    //
    String aString = aReader.readLine();
    while (aString != null)
      {
      if (aString.indexOf(searchString) > 0)
        {
        retCode = true;
        break;
        }

      aString = aReader.readLine();
      }
    aReader.close();
    aFileInputStream.close();

    }
  catch (IOException e)
    {
    retCode = false;
    }

  return(retCode);
  }

/**
* Loads a file into a String. Note that this will not work with really big files.
* Non-existant or zero length files are returned as a zero length String.This routine
* has been tested with files up to 1MB in size.
* @param File inFile the file you want turned into a String
* @return String a String
* @since JDBCWizard 4.0.2108
*/
public static String loadFileIntoString(File inFile) throws CSException
  {
  return(new String(loadFileIntoByteArray(inFile)));
  }

/**
* Turn a String into a File. A null String will be turned into a zero length file.
* @param String theString
* @param String tempFilePrefix
* @param String tempFileSuffix
* @param String tempFileDir
* @param LogInterface theLog
* @since JDBCWizard 4.0.2098
*/
public static java.io.File loadStringIntoFile(String theString, String tempFilePrefix, String tempFileSuffix, String tempFileDir, LogInterface theLog) throws CSException
  {
  if (theString == null)
    {
    return (loadByteArrayIntoFile(null, tempFilePrefix, tempFileSuffix, tempFileDir, theLog));
    }

  return (loadByteArrayIntoFile(theString.getBytes(), tempFilePrefix, tempFileSuffix, tempFileDir, theLog));
}

/**
* Turn a String into a File. A null String will be turned into a zero length file.
* @param String theString
* @param File   theFile
* @since JDBCWizard 6.0.2746
*/
public static java.io.File loadStringIntoFile(String theString, java.io.File newFile, LogInterface theLog) throws CSException
  {
  if (theString == null)
    {
    return (loadByteArrayIntoFile(null, newFile, theLog));
    }

  return (loadByteArrayIntoFile(theString.getBytes(), newFile, theLog));
}

  /**
  * Turn a char array into a Temporary File.  A null value for <code>theChars</code>
  * will be turned into a zero length file. java.io.File.createTempFile() is used to
  * create what is supposed to be a unique file.
  *
  * @param char[] theChars The chars you wish to be turned into a temporary File
  * @param String tempFilePrefix The prefix for the temporary file's name
  * @param String tempFileSuffix  The suffix for the temporary file's name
  * @param String tempFileDir The directory for the temporary files.
  * @param LogInterface A logging mechanism.
  * @since JDBCWizard 4.0.2098
  */
  public static java.io.File loadCharArrayIntoFile(char[] theChars, String tempFilePrefix, String tempFileSuffix, String tempFileDir, LogInterface theLog) throws CSException
    {
    java.io.File tempFile = null;

    try
      {
      tempFile = java.io.File.createTempFile(tempFilePrefix, tempFileSuffix, new File(tempFileDir));
      }
    catch (java.io.IOException e)
      {
      throw new CSException("IOUtils.loadCharArrayIntoFile:" + e.getMessage());
      }

    return(loadCharArrayIntoFile ( theChars
                                 , tempFile
                                 , theLog));
    }
  /**
  * Turn a char array into a File.  A null Array will be turned into a zero length file.
  * @param char[] theChars
  * @param File newFile
  * @param LogInterface
  * @since JDBCWizard 4.0.2108
  */
  public static java.io.File loadCharArrayIntoFile(char[] theChars, java.io.File newFile, LogInterface theLog) throws CSException
    {

    try
      {
      java.io.FileOutputStream outStream = new java.io.FileOutputStream(newFile);
      java.io.BufferedWriter theWriter = new java.io.BufferedWriter (new java.io.OutputStreamWriter(outStream));

      if (theChars != null && theChars.length > 0)
        {
        theWriter.write(theChars,0,theChars.length);
        }

      // Flush and close output stream
      theWriter.close();
      outStream.flush();
      outStream.close();
      }
    catch (Exception e)
      {
      throw new CSException("Unable to turn char[] into File: " + e.getMessage());
      }

    return(newFile);
    }
/**
* Turn a byte array into a Temporary File.  A null value for <code>theBytes</code>
* will be turned into a zero length file. java.io.File.createTempFile() is used to
* create what is supposed to be a unique file.
*
* @param byte[] theBytes The bytes you wish to be turned into a temporary File
* @param String tempFilePrefix The prefix for the temporary file's name
* @param String tempFileSuffix  The suffix for the temporary file's name
* @param String tempFileDir The directory for the temporary files.
* @param LogInterface A logging mechanism.
* @since JDBCWizard 4.0.2098
*/
public static java.io.File loadByteArrayIntoFile(byte[] theBytes, String tempFilePrefix, String tempFileSuffix, String tempFileDir, LogInterface theLog) throws CSException
  {
  java.io.File tempFile = null;

  try
    {
    tempFile = java.io.File.createTempFile(tempFilePrefix, tempFileSuffix, new File(tempFileDir));
    }
  catch (java.io.IOException e)
    {
    throw new CSException("IOUtils.loadByteArrayIntoFile:" + e.getMessage());
    }

  return(loadByteArrayIntoFile ( theBytes
                               , tempFile
                               , theLog));
  }
/**
* Turn a byte array into a File.  A null Array will be turned into a zero length file.
* @param byte[] theBytes
* @param File newFile
* @param LogInterface
* @since JDBCWizard 4.0.2108
*/
public static java.io.File loadByteArrayIntoFile(byte[] theBytes, java.io.File newFile, LogInterface theLog) throws CSException
  {

  try
    {
    java.io.FileOutputStream outStream = new java.io.FileOutputStream(newFile);

    if (theBytes != null && theBytes.length > 0)
      {
      outStream.write(theBytes,0,theBytes.length);
      }

    // Flush and close output stream
    outStream.flush();
    outStream.close();
    }
  catch (Exception e)
    {
    throw new CSException("Unable to turn byte[] into File: " + e.getMessage());
    }

  return(newFile);
  }

/**
* Loads a file into a char array. Note that this will not work with really big files.
* Non-existant or zero length files are returned as a zero length array. This routine
* has been tested with files up to 1MB in size.
* @param File inFile the file you want copied
* @return char[] a char array
* @since JDBCWizard 4.0.2108
*/
  public static char[] loadFileIntoCharArray(File inFile) throws CSException
    {
    char[] buff;

    if (inFile == null || (! inFile.exists()) || inFile.length() == 0)
      {
      // Return zero length array.
      buff = new char[0];
      return(buff);
      }

    try
      {
      // Note that a file can be Long.MAX_VALUE but that
      // our char array can only be Integer.MAX_VALUE in size
      buff = new char[(int)inFile.length()];
      }
    catch (Exception e)
      {
      throw new CSException("loadFileIntoCharArray: File " + inFile.getAbsolutePath()
                           + " is too big to be turned into a char array. Size is "
                           + inFile.length());
      }

    try
      {
      BufferedInputStream  source = new BufferedInputStream(new FileInputStream(inFile),IO_BUFFER_SIZE );
      java.io.Reader r = new java.io.InputStreamReader(source);
      r.read(buff);
      r.close();
      }
    catch(IOException error)
      {
      throw new CSException("loadFileIntoCharArray: Error while reading: " + error.getMessage());
      }

    return(buff);
  }

}





