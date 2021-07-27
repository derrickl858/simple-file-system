package Assignment1;

public class Filelevel {
    
    private String fileName;
    private int level;
    
    public Filelevel()
    {
        fileName = null;
        level = 0;
    }
    
    public Filelevel(String fileName, int level)
    {
        this.fileName = fileName;
        this.level = level;
    }
    
    public void setFileName (String fileName)
    {
        this.fileName = fileName;
    }
    
    public void setLevel(int level)
    {
        this.level = level;
    }
    
    public String getFileName ()
    {
        return fileName;
    }
    
    public int getLevel()
    {
        return level;
    }
    

}
