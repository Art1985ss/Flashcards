package flashcards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Repository {
    List<Card> list = new ArrayList<>();

    public void add(Card card) {
        if (card == null) {
            return;
        }
        list.add(card);
    }

    public void addOrUpdate(Card card) {
        try {
            Card card1 = this.getByTerm(card.getTerm());
            card1.setDefinition(card.getDefinition());
            card1.setMistakes(card.getMistakes());
        } catch (FlashCardsException e) {
            list.add(card);
        }

    }

    public Card get(int index) {
        return list.get(index);
    }

    public List<Card> getAll() {
        return list;
    }

    public Card getRandom() {
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    public List<Card> getHardestCards() throws FlashCardsException {
        int mistakes = list.stream().mapToInt(Card::getMistakes).max()
                .orElseThrow(()-> new FlashCardsException("There are no cards with errors."));
        if (mistakes < 1) {
            throw new FlashCardsException("There are no cards with errors.");
        }
        return list.stream().filter(card -> card.getMistakes() == mistakes).collect(Collectors.toList());
    }

    public void remove(String term) throws FlashCardsException {
        Card card;
        try {
            card = this.getByTerm(term);
        } catch (FlashCardsException e) {
            throw new FlashCardsException("Can't remove \"" + term + "\": there is no such card.");
        }
        list.remove(card);
    }

    public int size() {
        return list.size();
    }

    public Card getByDefinition(String definition) throws FlashCardsException {
        return list.stream()
                .filter(card -> card.getDefinition().equals(definition))
                .findAny()
                .orElseThrow(() -> new FlashCardsException("No card with definition " + definition));
    }

    public Card getByTerm(String term) throws FlashCardsException {
        return list.stream()
                .filter(card -> card.getTerm().equals(term))
                .findAny()
                .orElseThrow(() -> new FlashCardsException("No card with term " + term));
    }

    public boolean containsByDefinition(String definition) {
        return list.stream().anyMatch(card -> card.getDefinition().equals(definition));
    }

    public boolean containsByTerm(String term) {
        return list.stream().anyMatch(card -> card.getTerm().equals(term));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        list.forEach(card -> sb.append(card).append("\n"));
        return sb.toString();
    }
}
