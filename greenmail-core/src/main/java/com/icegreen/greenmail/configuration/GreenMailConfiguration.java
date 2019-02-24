package com.icegreen.greenmail.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties that can be defined to configure a GreenMail instance or GreenMailRule.
 */
public class GreenMailConfiguration {
    private final List<UserBean> usersToCreate = new ArrayList<>();
    private boolean disableAuthenticationCheck = false;
    private boolean enablePurgeScheduler = false;
    private Long purgeInterval = 0L;

    /**
     * The given {@link com.icegreen.greenmail.user.GreenMailUser} will be created when servers will start.
     *
     * @param login User id and email address
     * @param password Password of user that belongs to login name
     * @return Modified configuration
     */
    public GreenMailConfiguration withUser(final String login, final String password) {
        return withUser(login, login, password);
    }

    /**
     * The given {@link com.icegreen.greenmail.user.GreenMailUser} will be created when servers will start.
     *
     * @param email Email address
     * @param login Login name of user
     * @param password Password of user that belongs to login name
     * @return Modified configuration
     */
    public GreenMailConfiguration withUser(final String email, final String login, final String password) {
        this.usersToCreate.add(new UserBean(email, login, password));
        return this;
    }

    /**
     * @return New GreenMail configuration
     */
    public static GreenMailConfiguration aConfig() {
        return new GreenMailConfiguration();
    }

    /**
     * @return Users that should be created on server startup
     */
    public List<UserBean> getUsersToCreate() {
        return usersToCreate;
    }
    
    /**
     * @return purgeInterval for purgeJob
     */
    public Long getPurgeInterval() {
        return purgeInterval;
    }

    /**
     * Disables authentication.
     *
     * Useful if you want to avoid setting up users up front.
     *
     * @return Modified configuration.
     */
    public GreenMailConfiguration withDisabledAuthentication() {
        disableAuthenticationCheck = true;
        return this;
    }

    /**
     * @return true, if authentication is disabled.
     *
     * @see GreenMailConfiguration#withDisabledAuthentication()
     */
    public boolean isAuthenticationDisabled() {
        return disableAuthenticationCheck;
    }

    /**
     * @return true, if purgeScheduler is enabled.
     *
     * @see GreenMailConfiguration#withPurgeScheduler()
     */
    public boolean isPurgeSchedulerEnabled() {
		return enablePurgeScheduler;
	}

    /**
     * Enables periodic purge at interval SECONDS.
     * @return Modified configuration.
     */
    public GreenMailConfiguration withPurgeScheduler(Long interval) {
		purgeInterval = interval;
		enablePurgeScheduler = true;
		return this;
	}
}
