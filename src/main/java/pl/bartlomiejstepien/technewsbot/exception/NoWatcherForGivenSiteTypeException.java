package pl.bartlomiejstepien.technewsbot.exception;

public class NoWatcherForGivenSiteTypeException extends Exception
{
    public NoWatcherForGivenSiteTypeException(String message)
    {
        super(message);
    }
}
