
package com.library.controller;

import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.LoanRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public HomeController(
            BookRepository bookRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository) {

        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("totalBooks",
                bookRepository.count());

        model.addAttribute("availableBooks",
                bookRepository.countByAvailableTrue());

        model.addAttribute("totalMembers",
                memberRepository.count());

        model.addAttribute("issuedBooks",
                loanRepository.countByReturnDateIsNull());

        return "index";
    }
}
