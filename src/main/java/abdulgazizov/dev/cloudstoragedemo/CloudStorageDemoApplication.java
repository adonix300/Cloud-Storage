package abdulgazizov.dev.cloudstoragedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CloudStorageDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudStorageDemoApplication.class, args);
    }
}
