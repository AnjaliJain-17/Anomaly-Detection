package com.bank.management.services;

import com.bank.management.domain.Payment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final ObjectMapper mapper;

    public PaymentService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<Payment> getAllPayments() {

        log.info("Fetching ALL payment details...");
        JSONArray paymentDetails = new JSONArray();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("payment.json"));
            JSONObject jsonObject = (JSONObject) obj;
            paymentDetails = (JSONArray) jsonObject.get("data");
            log.info("Get all payment details response.... => {}", paymentDetails);

        } catch (Exception e) {
            log.error("Error occurred in fetching all payment details... => {}", e.getMessage());
            e.printStackTrace();
        }
        return paymentDetails;
    }

    public List<Payment> getAllPaymentsErrorResponse() {

        log.info("Fetching ALL payment details...");
        JSONArray paymentDetails = new JSONArray();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("payment1.json"));
            JSONObject jsonObject = (JSONObject) obj;
            paymentDetails = (JSONArray) jsonObject.get("data");
            log.info("Get all payment details response.... => {}", paymentDetails);

        } catch (Exception e) {
            log.error("Error occurred in fetching all payment details... => {}", e.getMessage());
            e.printStackTrace();
        }
        return paymentDetails;
    }

    public Optional<Payment> getPaymentById(Long id) {
        log.info("Fetching payment details by id...");
        try {
            File file = new File("payment.json");
            JsonNode rootNode = mapper.readTree(file);

            JsonNode dataArray = rootNode.path("data");
            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    if (node.get("id").asLong() == id) {
                        Payment payment = mapper.treeToValue(node, Payment.class);
                        log.info("Payment details found: {}", payment);
                        return Optional.of(payment);
                    }
                }
            }
            log.info("Payment details not found with id {}", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error occurred in fetching payment details for id... => {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Payment createPayment(Payment payment) {
        log.info("Inside create payment...");
        try {
            // Read the existing JSON data from the file into a JsonNode tree model
            JsonNode rootNode = mapper.readTree(new File("payment.json"));

            // Get the "data" array node from the tree
            ArrayNode dataArray = (ArrayNode) rootNode.get("data");

            // Convert the new user object to a JsonNode object
            JsonNode userNode = mapper.valueToTree(payment);

            // Append the new user object to the "data" array
            dataArray.add(userNode);

            // Write the updated tree back to the file
            mapper.writeValue(new File("payment.json"), rootNode);
            log.info("payment created successfully!");

        } catch (Exception e) {
            log.error("Error occurred while creating payment... => {}", e.getMessage());
            e.printStackTrace();
        }
        return payment;
    }

    public Payment updatePayment(Long id, Payment payment) {
        log.info("Inside update payment...");
        try {
            // Read user.json file
            JSONParser parser = new JSONParser();
            File file = new File("payment.json");
            Object obj = parser.parse(new FileReader(file));

            // Get the user object with the given ID and update it
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            for (Object o : jsonArray) {
                JSONObject paymentObj = (JSONObject) o;
                if ((long) paymentObj.get("id") == id) {
                    paymentObj.put("cardId", payment.getCardId());
                    paymentObj.put("amount", payment.getAmount());
                    paymentObj.put("currency", payment.getCurrency());
                    paymentObj.put("status", payment.getStatus());
                    paymentObj.put("timestamp", payment.getTimestamp());
                    break;
                }
            }

            // Write the updated user.json file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();
            log.info("Payment object with ID " + id + " has been updated successfully.");
        } catch (Exception e) {
            log.error("Error occurred while update Payment... => {}", e.getMessage());
            e.printStackTrace();
        }
        return payment;
    }

    public Object deletePayment(Long id)  {
        log.info("Inside delete payment...");
        try {
            // Read the contents of the user.json file
            FileReader fileReader = new FileReader("payment.json");
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");

            // Remove the user object with the specified ID
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject paymentObject = (JSONObject) jsonArray.get(i);
                int paymentId = ((Long) paymentObject.get("id")).intValue();
                if (paymentId == id) {
                    jsonArray.remove(i);
                    break;
                }
            }

            // Write the updated JSON data to the user.json file
            FileWriter fileWriter = new FileWriter("payment.json");
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();

            log.info("Payment Object with ID " + id + " deleted successfully.");
            return "paymentObject deleted successfully!";
        } catch (Exception e) {
            log.error("Error occurred while delete paymentObject... => {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



}
