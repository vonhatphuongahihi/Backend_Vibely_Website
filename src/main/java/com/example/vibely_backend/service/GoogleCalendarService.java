package com.example.vibely_backend.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.example.vibely_backend.entity.Schedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Vibely Calendar Integration";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(
            CalendarScopes.CALENDAR,
            CalendarScopes.CALENDAR_EVENTS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${backend.url}")
    private String backendUrl;

    private Calendar getCalendarService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        // Tạo GoogleClientSecrets từ client ID và secret đã cấu hình
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        web.setAuthUri("https://accounts.google.com/o/oauth2/auth");
        web.setTokenUri("https://oauth2.googleapis.com/token");
        clientSecrets.setWeb(web);

        // Tạo thư mục tokens nếu chưa tồn tại
        java.io.File tokensDir = new java.io.File(TOKENS_DIRECTORY_PATH);

        if (!tokensDir.exists()) {
            boolean created = tokensDir.mkdirs();
            if (created) {
            } else {
                log.error("Không thể tạo thư mục tokens");
                throw new RuntimeException("Không thể tạo thư mục tokens. Vui lòng kiểm tra quyền truy cập.");
            }
        } else {
            // Kiểm tra nội dung thư mục tokens
            File[] files = tokensDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    log.info("File trong thư mục tokens: {}", file.getName());
                }
            }
        }

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(tokensDir))
                .setAccessType("offline")
                .build();

        // Sử dụng redirect URI từ frontend
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .setCallbackPath("/oauth2callback")
                .build();

        try {
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            return credential;
        } catch (Exception e) {
            log.error("Lỗi xác thực Google Calendar: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xác thực với Google Calendar. Vui lòng thử lại.", e);
        }
    }

    public Event createGoogleCalendarEvent(Schedule schedule) {
        try {
            Calendar service = getCalendarService();

            Event event = new Event()
                    .setSummary(schedule.getSubject());

            // Format thời gian theo chuẩn RFC3339 với timezone
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

            // Trừ 5 tiếng và format lại
            String startTimeStr = schedule.getStartTime()
                    .minusHours(5)
                    .atZone(zoneId)
                    .format(formatter);
            String endTimeStr = schedule.getEndTime()
                    .minusHours(5)
                    .atZone(zoneId)
                    .format(formatter);

            DateTime startDateTime = new DateTime(startTimeStr);
            DateTime endDateTime = new DateTime(endTimeStr);
            // Set timezone cho event
            event.setStart(new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Ho_Chi_Minh"));
            event.setEnd(new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Ho_Chi_Minh"));

            // Chuyển đổi mã màu hex sang colorId của Google Calendar
            String colorId = convertHexToGoogleColorId(schedule.getCategoryColor());
            if (colorId != null) {
                event.setColorId(colorId);
            }

            Event createdEvent = service.events().insert("primary", event).execute();

            return createdEvent;
        } catch (Exception e) {
            log.error("Lỗi khi tạo sự kiện Google Calendar: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể tạo sự kiện trên Google Calendar. Vui lòng kiểm tra quyền truy cập.",
                    e);
        }
    }

    private String convertHexToGoogleColorId(String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            return null;
        }
        // Bỏ dấu # nếu có
        hexColor = hexColor.replace("#", "");
        // Map các mã màu hex sang colorId của Google Calendar
        switch (hexColor.toUpperCase()) {
            case "0000FF": // Blue
                return "1";
            case "00FF00": // Green
                return "2";
            case "FF0000": // Red
                return "3";
            case "FFFF00": // Yellow
                return "4";
            case "FF00FF": // Purple
                return "5";
            case "00FFFF": // Cyan
                return "6";
            case "FF8000": // Orange
                return "7";
            case "800080": // Purple
                return "8";
            case "008000": // Green
                return "9";
            case "800000": // Red
                return "10";
            case "000080": // Blue
                return "11";
            default:
                log.warn("Mã màu không hợp lệ: {}, sử dụng màu mặc định", hexColor);
                return "1"; // Mặc định là màu xanh dương
        }
    }

    public void deleteGoogleCalendarEvent(String eventId) {
        try {
            Calendar service = getCalendarService();
            service.events().delete("primary", eventId).execute();
        } catch (Exception e) {
            log.error("Lỗi khi xóa sự kiện Google Calendar: {}", e.getMessage());
            throw new RuntimeException("Không thể xóa sự kiện trên Google Calendar. Vui lòng kiểm tra quyền truy cập.",
                    e);
        }
    }

    public Event updateGoogleCalendarEvent(String eventId, Schedule schedule) {
        try {
            Calendar service = getCalendarService();
            Event event = service.events().get("primary", eventId).execute();

            event.setSummary(schedule.getSubject())
                    .setColorId(schedule.getCategoryColor());

            // Format thời gian theo chuẩn RFC3339 với timezone
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

            // Trừ 5 tiếng và format lại
            String startTimeStr = schedule.getStartTime()
                    .minusHours(5)
                    .atZone(zoneId)
                    .format(formatter);
            String endTimeStr = schedule.getEndTime()
                    .minusHours(5)
                    .atZone(zoneId)
                    .format(formatter);

            DateTime startDateTime = new DateTime(startTimeStr);
            DateTime endDateTime = new DateTime(endTimeStr);

            // Set timezone cho event
            event.setStart(new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Ho_Chi_Minh"));
            event.setEnd(new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Ho_Chi_Minh"));

            return service.events().update("primary", eventId, event).execute();
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật sự kiện Google Calendar: {}", e.getMessage());
            throw new RuntimeException(
                    "Không thể cập nhật sự kiện trên Google Calendar. Vui lòng kiểm tra quyền truy cập.", e);
        }
    }

    public String buildAuthUrl() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        web.setAuthUri("https://accounts.google.com/o/oauth2/auth");
        web.setTokenUri("https://oauth2.googleapis.com/token");
        clientSecrets.setWeb(web);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        // Sử dụng backend URL từ cấu hình
        String redirectUri = backendUrl + "/google-calendar/oauth2callback";
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                .build();
    }

    public void handleAuthCallback(String code) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        web.setAuthUri("https://accounts.google.com/o/oauth2/auth");
        web.setTokenUri("https://oauth2.googleapis.com/token");
        clientSecrets.setWeb(web);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .build();

        // Sử dụng backend URL từ cấu hình
        String redirectUri = backendUrl + "/google-calendar/oauth2callback";
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        flow.createAndStoreCredential(tokenResponse, "user");
    }
}