import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class HealthTransition extends JFrame {

    private static final String[] states = {"Normal", "Pre-Diabetic", "Diabetic"};
    private static final double[][] baseTransitionMatrix = {
            {0.80, 0.15, 0.05},
            {0.10, 0.70,0.20},
            {0.05, 0.10,0.85}
    };

    private JTextField nameField;
    private JTextField ageField;
    private JComboBox<String> eatingHabitsCombo;
    private JComboBox<String> symptomsCombo;
    private JTextArea resultArea;

    public HealthTransition() {
        setTitle("Health Condition Transition Model");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Age:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Eating Habits:"));
        String[] eatingHabitsOptions = {"Healthy", "Moderate", "Unhealthy"};
        eatingHabitsCombo = new JComboBox<>(eatingHabitsOptions);
        add(eatingHabitsCombo);

        add(new JLabel("Symptoms:"));
        String[] symptomsOptions = {"None", "Mild", "Severe"};
        symptomsCombo = new JComboBox<>(symptomsOptions);
        add(symptomsCombo);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea));

        JButton simulateButton = new JButton("Run Simulation");
        simulateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSimulation();
            }
        });
        add(simulateButton);

        setVisible(true);
    }

    private double[][] adjustTransitionMatrix(int age, String eatingHabits, String symptoms) {
        double[][] transitionMatrix = new double[3][3];

        for (int i = 0; i < baseTransitionMatrix.length; i++) {
            transitionMatrix[i] = baseTransitionMatrix[i].clone();
        }

        if (age < 30) {
            transitionMatrix[1][0] += 0.05; // Increase chance of recovery from "Pre-Diabetic" to "Normal"
        } else if (age > 50) {
            transitionMatrix[0][1] += 0.05; // Increase chance of going from "Normal" to "Pre-Diabetic"
            transitionMatrix[1][2] += 0.05; // Increase chance of going from "Pre-Diabetic" to "Diabetic"
        }

        if (eatingHabits.equals("Unhealthy")) {
            transitionMatrix[0][1] += 0.35;
            transitionMatrix[1][2] += 0.35;
        } else if (eatingHabits.equals("Moderate")) {
            transitionMatrix[0][1] += 0.25;
            transitionMatrix[1][2] += 0.25;
        }

        if (symptoms.equals("Severe")) {
            transitionMatrix[1][2] += 0.75;
        } else if (symptoms.equals("Mild")) {
            transitionMatrix[0][1] += 0.25;
        }

        for (int i = 0; i < transitionMatrix.length; i++) {
            double rowSum = Arrays.stream(transitionMatrix[i]).sum();
            for (int j = 0; j < transitionMatrix[i].length; j++) {
                transitionMatrix[i][j] /= rowSum;
            }
        }

        return transitionMatrix;
    }

    private void runSimulation() {
        try {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());

            if (age <= 0 || age > 120) {
                resultArea.setText("Please enter a valid age between 1 and 120.");
                return;
            }

            String eatingHabits = (String) eatingHabitsCombo.getSelectedItem();
            String symptoms = (String) symptomsCombo.getSelectedItem();

            double[][] transitionMatrix = adjustTransitionMatrix(age, eatingHabits, symptoms);
            double[] finalProbabilities = simulateHealthTransitions(transitionMatrix, "Normal", 100);

            double normalProbability = finalProbabilities[0] * 100;
            double preDiabeticProbability = finalProbabilities[1] * 100;
            double diabeticProbability = finalProbabilities[2] * 100;

            resultArea.setText("Health condition transitions after 10 years for " + name + ":\n" +
                    "Probability of being Normal: " + String.format("%.2f", normalProbability) + "%\n" +
                    "Probability of being Pre-Diabetic: " + String.format("%.2f", preDiabeticProbability) + "%\n" +
                    "Probability of being Diabetic: " + String.format("%.2f", diabeticProbability) + "%");
        } catch (NumberFormatException e) {
            resultArea.setText("Please enter a valid age.");
        }
    }

    private double[] simulateHealthTransitions(double[][] transitionMatrix, String startState, int steps) {
        int currentStateIndex = Arrays.asList(states).indexOf(startState);
        double[] stateProbabilities = new double[states.length];
        stateProbabilities[currentStateIndex] = 1.0;

        for (int i = 0; i < steps; i++) {
            double[] nextProbabilities = new double[states.length];

            for (int j = 0; j < states.length; j++) {
                for (int k = 0; k < states.length; k++) {
                    nextProbabilities[k] += stateProbabilities[j] * transitionMatrix[j][k];
                }
            }

            stateProbabilities = nextProbabilities;
        }

        return stateProbabilities;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HealthTransition());
    }
}
