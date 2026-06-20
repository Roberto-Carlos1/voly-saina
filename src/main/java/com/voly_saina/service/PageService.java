package com.voly_saina.service;

import com.voly_saina.entity.Pages;
import com.voly_saina.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private PageRepository PageRepository;

    public List<Pages> findAll() {
        return PageRepository.findAll();
    }

    public Pages findById(Long id) {
        return PageRepository.findById(id).orElse(null);
    }

    public Pages save(Pages Page) {
        return PageRepository.save(Page);
    }

    public boolean existsById(Long id) {
        return PageRepository.existsById(id);
    }

    public void deleteById(Long id) {
        PageRepository.deleteById(id);
    }

    public Pages getConfiguration() {
        return PageRepository.findById(1L).orElse(null);
    }
}
