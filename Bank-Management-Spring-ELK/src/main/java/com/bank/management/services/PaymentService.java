package com.bank.management.services;

import com.bank.management.domain.Card;
import com.bank.management.domain.Payment;
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

        } catch (IOException | ParseException e) {
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
        } catch (IOException e) {
            log.error("Error occurred in fetching payment details for id... => {}", e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
