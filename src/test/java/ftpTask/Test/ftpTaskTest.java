package ftpTask.Test;

import org.example.Exceptions.NoSuchStudentIdException;
import org.example.Exceptions.NoSuchStudentNameException;
import org.example.entities.Student;
import org.example.ftp.Ftp;
import org.example.services.impl.StudentServiceImpl;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertThrows;

public class ftpTaskTest {
    private static final Ftp ftp = new Ftp();
    public StudentServiceImpl service = new StudentServiceImpl();

    @BeforeTest
    public void connectToFtp() throws IOException {
        ftp.openConnection();
        ftp.loadStudents(service);
    }

    @Test
    public void getByName() throws NoSuchStudentNameException {
        List<Student> students = service.getByName("Student1");
        Assert.assertEquals(students.get(0).getName(), "Student1" );
    }

    @Test
    public void getById() throws NoSuchStudentIdException {
        Student student = service.getById(UUID.fromString("d81fa98e-3546-11ee-be56-0242ac120002"));
        Assert.assertEquals(student.getName(), "Student1");
    }

    @Test
    public void addStudent() throws IOException {
        Student student = service.addStudent("NewStudent");
        ftp.saveStudentsToFile(service);
        ftp.closeConnection();
        ftp.openConnection();
        ftp.loadStudents(service);

        Boolean studentDel = service.students.stream().anyMatch(stud -> stud.getId().equals(student.getId()));

        Assert.assertTrue(studentDel);
    }

    @Test
    public void deleteStudent() throws NoSuchStudentIdException, IOException {
        Student student = service.addStudent("StudentForDelete");

        ftp.saveStudentsToFile(service);
        ftp.closeConnection();
        ftp.openConnection();
        ftp.loadStudents(service);

        Boolean studentSave = service.students.stream().anyMatch(stud -> stud.getId().equals(student.getId()));

        Assert.assertTrue(studentSave);

        service.deleteById(student.getId());
        ftp.saveStudentsToFile(service);
        ftp.closeConnection();
        ftp.openConnection();
        ftp.loadStudents(service);

        Boolean studentDel = service.students.stream().noneMatch(stud -> stud.getId().equals(UUID.fromString("fa74092c-edd4-497e-97d7-8d378794ecc9")));

        Assert.assertTrue(studentDel);

    }

    @Test
    public void NoSuchStudentNameException(){
        assertThrows(NoSuchStudentNameException.class, () -> {
            service.getByName("Name");
        });
    }

    @Test
    public void NoSuchStudentIdException(){
        assertThrows(NoSuchStudentIdException.class, () -> {
            service.getById(UUID.randomUUID());
        });
    }

    @AfterTest
    public void closeConnection(){
        ftp.closeConnection();
    }
}
