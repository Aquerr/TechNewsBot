package pl.bartlomiejstepien.technewsbot.exception;

public class UnrecognizedURLException extends Exception
{
    public UnrecognizedURLException()
    {
        super("Unrecognized URL. This URL may not be supported or you provided wrong URL.");
    }

    public UnrecognizedURLException(String message)
    {
        super(message);
    }
}
