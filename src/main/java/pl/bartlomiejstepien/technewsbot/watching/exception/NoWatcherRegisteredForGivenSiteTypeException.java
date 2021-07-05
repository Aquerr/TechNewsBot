package pl.bartlomiejstepien.technewsbot.watching.exception;

public class NoWatcherRegisteredForGivenSiteTypeException extends Exception
{
    public NoWatcherRegisteredForGivenSiteTypeException(String message)
    {
        super(message);
    }
}
