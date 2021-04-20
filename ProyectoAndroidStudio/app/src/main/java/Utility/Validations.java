package Utility;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {

    public static enum fieldType {num, text, decimal};

    //Valida Campo EditText
    public static boolean validateEditText(int id, View v, fieldType field) {
        boolean toret = true;
        EditText toValidate = (EditText) v.findViewById(id);
        String toValidateData = toValidate.getText().toString().trim();
        //Si no tiene Datos
        if (toValidateData.length() == 0) {
            toValidate.setError("El campo no pude ser vacío");
            toValidate.requestFocus();

            toret = false;
        } else {

            //Si es entero
            if (field.equals(fieldType.num)) {

                if ((!isNumeric(toValidateData)) || Integer.parseInt(toValidateData) <= 0) {
                    System.out.println(Integer.parseInt(toValidateData) > 0);
                    System.out.println(isNumeric(toValidateData));
                    toValidate.setError("El campo debe ser numérico positivo no decimal");
                    toValidate.requestFocus();
                    toret = false;
                }
                //Si permite caracteres especiales
            } else if (field.equals(fieldType.text)) {
                Pattern pattern = Pattern.compile("[|*^{}#@&\'_)(\\[\\]]", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(toValidateData);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    toValidate.setError("Introduzca letras números o símbolos de puntuación, los caracteres especiales no está permitidos");
                    toValidate.requestFocus();
                    toret = false;
                }
                //Si es decimal
            } else if ((!field.equals(fieldType.decimal)) || Double.parseDouble(toValidateData) <= 0) {
                if (isDecimal(toValidateData)) {
                    toValidate.setError("El campo debe ser numérico");
                    toValidate.requestFocus();
                    toret = false;
                }
            }
        }

        return toret;

    }

    //Valida si el campo es entero
    private static boolean isNumeric(String cadena) {
        boolean result=false;
        try {
            Integer.parseInt(cadena);
            result = true;
        } catch (NumberFormatException excepcion) {

        }
        return result;
    }

    //Valida si el campo es double
    private static boolean isDecimal(String cadena) {
        boolean result=false;
        try {
            Double.parseDouble(cadena);
            result = true;
        } catch (NumberFormatException excepcion) {

        }
        return result;
    }


}
