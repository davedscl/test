package ch.epfl.gsn.http.ac;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 14, 2010
 * Time: 1:51:47 PM
 * To change this template use File | Settings | File Templates.
 */

/* This class helps to use access control general functionalities without using the Web application user interface */
public class GeneralServicesAPI
{
    private static GeneralServicesAPI singleton = new GeneralServicesAPI();
    private static transient Logger logger= LoggerFactory.getLogger( GeneralServicesAPI.class );

    public static GeneralServicesAPI getInstance()
    {
        return singleton;
    }
    /* this method return an object User if it succeeds to doLogin and null otherwise */
    public User doLogin(String username, String password)
    {
        User user= null;
        ConnectToDB ctdb = null;
        try
        {
            ctdb = new ConnectToDB();
            if(ctdb.valueExistsForThisColumnUnderOneCondition(new Column("USERNAME",username),new Column("ISCANDIDATE","no"),"ACUSER")==true)
            {
                String enc= Protector.encrypt(password);
                if((ctdb.isPasswordCorrectForThisUser(username,enc)== false))
                {
                   //throws exception
                    logger.warn("WARN IN DOLOGIN : incorect password!");

                }
                else
                {
                    user = new User(username,enc,ctdb.getDataSourceListForUserLogin(username),ctdb.getGroupListForUser(username));
                }
            }
            else
            {
               //throws exception
                logger.warn("WARN IN DOLOGIN : this username does not exist!");
            }
        }
        catch(Exception e)
        {
            logger.error("ERROR IN DOLOGIN");
			logger.error(e.getMessage(),e);
        }
        finally
        {
            if(ctdb!=null)
            {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
        return user;
    }
     public void doLogout(User user)
    {
        if(user != null)
        {
            user=null;
        }

    }
    /* given the name of the group, this method returns the list of all DataSources in the group */
    public Vector getGroupCombination(String groupname)
    {
        Vector v = new Vector();
        ConnectToDB ctdb= null;
        try
        {
            ctdb = new ConnectToDB();
            v= ctdb.getDataSourceListForGroup(groupname);
        }
         catch(Exception e)
        {

            logger.error("ERROR IN GETGROUPCOMBINATION");
			logger.error(e.getMessage(),e);
        }
        finally
        {
            if(ctdb!=null)
            {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
        return v;
    }

    /* return the list of all virtual sensors in GSN virtual sensor pool */
    public Vector getListOfAllVirtualSensors()
    {
        Vector v = new Vector();
        ConnectToDB ctdb= null;
        try
        {
            ctdb = new ConnectToDB();
            v= ctdb.getDataSourceList();
        }
         catch(Exception e)
        {

            logger.error("ERROR IN GETLISTOFALLVIRTUALSENSORS");
			logger.error(e.getMessage(),e);
        }
        finally
        {
            if(ctdb!=null)
            {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
        return v;

    }
    /* return the list of all groups */
    public Vector getListOfAllGroups()
    {
        Vector v = new Vector();
        ConnectToDB ctdb= null;
        try
        {
            ctdb = new ConnectToDB();
            v= ctdb.getGroupList();
        }
         catch(Exception e)
        {
           
            logger.error("ERROR IN GETLISTOFALLGROUPS");
			logger.error(e.getMessage(),e);
        }
        finally
        {
            if(ctdb!=null)
            {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
        return v;

    }

}
