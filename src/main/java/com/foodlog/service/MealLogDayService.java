package com.foodlog.service;

import com.foodlog.domain.MealLog;
import com.foodlog.domain.MealLogDay;
import com.foodlog.repository.MealLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rafael on 07/07/17.
 */
@Service
public class MealLogDayService {

    @Autowired
    private MealLogRepository mealLogRepository;

    public Page<MealLogDay> findAll(Pageable pageable) {

        HashMap<Long, MealLogDay> days = new HashMap<>();

        //Agrupa os meallog por dia no mapa
        for(MealLog mealLog:mealLogRepository.findByOrderByMealDateTimeDesc(pageable)){
            Long day = mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo")).getLong(ChronoField.DAY_OF_YEAR);
            if(days.get(day) == null){
                MealLogDay mealLogDay = new MealLogDay();
                mealLogDay.setMealLogList(new ArrayList<>());
                mealLogDay.setMealLogDayDate(mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS));
                days.put(day, mealLogDay);
            }
            days.get(day).getMealLogList().add(mealLog);

        }

        // trasforma cada dia do mapa em um item da lista
        List<MealLogDay> result = new ArrayList<MealLogDay>(days.values());

        return new PageImpl<MealLogDay>(result, pageable, result.size());
    }
}
