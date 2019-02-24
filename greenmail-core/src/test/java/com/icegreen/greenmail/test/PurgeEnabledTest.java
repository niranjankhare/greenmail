package com.icegreen.greenmail.test;

import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.Retriever;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PurgeEnabledTest {
    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(new ServerSetup[]{ServerSetupTest.SMTP, ServerSetupTest.IMAP})
            .withConfiguration(GreenMailConfiguration.aConfig().withPurgeScheduler(10L));

    @Test
    public void testPurgeJob() throws MessagingException, IOException, InterruptedException {
        GreenMailUtil.sendTextEmailTest("to1@domain1.com", "from@localhost", "subject1", "body");
        GreenMailUtil.sendTextEmailTest("to1@domain1.com", "from@localhost", "subject2", "body");
        GreenMailUtil.sendTextEmailTest("to2@domain1.com", "from@localhost", "subject1", "body");
        GreenMailUtil.sendTextEmailTest("to2@domain1.com", "from@localhost", "subject2", "body");
        GreenMailUtil.sendTextEmailTest("to1@domain2.com", "from@localhost", "subject1", "body");
        GreenMailUtil.sendTextEmailTest("to1@domain2.com", "from@localhost", "subject2", "body");
        GreenMailUtil.sendTextEmailTest("to2@domain2.com", "from@localhost", "subject1", "body");
        GreenMailUtil.sendTextEmailTest("to2@domain2.com", "from@localhost", "subject2", "body");
        
        greenMail.waitForIncomingEmail(8);
        MimeMessage[] domain1Msgs = greenMail.getReceivedMessagesForDomain("domain1.com");
        MimeMessage[] domain2Msgs = greenMail.getReceivedMessagesForDomain("domain2.com");
        assertEquals(4, domain1Msgs.length);
        assertEquals(4, domain2Msgs.length);

        Thread.sleep(10*1000);
		
        domain1Msgs = greenMail.getReceivedMessagesForDomain("domain1.com");
        domain2Msgs = greenMail.getReceivedMessagesForDomain("domain2.com");
        
        assertEquals(0, domain1Msgs.length);
        assertEquals(0, domain2Msgs.length);
    }

    @Test
    public void testReceiveWithAuthDisabledAndProvisionedUser() {
        final String to = "to@localhost";
        greenMail.setUser(to,"to","secret");

        greenMail.waitForIncomingEmail(500, 1);

        try (Retriever retriever = new Retriever(greenMail.getImap())) {
            Message[] messages = retriever.getMessages(to);
            assertEquals(0, messages.length);
        }
    }
}
