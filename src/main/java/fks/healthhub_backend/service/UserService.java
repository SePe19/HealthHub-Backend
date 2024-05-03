package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.repository.UserRepository;
import jakarta.persistence.NoResultException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public JsonNode getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoResultException("User with id: " + id + " does not exist"));
        return objectMapper.valueToTree(user);
    }

    public List<User> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users;
    }

    public List<Workout> getAllWorkoutsByUser(Long userId){
        List<Workout> workouts = userRepository.findWorkoutsByUserId(userId);
        return workouts;
    }

    public void createUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("Username: " + user.getUsername() + " is already taken");
        }
        userRepository.save(user);
    }

    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoResultException("User with ID: " + id + " could not be found"));
        if(updatedUser != null) {
            user.setUsername(!updatedUser.getUsername().equals("") ? updatedUser.getUsername() : user.getUsername());
        }
        userRepository.save(user);
    }
}
