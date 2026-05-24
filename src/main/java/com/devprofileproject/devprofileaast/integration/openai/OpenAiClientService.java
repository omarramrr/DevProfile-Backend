package com.devprofileproject.devprofileaast.integration.openai;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.devprofileproject.devprofileaast.exception.AiAnalysisException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAiClientService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClientService.class);

    private final RestClient restClient;
    private final String model;
    private final ObjectMapper objectMapper;

    public OpenAiClientService(
            @Qualifier("openAiRestClient") RestClient restClient,
            @Value("${openai.api.model:gpt-4o}") String model,
            ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.model = model;
        this.objectMapper = objectMapper;
    }

    public OpenAiAnalysisResponseData analyze(OpenAiAnalysisRequest request) {

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(request);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.3
        );

        String responseContent;
        try {
            JsonNode response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || !response.has("choices") || response.get("choices").isEmpty()) {
                throw new AiAnalysisException("OpenAI returned an empty response");
            }

            responseContent = response.get("choices").get(0).get("message").get("content").asText();

        } catch (AiAnalysisException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("OpenAI API call failed", ex);
            throw new AiAnalysisException("Failed to communicate with AI service", ex);
        }

        try {
            OpenAiAnalysisResponseData data = objectMapper.readValue(responseContent,
                    OpenAiAnalysisResponseData.class);
            validateResponse(data);
            return data;
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse OpenAI response: {}", responseContent, ex);
            throw new AiAnalysisException("Failed to parse AI response", ex);
        }
    }

    private void validateResponse(OpenAiAnalysisResponseData data) {
        if (data.overallScore() < 0 || data.overallScore() > 100 ||
            data.codeQualityScore() < 0 || data.codeQualityScore() > 100 ||
            data.complexityScore() < 0 || data.complexityScore() > 100 ||
            data.activityScore() < 0 || data.activityScore() > 100 ||
            data.resumeScore() < 0 || data.resumeScore() > 100 ||
            data.techAlignScore() < 0 || data.techAlignScore() > 100) {
            throw new AiAnalysisException("AI returned scores outside valid range (0-100)");
        }

        if (data.recruiterPerspective() == null || data.recruiterPerspective().isBlank()) {
            throw new AiAnalysisException("AI did not return a recruiter perspective");
        }

        if (data.strengths() == null || data.strengths().isEmpty()) {
            throw new AiAnalysisException("AI did not return strengths");
        }

        if (data.weaknesses() == null || data.weaknesses().isEmpty()) {
            throw new AiAnalysisException("AI did not return weaknesses");
        }

        if (data.roadmap() == null || data.roadmap().weeks() == null || data.roadmap().weeks().isEmpty()) {
            throw new AiAnalysisException("AI did not return a roadmap");
        }
    }

    private String buildSystemPrompt() {
        return """
                You are a senior technical recruiter and developer evaluator for DevProfile, \
                an AI-powered developer profile analyzer.

                You will receive a developer's GitHub profile data and resume text. \
                Your job is to evaluate them and return a structured JSON response.

                You MUST return ONLY valid JSON with this exact structure:
                {
                    "overallScore": <0-100 integer>,
                    "codeQualityScore": <0-100 integer>,
                    "complexityScore": <0-100 integer>,
                    "activityScore": <0-100 integer>,
                    "resumeScore": <0-100 integer>,
                    "techAlignScore": <0-100 integer>,
                    "recruiterPerspective": "<3-4 sentence paragraph from a recruiter's point of view>",
                    "strengths": ["<strength 1>", "<strength 2>", "<strength 3>", "<strength 4>", "<strength 5>"],
                    "weaknesses": ["<weakness 1>", "<weakness 2>", "<weakness 3>", "<weakness 4>", "<weakness 5>"],
                    "roadmap": {
                        "summary": "<1-2 sentence overview of the improvement plan>",
                        "weeks": [
                            {
                                "weekNumber": 1,
                                "theme": "<focus area for this week>",
                                "technicalTasks": ["<task 1>", "<task 2>", "<task 3>"],
                                "measurableOutcomes": ["<outcome 1>", "<outcome 2>"],
                                "technologies": ["<tech 1>", "<tech 2>"],
                                "projectIdea": "<specific project suggestion using their actual repos>"
                            }
                        ]
                    }
                }

                Scoring criteria:
                - codeQualityScore: Clean code, patterns, naming, structure visible in repos
                - complexityScore: Project difficulty, architecture decisions, technology diversity
                - activityScore: Commit frequency, recent contributions, repo freshness
                - resumeScore: ATS optimization, quantifiable achievements, clear structure
                - techAlignScore: Modern tech stack, in-demand skills, market relevance
                - overallScore: Weighted average of all dimensions

                The roadmap must have exactly 4 weeks. Each week should reference the developer's \
                actual repositories and weaknesses. Tasks should be specific and actionable, not generic.

                Return 3-5 strengths and 3-5 weaknesses. Be honest but constructive.
                """;
    }

    private String buildUserPrompt(OpenAiAnalysisRequest request) {
        StringBuilder sb = new StringBuilder();

        if (request.techField() != null && request.careerGoal() != null) {
            sb.append("## Developer Context\n");
            sb.append("Tech Field: ").append(request.techField()).append("\n");
            sb.append("Career Goal: ").append(request.careerGoal()).append("\n\n");
            sb.append("Adjust your evaluation: weight technical skills relevant to ")
                    .append(request.techField()).append(" development higher, ");
            sb.append("set expectations appropriate for a ").append(request.careerGoal())
                    .append(" candidate, ");
            sb.append("focus strengths/weaknesses on what matters for this role, ");
            sb.append("and generate roadmap tasks that help them land a ")
                    .append(request.techField()).append(" ").append(request.careerGoal())
                    .append(".\n\n");
        }

        sb.append("## GitHub Profile\n");
        sb.append("Username: ").append(request.githubUsername()).append("\n");
        sb.append("Total public repos: ").append(request.totalRepos()).append("\n");
        sb.append("Total stars: ").append(request.totalStars()).append("\n");
        sb.append("Contributions last year: ").append(request.contributionsLastYear()).append("\n\n");

        sb.append("## Top Repositories\n");
        for (OpenAiAnalysisRequest.RepositorySummary repo : request.topRepositories()) {
            sb.append("- **").append(repo.name()).append("**");
            if (repo.primaryLanguage() != null) {
                sb.append(" (").append(repo.primaryLanguage()).append(")");
            }
            sb.append(" - ").append(repo.stars()).append(" stars");
            if (repo.description() != null) {
                sb.append(" | ").append(repo.description());
            }
            sb.append("\n");
        }

        sb.append("\n## Resume\n");
        if (request.resumeText() != null && !request.resumeText().isBlank()) {
            sb.append(request.resumeText());
        } else {
            sb.append("No resume provided.");
        }

        sb.append("\n\nPlease evaluate this developer and return the JSON analysis.");

        return sb.toString();
    }
}