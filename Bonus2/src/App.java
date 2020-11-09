import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class App extends JFrame{    
    JTextField txt_math_exp = new JTextField(10);
    JTextField txt_result = new JTextField(8);
    String regExp = "[^0-9]";
    char c;

    JButton button[] = new JButton[16];

    static class Calc {
        static public String plus(String num1, String num2) {
            return Integer.toString(Integer.parseInt(num1)+Integer.parseInt(num2));
        } 
        static public String minus(String num1, String num2) {
            return Integer.toString(Integer.parseInt(num1)-Integer.parseInt(num2));
        }
        static public String mul(String num1, String num2) {
            return Double.toString((double)Integer.parseInt(num1)*Integer.parseInt(num2));
        }  
        static public String div(String num1, String num2) {
            return String.format("%.6f", (double)Integer.parseInt(num1)/Integer.parseInt(num2));
        } 
    }

    
    App() {
        setTitle("Simple Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        JPanel jp0 = new JPanel();
        jp0.setBackground(Color.LIGHT_GRAY);
        jp0.add(new JLabel("Exp"));
        jp0.add(txt_math_exp);
        jp0.add(new JLabel("Result"));
        jp0.add(txt_result);

        JPanel jp1 = new JPanel();
        jp1.setLayout(new GridLayout(4, 4, 5, 5));
        

        for(int i=0; i<10; i++) {
            String num = Integer.toString(i);
            button[i] = new JButton(num);
        }
        button[10] = new JButton("CE");
        button[11] = new JButton("Calc");
        button[12] = new JButton("+");
        button[13] = new JButton("-");
        button[14] = new JButton("x");
        button[15] = new JButton("/");

        for(int i=0; i<16; i++) {
            if(i>11)
                button[i].setBackground(Color.CYAN);
            jp1.add(button[i]);
            button[i].addMouseListener(new MyMouseListener());
        }

        contentPane.add(jp0, BorderLayout.NORTH);
        contentPane.add(jp1, BorderLayout.CENTER);

        setSize(300, 300);
        setVisible(true);
    }


    class MyMouseListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            JButton b = (JButton)e.getSource();
            String text=txt_math_exp.getText();

            if(!b.getText().equals("Calc"))
                txt_math_exp.setText(text+b.getText());

            if(b.getText().equals("+")||b.getText().equals("-")
                ||b.getText().equals("x")||b.getText().equals("/"))
                c = b.getText().charAt(0);
            //Clear
            else if(b.getText().equals("CE")) {
                txt_math_exp.setText("");
                txt_result.setText("");
            }
            //Calculate
            else if(b.getText().equals("Calc")) {
                String operands[] = txt_math_exp.getText().split(regExp);
                if(operands.length==2) {    
                    switch(c) {
                        case '+':
                            txt_result.setText(Calc.plus(operands[0], operands[1]));
                            break;
                        case '-':
                            txt_result.setText(Calc.minus(operands[0], operands[1]));
                            break;
                        case 'x':
                            txt_result.setText(Calc.mul(operands[0], operands[1]));
                            break;
                        case '/':
                            txt_result.setText(Calc.div(operands[0], operands[1]));
                            break;
                    }    
                }   
                else
                    txt_result.setText("Error");
            }
        }
    }


    public static void main(String[] args) {
        new App();
    }
}
