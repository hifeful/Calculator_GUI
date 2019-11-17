package com.hifeful;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.prefs.Preferences;

public class CalculatorFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 400;

    private String[] buttonsChars = { "CE", "C", "Del", "/",
                                      "7", "8", "9", "x",
                                      "4", "5", "6", "-",
                                      "1", "2", "3", "+",
                                      "+/-", "0", ".", "="};
    private JPanel textFields;
    private JTextField expression;
    private JTextField valueResult;

    private JPanel panelButtons;
    private JButton[] buttons;

    private String expressionStr;
    private String valueResultStr;

    private boolean negative = false;
    private boolean dot = false;
    private boolean sign = false;
    private boolean equal = false;
    private boolean cleared = false;

    private ArrayList<Double> values;
    private int valuesCounter = 0;
    private ArrayList<Character> chars;

    public CalculatorFrame()
    {
        values = new ArrayList<>(Collections.nCopies(60, 0.0));
        chars = new ArrayList<>();

        Preferences root = Preferences.userRoot();
        Preferences node = root.node("/com/hifeful/Calculator");

        int left = node.getInt("left", 0);
        int top = node.getInt("top", 0);
        int width = node.getInt("width", DEFAULT_WIDTH);
        int height = node.getInt("height", DEFAULT_HEIGHT);
        expressionStr = "";
        valueResultStr = "";

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(left, top);
        setTitle("Calculator");
        setIconImage(new ImageIcon("images//icon.png").getImage());

        addWindowListener(new WindowAdapter() { // window listener for windowClosing
            @Override
            public void windowClosing(WindowEvent e) {
                node.putInt("left", getX());
                node.putInt("top", getY());
                node.putInt("width", getWidth());
                node.putInt("height", getHeight());

                System.exit(0);
            }
        });

        textFields = new JPanel(new GridLayout(2, 1));
        expression = new JTextField(expressionStr,30);
        expression.setEditable(false);
        expression.setFont(new Font("Times New Roman", Font.BOLD, 18));
        textFields.add(expression);
        valueResult = new JTextField(valueResultStr,30);
        valueResult.setEditable(false);
        valueResult.setFont(new Font("Times New Roman", Font.BOLD, 18));
        textFields.add(valueResult);

        add(textFields, BorderLayout.NORTH);

        createButtons();

        pack();
        setSize(width, height);
    }

    private void createButtons()
    {
        panelButtons = new JPanel(new GridLayout(5, 4));
        buttons = new JButton[20];

        for (int i = 0; i < 20 ; i++)
        {
            buttons[i] = new JButton(buttonsChars[i]);

            try
            {
                if (Integer.parseInt(buttonsChars[i]) >= 0 && Integer.parseInt(buttonsChars[i]) <= 9)
                {
                    int finalI = i;
                    buttons[i].addActionListener(event ->
                    {
                        if (!sign)
                        {
                            valueResultStr += buttonsChars[finalI];
                            valueResult.setText(valueResultStr);

                            values.set(valuesCounter, Double.parseDouble(valueResultStr));
                        }
                        else
                        {
                            valueResultStr = "";
                            valueResultStr += buttonsChars[finalI];
                            valueResult.setText(valueResultStr);

                            values.set(valuesCounter, Double.parseDouble(valueResultStr));

                            sign = false;
                        }
                    });
                }
            }
            catch (NumberFormatException ignored) { }

            if (buttonsChars[i].equals("CE"))
            {
                buttons[i].addActionListener(event ->
                {
                    valueResultStr = "";
                    valueResult.setText(valueResultStr);

                    equal = false;
                    cleared = true;
                });
            }
            else if(buttonsChars[i].equals("C"))
            {
                buttons[i].addActionListener(event ->
                {
                    valueResultStr = "";
                    valueResult.setText(valueResultStr);

                    expressionStr = "";
                    expression.setText(expressionStr);

                    sign = false;
                    negative = false;
                    values = new ArrayList<>(Collections.nCopies(60, 0.0));
                    valuesCounter = 0;
                    chars.clear();

                    equal = false;
                });
            }
            else if(buttonsChars[i].equals("Del"))
            {
                buttons[i].addActionListener(event ->
                {
                    if (valueResultStr.length()-1 > 0)
                    {
                        valueResultStr = valueResultStr.substring(0, valueResultStr.length()-1);
                        valueResult.setText(valueResultStr);

                        if (!valueResultStr.contentEquals("-"))
                            values.set(valuesCounter, Double.parseDouble(valueResultStr));
                    }
                    else
                    {
                        valueResultStr = "";
                        valueResult.setText(valueResultStr);
                        values.set(valuesCounter, 0.0);
                    }

                });
            }
            else if(buttonsChars[i].equals("+/-"))
            {
                buttons[i].addActionListener(event ->
                {
                    if (!negative)
                    {
                        values.set(valuesCounter, -values.get(valuesCounter));
                        valueResultStr = String.valueOf(values.get(valuesCounter));
                        valueResult.setText(valueResultStr);

                        negative = true;
                    }
                    else
                    {
                        values.set(valuesCounter, -values.get(valuesCounter));
                        valueResultStr = String.valueOf(values.get(valuesCounter));
                        valueResult.setText(valueResultStr);

                        negative = false;
                    }
                });
            }
            else if(buttonsChars[i].equals("."))
            {
                buttons[i].addActionListener(event ->
                {
                    if (!dot)
                    {
                        valueResultStr = valueResultStr + ".";
                        valueResult.setText(valueResultStr);
                        dot = true;
                    }
                });
            }
            else if(buttonsChars[i].equals("+") || buttonsChars[i].equals("-") ||
                    buttonsChars[i].equals("/") || buttonsChars[i].equals("x"))
            {
                int finalI1 = i;
                buttons[i].addActionListener(event ->
                {
                    if (!sign)
                    {
                        valuesCounter++;
                        negative = false;
                        if (equal || cleared)
                        {
                            expressionStr += buttonsChars[finalI1] + " ";
                            equal = false;
                            cleared = false;
                        }
                        else
                            expressionStr += valueResultStr + " " + buttonsChars[finalI1] + " ";
                        expression.setText(expressionStr);

                        chars.add(buttonsChars[finalI1].charAt(0));

                        sign = true;
                        dot = false;

                        valueResultStr = String.valueOf(resultCalc());
                        valueResult.setText(String.valueOf(resultCalc()));
                    }
                    else
                    {
                        expressionStr = expressionStr.substring(0, expressionStr.length()-2)
                                + buttonsChars[finalI1] + " ";
                        expression.setText(expressionStr);
                        chars.set(chars.size()-1, buttonsChars[finalI1].charAt(0));
                    }
                });
            }
            else if(buttonsChars[i].equals("="))
            {
                buttons[i].addActionListener(event ->
                {
                    if (!equal && !sign)
                    {
                        expressionStr += valueResultStr + " ";
                        expression.setText(expressionStr);
                        valueResult.setText(String.valueOf(resultCalc()));
                        equal = true;
                    }
                });
            }

            panelButtons.add(buttons[i]);
        }

        add(panelButtons, BorderLayout.CENTER);
    }

    private double resultCalc()
    {
        double resultFunc = values.get(0);
        for (int i = 0; i < chars.size(); i++)
        {
            if (chars.get(i).equals('+'))
                resultFunc += values.get(1+i);
            else if(chars.get(i).equals('-'))
                resultFunc -= values.get(1+i);
            else if(chars.get(i).equals('x'))
            {
                if (values.get(1+i) == 0)
                    resultFunc *= 1;
                else
                    resultFunc *= values.get(1+i);
            }
            else if (chars.get(i).equals('/'))
                if (values.get(1+i) == 0)
                    resultFunc /= 1;
                else
                    resultFunc /= values.get(1+i);
        }
        return resultFunc;
    }

}
