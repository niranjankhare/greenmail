package com.icegreen.greenmail.configuration;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Creates GreenMailConfiguration from properties.
 * <p/>
 * Example usage:
 * GreenMailConfiguration config = new PropertyBasedGreenMailConfigurationBuilder().build(System.getProperties());
 * <ul>
 * <li>greenmail.users :  List of comma separated users of format login:pwd[@domain][,login:pwd[@domain]]
 * <p>Example: user1:pwd1@localhost,user2:pwd2@0.0.0.0</p>
 * <p>Note: domain part must be DNS resolvable!</p>
 * </li>
 * </ul>
 */
public class PropertiesBasedGreenMailConfigurationBuilder {
    /**
     * Property for list of users.
     */
    public static final String GREENMAIL_USERS = "greenmail.users";
    /**
     * Disables authentication check.
     *
     * @see GreenMailConfiguration#withDisabledAuthentication()
     */
    public static final String GREENMAIL_AUTH_DISABLED = "greenmail.auth.disabled";

    /**
     * Property for periodic purge interval. 0 is disabled.
     * Used with GREENMAIL_PURGE_INTERVAL_UNIT
     * 
     */
    public static final String GREENMAIL_PURGE_INTERVAL = "greenmail.purge.interval";
    /**
     * Property for periodic purge interval TimeUnit.
     * Used with GREENMAIL_PURGE_INTERVAL
     * 
     */
    public static final String GREENMAIL_PURGE_UNIT = "greenmail.purge.unit";
    
    /**
     * Builds a configuration object based on given properties.
     *
     * @param properties the properties.
     * @return a configuration and never null.
     */
    public GreenMailConfiguration build(Properties properties) {
        GreenMailConfiguration configuration = new GreenMailConfiguration();
        String usersParam = properties.getProperty(GREENMAIL_USERS);
        if (null != usersParam) {
            String[] usersArray = usersParam.split(",");
            for (String user : usersArray) {
                extractAndAddUser(configuration, user);
            }
        }
        String disabledAuthentication = properties.getProperty(GREENMAIL_AUTH_DISABLED);
        if (null != disabledAuthentication) {
            configuration.withDisabledAuthentication();
        }
        String interval = properties.getProperty(GREENMAIL_PURGE_INTERVAL);
        if (null != interval) {
        	configurePurge(configuration, interval,
        			properties.getProperty(GREENMAIL_PURGE_UNIT));
        }
        return configuration;
    }

    protected void extractAndAddUser(GreenMailConfiguration configuration, String user) {
        // login:pwd@domain
        String[] userParts = user.split(":|@");
        switch (userParts.length) {
            case 2:
                configuration.withUser(userParts[0], userParts[1]);
                break;
            case 3:
                configuration.withUser(userParts[0] + '@' + userParts[2], userParts[0], userParts[1]);
                break;
            default:
                throw new IllegalArgumentException("Expected format login:pwd[@domain] but got " + user
                        + " parsed to " + Arrays.toString(userParts));
        }
    }
    
    protected void configurePurge (GreenMailConfiguration configuration, 
    		String purgeInterval, String unit) {
    	Long interval = 0L;
    	try {
    		interval = Long.parseLong(purgeInterval);
    	} catch (Exception e) {
    		throw new IllegalArgumentException("Expected PURGE_INTERVAL as number but got " + purgeInterval);
    	}
    	TimeUnit purgeUnit = null;
    	for (TimeUnit t: TimeUnit.values()) {
    		if (t.name().equalsIgnoreCase(unit)){
    			purgeUnit = TimeUnit.valueOf(unit.toUpperCase());
    			break;
    		}
    	}
    	if (purgeUnit == null) {
    		throw new IllegalArgumentException("Expected PURGE_UNIT one of: "
    	+ TimeUnit.values()+" but got " + unit);
    	}
    	configuration.withPurgeScheduler(TimeUnit.SECONDS.convert(interval, purgeUnit));
    }
}
