package com.example.module3_playingcards;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardGameController {

    @FXML
    private TextField answerText;

    @FXML
    private ImageView card1,card2,card3,card4;

    @FXML
    private Text hintText;

    @FXML
    private Button hintButton,refreshButton,verifyButton;

    //Store card value
    private final List<Integer> currentCardValues = new ArrayList<>();

    private final Map<String, Integer> cardValueMap = new HashMap<>();

    private final List<String> cardImages = new ArrayList<>();

    @FXML
    public void initialize() {
        loadCardImages();
        generateRandomCards();
        refreshButton.setOnAction(event -> refreshCards());
        verifyButton.setOnAction(event -> verifyExpression());
        hintButton.setOnAction(event -> generateHint());

    }

    private void loadCardImages() {
        String[] suits = {"spades", "hearts", "diamonds", "clubs"};
        // numbers 2-10
        for (int i = 2; i <= 10; i++) {
            for (String suit : suits) {
                String fileName = i + "_of_" + suit + ".png";
                cardValueMap.put(fileName, i);
                cardImages.add(fileName);
            }
        }
        //face cards (Jack = 11, Queen = 12, King = 13, Ace = 1)
        String[] faces = {"jack", "queen", "king", "ace"};
        int[] values = {11, 12, 13, 1}; // Corresponding values

        for (int i = 0; i < faces.length; i++) {
            for (String suit : suits) {
                String fileName = faces[i] + "_of_" + suit + ".png";
                cardValueMap.put(fileName, values[i]);
                cardImages.add(fileName);
            }
        }
    }

    private void generateRandomCards() {
        if (cardImages.isEmpty()) {
            System.out.println("No cards found");
            return;
        }
        List<String> shuffledCards = new ArrayList<>(cardImages);
        Collections.shuffle(shuffledCards);

        card1.setImage(loadCardImage(shuffledCards.get(0)));
        card2.setImage(loadCardImage(shuffledCards.get(1)));
        card3.setImage(loadCardImage(shuffledCards.get(2)));
        card4.setImage(loadCardImage(shuffledCards.get(3)));

        currentCardValues.clear();
        currentCardValues.add(cardValueMap.get(shuffledCards.get(0)));
        currentCardValues.add(cardValueMap.get(shuffledCards.get(1)));
        currentCardValues.add(cardValueMap.get(shuffledCards.get(2)));
        currentCardValues.add(cardValueMap.get(shuffledCards.get(3)));

    }

    private Image loadCardImage(String imageName) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/PlayingCards/" + imageName)));
    }

    //refresh the window
    private void refreshCards() {
        generateRandomCards();
        answerText.clear();
        hintText.setText("");
    }


    //---------------------------------------------------------------------//


    private void verifyExpression() {
        String expression = answerText.getText().trim();
        if (expression.isEmpty()) {
            showAlert("Error", "Please enter an arithmetic expression.");
            return;
        }

        // Extract numbers from the expression
        List<Integer> numbersInExpression = extractNumbers(expression);

        // Check if all four numbers are used exactly once
        List<Integer> sortedCardValues = new ArrayList<>(currentCardValues);
        List<Integer> sortedExpressionValues = new ArrayList<>(numbersInExpression);
        Collections.sort(sortedCardValues);
        Collections.sort(sortedExpressionValues);

        if (!sortedCardValues.equals(sortedExpressionValues)) {
            showAlert("Invalid Expression", "Your expression must use exactly these four numbers: " + currentCardValues);
            return;
        }

        // Evaluate the expression and check if it equals 24
        double result = evaluateExpression(expression);

        if (Double.isNaN(result)) {
            showAlert("Error", "Invalid mathematical expression.");
            return;
        }

        if (Math.abs(result - 24) < 0.001) { // Allow small floating-point errors
            showAlert("Success", "Correct! Your expression evaluates to 24.");
        } else {
            showAlert("Incorrect", "Your expression evaluates to " + result + ". Try again.");
        }
    }

    //shows me that the numbers the user is using are the cards that were generated
    private List<Integer> extractNumbers(String expression) {
        List<Integer> numbers = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+").matcher(expression);

        while (matcher.find()) {
            int extracted = Integer.parseInt(matcher.group());
            numbers.add(extracted);
        }

        return numbers;
    }


    private double evaluateExpression(String expression) {
        return new ExpressionParser(expression).parse();
    }

    class ExpressionParser {
        private final String expression;
        private int index = 0;

        public ExpressionParser(String expression) {
            this.expression = expression.replaceAll("\\s", ""); // Remove spaces
        }

        public double parse() {
            double result = parseExpression();
            if (index < expression.length())
                throw new IllegalArgumentException("Unexpected character at end of expression.");
            return result;
        }

        private double parseExpression() {
            double result = parseTerm();
            while (index < expression.length()) {
                char op = expression.charAt(index);
                if (op == '+' || op == '-') {
                    index++;
                    double nextTerm = parseTerm();
                    if (op == '+') result += nextTerm;
                    else result -= nextTerm;
                } else break;
            }
            return result;
        }

        private double parseTerm() {
            double result = parseFactor();
            while (index < expression.length()) {
                char op = expression.charAt(index);
                if (op == '*' || op == '/') {
                    index++;
                    double nextFactor = parseFactor();
                    if (op == '*') result *= nextFactor;
                    else result /= nextFactor;
                } else break;
            }
            return result;
        }

        private double parseFactor() {
            if (index >= expression.length()) throw new IllegalArgumentException("Unexpected end of expression.");

            char ch = expression.charAt(index);
            if (ch == '(') {
                index++;
                double result = parseExpression();
                if (index >= expression.length() || expression.charAt(index) != ')')
                    throw new IllegalArgumentException("Mismatched parentheses.");
                index++;
                return result;
            }

            int start = index;
            while (index < expression.length() && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
                index++;
            }

            if (start == index) throw new IllegalArgumentException("Unexpected character: " + expression.charAt(index));
            return Double.parseDouble(expression.substring(start, index));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    //Hint

    private void generateHint() {
        if (currentCardValues.isEmpty()) {
            hintText.setText("No cards found");
            return;
        }

        String hint = solveFor24(currentCardValues);

        hintText.setText(hint);
    }

    private String solveFor24(List<Integer> numbers) {
        int[] numArray = {numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3)};
        String[] operators = {"+", "-", "*", "/"};

        // Try every possible combination of numbers and operators
        for (int a : numArray) {
            for (int b : numArray) {
                if (b == a) continue;
                for (int c : numArray) {
                    if (c == a || c == b) continue;
                    for (int d : numArray) {
                        if (d == a || d == b || d == c) continue;
                        for (String op1 : operators) {
                            for (String op2 : operators) {
                                for (String op3 : operators) {
                                    String expression1 = "(" + a + op1 + b + ")" + op2 + "(" + c + op3 + d + ")";
                                    String expression2 = "((" + a + op1 + b + ")" + op2 + c + ")" + op3 + d;
                                    String expression3 = "(" + a + op1 + "(" + b + op2 + c + "))" + op3 + d;

                                    if (evaluateExpression(expression1) == 24) return expression1;
                                    if (evaluateExpression(expression2) == 24) return expression2;
                                    if (evaluateExpression(expression3) == 24) return expression3;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null; // No solution found
    }

}
