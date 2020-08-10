package flashcards;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Application {
    private final String importFilePath;
    private final String exportFilePath;
    private final Scanner scanner = new Scanner(System.in);
    private final Repository cardRepository = new Repository();
    private final List<String> strings = new ArrayList<>();

    public Application(String importFilePath, String exportFilePath) {
        this.importFilePath = importFilePath;
        this.exportFilePath = exportFilePath;
    }

    public void execute() {
        importFrom(importFilePath);
        boolean run = true;
        String menu = "Input the action " +
                "(add, remove, import, export, ask, exit, log, hardest card, reset stats):";
        do {
            output(menu);
            try {
                run = userMenu();
            } catch (FlashCardsException e) {
                output(e.getMessage());
            }
            System.out.println();
        } while (run);
        System.out.println("Bye bye!");
        export(exportFilePath);
    }

    private boolean userMenu() throws FlashCardsException {
        String input = input();
        switch (input) {
            case "add":
                add();
                break;
            case "remove":
                remove();
                break;
            case "ask":
                ask();
                break;
            case "export":
                output("File name:");
                String fileNameOut = input();
                export(fileNameOut);
                break;
            case "import":
                output("File name:");
                String fileNameIn = input();
                importFrom(fileNameIn);
                break;
            case "hardest card":
                hardest();
                break;
            case "reset stats":
                List<Card> cards = cardRepository.getAll();
                cards.forEach(card -> card.setMistakes(0));
                output("Card statistics has been reset.");
                break;
            case "log":
                log();
                break;
            case "exit":
                return false;
            default:
        }
        return true;
    }

    private void add() throws FlashCardsException {
        output("The card:");
        String term = input();
        if (cardRepository.containsByTerm(term)) {
            throw new FlashCardsException("The card \"" + term + "\" already exists.");
        }
        System.out.println("The definition of the card:");
        String definition = input();
        if (cardRepository.containsByDefinition(definition)) {
            throw new FlashCardsException("The definition \"" + definition + "\" already exists.");
        }
        cardRepository.add(new Card(term, definition));
        output("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
    }

    private void remove() throws FlashCardsException {
        output("The card:");
        cardRepository.remove(input());
        output("The card has been removed.");
    }

    private void ask() {
        output("How many times to ask?");
        int number = Integer.parseInt(input());
        for (int i = 0; i < number; i++) {
            Card card = cardRepository.getRandom();
            output("Print the definition of \"" + card.getTerm() + "\":");
            String definition = input();
            boolean correct = card.checkAnswer(definition);
            String output = correct ? "Correct!" :
                    "Wrong. The right answer is \"" + card.getDefinition() + "\"" +
                            (cardRepository.containsByDefinition(definition) ?
                                    "but your definition is correct for \"" +
                                            cardRepository.getByDefinition(definition).getTerm() + "\"." :
                                    ".");
            output(output);
        }
    }

    private void export(String fileName) {
        if (fileName == null) {
            return;
        }
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(cardRepository.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        output(cardRepository.size() + " cards have been saved.");
    }

    private void importFrom(String fileName) throws FlashCardsException {
        if (fileName == null) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(fileName));
        } catch (IOException e) {
            throw new FlashCardsException("File not found.");
        }
        lines.stream().map(l -> l.trim().split("\\s+-\\s+"))
                .map(words -> {
                    Card card = new Card(words[0], words[1]);
                    card.setMistakes(Integer.parseInt(words[2]));
                    return card;
                })
                .forEach(cardRepository::addOrUpdate);
        output(lines.size() + " cards have been loaded.");
    }

    private void hardest() {
        List<Card> cards = cardRepository.getHardestCards();
        String cardsString = cards.stream()
                .map(Card::getTerm)
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(", "));
        output("The hardest card is " + cardsString +
                ". You have " + cards.get(0).getMistakes() + " errors answering it.");
    }

    private void log() {
        output("File name:");
        String fileName = input();
        try (FileWriter writer = new FileWriter(fileName)) {
            strings.forEach(s -> {
                try {
                    writer.write(s + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        output("The log has been saved.");
    }

    private void output(String out) {
        strings.add(out);
        System.out.println(out);
    }

    private String input() {
        String in = scanner.nextLine();
        strings.add(in);
        return in;
    }

}
