
package com.library.controller;

import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookController(
            BookRepository bookRepository,
            LoanRepository loanRepository) {

        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    // Display and search books
    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "") String search,
            Model model) {

        model.addAttribute(
                "books",
                search.isBlank()
                        ? bookRepository.findAll()
                        : bookRepository
                                .findByTitleContainingIgnoreCase(search));

        model.addAttribute("search", search);
        model.addAttribute("book", new Book());

        model.addAttribute(
                "totalBooks",
                bookRepository.count());

        model.addAttribute(
                "availableBooks",
                bookRepository.countByAvailableTrue());

        return "books";
    }

    // Add a new book
    @PostMapping("/add")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "books",
                    bookRepository.findAll());

            model.addAttribute("search", "");

            model.addAttribute(
                    "totalBooks",
                    bookRepository.count());

            model.addAttribute(
                    "availableBooks",
                    bookRepository.countByAvailableTrue());

            return "books";
        }

        book.setId(null);
        book.setAvailable(true);

        bookRepository.save(book);

        redirectAttributes.addFlashAttribute(
                "success",
                "Book added successfully!");

        return "redirect:/books";
    }

    // Delete only books without borrowing history
    @PostMapping("/delete/{id}")
    public String deleteBook(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (!bookRepository.existsById(id)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Book not found.");

            return "redirect:/books";
        }

        if (loanRepository.existsByBookId(id)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete this book because borrowing history exists.");

            return "redirect:/books";
        }

        bookRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Book deleted successfully!");

        return "redirect:/books";
    }
}
