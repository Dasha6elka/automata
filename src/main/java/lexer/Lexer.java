package lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Lexer {
    public static void main(String[] args) {
        File input = new File("lexer.txt");
        int lineCount = 1;

        try (FileInputStream inputStream = new FileInputStream(input)) {
            Scanner scanner = new Scanner(inputStream);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String output = "";

                int str = lineCount;

                int i = 0;
                while (i < line.length()) {

                    char currVal = line.charAt(i);
                    char nextVal = i < line.length() - 1 ? line.charAt(i + 1) : currVal;
                    char prevVal = i > 0 ? line.charAt(i - 1) : currVal;
                    TokenType currToken = TokenType.INIT;
                    int position = i + 1;

                    switch (currVal) {
                        case '!':
                            if (nextVal == '=') {
                                output = "!=";
                                currToken = TokenType.NOT_EQUAL;
                                i++;
                            } else {
                                output = "!";
                                currToken = TokenType.ERROR;
                            }
                            break;
                        case '=':
                            if (nextVal != '=' && (i != line.length() - 1)) {
                                output = "=";
                                currToken = TokenType.ASSIGNMENT;
                            } else {
                                output = "==";
                                currToken = TokenType.COMPARISON;
                                i++;
                            }
                            break;
                        case '+':
                            output = "+";
                            currToken = TokenType.PLUS;
                            break;
                        case '-':
                            output = "-";
                            currToken = TokenType.MINUS;
                            break;
                        case '*':
                            output = "*";
                            currToken = TokenType.MULTIPLICATION;
                            break;
                        case '/':
                            if (nextVal == '/' && (i != line.length() - 1)) {
                                output = "//";
                                currToken = TokenType.ONE_LINE_COMMENT;
                            } else if (nextVal == '*') {
                                output = "/*";
                                currToken = TokenType.MULTILINE_COMMENT;
                            } else if (i != line.length() - 1 && prevVal != '*') {
                                output = "/";
                                currToken = TokenType.DIVISION;
                            }
                            break;
                        case '^':
                            output = "^";
                            currToken = TokenType.EXPONENTIATION;
                            break;
                        case '(':
                            output = "(";
                            currToken = TokenType.BRACKET_OPEN;
                            break;
                        case ')':
                            output = ")";
                            currToken = TokenType.BRACKET_CLOSE;
                            break;
                        case '{':
                            output = "{";
                            currToken = TokenType.BRACE_OPEN;
                            break;
                        case '}':
                            output = "}";
                            currToken = TokenType.BRACE_CLOSE;
                            break;
                        case '.':
                            output = ".";
                            currToken = TokenType.POINT;
                            break;
                        case ',':
                            output = ",";
                            currToken = TokenType.COMMA;
                            break;
                        case ':':
                            output = ":";
                            currToken = TokenType.COLON;
                            break;
                        case ';':
                            output = ";";
                            currToken = TokenType.SEMICOLON;
                            break;
                        case '[':
                            output = "[";
                            currToken = TokenType.SQUARE_BRACKETS_OPEN;
                            break;
                        case ']':
                            output = "]";
                            currToken = TokenType.SQUARE_BRACKETS_CLOSE;
                            break;
                        case '<':
                            if (nextVal == '=') {
                                output = "<=";
                                currToken = TokenType.LESS_EQUAL;
                                i++;
                            } else {
                                output = "<";
                                currToken = TokenType.SMALLER;
                            }
                            break;
                        case '>':
                            if (nextVal == '=') {
                                output = ">=";
                                currToken = TokenType.MORE_EQUAL;
                                i++;
                            } else {
                                output = ">";
                                currToken = TokenType.MORE;
                            }
                            break;
                        case ' ':
                            currToken = TokenType.SPACE;
                            break;
                        default:
                            if (currVal == '"') {
                                currToken = TokenType.STRING_PARAM;
                                String string = Character.toString(currVal);
                                while (nextVal != '"') {
                                    i++;
                                    if (i == line.length()) {
                                        line = scanner.nextLine();
                                        i = 0;
                                        lineCount++;
                                    }
                                    currVal = line.charAt(i);
                                    nextVal = i < line.length() - 1 ? line.charAt(i + 1) : currVal;
                                    string += Character.toString(currVal);
                                }
                                output = string + '"';
                                i++;
                            } else if (Character.isDigit(currVal)) {
                                boolean isInt = true;
                                boolean isDouble = false;
                                boolean isFloat = false;
                                boolean isOctal = true;
                                boolean isHex = false;
                                boolean isBinary = false;
                                output = Character.toString(currVal);
                                if (currVal == '0' && (Character.isDigit(nextVal) || nextVal == 'b' || nextVal == 'B'
                                    || nextVal == 'x' || nextVal == 'X' || nextVal == 'A' || nextVal == 'C'
                                    || nextVal == 'D' || nextVal == 'E' || nextVal == 'F')) {
                                    while (Character.isDigit(currVal) || currVal == 'b' || currVal == 'B'
                                        || currVal == 'x' || currVal == 'X' || currVal == 'A' || currVal == 'C'
                                        || currVal == 'D' || currVal == 'E' || currVal == 'F') {
                                        if ((currVal == 'B' || currVal == 'b') && !isHex) {
                                            isBinary = true;
                                            isOctal = false;
                                        }
                                        if (currVal == 'X' || currVal == 'x') {
                                            isHex = true;
                                            isOctal = false;
                                        }
                                        if (isBinary && currVal != '0' && currVal != '1' && currVal != 'b' && currVal != 'B') {
                                            currToken = TokenType.ERROR;
                                            break;
                                        }
                                        if (isOctal && (currVal == '8' || currVal == '9')) {
                                            currToken = TokenType.ERROR;
                                            break;
                                        }
                                        if (isHex && currVal != 'x' && currVal != 'X' && currVal != 'B' && currVal != 'A' && currVal != 'C'
                                            && currVal != 'D' && currVal != 'E' && currVal != 'F' && !Character.isDigit(currVal)) {
                                            currToken = TokenType.ERROR;
                                            break;
                                        }
                                        i++;
                                        if (i >= line.length()) {
                                            break;
                                        }
                                        currVal = line.charAt(i);
                                        nextVal = i < line.length() - 1 ? line.charAt(i + 1) : currVal;
                                        if (Character.isDigit(currVal) || currVal == 'b' || currVal == 'B' || currVal == 'x' || currVal == 'X'
                                        || currVal == 'A' || currVal == 'C'
                                            || currVal == 'D' || currVal == 'E' || currVal == 'F') {
                                            output += currVal;
                                        }
                                    }
                                    if (!Character.isDigit(currVal)) {
                                        i--;
                                    }
                                    if (isBinary) {
                                        currToken = TokenType.BINARY;
                                    } else if (isOctal) {
                                        currToken = TokenType.OCTAL;
                                    } else if (isHex) {
                                        currToken = TokenType.HEX;
                                    }
                                } else {
                                    while (Character.isDigit(currVal) || currVal == '.' || currVal == 'E' || currVal == 'e' || currVal == '-') {
                                        if (currVal == '.') {
                                            isInt = false;
                                            isDouble = true;
                                        }
                                        if (currVal == 'E' || currVal == 'e') {
                                            isInt = false;
                                            isDouble = false;
                                            isFloat = true;
                                        }
                                        i++;
                                        currVal = line.charAt(i);
                                        nextVal = i < line.length() - 1 ? line.charAt(i + 1) : currVal;
                                        if (Character.isDigit(currVal) || currVal == '.' || currVal == 'E' || currVal == 'e' || currVal == '-') {
                                            output += currVal;
                                        }
                                    }
                                    if (!Character.isDigit(currVal)) {
                                        i--;
                                    }
                                    if (isInt) {
                                        currToken = TokenType.INT_NUMBER;
                                    } else if (isDouble) {
                                        currToken = TokenType.DOUBLE_NUMBER;
                                    } else if (isFloat) {
                                        currToken = TokenType.FLOAT_NUMBER;
                                    }
                                }
                            } else if (Character.isAlphabetic(currVal) || currVal == '_') {
                                String ident = "";
                                while (Character.isAlphabetic(currVal) || currVal == '_') {
                                    if (Character.isAlphabetic(currVal) || currVal == '_' || Character.isDigit(currVal)) {
                                        ident += currVal;
                                        i++;
                                        if (i >= line.length()) {
                                            break;
                                        }
                                        currVal = line.charAt(i);
                                        nextVal = i < line.length() - 1 ? line.charAt(i + 1) : currVal;
                                    }
                                }
                                if (!Character.isAlphabetic(currVal) && currVal != '_' && !Character.isDigit(currVal)) {
                                    i--;
                                }
                                output = ident;
                                switch (ident) {
                                    case "private":
                                        currToken = TokenType.PRIVATE;
                                        break;
                                    case "public":
                                        currToken = TokenType.PUBLIC;
                                        break;
                                    case "void":
                                        currToken = TokenType.VOID;
                                        break;
                                    case "var":
                                        currToken = TokenType.VAR;
                                        break;
                                    case "class":
                                        currToken = TokenType.CLASS;
                                        break;
                                    case "int":
                                        currToken = TokenType.INT;
                                        break;
                                    case "double":
                                        currToken = TokenType.DOUBLE;
                                        break;
                                    case "bool":
                                        currToken = TokenType.BOOL;
                                        break;
                                    case "char":
                                        currToken = TokenType.CHAR;
                                        break;
                                    case "String":
                                        currToken = TokenType.STRING;
                                        break;
                                    case "if":
                                        currToken = TokenType.IF;
                                        break;
                                    case "else":
                                        currToken = TokenType.ELSE;
                                        break;
                                    case "while":
                                        currToken = TokenType.WHILE;
                                        break;
                                    case "for":
                                        currToken = TokenType.FOR;
                                        break;
                                    case "read":
                                        currToken = TokenType.READ;
                                        break;
                                    case "write":
                                        currToken = TokenType.WRITE;
                                        break;
                                    default:
                                        currToken = TokenType.IDENTIFICATION;
                                        break;
                                }
                            } else {
                                currToken = TokenType.ERROR;
                            }
                            break;
                    }

                    if (currToken.equals(TokenType.ONE_LINE_COMMENT)) {
                        i = line.length();
                    }

                    if (currToken.equals(TokenType.MULTILINE_COMMENT)) {
                        while (currVal != '*' || nextVal != '/') {
                            i++;
                            if (i == line.length() - 1) {
                                line = scanner.nextLine();
                                i = 1;
                                lineCount++;
                            }
                            currVal = line.charAt(i);
                            nextVal = i < line.length() - 1 ? line.charAt(i + 1) : currVal;
                        }
                    }

                    if (!currToken.equals(TokenType.SPACE)
                        && !currToken.equals(TokenType.ERROR)
                        && !currToken.equals(TokenType.INIT)) {
                        System.out.println(currToken.toString() + ' ' + output + ' ' + str + ' ' + position);
                    } else if (currToken.equals(TokenType.ERROR)) {
                        System.out.println(currToken.toString() + ' ' + output + " in line " + str + " position " + position);
                    }

                    i++;

                }

                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
