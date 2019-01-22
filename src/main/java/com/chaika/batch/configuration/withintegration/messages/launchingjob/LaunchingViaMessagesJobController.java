package com.chaika.batch.configuration.withintegration.messages.launchingjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by echaika on 22.01.2019
 */
@RestController
public class LaunchingViaMessagesJobController {

    private final MessageChannel requests;
    private final DirectChannel replies;
    private final Job launchingViaMessagesJob;

    @Autowired
    public LaunchingViaMessagesJobController(@Qualifier("requestsLaunchingViaMessagesJob") MessageChannel requests,
                                             @Qualifier("repliesLaunchingViaMessagesJob") DirectChannel replies,
                                             @Qualifier("launchingViaMessagesJob") Job launchingViaMessagesJob) {
        this.requests = requests;
        this.replies = replies;
        this.launchingViaMessagesJob = launchingViaMessagesJob;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void launch(@RequestParam("name") String name) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", name)
                .addDate("date", new Date())
                .toJobParameters();
        JobLaunchRequest jobLaunchRequest = new JobLaunchRequest(launchingViaMessagesJob, jobParameters);

        replies.subscribe(new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                JobExecution payload = (JobExecution) message.getPayload();

                System.out.println(">> " + payload.getJobInstance().getJobName() + " resulted in " + payload.getStatus());
            }
        });

        requests.send(MessageBuilder.withPayload(jobLaunchRequest).setReplyChannel(replies).build());
    }
}

