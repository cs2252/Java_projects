import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class Calculator {

    private JTextArea display;
    public static void main(String[] args) {
        Calculator c=new Calculator();
        c.gui();
    }
    void gui()
    {
        JFrame frame=new JFrame("my claculator");
        frame.setLayout(null);
        frame.add(new buttons());
        display=new JTextArea(1,20);
        display.setBackground(Color.white);
        display.setEditable(false);
        display.setBounds(30, 10, 300, 80);
        frame.add(display);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.RED);
       // frame.pack();
        frame.setVisible(true);
    }


    class buttons extends JPanel implements ActionListener
    {
        public buttons()
        {
            JButton arr[]=new JButton[20];
            int i;
            setLayout(new GridLayout(0,4));


            JButton clear=new JButton("C");
            clear.setName("clear");
            clear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    display.setText("");
                }
            });



            JButton cancle=new JButton("X");
            cancle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String s=display.getText();
                    if(s.length()!=0)
                    s=s.substring(0,s.length()-1);
                    display.setText(s);
                }
            });


            int k;
            for( i=1, k=4;i<10;i++,k++)
            {
                if(k==7||k==11||k==15)
                    k++;
                int test=i;
                String temp= ((Integer) test).toString();
                JButton digits=new JButton(temp);
                digits.setName(temp);
                arr[k]=digits;
                digits.addActionListener(this);
            }
            JButton log=new JButton("log");
            log.setName("log");
            log.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                int val=Integer.parseInt(display.getText());
                double result=Math.log(val)/Math.log(10);
                display.setText(Double.toString(result));
                }
            });






            JButton plus=new JButton("+");
            plus.setName("plus");
            plus.addActionListener(this);


            JButton minus=new JButton("-");
            minus.setName("minus");
            minus.addActionListener(this);

            JButton mul=new JButton("*");
            mul.setName("mul");
            mul.addActionListener(this);

            JButton div=new JButton("/");
            div.setName("div");
            div.addActionListener(this);

            JButton dot=new JButton(".");
            dot.setName(".");
            dot.addActionListener(this);

            JButton zero=new JButton("0");
            zero.setName("zero");
            zero.addActionListener(this);

            JButton M=new JButton("M");

            JButton equal=new JButton("=");
            equal.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double result=evaluate(display.getText());
                    display.setText(Double.toString(result));
                }
            });



            arr[0]=clear;
            arr[1]=mul;
            arr[2]=div;
            arr[3]=cancle;
            arr[7]=log;
            arr[11]=minus;
            arr[15]=plus;
            arr[19]=equal;
            arr[18]=M;
            arr[17]=dot;
            arr[16]=zero;
            for(i=0;i<20;i++)
                add(arr[i]);
            this.setBounds(30, 95, 300, 300);
        }

        public void actionPerformed(ActionEvent ev)
        {

            JButton button =(JButton)ev.getSource();
            String s=display.getText();
            String temp=button.getName();
                s=s.concat(button.getText());
                display.setText(s);

        }


    }

    public static double evaluate(String expression)
    {
        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<Double>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++)
        {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number, push it to stack for numbers
            if (tokens[i] >= '0' && tokens[i] <= '9'||tokens[i]=='.')
            {
                StringBuffer sbuf = new StringBuffer();
                // There may be more than one digits in number
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9')||tokens[i]=='.'))
                    sbuf.append(tokens[i++]);
                values.push(Double.parseDouble(sbuf.toString()));
                if(i>=tokens.length)
                    break;
            }

            // Current token is an opening brace, push it to 'ops'
            if (tokens[i] == '(')
                ops.push(tokens[i]);

                // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')')
            {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' ||
                    tokens[i] == '*' || tokens[i] == '/')
            {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        // Top of 'values' contains result, return it
        double result=values.pop();
        if(values.empty())
            return result;
        return 0;
    }

    public static boolean hasPrecedence(char op1, char op2)
    {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    public static double applyOp(char op, double b, double a)
    {
        switch (op)
        {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new
                            UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}