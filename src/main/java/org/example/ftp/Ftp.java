package org.example.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.example.entities.Student;
import org.example.services.impl.StudentServiceImpl;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Ftp {
    private static final String FTP_SERVER = "127.0.0.1";
    private static final int FTP_PORT = 21;
    private static final String FTP_USERNAME = "user";
    private static final String FTP_PASSWORD = "asdf";
    private static final String FILE_PATH = "/students.JSON";
    private static final FTPClient ftpClient = new FTPClient();

    public void openConnection() throws IOException {
        ftpClient.connect(FTP_SERVER, FTP_PORT);
        ftpClient.enterLocalPassiveMode();
        int replyCode = ftpClient.getReplyCode();

        if (!FTPReply.isPositiveCompletion(replyCode)) {
            System.out.println("FTP server refused connection.");
            return;
        }

        if (!ftpClient.login(FTP_USERNAME, FTP_PASSWORD)) {
            System.out.println("Failed to login to the FTP server.");
            return;
        }

        System.out.println("Connected to FTP server");
    }
    public void loadStudents(StudentServiceImpl service) {
        try (InputStream inputStream = ftpClient.retrieveFileStream(FILE_PATH);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            if (!service.students.isEmpty()){
                service.clearStudents();
            }

            String json = jsonBuilder.toString();
            int studentsStart = json.indexOf("\"students\":[") + 12;
            int studentsEnd = json.lastIndexOf("]");
            String studentsJson = json.substring(studentsStart, studentsEnd);
            String[] studentJsonArray = studentsJson.split("\\},\\s?\\{");

            for (String studentJson : studentJsonArray) {
                studentJson = studentJson.replace("{", "").replace("}", "");
                String[] keyValuePairs = studentJson.split(",");
                String id = "";
                String name = "";
                for (String pair : keyValuePairs) {
                    String[] keyValue = pair.split(":");
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    if ("\"id\"".equals(key)) {
                        id = value.replace("\"", "");
                    } else if ("\"name\"".equals(key)) {
                        name = value.replace("\"", "");
                    }
                }
                Student student = new Student(UUID.fromString(id), name);
                service.addStudentToList(student);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveStudentsToFile(StudentServiceImpl service) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"students\":[");
        for (Student student : service.students) {
            if (jsonBuilder.length() > "{\"students\":[".length()) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("{\"id\":\"").append(student.getStringId()).append("\",")
                    .append("\"name\":\"").append(student.getName()).append("\"}");
        }
        jsonBuilder.append("]}");
        String json = jsonBuilder.toString();

        try {
            String ftpUrl = "ftp://%s:%s@%s:%d%s;type=i";
            ftpUrl = String.format(ftpUrl, FTP_USERNAME, FTP_PASSWORD, FTP_SERVER, FTP_PORT, FILE_PATH);
            byte[] jsonData = json.getBytes(StandardCharsets.UTF_8);
            URL url = new URL(ftpUrl);
            OutputStream outputStream = url.openConnection().getOutputStream();
            outputStream.write(jsonData);
            outputStream.close();
            System.out.println("JSON file has been updated on the FTP server");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        public void closeConnection() {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    System.out.println("Disconnected from FTP server");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }