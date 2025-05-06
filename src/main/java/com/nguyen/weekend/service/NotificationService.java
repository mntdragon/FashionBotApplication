package com.nguyen.weekend.service;

import com.nguyen.weekend.config.MailProperties;
import com.nguyen.weekend.error.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private static final DecimalFormat DF = new DecimalFormat("#.00");


    /**
     * Sends a price drop alert email.
     *
     * @param productUrl the URL of the Zalando product
     * @param oldPrice   previous price
     * @param newPrice   current price
     * @param pct        percentage drop
     */
    public void sendPriceDropAlert(String productUrl, double oldPrice, double newPrice, double pct) {
        try {
            var msg = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, false, "UTF-8");
            helper.setTo(mailProperties.getNotification().getSendToEmail());
            helper.setSubject("Price drop detected!");
            helper.setText(String.format(
                    "Price dropped for %s from €%s to €%s (%.1f%%).\nSee: %s",
                    productUrl,
                    DF.format(oldPrice),
                    DF.format(newPrice),
                    pct,
                    productUrl
            ));
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Send price drop alert failed.", e);
            throw new NotificationException("Send price drop alert failed.");
        }
    }

    public void sendErrorAlert(String errorMessage) {
        try {
            var msg = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, false, "UTF-8");
            helper.setTo(mailProperties.getNotification().getSendToEmail());
            helper.setSubject("PriceWatch Bot ERROR");
            helper.setText(errorMessage);
            mailSender.send(msg);
            return; // success
        } catch (Exception ex) {
            log.error("Something went wrong", ex);
        }
    }
}
