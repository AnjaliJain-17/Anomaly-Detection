package com.bank.management.controller;

import com.bank.management.domain.Card;
import com.bank.management.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
//
//    @PostMapping("")
//    public Card createCard(@RequestBody Card card) {
//        return cardService.createCard(card);
//    }
//
//    @PutMapping("/{id}")
//    public Card updateCard(@PathVariable Long id, @RequestBody Card card) {
//        return cardService.updateCard(id, card);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteCard(@PathVariable Long id) {
//        cardService.deleteCard(id);
//    }
}
