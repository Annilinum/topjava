package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles

        Map<LocalDate, Integer> allCalories = new HashMap<>();
        for (UserMeal meal : meals) {
            int calories = allCalories.getOrDefault(meal.getDateTime().toLocalDate(), 0);
            allCalories.put(meal.getDateTime().toLocalDate(), calories + meal.getCalories());
        }


        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            boolean excess = allCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
            if (isCorrect(meal, startTime, endTime)) {
                result.add(toUserWithExcess(meal, excess));
            }

        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        Map<LocalDate, Integer> calories = meals.stream().collect(
                Collectors.groupingBy(
                        m -> m.getDateTime().toLocalDate(),
                        Collectors.summingInt(m -> m.getCalories())
                )
        );

        return meals.stream().filter(m -> isCorrect(m, startTime, endTime)).map(m -> {
            boolean excess = calories.get(m.getDateTime().toLocalDate()) > caloriesPerDay;
            return  toUserWithExcess(m, excess);
        }).collect(Collectors.toList());
    }

    private static boolean isCorrect(UserMeal m, LocalTime startTime, LocalTime endTime) {
        LocalTime mealTime = m.getDateTime().toLocalTime();
        if (mealTime.isAfter(startTime) && mealTime.isBefore(endTime)) {
            return true;
        }
        return false;
    }

    private static UserMealWithExcess toUserWithExcess(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }
}
