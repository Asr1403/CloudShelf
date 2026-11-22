
package com.library.controller;

import com.library.model.Book;
import com.library.repository.BookRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "") String search,
            Model model) {

        model.addAttribute("books",
                search.isBlank()
                        ? repository.findAll()
                        : repository.findByTitleContainingIgnoreCase(search));

        model.addAttribute("search", search);
        model.addAttribute("book", new Book());

        return "books";
    }

    @PostMapping("/add")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("books", repository.findAll());
            model.addAttribute("search", "");
            return "books";
        }

        repository.save(book);
        return "redirect:/books";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
        return "redirect:/books";
    }
}
