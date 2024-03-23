package com.shopme.customer;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final CustomerService customerService;
    private final SettingService settingService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.generateResetPasswordToken(email);
            String link = Utility.getSiteUrl(request) + "/reset_password?token=" + token;
            sendEmail(link, email);
            model.addAttribute("message", "We have sent a reset password link to your email.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Could not send email");
        }
        return "customer/forgot_password_form";
    }

    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = email;
        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        try {
            Customer customer = customerService.getByResetPasswordToken(token);
            model.addAttribute("token", token);
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", e.getMessage());
            return "message";
        }
        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        try {
            customerService.resetPassword(token, password);

            model.addAttribute("pageTitle", "Reset Your Password");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "You have successfully changed your password.");
            return "message";
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", e.getMessage());
            return "message";
        }
    }
}
