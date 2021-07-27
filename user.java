package Assignment1;

public class user {

    private String userName;
    private String shadowHash;
    private String userId;
    private String groupId;
    private String comment;
    private String homeDirectory;
    private int clearanceLevel;
    private String shell = "/bin/bash";
    private String salt;
    
    public user(String userName, int clearanceLevel, String salt, String shadowHash, String userId, String groupId)
    {
        this.userName = userName;
        this.userId = userId;
        this.groupId = groupId;
        this.salt = salt;
        this.shadowHash = shadowHash;
        this.homeDirectory = "storage/home/"+userName+"/";
        this.clearanceLevel = clearanceLevel;
        this.comment = userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public void setShadowHash(String shadowHash)
    {
        this.shadowHash = shadowHash;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }
    
    public void setHomeDirectory()
    {
        this.homeDirectory = "storage/etc/"+userName+"/";
    }
    
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
    public void setShell(String shell)
    {
        this.shell  = shell;
    }
    
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
    
    public void setClearance(int clearanceLevel)
    {
        this.clearanceLevel = clearanceLevel;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public String getShadowHash()
    {
        return shadowHash;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public String getGroupId()
    {
        return groupId;
    }
    
    public String getComment()
    {
        return comment;
    }
    
    public String getHomeDirectory()
    {
        return homeDirectory;
    }
    
    public String getSalt()
    {
        return salt;
    }
    public int getClearance()
    {
        return clearanceLevel;
    }
    
    public String getShell()
    {
        return shell;
    }
    
    
    
}
