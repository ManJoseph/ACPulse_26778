package com.acpulse.service;

import com.acpulse.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApprovalEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to ACPulse! Your Account is Approved ✅");
        message.setText(
                "Dear " + user.getName() + ",\n\n" +
                        "Thank you for registering with ACPulse!\n\n" +
                        "Your registration request has been validated and approved.\n\n" +
                        "Login Credentials:\n" +
                        "- Email: " + user.getEmail() + "\n" +
                        "- Password: (the password you created during registration)\n\n" +
                        "You can now login to the system.\n\n" +
                        "Best regards,\n" +
                        "ACPulse Team\n" +
                        "Adventist University of Central Africa"
        );

        mailSender.send(message);
    }

    public void sendRejectionEmail(User user, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("ACPulse Registration - Action Required");
        message.setText(
                "Dear " + user.getName() + ",\n\n" +
                        "Thank you for your interest in ACPulse.\n\n" +
                        "Unfortunately, we could not approve your registration at this time.\n\n" +
                        "Reason: " + reason + "\n\n" +
                        "Please contact the admin office for assistance or re-submit your " +
                        "registration with the correct information.\n\n" +
                        "Contact: admin@auca.ac.rw\n\n" +
                        "Best regards,\n" +
                        "ACPulse Team\n" +
                        "Adventist University of Central Africa"
        );

        mailSender.send(message);
    }

    public void sendRoomExpiryNotification(User lecturer, String roomNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(lecturer.getEmail());
        message.setSubject("Room Occupation Expired - ACPulse");
        message.setText(
                "Dear " + lecturer.getName() + ",\n\n" +
                        "Your occupation of Room " + roomNumber + " has expired.\n\n" +
                        "Please extend your occupation if still needed or release the room.\n\n" +
                        "Best regards,\n" +
                        "ACPulse System"
        );

        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Login OTP Code");
        message.setText("Your OTP is: " + otp + " (expires in 5 minutes)");
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(User user, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yourgmail@gmail.com"); // Match sender with config
        message.setTo(user.getEmail());
        message.setSubject("ACPulse - Password Reset Request Approved");
        
        // TODO: Replace localhost with the actual frontend URL from application properties
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;

        message.setText(
                "Dear " + user.getName() + ",\n\n" +
                "Your request to reset your password has been approved.\n\n" +
                "Please click the link below to set a new password:\n" +
                resetUrl + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request a password reset, please ignore this email or contact support.\n\n" +
                "Best regards,\n" +
                "The ACPulse Team"
        );
        mailSender.send(message);
    }
}
