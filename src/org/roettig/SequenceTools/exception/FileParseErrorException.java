/**
 * 
 */
package org.roettig.SequenceTools.exception;

/**
 * @author roettig
 *
 */
public class FileParseErrorException extends Exception
{

    private static final long serialVersionUID = 3104488867109298334L;

    public FileParseErrorException()
    {
	super();
    }

    public FileParseErrorException(String arg0)
    {
	super(arg0);
    }

    public FileParseErrorException(Throwable arg0)
    {
	super(arg0);
    }

    public FileParseErrorException(String arg0, Throwable arg1)
    {
	super(arg0, arg1);	
    }

}
