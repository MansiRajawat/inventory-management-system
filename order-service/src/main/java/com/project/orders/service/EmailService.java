package com.project.orders.service;

import com.project.orders.model.OrderDetailsResponse;
import com.project.orders.model.OrderResponse;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    public void sendOrderConfirmationEmail (String toEmail, OrderResponse response) throws IOException {


        String subject = "Order Confirmation - Thank you for your purchase!";

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<h2>Dear ").append(response.getCustomerName()).append(",</h2>");
        htmlContent.append("<p>Thank you for your order. Here are the details:</p>");
        htmlContent.append("<table style='width:100%; border-collapse: collapse;' border='1'>")
                .append("<tr style='background-color: #f2f2f2;'>")
                .append("<th>Order ID</th><th>Order Name</th><th>Price</th><th>Quantity</th></tr>");

        for (OrderDetailsResponse detail : response.getOrderDetailsResponses()) {
            htmlContent.append("<tr>")
                    .append("<td>").append(detail.getOrderId()).append("</td>")
                    .append("<td>").append(detail.getOrderName()).append("</td>")
                    .append("<td>").append(detail.getPrice()).append("</td>")
                    .append("<td>").append(detail.getQuantity()).append("</td>")
                    .append("</tr>");
        }

        htmlContent.append("</table>");
        htmlContent.append("<br/><p><strong>Total Orders:</strong> ")
                .append(response.getOrderDetailsResponses().size())
                .append("</p>");
        htmlContent.append("<p>We hope to serve you again soon!</p>");
        htmlContent.append("<p>Best regards,<br/>Your Store Team</p>");

        sendEmailViaSendGrid(toEmail, subject, htmlContent.toString());


    }
    private void sendEmailViaSendGrid(String to, String subject, String htmlContent) throws IOException {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        int statusCode = response.getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            logger.info("Email sent successfully to {}", to);
        } else {
            logger.error("Failed to send email to {}: StatusCode={}, Body={}", to, statusCode, response.getBody());
        }
    }
}
