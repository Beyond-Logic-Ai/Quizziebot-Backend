package com.quizzka.backend.service.impl;

import com.quizzka.backend.service.OtpService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {
    @Override
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public void sendOtp(String mobileNumber, String otp) {
        // Integrate with an SMS provider to send the OTP
        // twilio = 689JZSQKL6QNN637KWYHLQSH
        System.out.println("Sending OTP " + otp + " to mobile number " + mobileNumber);

    }
}
