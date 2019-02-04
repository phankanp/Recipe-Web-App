package phan.recipesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import phan.recipesite.model.Step;


public interface StepRepository extends JpaRepository<Step, Long> {
}
