/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : 127.0.0.1:3306
Source Database       : net_admin

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2018-05-02 17:24:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for biz_job_config
-- ----------------------------
DROP TABLE IF EXISTS `biz_job_config`;
CREATE TABLE `biz_job_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_classpath` varchar(100) DEFAULT NULL,
  `cron_expression` varchar(20) DEFAULT NULL,
  `resource_name` varchar(50) DEFAULT NULL,
  `job_name` varchar(20) DEFAULT NULL,
  `job_group` varchar(20) DEFAULT NULL,
  `trigger_name` varchar(20) DEFAULT NULL,
  `trigger_group` varchar(20) DEFAULT NULL,
  `remark` varchar(50) DEFAULT NULL,
  `state` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of biz_job_config
-- ----------------------------
INSERT INTO `biz_job_config` VALUES ('6', 'com.cv4j.netdiscovery.admin.job.CheckProxyJob', '0 0 0/1 * * ?', '', 'CheckProxyJob', 'DEFAULT', 'CheckProxyJob', 'DEFAULT', '', null, '2018-05-01 11:47:23', '2018-05-02 15:25:15');

-- ----------------------------
-- Table structure for biz_resource
-- ----------------------------
DROP TABLE IF EXISTS `biz_resource`;
CREATE TABLE `biz_resource` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `resource_name` varchar(100) DEFAULT NULL COMMENT '唯一性',
  `parser_classpath` varchar(100) DEFAULT NULL,
  `url_prefix` varchar(100) DEFAULT NULL,
  `url_suffix` varchar(20) DEFAULT NULL,
  `start_page` int(10) DEFAULT NULL,
  `end_page` int(10) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of biz_resource
-- ----------------------------
INSERT INTO `biz_resource` VALUES ('1', '西祠1-10', 'com.cv4j.proxy.site.xicidaili.XicidailiProxyListPageParser', 'http://www.xicidaili.com/nn/', '', '1', '10', '2018-04-08 19:35:53', '2018-04-30 22:06:17');
INSERT INTO `biz_resource` VALUES ('5', '西祠30-50', 'com.cv4j.proxy.site.xicidaili.XicidailiProxyListPageParser', 'http://www.xicidaili.com/nn/', '', '30', '50', '2018-04-30 22:08:17', '2018-04-30 22:08:17');
INSERT INTO `biz_resource` VALUES ('6', '西祠60-63', 'com.cv4j.proxy.site.xicidaili.XicidailiProxyListPageParser', 'http://www.xicidaili.com/nn/', '', '60', '63', '2018-05-01 11:17:01', '2018-05-01 11:17:01');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `permission_id` varchar(8) NOT NULL COMMENT '主键id',
  `parent_id` varchar(8) DEFAULT NULL COMMENT '上级权限',
  `permission_name` varchar(20) DEFAULT NULL COMMENT '权限名',
  `permission_value` varchar(50) DEFAULT NULL COMMENT '权限值',
  `permission_icon` varchar(20) DEFAULT NULL COMMENT '图标',
  `order_number` int(11) DEFAULT '0' COMMENT '菜单排序编号',
  `is_delete` int(11) DEFAULT '0' COMMENT '是否删除：0否,1是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1', '0', '爬虫管理', null, '&#xe628;', '1', '0', '2018-04-05 13:42:46', '2018-04-05 12:45:35');
INSERT INTO `sys_permission` VALUES ('11', '1', '监控引擎', 'spider/spider_list', null, '1', '0', '2018-04-05 13:54:23', '2018-04-05 13:42:50');
INSERT INTO `sys_permission` VALUES ('2', '0', '代理管理', null, '&#xe857;', '2', '0', '2018-04-04 18:10:53', '2018-04-05 12:45:42');
INSERT INTO `sys_permission` VALUES ('21', '2', 'Job资源', 'proxy/resource_list', null, '1', '0', '2018-04-05 12:47:52', '2018-04-05 13:42:56');
INSERT INTO `sys_permission` VALUES ('22', '2', 'Job配置', 'proxy/job_config_list', null, '2', '0', '2018-04-08 21:38:24', '2018-04-08 21:38:26');
INSERT INTO `sys_permission` VALUES ('23', '2', 'Job监控', 'proxy/manage_scheduler', null, '3', '0', null, null);
