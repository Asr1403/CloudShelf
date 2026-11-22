
package com.library.controller;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Loan;

import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.LoanRepository;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanController(
            LoanRepository loanRepository,
            BookRepository bookRepository,
            MemberRepository memberRepository) {

        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    // Display loan records and issue-book form
    @GetMapping
    public String listLoans(Model model) {

        model.addAttribute(
                "loans",
                loanRepository.findAllByOrderByIssueDateDescIdDesc());

        model.addAttribute(
                "availableBooks",
                bookRepository.findByAvailableTrue());

        model.addAttribute(
                "members",
                memberRepository.findAllByOrderByNameAsc());

        model.addAttribute(
                "activeLoans",
                loanRepository.countByReturnDateIsNull());

        model.addAttribute(
                "overdueLoans",
                loanRepository
                        .findByReturnDateIsNullAndDueDateBefore(
                                LocalDate.now())
                        .size());

        model.addAttribute(
                "defaultDueDate",
                LocalDate.now().plusDays(14));

        model.addAttribute(
                "today",
                LocalDate.now());

        return "loans";
    }

    // Issue a book to a registered member
    @PostMapping("/issue")
    @Transactional
    public String issueBook(
            @RequestParam Long bookId,
            @RequestParam Long memberId,
            @RequestParam(required = false) LocalDate dueDate,
            RedirectAttributes redirectAttributes) {

        Book book = bookRepository.findById(bookId).orElse(null);

        Member member = memberRepository.findById(memberId).orElse(null);

        if (book == null || member == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please select a valid book and member.");
            return "redirect:/loans";
        }

        if (!book.isAvailable()
                || loanRepository.existsByBookIdAndReturnDateIsNull(bookId)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "This book is already issued or unavailable.");
            return "redirect:/loans";
        }

        LocalDate today = LocalDate.now();

        if (dueDate == null) {
            dueDate = today.plusDays(14);
        }

        if (dueDate.isBefore(today)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Due date cannot be in the past.");
            return "redirect:/loans";
        }

        Loan loan = new Loan(book, member, dueDate);

        book.setAvailable(false);

        bookRepository.save(book);
        loanRepository.save(loan);

        redirectAttributes.addFlashAttribute(
                "success",
                "Book issued successfully!");

        return "redirect:/loans";
    }

    // Return an issued book
    @PostMapping("/return/{id}")
    @Transactional
    public String returnBook(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Loan loan = loanRepository.findById(id).orElse(null);

        if (loan == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Loan record not found.");
            return "redirect:/loans";
        }

        if (loan.getReturnDate() != null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "This book has already been returned.");
            return "redirect:/loans";
        }

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailable(true);

        bookRepository.save(book);
        loanRepository.save(loan);

        redirectAttributes.addFlashAttribute(
                "success",
                "Book returned successfully!");

        return "redirect:/loans";
    }
}
