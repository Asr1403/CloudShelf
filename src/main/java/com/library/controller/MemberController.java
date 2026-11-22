
package com.library.controller;

import com.library.model.Member;
import com.library.repository.MemberRepository;
import com.library.repository.LoanRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public MemberController(
            MemberRepository memberRepository,
            LoanRepository loanRepository) {

        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    // Display members and search by name or email
    @GetMapping
    public String listMembers(
            @RequestParam(defaultValue = "") String search,
            Model model) {

        model.addAttribute("members",
                search.isBlank()
                        ? memberRepository.findAllByOrderByNameAsc()
                        : memberRepository
                                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                        search, search));

        model.addAttribute("search", search);
        model.addAttribute("member", new Member());
        model.addAttribute("totalMembers", memberRepository.count());

        return "members";
    }

    // Register a new member
    @PostMapping("/add")
    public String addMember(
            @Valid @ModelAttribute("member") Member member,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (member.getEmail() != null) {
            member.setEmail(member.getEmail().trim().toLowerCase());
        }

        if (member.getName() != null) {
            member.setName(member.getName().trim());
        }

        if (memberRepository.existsByEmailIgnoreCase(
                member.getEmail() == null ? "" : member.getEmail())) {

            result.rejectValue(
                    "email",
                    "duplicate",
                    "This email address is already registered");
        }

        if (result.hasErrors()) {
            model.addAttribute(
                    "members",
                    memberRepository.findAllByOrderByNameAsc());
            model.addAttribute("search", "");
            model.addAttribute("totalMembers", memberRepository.count());

            return "members";
        }

        member.setId(null);
        memberRepository.save(member);

        redirectAttributes.addFlashAttribute(
                "success",
                "Member registered successfully!");

        return "redirect:/members";
    }

    // Delete a member only if they have no loan history
    @PostMapping("/delete/{id}")
    public String deleteMember(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (!memberRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Member not found.");

            return "redirect:/members";
        }

        if (loanRepository.existsByMemberId(id)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete this member because borrowing history exists.");

            return "redirect:/members";
        }

        memberRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Member deleted successfully!");

        return "redirect:/members";
    }
}
