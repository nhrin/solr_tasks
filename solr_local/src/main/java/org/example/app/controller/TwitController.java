package org.example.app.controller;

import org.example.app.entity.Twit;
import org.example.app.service.TwitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TwitController {

    @Autowired
    private TwitService twitService;

    @GetMapping(value = "/")
    public String home(Model model) {
        model.addAttribute("title", "Simple Twitter");
        model.addAttribute("message", "Simple Twitter");
        return "welcomePage";
    }

    @GetMapping("/listTwits")
    public String listTwits(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Twit> twitPage = twitService.findAll(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("twitPage", twitPage);

        int totalPages = twitPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "listTwits";
    }
    @PostMapping("/saveTwit")
    public String saveTwit(Twit twit, Model model) {
        model.addAttribute("message", "Twit created by "
                + twit.getUserName()
                + " successfully!");
        twitService.saveTwit(twit);
        return "saveTwit";
    }

    @GetMapping("/searchingResult")
    public String getSearchedTwits(@RequestParam("query") String query, Model model) {
        List<Twit> twits = twitService.findDocsByContent(query);
        model.addAttribute("twits", twits);
        return "listFoundTwits";
    }

    @GetMapping("/createTwit")
    public String createTwit(Model model) {
        model.addAttribute("twit", new Twit());
        return "newTwit";
    }

    @GetMapping("/searchPage")
    public String findTwitByContent(Model model) {
       return "searchPage";
    }
}

