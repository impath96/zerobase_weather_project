package com.zerobase.weather.repository;

import com.zerobase.weather.domain.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest() {
        // given
        Memo newMemo = new Memo(1, "this is new memo");

        // when
        jdbcMemoRepository.save(newMemo);

        // then
        Optional<Memo> memo = jdbcMemoRepository.findById(1);

        assertEquals(memo.get().getText(), "this is new memo");
    }

    @Test
    void findAllMemoTest() {
        // given
        jdbcMemoRepository.save(new Memo(1, "text"));
        List<Memo> memos = jdbcMemoRepository.findAll();
        System.out.println(memos);
        // when
        // then
    }

}