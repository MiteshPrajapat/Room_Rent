package com.aiuservices.aiservice.service;


import com.aiuservices.aiservice.model.Activity;
import com.aiuservices.aiservice.model.Recommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor

public class ActivityAiService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt =  createPromptForActivity(activity);
        String airesponse = geminiService.getRecommendatation(prompt);

        return processAIResponse(activity, airesponse);
    }

    private Recommendation processAIResponse(Activity activity, String airesponse) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(airesponse);
            JsonNode textNode = rootNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();


            log.info("Response from AI: {}", jsonContent);
            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode , "overall", "Overall");
            addAnalysisSection(fullAnalysis, analysisNode , "pace", "Pace");
            addAnalysisSection(fullAnalysis, analysisNode , "heartRate", "Heart Rate");
            addAnalysisSection(fullAnalysis, analysisNode , "caloriesBurned", "Calories Burned");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractsuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyguidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type( activity.getType().toString())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        catch(Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);


        }
       

    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type( activity.getType().toString())
                .recommendation("unable to generate recommendation at this time")
                .improvements(Collections.singletonList("No improvements"))
                .suggestions( Collections.singletonList("No suggestions"))
                .safety(Arrays.asList("No safety guidelines"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyguidelines(JsonNode safety) {
        List<String> safetyList = new ArrayList<>();
        if(safety.isArray()){
            for(JsonNode safetyPoint : safety){
                safetyList.add(safetyPoint.asText());
            }
        }
        return safetyList.isEmpty()? Collections.singletonList("No safety guidelines") : safetyList;
    }

    private List<String> extractsuggestions(JsonNode suggestions) {
        List<String> suggestionList = new ArrayList<>();
        if(suggestions.isArray()){
            for(JsonNode suggestion : suggestions){
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestionList.add(workout + ": " + description);
            }
        }
        return suggestionList.isEmpty()? Collections.singletonList("No suggestions") : suggestionList;
    }

    private List<String> extractImprovements(JsonNode improvements) {
        List<String> improvementList = new ArrayList<>();
        if(improvements.isArray()){
            for(JsonNode improvement : improvements){
                String area = improvement.path("area").asText();
                String recommendation = improvement.path("recommendation").asText();
                improvementList.add(area + ": " + recommendation);
            }
        }
        return improvementList.isEmpty()? Collections.singletonList("No improments") : improvementList;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix).append(" Analysis: ").append(analysisNode.path(key).asText()).append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return  String.format("""
                Analyes this fritness activity and provide detailed recommnendation in the formate of json
                {
                  "analysis": {
                    "overall": "Overall analysis here",
                    "pace": "Pace analysis here",
                    "heartRate": "Heart rate analysis here",
                    "caloriesBurned": "Calories analysis here"
                  },
                  "improvements": [
                        {
                          "area": "Area name",
                          "recommendation": "Detailed recommendation"
                        }
                      ],
                      "suggestions": [
                        {
                          "workout": "Workout name",
                          "description": "Detailed workout description"
                        }
                      ],
                      "safety": [
                        "Safety point 1",
                        "Safety point 2"
                        ]
                        }
                        Analyze this activity:
                        Activity Type: %s
                        Duration: %d minutes
                        Calories Burned: %d
                        Additional Metrics: %s
                
                        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                        Ensure the response follows the EXACT JSON format shown above.
                   
                """,activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionMetics()
        );
    }

}
