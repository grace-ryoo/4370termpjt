package uga.menik.cs4370.services;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.cs4370.models.UserService;

@Service
public class ReviewService {
    @Autowired
    private DataSource dataSource;
    private UserService userService;

    public RecipeService(DataSource dataSource, UserService userService) {
        this.dataSource = dataSource;
        this.userService = userService;
    }
}
