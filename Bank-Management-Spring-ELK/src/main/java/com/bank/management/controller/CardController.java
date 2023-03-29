package com.bank.management.controller;

import com.bank.management.domain.Card;
import com.bank.management.services.CardService;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("")
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/{id}")
    public Optional<Card> getCardById(@PathVariable Long id) {
        return cardService.getCardById(id);
    }

    @PostMapping("")
    public Card createCard(@RequestBody Card card) {
        return cardService.createCard(card);
    }

    @PutMapping("/{id}")
    public Card updateCard(@PathVariable Long id, @RequestBody Card card) {
        return cardService.updateCard(id, card);
    }

    @DeleteMapping("/{id}")
    public Object deleteCard(@PathVariable Long id) {
        return cardService.deleteCard(id);
    }

    @GetMapping("/error")
    public List<String> getError(@RequestParam int count, @RequestParam(required = false) String type) throws IOException, ParseException {
        return cardService.generateError(count,type);
    }
}
