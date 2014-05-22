SET SQL_SAFE_UPDATES=0;

delete from `humbuch`.`schoolYear`;
delete from `humbuch`.`student`;
delete from `humbuch`.`studentSubject`;
delete from `humbuch`.`teachingMaterial`;
delete from `humbuch`.`teachingMaterialSubject`;
delete from `humbuch`.`borrowedMaterial`;
delete from `humbuch`.`category`;
delete from `humbuch`.`dunningDate`;
delete from `humbuch`.`dunning_has_borrowedMaterial`;
delete from `humbuch`.`dunning`;
delete from `humbuch`.`parent`;
delete from `humbuch`.`role_has_permission`;
delete from `humbuch`.`user_has_role`;
delete from `humbuch`.`permission`;
delete from `humbuch`.`role`;
delete from `humbuch`.`user`;
delete from `humbuch`.`grade`;
delete from `humbuch`.`setting`;

-- -----------------------------------------------------
-- Data for table `humbuch`.`grade`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`grade` (`id`, `grade`, `suffix`, `teacher`) VALUES (1, 5, 'a', NULL);
INSERT INTO `humbuch`.`grade` (`id`, `grade`, `suffix`, `teacher`) VALUES (2, 6, 'a', NULL);
INSERT INTO `humbuch`.`grade` (`id`, `grade`, `suffix`, `teacher`) VALUES (3, 7, 'a', NULL);
INSERT INTO `humbuch`.`grade` (`id`, `grade`, `suffix`, `teacher`) VALUES (4, 8, 'a', NULL);
INSERT INTO `humbuch`.`grade` (`id`, `grade`, `suffix`, `teacher`) VALUES (5, 9, 'a', NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`parent`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`parent` (`id`, `title`, `firstname`, `lastname`, `street`, `postcode`, `city`) VALUES (1, 'Herr', 'Siegfried', 'Müller', 'Musterstraße 1', 12345, 'Musterstadt');
INSERT INTO `humbuch`.`parent` (`id`, `title`, `firstname`, `lastname`, `street`, `postcode`, `city`) VALUES (2, 'Herr', 'Manfred', 'Perterson', 'Musterstraße 1', 12345, 'Musterstadt');

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`student`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`student` (`id`, `gradeId`, `lastname`, `firstname`, `birthday`, `gender`, `parentId`, `leavingSchool`) VALUES (1, 1, 'Müller', 'Hans', '1998-05-01', 'm', 1, 0);
INSERT INTO `humbuch`.`student` (`id`, `gradeId`, `lastname`, `firstname`, `birthday`, `gender`, `parentId`, `leavingSchool`) VALUES (2, 1, 'Maier', 'Peter', '1998-05-01', 'm', 1, 0);
INSERT INTO `humbuch`.`student` (`id`, `gradeId`, `lastname`, `firstname`, `birthday`, `gender`, `parentId`, `leavingSchool`) VALUES (3, 3, 'Peterson', 'Alfred', '2013-12-28', 'm', 2, 0);
INSERT INTO `humbuch`.`student` (`id`, `gradeId`, `lastname`, `firstname`, `birthday`, `gender`, `parentId`, `leavingSchool`) VALUES (4, 3, 'Flick', 'Hansi', '2013-12-28', 'm', 2, 0);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`category`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`category` (`id`, `name`, `description`) VALUES (1, 'Bücher', NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`teachingMaterial`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (1, 1, 'Englisch 5', '442275214', 'Cornelsen', 0.00, NULL, 5, 5, 0, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (2, 1, 'Englisch 6', '136911291', 'Cornelsen', 0.00, NULL, 6, 6, 0, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (3, 1, 'Englisch 5-8', '226899435', 'Cornelsen', 0.00, NULL, 5, 8, 0, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (4, 1, 'Englisch 5 ALT', '419939614', 'Cornelsen', 0.00, NULL, 5, 5, 0, 1, '2011-10-01', '2013-10-01');
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (5, 1, 'Deutsch 5', '648778295', 'Cornelsen', 0.00, NULL, 5, 5, 0, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (6, 1, 'Latein 5', '208496904', 'Cornelsen', 0.00, NULL, 5, 5, 0, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (7, 1, 'Evg Reli 5', '391918769', 'Cornelsen', 0.00, NULL, 5, 5, 0, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (8, 1, 'Evg Reli 7', '440499670', 'Cornelsen', 0.00, NULL, 7, 7, 0, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (9, 1, 'Gemeinschaftskunde 5', '129991165', 'Cornelsen', 0.00, NULL, 5, 5, 0, 0, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (10, 1, 'Erdkunde 5', '354180810', 'Cornelsen', 0.00, NULL, 5, 5, 1, 1, '2013-12-01', NULL);
INSERT INTO `humbuch`.`teachingMaterial` (`id`, `categoryId`, `name`, `identifyingNumber`, `producer`, `price`, `comment`, `fromGrade`, `toGrade`, `fromTerm`, `toTerm`, `validFrom`, `validUntil`) VALUES (11, 1, 'Atlas', '860937574', 'Cornelsen', 0.00, NULL, 5, 6, 1, 0, '2013-12-01', NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`user`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`user` (`id`, `username`, `password`, `email`) VALUES (1, 'admin', '500:d854d1abe5e05e2e2ad4d3bde6d6c1e858ec18c1:c816e7e095abc356c27ff84ccf5c0c1275299ae4', 'test@test.com');
INSERT INTO `humbuch`.`user` (`id`, `username`, `password`, `email`) VALUES (2, 'schlager', '500:1dac1aeacf9cc190a569672bea49432f6be88d5e:45446a81139d8aca30ec3762cdf734cbb60adaf9', 'schlager@test.com');

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`role`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`role` (`id`, `name`, `description`) VALUES (1, 'Admin', NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`permission`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`permission` (`id`, `name`, `description`) VALUES (1, 'darf alles', NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`user_has_role`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`user_has_role` (`user_id`, `role_id`) VALUES (1, 1);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`role_has_permission`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`role_has_permission` (`role_id`, `permission_id`) VALUES (1, 1);

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`schoolYear`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`schoolYear` (`id`, `year`, `fromDate`, `toDate`, `endFirstTerm`, `beginSecondTerm`) VALUES (1, '2012/2013', '2012-08-01', '2013-07-31', '2013-01-31', '2013-02-01');
INSERT INTO `humbuch`.`schoolYear` (`id`, `year`, `fromDate`, `toDate`, `endFirstTerm`, `beginSecondTerm`) VALUES (2, '2013/2014', '2013-08-01', '2014-07-31', '2014-01-31', '2014-02-01');
INSERT INTO `humbuch`.`schoolYear` (`id`, `year`, `fromDate`, `toDate`, `endFirstTerm`, `beginSecondTerm`) VALUES (3, '2014/2015', '2014-08-01', '2015-07-31', '2015-01-31', '2015-02-01');

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`studentSubject`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (1, 1, 'STANDARD');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (2, 1, 'LATIN');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (3, 1, 'EVANGELIC');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (4, 2, 'STANDARD');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (5, 2, 'LATIN');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (6, 2, 'EVANGELIC');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (7, 3, 'STANDARD');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (8, 3, 'LATIN');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (9, 3, 'EVANGELIC');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (10, 4, 'STANDARD');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (11, 4, 'LATIN');
INSERT INTO `humbuch`.`studentSubject` (`id`, `studentId`, `subject`) VALUES (12, 4, 'EVANGELIC');

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`teachingMaterialSubject`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (1, 1, 'STANDARD');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (2, 2, 'STANDARD');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (3, 3, 'STANDARD');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (4, 4, 'STANDARD');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (5, 5, 'STANDARD');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (6, 6, 'LATIN');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (7, 7, 'EVANGELIC');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (8, 8, 'EVANGELIC');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (9, 9, 'STANDARD');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (10, 10, 'STANDARD');
INSERT INTO `humbuch`.`teachingMaterialSubject` (`id`, `teachingMaterialId`, `subject`) VALUES (11, 11, 'STANDARD');

COMMIT;


-- -----------------------------------------------------
-- Data for table `humbuch`.`setting`
-- -----------------------------------------------------
START TRANSACTION;
USE `humbuch`;
INSERT INTO `humbuch`.`setting` (`id`, `settingKey`, `settingValue`, `settingStandardValue`) VALUES (4, 'dun_firstDunningDeadline', '14', '14');
INSERT INTO `humbuch`.`setting` (`id`, `settingKey`, `settingValue`, `settingStandardValue`) VALUES (5, 'dun_secondDunningDeadline', '14', '14');

COMMIT;
