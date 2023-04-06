package com.bank.management.controller;

import com.bank.management.domain.Card;
import com.bank.management.services.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cards")
public class CardController {

    private static final Logger log = LoggerFactory.getLogger(CardController.class);


    @Autowired
    private CardService cardService;

    @GetMapping("")
    public List<Card> getAllCards() {
        log.info("Inside getAllCards Controller");
        return cardService.getAllCards();
    }

    @GetMapping("/{id}")
    public Optional<Card> getCardById(@PathVariable Long id) {
        log.info("Inside getCardById Controller");
        return cardService.getCardById(id);
    }

    @PostMapping("")
    public Card createCard(@RequestBody Card card) {
        log.info("Inside createCard Controller");
        return cardService.createCard(card);
    }

    @PutMapping("/{id}")
    public Card updateCard(@PathVariable Long id, @RequestBody Card card) {
        log.info("Inside updateCard Controller");
        return cardService.updateCard(id, card);
    }

    @DeleteMapping("/{id}")
    public Object deleteCard(@PathVariable Long id) {
        log.info("Inside deleteCard Controller");
        return cardService.deleteCard(id);
    }
}
