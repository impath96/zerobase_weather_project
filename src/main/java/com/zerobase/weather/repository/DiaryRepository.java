package com.zerobase.weather.repository;

import com.zerobase.weather.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {

    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    // JPA 통일성을 위해 findFirstByDate로 변경하는게 나을까?
    Diary getFirstByDate(LocalDate date);

    void deleteAllByDate(LocalDate date);

    // Diary findFirstByDate(LocalDate date);



}
