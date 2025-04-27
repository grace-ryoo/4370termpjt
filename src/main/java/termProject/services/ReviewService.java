package termProject.services;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private DataSource dataSource;
    private UserService userService;

    public ReviewService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }
}
