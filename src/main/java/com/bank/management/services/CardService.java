package com.bank.management.services;

import com.bank.management.domain.Card;
import com.bank.management.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final ObjectMapper mapper;

    public CardService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<Card> getAllCards() {

        log.info("Fetching ALL card details...");
        JSONArray cardDetails = new JSONArray();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("card.json"));
            JSONObject jsonObject = (JSONObject) obj;
            cardDetails = (JSONArray) jsonObject.get("data");

        } catch (IOException | ParseException e) {
            log.error("Error occurred in fetching all cards details... => {}", e.getMessage());
            e.printStackTrace();
        }
        return cardDetails;
    }

    public Optional<Card> getCardById(Long id) {
        log.info("Fetching card details for card id...");
        try {
            File file = new File("card.json");
            JsonNode rootNode = mapper.readTree(file);

            JsonNode dataArray = rootNode.path("data");
            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    if (node.get("id").asLong() == id) {
                        Card card = mapper.treeToValue(node, Card.class);
                        log.info("Card details found: {}", card);
                        return Optional.of(card);
                    }
                }
            }
            log.info("Card details not found with id {}", id);
            return Optional.empty();
        } catch (IOException e) {
            log.error("Error occurred in fetching card details for card id... => {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
