package com._p1m.portfolio.common.component;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpStore {

    private final Map<String, OtpEntry> otpCache = new ConcurrentHashMap<>();

    public void saveOtp(String email, String otp, long ttlMinutes) {
        Instant expiry = Instant.now().plusSeconds(ttlMinutes * 60);
        otpCache.put(email, new OtpEntry(otp, expiry));
    }

    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpCache.get(email);
        if (entry == null) {
            return false;
        }
        if (Instant.now().isAfter(entry.getExpiry())) {
            otpCache.remove(email); // expired
            return false;
        }
        boolean valid = entry.getCode().equals(otp);
        if (valid) {
            otpCache.remove(email); // OTP should be one-time use
        }
        return valid;
    }

    private static class OtpEntry {
        private final String code;
        private final Instant expiry;

        public OtpEntry(String code, Instant expiry) {
            this.code = code;
            this.expiry = expiry;
        }

        public String getCode() {
            return code;
        }

        public Instant getExpiry() {
            return expiry;
        }
    }
}
