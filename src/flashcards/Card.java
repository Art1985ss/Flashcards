package flashcards;

import java.util.Objects;

public class Card {
    private String term;
    private String definition;
    private int mistakes = 0;

    public Card() {
    }

    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public boolean checkAnswer(String answer) {
        boolean correct = definition.equals(answer);
        if (!correct) {
            mistakes++;
        }
        return correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return term.equals(card.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term);
    }

    @Override
    public String toString() {
        return term + " - " + definition + " - " + mistakes;
    }
}
