package com.core.jdbc;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HexFormat;

public class JdbcTASK {

    public static void main(String[] args) throws SQLException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String url = "jdbc:mysql://127.0.0.1:3306/";
        String user = "root";
        String password = "Admin";
        String sql = "SELECT * FROM test.task";
        String token = cifradoHex().toString();

        // 1. Conexión a mysql
        Connection connection = DriverManager.getConnection(url, user, password);

        // 2. Crear y ejecutar sentencia
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO test.task VALUES (1,\"Andres\",\"Rodriguez\",'"+token+"')");
        ResultSet resultSet = statement.executeQuery(sql);

        var directions = new ArrayList<TaskDAO>();
        // 3. Procesar resultados
        while(resultSet.next()){
            var id = resultSet.getLong("id");
            var name = resultSet.getString("name");
            var lastname = resultSet.getString("lastname");
            var description = resultSet.getString("description");

            var direction = new TaskDAO(id, name, lastname, description);
            directions.add(direction);
        }
        System.out.println(directions);
        resultSet.close();
        statement.close();
        connection.close();
    }

    public static String cifradoHex() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String plainText = "Ejercicio Mysql";

        // 1. Generador de claves
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);

        // 2. Crear clave privada
        SecretKey key = keyGen.generateKey();

        // 3. Crear Cipher (encriptador/cifrador)
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // 4. Configuración Cipher con Vector de inicialización (IV) / nonce
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom random = SecureRandom.getInstanceStrong();
        random.nextBytes(iv);

        int bits = cipher.getBlockSize() * 8;
        GCMParameterSpec gcmParam = new GCMParameterSpec(bits, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParam); // MODO CIFRADO

        // 5. Realizar cifrado
        byte[] cipherBytes = cipher.doFinal(plainText.getBytes());
        String cipherText = HexFormat.of().formatHex(cipherBytes);
        System.out.println(cipherText);

        return cipherText;
    };
}






