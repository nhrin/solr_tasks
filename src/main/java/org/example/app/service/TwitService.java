package org.example.app.service;

import lombok.AllArgsConstructor;
import org.example.app.entity.Twit;
import org.example.app.repository.TwitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TwitService {

    @Autowired
    private final TwitRepository twitRepository;


    public Page<Twit> findAll(Pageable pageable) {
        List<Twit> twits = twitRepository.findAll();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Twit> list;

        if (twits.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, twits.size());
            list = twits.subList(startItem, toIndex);
        }

        Page<Twit> bookPage
                = new PageImpl<Twit>(list, PageRequest.of(currentPage, pageSize), twits.size());

        return bookPage;
    }

    public void saveTwit(Twit twit) {
        twitRepository.save(twit);
    }

    public Optional<Twit> findById(int id) {
        return twitRepository.findById(String.valueOf(id));
    }

    public void removeTwit(Twit twit) {
        twitRepository.delete(twit);
    }
}
