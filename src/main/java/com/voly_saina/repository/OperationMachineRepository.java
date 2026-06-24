package com.voly_saina.repository;

<<<<<<< Updated upstream:src/main/java/com/voly_saina/repository/OperationMachineRepository.java
import com.voly_saina.entity.OperationMachine;
=======
<<<<<<< Updated upstream:src/main/java/com/voly_saina/repository/OperationRepository.java
import com.voly_saina.entity.Operation;
=======
import com.voly_saina.entity.OperationMachine;

import java.util.List;

>>>>>>> Stashed changes:src/main/java/com/voly_saina/repository/OperationMachineRepository.java
>>>>>>> Stashed changes:src/main/java/com/voly_saina/repository/OperationRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
<<<<<<< Updated upstream:src/main/java/com/voly_saina/repository/OperationMachineRepository.java
public interface OperationMachineRepository extends JpaRepository<OperationMachine, Long> {
=======
<<<<<<< Updated upstream:src/main/java/com/voly_saina/repository/OperationRepository.java
public interface OperationRepository extends JpaRepository<Operation, Long> {
=======
public interface OperationMachineRepository extends JpaRepository<OperationMachine, Long> {

    public List<OperationMachine> findByIdFacture_IdFacture(Long id);
>>>>>>> Stashed changes:src/main/java/com/voly_saina/repository/OperationMachineRepository.java
>>>>>>> Stashed changes:src/main/java/com/voly_saina/repository/OperationRepository.java
}
