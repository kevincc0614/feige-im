/*
-- Query: SELECT * FROM im.t_app
LIMIT 0, 1000

-- Date: 2020-11-02 16:23
*/
INSERT INTO `oim`.`t_app`(`id`, `enterprise_id`, `name`, `avatar`, `secret`, `config`, `create_time`, `update_time`) VALUES (396718441270341632, 396718136373800960, 'DX-客服管理', NULL, 'IgpFPcQCHKmiwglaACcfEFmbriqnSfkY', '{\"url\":\"http://172.28.108.30:7070/customer_service_war\"}', '2020-10-30 17:37:27', '2020-10-30 17:37:27');
INSERT INTO `oim`.`t_app`(`id`, `enterprise_id`, `name`, `avatar`, `secret`, `config`, `create_time`, `update_time`) VALUES (396719290671427584, 396718136373800960, 'HQ-客服管理', NULL, 'kuUJvqazqYyaQIrRKOUMdZrUWOzVpwvu', '{\"url\":\"http://172.28.108.30:7070/customer_service_war\"}', '2020-10-30 17:40:50', '2020-10-30 17:40:50');
/*
-- Query: SELECT * FROM im.t_emp_role
LIMIT 0, 1000

-- Date: 2020-11-02 16:22
*/
INSERT INTO `t_emp_role` (`id`,`enterprise_id`,`role_name`,`authority_ids`,`create_time`,`update_time`) VALUES (397043251813432320,396718136373800960,'超级管理员','397039963493262336,397040500347397120,397040643205391360,397040957077742592,397041012652270592,397041059024495616','2020-10-31 15:08:08','2020-10-31 15:08:08');
INSERT INTO `t_emp_role` (`id`,`enterprise_id`,`role_name`,`authority_ids`,`create_time`,`update_time`) VALUES (397070438281162752,396718136373800960,'DX平台管理员','397039963493262336,397040500347397120,397040643205391360','2020-10-31 16:56:10','2020-10-31 16:56:10');
INSERT INTO `t_emp_role` (`id`,`enterprise_id`,`role_name`,`authority_ids`,`create_time`,`update_time`) VALUES (397070564529713152,396718136373800960,'HQ平台管理员','397040957077742592,397041012652270592,397041059024495616','2020-10-31 16:56:40','2020-10-31 16:56:40');
INSERT INTO `t_emp_role` (`id`,`enterprise_id`,`role_name`,`authority_ids`,`create_time`,`update_time`) VALUES (397078220882867200,396718136373800960,'DX客服组长','397039963493262336','2020-10-31 17:27:05','2020-10-31 17:27:05');
INSERT INTO `t_emp_role` (`id`,`enterprise_id`,`role_name`,`authority_ids`,`create_time`,`update_time`) VALUES (397078347190137856,396718136373800960,'HQ客服组长','397040957077742592','2020-10-31 17:27:35','2020-10-31 17:27:35');
/*
-- Query: SELECT * FROM im.t_emp_role_authority
LIMIT 0, 1000

-- Date: 2020-11-02 16:09
*/
INSERT INTO `t_emp_role_authority` (`id`,`enterprise_id`,`app_id`,`name`,`resource`,`method`,`create_time`,`update_time`) VALUES (397039963493262336,396718136373800960,396718441270341632,'DX订单查询','/customer-service/order','query','2020-10-31 14:55:04','2020-10-31 14:55:04');
INSERT INTO `t_emp_role_authority` (`id`,`enterprise_id`,`app_id`,`name`,`resource`,`method`,`create_time`,`update_time`) VALUES (397040500347397120,396718136373800960,396718441270341632,'DX订单评分','/customer-service/order','grade','2020-10-31 14:57:12','2020-10-31 14:57:12');
INSERT INTO `t_emp_role_authority` (`id`,`enterprise_id`,`app_id`,`name`,`resource`,`method`,`create_time`,`update_time`) VALUES (397040643205391360,396718136373800960,396718441270341632,'DX订单批注','/customer-service/order','comment','2020-10-31 14:57:46','2020-10-31 14:57:46');
INSERT INTO `t_emp_role_authority` (`id`,`enterprise_id`,`app_id`,`name`,`resource`,`method`,`create_time`,`update_time`) VALUES (397040957077742592,396718136373800960,396719290671427584,'HQ订单查询','/customer-service/order','query','2020-10-31 14:59:01','2020-10-31 14:59:01');
INSERT INTO `t_emp_role_authority` (`id`,`enterprise_id`,`app_id`,`name`,`resource`,`method`,`create_time`,`update_time`) VALUES (397041012652270592,396718136373800960,396719290671427584,'HQ订单评分','/customer-service/order','grade','2020-10-31 14:59:14','2020-10-31 14:59:14');
INSERT INTO `t_emp_role_authority` (`id`,`enterprise_id`,`app_id`,`name`,`resource`,`method`,`create_time`,`update_time`) VALUES (397041059024495616,396718136373800960,396719290671427584,'HQ订单批注','/customer-service/order','comment','2020-10-31 14:59:25','2020-10-31 14:59:25');
/*
-- Query: SELECT * FROM im.t_enterprise
LIMIT 0, 1000

-- Date: 2020-11-02 16:24
*/
INSERT INTO `t_enterprise` (`id`,`name`,`description`,`create_time`,`update_time`) VALUES (396718136373800960,'梦幻热游','梦幻热游科技有限公司','2020-10-30 17:36:14','2020-10-30 17:36:14');
