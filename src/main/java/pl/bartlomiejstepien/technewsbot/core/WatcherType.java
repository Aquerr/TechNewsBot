package pl.bartlomiejstepien.technewsbot.core;

public enum WatcherType
{
    GITHUB("github"),
    RSS("rss");

    private String name;

    WatcherType(String name)
        {
            this.name = name;
        }

    public String getName()
        {
            return name;
        }
}
