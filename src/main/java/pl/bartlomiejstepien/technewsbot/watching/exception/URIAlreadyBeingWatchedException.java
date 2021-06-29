package pl.bartlomiejstepien.technewsbot.watching.exception;

public class URIAlreadyBeingWatchedException extends Exception
{
    public URIAlreadyBeingWatchedException(String message)
    {
        super(message);
    }
}
