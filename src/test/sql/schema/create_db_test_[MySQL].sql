SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `humbuch_test` ;
CREATE SCHEMA IF NOT EXISTS `humbuch_test` DEFAULT CHARACTER SET utf8 ;
USE `humbuch_test` ;

-- -----------------------------------------------------
-- Table `humbuch_test`.`grade`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`grade` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `grade` INT(2) NOT NULL,
  `suffix` VARCHAR(45) NOT NULL,
  `teacher` VARCHAR(80) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`parent`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`parent` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NULL,
  `firstname` VARCHAR(45) NOT NULL,
  `lastname` VARCHAR(45) NOT NULL,
  `street` VARCHAR(45) NULL,
  `postcode` INT(11) NULL,
  `city` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`student`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`student` (
  `id` INT(11) NOT NULL,
  `gradeId` INT(11) NOT NULL,
  `lastname` VARCHAR(45) NOT NULL,
  `firstname` VARCHAR(45) NOT NULL,
  `birthday` DATE NOT NULL,
  `gender` VARCHAR(1) NULL,
  `parentId` INT NULL,
  `leavingSchool` TINYINT(1) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_student_class1_idx` (`gradeId` ASC),
  INDEX `fk_student_parent1_idx` (`parentId` ASC),
  CONSTRAINT `fk_student_class1`
    FOREIGN KEY (`gradeId`)
    REFERENCES `humbuch_test`.`grade` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_student_parent1`
    FOREIGN KEY (`parentId`)
    REFERENCES `humbuch_test`.`parent` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 7;


-- -----------------------------------------------------
-- Table `humbuch_test`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(128) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `humbuch_test`.`teachingMaterial`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`teachingMaterial` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `categoryId` INT(11) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `identifyingNumber` VARCHAR(45) NOT NULL,
  `producer` VARCHAR(80) NULL,
  `price` DECIMAL(5,2) NULL DEFAULT 0.00,
  `comment` VARCHAR(45) NULL,
  `fromGrade` INT(11) NULL,
  `fromTerm` INT(1) NULL,
  `toGrade` INT(11) NULL,
  `toTerm` INT(2) NULL,
  `validFrom` DATE NOT NULL,
  `validUntil` DATE NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Lehrmittel_Kategorie1_idx` (`categoryId` ASC),
  CONSTRAINT `fk_TeachingMaterial_Category1`
    FOREIGN KEY (`categoryId`)
    REFERENCES `humbuch_test`.`category` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 15;


-- -----------------------------------------------------
-- Table `humbuch_test`.`borrowedMaterial`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`borrowedMaterial` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `studentId` INT(11) NOT NULL,
  `teachingMaterialId` INT(11) NOT NULL,
  `borrowFrom` DATE NOT NULL,
  `borrowUntil` DATE NULL,
  `returnDate` DATE NULL,
  `received` TINYINT(1) NULL,
  `defect` TINYINT(1) NULL,
  `defectComment` TEXT NULL,
  PRIMARY KEY (`id`, `studentId`),
  INDEX `fk_Ausleihliste_Schueler1_idx` (`studentId` ASC),
  INDEX `fk_Ausleihliste_Lehrmittel1_idx` (`teachingMaterialId` ASC),
  CONSTRAINT `fk_Borrowed_Student1`
    FOREIGN KEY (`studentId`)
    REFERENCES `humbuch_test`.`student` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Borrowed_TeachingMaterial1`
    FOREIGN KEY (`teachingMaterialId`)
    REFERENCES `humbuch_test`.`teachingMaterial` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `email` VARCHAR(60) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `humbuch_test`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`permission` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`user_has_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`user_has_role` (
  `user_id` INT(11) NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `fk_user_has_role_role1_idx` (`role_id` ASC),
  INDEX `fk_user_has_role_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_user_has_role_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `humbuch_test`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_has_role_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `humbuch_test`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`role_has_permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`role_has_permission` (
  `role_id` INT NOT NULL,
  `permission_id` INT NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  INDEX `fk_role_has_privilege_privilege1_idx` (`permission_id` ASC),
  INDEX `fk_role_has_privilege_role1_idx` (`role_id` ASC),
  CONSTRAINT `fk_role_has_privilege_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `humbuch_test`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_role_has_privilege_permission1`
    FOREIGN KEY (`permission_id`)
    REFERENCES `humbuch_test`.`permission` (`id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`schoolYear`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`schoolYear` (
  `year` INT NOT NULL,
  `from` DATE NOT NULL,
  `to` DATE NOT NULL,
  `endFirstTerm` DATE NULL,
  `beginSecondTerm` DATE NULL,
  PRIMARY KEY (`year`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`studentSubject`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`studentSubject` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `studentId` INT(11) NOT NULL,
  `subject` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`, `studentId`),
  INDEX `fk_studentProfile_student1_idx` (`studentId` ASC),
  CONSTRAINT `fk_studentProfile_student1`
    FOREIGN KEY (`studentId`)
    REFERENCES `humbuch_test`.`student` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`teachingMaterialSubject`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`teachingMaterialSubject` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `teachingMaterialId` INT(11) NOT NULL,
  `subject` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`, `teachingMaterialId`),
  INDEX `fk_teachingMaterialSubject_teachingMaterial1_idx` (`teachingMaterialId` ASC),
  CONSTRAINT `fk_teachingMaterialSubject_teachingMaterial1`
    FOREIGN KEY (`teachingMaterialId`)
    REFERENCES `humbuch_test`.`teachingMaterial` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`dunning`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`dunning` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `studentId` INT(11) NOT NULL,
  `type` VARCHAR(45) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_dunning_student1_idx` (`studentId` ASC),
  CONSTRAINT `fk_dunning_student1`
    FOREIGN KEY (`studentId`)
    REFERENCES `humbuch_test`.`student` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`dunning_has_borrowedMaterial`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`dunning_has_borrowedMaterial` (
  `dunningId` INT NOT NULL,
  `borrowedMaterialId` INT(11) NOT NULL,
  `borrowedMaterial_studentId` INT(11) NOT NULL,
  PRIMARY KEY (`dunningId`, `borrowedMaterialId`, `borrowedMaterial_studentId`),
  INDEX `fk_dunning_has_borrowedMaterial_borrowedMaterial1_idx` (`borrowedMaterialId` ASC, `borrowedMaterial_studentId` ASC),
  INDEX `fk_dunning_has_borrowedMaterial_dunning1_idx` (`dunningId` ASC),
  CONSTRAINT `fk_dunning_has_borrowedMaterial_dunning1`
    FOREIGN KEY (`dunningId`)
    REFERENCES `humbuch_test`.`dunning` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_dunning_has_borrowedMaterial_borrowedMaterial1`
    FOREIGN KEY (`borrowedMaterialId` , `borrowedMaterial_studentId`)
    REFERENCES `humbuch_test`.`borrowedMaterial` (`id` , `studentId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `humbuch_test`.`dunningDate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `humbuch_test`.`dunningDate` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `dunningId` INT NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  `statusDate` DATE NOT NULL,
  PRIMARY KEY (`id`, `dunningId`),
  INDEX `fk_dunningDate_dunning1_idx` (`dunningId` ASC),
  CONSTRAINT `fk_dunningDate_dunning1`
    FOREIGN KEY (`dunningId`)
    REFERENCES `humbuch_test`.`dunning` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
