package com.devprofileproject.devprofileaast.integration.github;

import java.time.Instant; //wa't
import java.util.Comparator; //tarteeb
import java.util.List; //list
import java.util.Map; //json data


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.devprofileproject.devprofileaast.exception.GitHubApiException;
import com.devprofileproject.devprofileaast.exception.GitHubErrorType;

//da elclass ely byklm el github api w ygeeb mno el data
@Service
public class GitHubClientService {
    private final RestClient restClient;
    private final RestClient graphqlClient;
    private final boolean hasToken;

       public GitHubClientService(
            @Value("${github.api.base-url:https://api.github.com}") String baseUrl,
            @Value("${github.api.token:}") String token) {

        this.hasToken = token != null && !token.isBlank(); //byshof fe tokens wla laa

        //bngahez elclient yklm github
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/vnd.github.v3+json");

        //lw feh token yhotha fe el header 3lshan el api limit        
        if (hasToken) {
            builder.defaultHeader("Authorization", "Bearer " + token);
        }

        this.restClient = builder.build();

        // bn3ml client tany 3lshan elgraph
        RestClient.Builder graphqlBuilder = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/json");

        if (hasToken) {
            graphqlBuilder.defaultHeader("Authorization", "Bearer " + token);
        }

        this.graphqlClient = graphqlBuilder.build();
    }

    //de method 3lshan tgeeb kol eldata bta3t eluser
    public GitHubProfileData fetchProfile(String username) {

        GitHubUserResponse user;
        try {
            user = restClient.get()
                    .uri("/users/{username}", username)//get user
                    .retrieve()//nfz eltalab
                    //lw eluser msh mawgood throw ex
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new GitHubApiException(
                                    "GitHub user '" + username + "' not found",
                                    GitHubErrorType.USER_NOT_FOUND);
                        }
                        throw new GitHubApiException(
                                "GitHub API client error: " + response.getStatusCode(),
                                GitHubErrorType.API_FAILURE);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new GitHubApiException(
                                "GitHub API is unavailable (status: " + response.getStatusCode() + ")",
                                GitHubErrorType.API_FAILURE);
                    })
                    .body(GitHubUserResponse.class);//hawel eljson l object 
        } catch (GitHubApiException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new GitHubApiException(
                    "Failed to connect to GitHub API", GitHubErrorType.API_FAILURE, ex);
        }

        if (user == null) {
            throw new GitHubApiException(
                    "GitHub API returned empty response for user '" + username + "'",
                    GitHubErrorType.API_FAILURE);
        }
        //get repos
        List<GitHubRepoResponse> repos;
        try {
            repos = restClient.get()
                    .uri("/users/{username}/repos?per_page=10&sort=updated", username)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new GitHubApiException(
                                "GitHub API client error fetching repos: " + response.getStatusCode(),
                                GitHubErrorType.API_FAILURE);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new GitHubApiException(
                                "GitHub API is unavailable (status: " + response.getStatusCode() + ")",
                                GitHubErrorType.API_FAILURE);
                    })
                    .body(new ParameterizedTypeReference<>() {});
        } catch (GitHubApiException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new GitHubApiException(
                    "Failed to connect to GitHub API while fetching repos",
                    GitHubErrorType.API_FAILURE, ex);
        }

        if (repos == null) {
            repos = List.of();
        }
        //bngeeb ahsn 3 repos
        List<GitHubProfileData.RepositoryData> top3 = repos.stream()
                .sorted(Comparator.comparing(GitHubRepoResponse::stargazers_count).reversed())
                .limit(3)
                .map(repo -> new GitHubProfileData.RepositoryData(
                        repo.name(),
                        repo.description(),
                        repo.language(),
                        repo.stargazers_count(),
                        repo.updated_at()))
                .toList();
        //hanhsb elstars
        int totalStars = repos.stream()
                .mapToInt(GitHubRepoResponse::stargazers_count)
                .sum();

        int contributions = fetchContributionsLastYear(username);

        return new GitHubProfileData(
                user.login(),
                user.public_repos(),
                totalStars,
                contributions,
                top3); //da ely byrag3 el object 3la elclass
    }

    @SuppressWarnings("unchecked")
    private int fetchContributionsLastYear(String username) {
        if (!hasToken) {
            return 0; // GraphQL API requires authentication
        }

        String query = "query($username: String!) { user(login: $username) { contributionsCollection { contributionCalendar { totalContributions } } } }";

        Map<String, Object> requestBody = Map.of(
                "query", query,
                "variables", Map.of("username", username)
        );

        try {
            Map<String, Object> response = graphqlClient.post()
                    .uri("/graphql")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null) return 0;

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null) return 0;

            Map<String, Object> user = (Map<String, Object>) data.get("user");
            if (user == null) return 0;

            Map<String, Object> collection = (Map<String, Object>) user.get("contributionsCollection");
            if (collection == null) return 0;

            Map<String, Object> calendar = (Map<String, Object>) collection.get("contributionCalendar");
            if (calendar == null) return 0;

            Object total = calendar.get("totalContributions");
            if (total instanceof Number) {
                return ((Number) total).intValue();
            }

            return 0;
        } catch (Exception ex) {
            // If GraphQL fails, return 0 rather than breaking the whole flow
            return 0;
        }
    }

    private record GitHubUserResponse(
            String login,
            Integer public_repos) {
    }

    private record GitHubRepoResponse(
            String name,
            String description,
            String language,
            Integer stargazers_count,
            Instant updated_at) {
    }

}
//b ekhtsaar ehna bngeeb user mn github w ba3dha ngeeb repos w nhsb el stars w contributins w top repos kol da f object f class el GitHubProfileData
