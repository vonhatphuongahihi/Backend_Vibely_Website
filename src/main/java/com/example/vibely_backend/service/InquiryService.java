package com.example.vibely_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.dto.response.InquiryResponse;
import com.example.vibely_backend.entity.Inquiry;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.InquiryRepository;
import com.example.vibely_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ApiResponse createInquiry(String userId, String message) {
        if (message == null || message.isBlank()) {
            return new ApiResponse("error", "Vui lòng điền đầy đủ thông tin.", null);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Inquiry inquiry = new Inquiry();
        inquiry.setUserId(user.getId());
        inquiry.setMessage(message);
        inquiry.setStatus("Chưa phản hồi");
        inquiry.setResponse("");

        inquiryRepository.save(inquiry);

        return new ApiResponse("success", "Thắc mắc đã được gửi!", inquiry);
    }

    public ApiResponse getInquiries(String query, String status) {
        List<Inquiry> all = inquiryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<InquiryResponse> result = new ArrayList<>();

        for (Inquiry i : all) {
            User user = userRepository.findById(i.getUserId()).orElse(null);
            if (user == null)
                continue;

            boolean matchStatus = (status == null || status.isBlank() || i.getStatus().equalsIgnoreCase(status));

            boolean matchQuery = true;
            if (query != null && !query.isBlank()) {
                String lowerQuery = query.toLowerCase();
                boolean inMessage = i.getMessage().toLowerCase().contains(lowerQuery);
                boolean inUser = user.getUsername().toLowerCase().contains(lowerQuery) ||
                        user.getEmail().toLowerCase().contains(lowerQuery);
                matchQuery = inMessage || inUser;
            }

            if (matchStatus && matchQuery) {
                InquiryResponse inq = new InquiryResponse();
                inq.setId(i.getId());
                inq.setMessage(i.getMessage());
                inq.setStatus(i.getStatus());
                inq.setResponse(i.getResponse());
                inq.setCreatedAt(i.getCreatedAt().toString());
                inq.setUpdatedAt(i.getUpdatedAt().toString());

                inq.setUserId(user.getId());
                inq.setUsername(user.getUsername());
                inq.setEmail(user.getEmail());

                result.add(inq);
            }
        }

        return new ApiResponse("success", "Lấy danh sách thắc mắc thành công!", result);
    }

    public ApiResponse updateInquiry(String id, String status, String response) {
        if (status == null || status.isBlank() || response == null || response.isBlank()) {
            return new ApiResponse("error", "Vui lòng điền đầy đủ thông tin.", null);
        }

        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thắc mắc."));

        inquiry.setStatus(status);
        inquiry.setResponse(response);
        inquiryRepository.save(inquiry);

        User user = userRepository.findById(inquiry.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        emailService.sendInquiryResponseEmail(
                user.getEmail(),
                inquiry.getResponse(),
                user.getUsername());

        InquiryResponse inq = new InquiryResponse();
        inq.setId(inquiry.getId());
        inq.setMessage(inquiry.getMessage());
        inq.setStatus(inquiry.getStatus());
        inq.setResponse(inquiry.getResponse());
        inq.setCreatedAt(inquiry.getCreatedAt().toString());
        inq.setUpdatedAt(inquiry.getUpdatedAt().toString());

        inq.setUserId(user.getId());
        inq.setUsername(user.getUsername());
        inq.setEmail(user.getEmail());

        return new ApiResponse("success", "Cập nhật thành công và đã gửi email phản hồi.", inq);
    }

    public ApiResponse deleteInquiry(String id) {
        Inquiry deleted = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thắc mắc."));

        inquiryRepository.deleteById(id);
        return new ApiResponse("success", "Xoá thắc mắc thành công", deleted);
    }
}
