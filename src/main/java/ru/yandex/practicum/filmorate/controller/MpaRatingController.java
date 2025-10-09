package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingRepository mpaRatingRepository;

    @GetMapping
    public List<MpaRating> findAll() {
        return mpaRatingRepository.findAll();
    }

    @GetMapping("/{id}")
    public MpaRating findById(@PathVariable Long id) {
        return mpaRatingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA с id=" + id + " не найден"));
    }
}