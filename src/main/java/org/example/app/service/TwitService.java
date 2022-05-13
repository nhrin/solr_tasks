package org.example.app.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.app.solrclient.TwitSolrClient;
import org.example.app.entity.Twit;
import org.example.app.repository.TwitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TwitService {

    @Autowired
    private final TwitRepository twitRepository;

    private final TwitSolrClient solrClient = new TwitSolrClient();

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

        Page<Twit> twitPage = new PageImpl<Twit>(list, PageRequest.of(currentPage, pageSize), twits.size());

        return twitPage;
    }

    @SneakyThrows
    public void saveTwit(Twit twit) {
        twitRepository.save(twit);
        solrClient.addDoc(twit);
    }

    @SneakyThrows
    public List<Twit> findTwitByContent(String query) {
        List<String> ids = solrClient.findListIdByContent(query);
        List<Twit> twits = ids.stream()
                .map(id -> findById(id).get())
                .collect(Collectors.toCollection(ArrayList::new));
        return twits;
    }

    public Optional<Twit> findById(String id) {
        return twitRepository.findById(Integer.valueOf(id));
    }
}
