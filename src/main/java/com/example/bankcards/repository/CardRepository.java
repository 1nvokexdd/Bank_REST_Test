package com.example.bankcards.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;

import jakarta.persistence.LockModeType;


public interface CardRepository extends JpaRepository<Card,Long>{


    /**
     * Находит страницу с картами определенного пользователя и статуса
     *
     * @param userId идентификатор пользователя-владельца
     * @param status статус карты для фильтрации
     * @param pageable параметры пагинации и сортировки
     * @return страница с картами, удовлетворяющими условиям
     */
    Page<Card> findByOwnerIdAndStatus(
        Long userId, 
        CARD_STATUS status, 
        Pageable pageable
    );

    /**
     * Находит страницу с картами по статусу
     *
     * @param status статус карты для фильтрации
     * @param pageable параметры пагинации и сортировки
     * @return страница с картами указанного статуса
     */
    Page<Card> findByStatus(CARD_STATUS status, Pageable pageable);

    /**
     * Находит страницу с картами пользователя
     *
     * @param userId идентификатор пользователя-владельца
     * @param pageable параметры пагинации и сортировки
     * @return страница с картами пользователя
     */
    Page<Card> findByOwnerId(Long userId, Pageable pageable);
    
    /**
     * Находит карту по идентификатору с пессимистичной блокировкой
     * 
     * <p>Используется для операций, требующих эксклюзивного доступа к записи</p>
     *
     * @param id идентификатор карты
     * @return Optional с найденной картой или пустой, если карта не найдена
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.id = :id")
    Optional<Card> findByIdWithLock(Long id);
}
