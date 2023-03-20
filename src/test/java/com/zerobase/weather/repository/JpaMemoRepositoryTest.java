package com.zerobase.weather.repository;

import com.zerobase.weather.domain.Memo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        // given
        Memo memo = new Memo(1, "test");
        // when
        jpaMemoRepository.save(memo);
        // then

        List<Memo> memos = jpaMemoRepository.findAll();

        assertTrue(memos.size() > 0);
    }

    @Test
    void findByIdTest() {
        // given
        Memo newMemo = new Memo(11, "jpa");
        // when
        Memo savedMemo = jpaMemoRepository.save(newMemo);
        // then
        Optional<Memo> memo = jpaMemoRepository.findById(savedMemo.getId());
        System.out.println(savedMemo.getId());
        assertEquals(savedMemo.getId(), memo.get().getId());
        assertEquals("jpa", memo.get().getText());
    }

}