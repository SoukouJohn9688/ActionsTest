package com.example.springboot3todoapplication.controllers;

import com.example.springboot3todoapplication.models.TodoItem;
import com.example.springboot3todoapplication.services.TodoItemService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoFormController.class)
class TodoFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoItemService todoItemService;

    @Test
    void createTodoItem_shouldRedirectToHome_whenValid() throws Exception {
        // Arrange
        TodoItem todoItem = new TodoItem();
        todoItem.setDescription("Test Task");
        todoItem.setIsComplete(false);
        when(todoItemService.save(any(TodoItem.class))).thenReturn(todoItem);

        // Act & Assert
        mockMvc.perform(post("/todo")
                        .param("description", "Test Task")
                        .param("isComplete", "false"))
                .andExpect(status().is3xxRedirection())  // Expecting a redirect
                .andExpect(redirectedUrl("/"));  // Expecting redirect to home page ("/")
    }

    @Test
    void deleteTodoItem_shouldRedirectToHome_whenValidId() throws Exception {
        // Arrange
        TodoItem todoItem = new TodoItem();
        todoItem.setId(1L);
        when(todoItemService.getById(1L)).thenReturn(Optional.of(todoItem));

        // Act & Assert
        mockMvc.perform(get("/delete/1"))
                .andExpect(status().is3xxRedirection())  // Expecting a redirect
                .andExpect(redirectedUrl("/"));  // Expecting redirect to home page ("/")

        Mockito.verify(todoItemService).delete(todoItem);  // Verifying service delete method is called
    }

    @Test
    void updateTodoItem_shouldRedirectToHome_whenValidId() throws Exception {
        // Arrange
        TodoItem todoItem = new TodoItem();
        todoItem.setId(1L);
        todoItem.setDescription("Updated Task");
        todoItem.setIsComplete(true);
        when(todoItemService.getById(1L)).thenReturn(Optional.of(todoItem));
        when(todoItemService.save(any(TodoItem.class))).thenReturn(todoItem);

        // Act & Assert
        mockMvc.perform(post("/todo/1")
                        .param("description", "Updated Task")
                        .param("isComplete", "true"))
                .andExpect(status().is3xxRedirection())  // Expecting a redirect
                .andExpect(redirectedUrl("/"));  // Expecting redirect to home page ("/")
    }

    @Test
    void showCreateForm_shouldReturnCreateTodoPage() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/create-todo"))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(view().name("new-todo-item"));  // Expecting the "new-todo-item" view
    }

    @Test
    void showUpdateForm_shouldReturnEditPage_whenTodoItemExists() throws Exception {
        // Arrange
        TodoItem todoItem = new TodoItem();
        todoItem.setId(1L);
        todoItem.setDescription("Test Task");
        todoItem.setIsComplete(false);
        when(todoItemService.getById(1L)).thenReturn(Optional.of(todoItem));

        // Act & Assert
        mockMvc.perform(get("/edit/1"))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(view().name("edit-todo-item"))  // Expecting the "edit-todo-item" view
                .andExpect(model().attribute("todo", todoItem));  // Expecting the "todo" attribute in the model
    }
}
