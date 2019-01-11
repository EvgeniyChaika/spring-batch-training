package com.chaika.batch.configuration.flow.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Created by echaika on 26.12.2018
 */
public class CustomJobListener implements JobExecutionListener {

    private JavaMailSender javaMailSender;

    public CustomJobListener(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        SimpleMailMessage mail = getSimpleMailMessage(
                String.format("%s is starting", jobName),
                String.format("Per your request, we are informing you that %s is starting", jobName));

        javaMailSender.send(mail);
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        SimpleMailMessage mail = getSimpleMailMessage(
                String.format("%s has completed", jobName),
                String.format("Per your request, we are informing you that %s has completed", jobName));

        javaMailSender.send(mail);
    }

    private SimpleMailMessage getSimpleMailMessage(String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo("your_mail@gmail.com");
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        return mailMessage;
    }
}
