package com.example.vibely_backend.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.example.vibely_backend.repository.DocumentRepository;
import com.example.vibely_backend.repository.InquiryRepository;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final DocumentRepository documentRepository;
    private final InquiryRepository inquiryRepository;
    private final MongoTemplate mongoTemplate;

    public Map<String, Object> getAdminDashboard(String timeUnit) {
        Map<String, Object> dashboardData = new HashMap<>();

        dashboardData.put("totalUsers", getTotalUsers());
        dashboardData.put("totalPosts", getTotalPosts());
        dashboardData.put("totalDocuments", getTotalDocuments());
        dashboardData.put("totalInquiries", getTotalInquiries());

        dashboardData.put("usersData", groupByTime("user", timeUnit));
        dashboardData.put("postsData", groupByTime("post", timeUnit));
        dashboardData.put("documentsData", groupByTime("document", timeUnit));
        dashboardData.put("inquiriesData", groupByTime("inquiry", timeUnit));

        return dashboardData;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalPosts() {
        return postRepository.count();
    }

    public long getTotalDocuments() {
        return documentRepository.count();
    }

    public long getTotalInquiries() {
        return inquiryRepository.count();
    }

    public Map<String, Object> getDashboardStats(String timeUnit) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("usersStats", groupByTime("user", timeUnit));
        stats.put("postsStats", groupByTime("post", timeUnit));
        stats.put("documentsStats", groupByTime("document", timeUnit));
        stats.put("inquiriesStats", groupByTime("inquiry", timeUnit));
        return stats;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> groupByTime(String collectionName, String timeUnit) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();

        log.info("Lấy dữ liệu {} từ ngày {}", collectionName, startDate);

        String isoStartDate = startDate.toInstant().toString();

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("createdAt").gte(isoStartDate));

        GroupOperation groupOperation;
        if (timeUnit.equals("day")) {
            groupOperation = Aggregation.group(
                    DateOperators.Year.yearOf("createdAt").toString(),
                    DateOperators.Month.monthOf("createdAt").toString(),
                    DateOperators.DayOfMonth.dayOfMonth("createdAt").toString())
                    .count().as("count");
        } else if (timeUnit.equals("month")) {
            groupOperation = Aggregation.group(
                    DateOperators.Year.yearOf("createdAt").toString(),
                    DateOperators.Month.monthOf("createdAt").toString())
                    .count().as("count");
        } else {
            groupOperation = Aggregation.group(
                    DateOperators.Year.yearOf("createdAt").toString())
                    .count().as("count");
        }

        SortOperation sortOperation = Aggregation.sort(
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Order.asc("_id.year"),
                        org.springframework.data.domain.Sort.Order.asc("_id.month"),
                        org.springframework.data.domain.Sort.Order.asc("_id.day")));

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                sortOperation);

        log.info("Aggregation pipeline: {}", aggregation);

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, collectionName, Map.class);
        List<Map<String, Object>> data = (List<Map<String, Object>>) (List<?>) results.getMappedResults();

        log.info("Kết quả {}: {}", collectionName, data);
        return data;
    }
}
