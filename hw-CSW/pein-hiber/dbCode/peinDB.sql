-- MySQL Script generated by MySQL Workbench
-- Tue Oct  17 11:04:26 2023
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema peindb
-- -----------------------------------------------------
-- DROP SCHEMA IF EXISTS `peindb` ;
-- CREATE SCHEMA IF NOT EXISTS `peindb` DEFAULT CHARACTER SET utf8 ;
USE `peindb` ;

-- -----------------------------------------------------
-- Table `peindb`.`address`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `peindb`.`address` ;

CREATE TABLE IF NOT EXISTS `peindb`.`address` (
  `name` VARCHAR(45) NOT NULL,
  `city` VARCHAR(45) NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `peindb`.`person`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `peindb`.`person` ;

CREATE TABLE IF NOT EXISTS `peindb`.`person` (
  `cf` VARCHAR(16) NOT NULL,
  `residence_name` VARCHAR(45) NULL,
  PRIMARY KEY (`cf`),
  CONSTRAINT `residence_name`
    FOREIGN KEY (`residence_name`)
    REFERENCES `peindb`.`address` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `residence_name_idx` ON `peindb`.`person` (`residence_name` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `peindb`.`person_address`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `peindb`.`person_address` ;

CREATE TABLE IF NOT EXISTS `peindb`.`person_address` (
  `Person_cf` VARCHAR(16) NOT NULL,
  `domiciles_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`Person_cf`, `domiciles_name`),
  CONSTRAINT `Person_cf`
    FOREIGN KEY (`Person_cf`)
    REFERENCES `peindb`.`person` (`cf`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `domiciles_name`
    FOREIGN KEY (`domiciles_name`)
    REFERENCES `peindb`.`address` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `domiciles_name_idx` ON `peindb`.`person_address` (`domiciles_name` ASC) VISIBLE;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;