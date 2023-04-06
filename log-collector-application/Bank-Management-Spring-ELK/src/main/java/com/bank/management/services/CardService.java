package com.bank.management.services;

import com.bank.management.domain.Card;
import com.bank.management.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
            log.info("Get all cards details response.... => {}", cardDetails);
        } catch (Exception e) {
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
        } catch (Exception e) {
            log.error("Error occurred in fetching card details for card id... => {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Card createCard(Card card) {
        log.info("Inside create card...");
        try {
            // Read the existing JSON data from the file into a JsonNode tree model
            JsonNode rootNode = mapper.readTree(new File("card.json"));

            // Get the "data" array node from the tree
            ArrayNode dataArray = (ArrayNode) rootNode.get("data");

            // Convert the new user object to a JsonNode object
            JsonNode userNode = mapper.valueToTree(card);

            // Append the new user object to the "data" array
            dataArray.add(userNode);

            // Write the updated tree back to the file
            mapper.writeValue(new File("card.json"), rootNode);
            log.info("card created successfully!");

        } catch (Exception e) {
            log.error("Error occurred while creating card... => {}", e.getMessage());
            e.printStackTrace();
        }
        return card;
    }

    public Card updateCard(Long id, Card card) {
        log.info("Inside update card...");
        try {
            // Read user.json file
            JSONParser parser = new JSONParser();
            File file = new File("card.json");
            Object obj = parser.parse(new FileReader(file));

            // Get the user object with the given ID and update it
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            for (Object o : jsonArray) {
                JSONObject cardObj = (JSONObject) o;
                if ((long) cardObj.get("id") == id) {
                    cardObj.put("cardNumber", card.getCardNumber());
                    cardObj.put("expirationDate", card.getExpirationDate());
                    cardObj.put("cardHolderName", card.getCardHolderName());
                    cardObj.put("userId", card.getUserId());
                    break;
                }
            }

            // Write the updated user.json file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();
            log.info("Card object with ID " + id + " has been updated successfully.");
        } catch (Exception e) {
            log.error("Error occurred while update Card... => {}", e.getMessage());
            e.printStackTrace();
        }
        return card;
    }

    public Object deleteCard(Long id)  {
        log.info("Inside delete card...");
        try {
            // Read the contents of the user.json file
            FileReader fileReader = new FileReader("card.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            // Remove the user object with the specified ID
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject cardObject = (JSONObject) jsonArray.get(i);
                int cardId = ((Long) cardObject.get("id")).intValue();
                if (cardId == id) {
                    jsonArray.remove(i);
                    break;
                }
            }

            // Write the updated JSON data to the user.json file
            FileWriter fileWriter = new FileWriter("card.json");
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();

            log.info("Card with ID " + id + " deleted successfully.");
            return "card deleted successfully!";
        } catch (Exception e) {
            log.error("Error occurred while delete card... => {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
