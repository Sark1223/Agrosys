CREATE TABLE `agrosys_worker`.`ATTENDANCE` (
  `attendanceId` INT NOT NULL AUTO_INCREMENT,
  `workerId` INT NOT NULL,
  `date` DATE NOT NULL,
  `attended` TINYINT(1) NOT NULL,
  `hoursWorked` INT NOT NULL,
  PRIMARY KEY (`attendanceId`),
  INDEX `FK_ATTENDANCE_WORKER_idx` (`workerId` ASC) VISIBLE,
  CONSTRAINT `FK_ATTENDANCE_WORKER`
    FOREIGN KEY (`workerId`)
    REFERENCES `agrosys_worker`.`WORKER` (`workerId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);