public class Quadruple {
    public String operator;
    public String leftOperand;
    public String rightOperand;
    public String result;

    public Quadruple(String operator, String leftOperand, String rightOperand, String result) {
        this.operator = operator;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.result = result;
    }

    @Override
    public String toString() {
        return "(" + operator + ", " + leftOperand + ", " + rightOperand + ", " + result + ")";
    }

    public void setResult(String result) {
        this.result = result;
    }
}