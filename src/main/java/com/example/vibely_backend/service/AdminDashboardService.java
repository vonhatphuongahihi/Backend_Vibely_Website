package com.example.vibely_backend.service;

import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.example.vibely_backend.repository.*;
import com.example.vibely_backend.entity.*;

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

        dashboardData.put("usersData", groupByTime(User.class, timeUnit));
        dashboardData.put("postsData", groupByTime(Post.class, timeUnit));
        dashboardData.put("documentsData", groupByTime(DocumentUser.class, timeUnit));
        dashboardData.put("inquiriesData", groupByTime(Inquiry.class, timeUnit));

        return dashboardData;
    }

    public Map<String, Object> getDashboardStats(String timeUnit) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("usersStats", groupByTime(User.class, timeUnit));
        stats.put("postsStats", groupByTime(Post.class, timeUnit));
        stats.put("documentsStats", groupByTime(DocumentUser.class, timeUnit));
        stats.put("inquiriesStats", groupByTime(Inquiry.class, timeUnit));

        return stats;
    }

    private List<Map<String, Object>> groupByTime(Class<?> entityClass, String timeUnit) {
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        Date startDate = Date.from(startOfYear.atZone(ZoneId.systemDefault()).toInstant());

        MatchOperation matchStage = Aggregation.match(
                Criteria.where("createdAt").gte(startDate));

        ProjectionOperation projectStage;
        GroupOperation groupStage;
        SortOperation sortStage;

        switch (timeUnit.toLowerCase()) {
            case "day":
                projectStage = Aggregation.project()
                        .and(DateOperators.Year.yearOf("createdAt")).as("year")
                        .and(DateOperators.Month.monthOf("createdAt")).as("month")
                        .and(DateOperators.DayOfMonth.dayOfMonth("createdAt")).as("day");

                groupStage = Aggregation.group("year", "month", "day")
                        .count().as("count");

                sortStage = Aggregation.sort(Sort.Direction.ASC, "_id.year", "_id.month", "_id.day");
                break;

            case "month":
                projectStage = Aggregation.project()
                        .and(DateOperators.Year.yearOf("createdAt")).as("year")
                        .and(DateOperators.Month.monthOf("createdAt")).as("month");

                groupStage = Aggregation.group("year", "month")
                        .count().as("count");

                sortStage = Aggregation.sort(Sort.Direction.ASC, "_id.year", "_id.month");
                break;

            default: // year
                projectStage = Aggregation.project()
                        .and(DateOperators.Year.yearOf("createdAt")).as("year");

                groupStage = Aggregation.group("year")
                        .count().as("count");

                sortStage = Aggregation.sort(Sort.Direction.ASC, "_id.year");
                break;
        }

        String collectionName = getCollectionName(entityClass);

        Aggregation aggregation = Aggregation.newAggregation(
                matchStage, projectStage, groupStage, sortStage);

        AggregationResults<Map> results = mongoTemplate.aggregate(
                aggregation, collectionName, Map.class);

        List<Map<String, Object>> mappedResults = new ArrayList<>();
        for (Map map : results.getMappedResults()) {
            mappedResults.add((Map<String, Object>) map);
        }
        return mappedResults;
    }

    private String getCollectionName(Class<?> entityClass) {
        if (entityClass == User.class)
            return "users";
        if (entityClass == Post.class)
            return "posts";
        if (entityClass == DocumentUser.class)
            return "documents";
        if (entityClass == Inquiry.class)
            return "inquiries";
        throw new IllegalArgumentException("Unknown entity class: " + entityClass.getSimpleName());
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
}
