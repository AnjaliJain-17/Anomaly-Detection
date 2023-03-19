//package com.bank.management.controller;
//
//@RestController
//@RequestMapping("/cards")
//public class CardController {
//
//    @Autowired
//    private CardService cardService;
//
//    @GetMapping("")
//    public List<Card> getAllCards() {
//        return cardService.getAllCards();
//    }
//
//    @GetMapping("/{id}")
//    public Card getCardById(@PathVariable Long id) {
//        return cardService.getCardById(id);
//    }
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
//}
