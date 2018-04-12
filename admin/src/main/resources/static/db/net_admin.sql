/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : 127.0.0.1:3306
Source Database       : net_admin

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2018-04-09 00:21:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for biz_job
-- ----------------------------
DROP TABLE IF EXISTS `biz_job`;
CREATE TABLE `biz_job` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_config_id` int(10) DEFAULT NULL COMMENT '同一个config也可以被配置成多个同时运行的实例',
  `name` varchar(20) DEFAULT NULL,
  `group` varchar(20) DEFAULT NULL,
  `cron` varchar(20) DEFAULT NULL,
  `remark` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of biz_job
-- ----------------------------
INSERT INTO `biz_job` VALUES ('1', '1', 'job1', 'group1', '0/5 * * * * ?', '测试1', '1', '2018-04-08 20:05:21', '2018-04-08 20:05:23');
INSERT INTO `biz_job` VALUES ('2', '2', 'job2', 'group1', '0/6 * * * * ?', '测试2', '1', '2018-04-08 20:05:21', '2018-04-08 20:05:23');

-- ----------------------------
-- Table structure for biz_job_class
-- ----------------------------
DROP TABLE IF EXISTS `biz_job_class`;
CREATE TABLE `biz_job_class` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_name` varchar(20) DEFAULT NULL,
  `job_class` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of biz_job_class
-- ----------------------------
INSERT INTO `biz_job_class` VALUES ('1', 'MimiipJob', 'com.cv4j.netdiscovery.admin.job.MimiipJob');
INSERT INTO `biz_job_class` VALUES ('2', 'XicidailiJob', 'com.cv4j.netdiscovery.admin.job.XicidailiJob');

-- ----------------------------
-- Table structure for biz_job_config
-- ----------------------------
DROP TABLE IF EXISTS `biz_job_config`;
CREATE TABLE `biz_job_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_name` varchar(50) DEFAULT NULL,
  `job_class` varchar(100) DEFAULT NULL,
  `parser_class` varchar(100) DEFAULT NULL,
  `url_prefix` varchar(100) DEFAULT NULL,
  `url_suffix` varchar(20) DEFAULT NULL,
  `start_page` int(10) DEFAULT NULL,
  `end_page` int(10) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of biz_job_config
-- ----------------------------
INSERT INTO `biz_job_config` VALUES ('1', '爬西祠国内高匿1-10页', 'com.cv4j.netdiscovery.admin.job.XicidailiJob', 'com.cv4j.proxy.site.xicidaili.XicidailiProxyListPageParser', 'http://www.xicidaili.com/nn/', '.html', '1', '10', '2018-04-08 19:35:53', '2018-04-08 19:46:12');
INSERT INTO `biz_job_config` VALUES ('2', '爬秘密国内高匿1-10页', 'com.cv4j.netdiscovery.admin.job.MimiipJob', 'com.cv4j.proxy.site.mimiip.MimiipProxyListPageParser', 'http://www.mimiip.com/gngao/', '', '1', '10', '2018-04-08 19:35:58', '2018-04-08 19:46:06');

-- ----------------------------
-- Table structure for biz_parser_class
-- ----------------------------
DROP TABLE IF EXISTS `biz_parser_class`;
CREATE TABLE `biz_parser_class` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parser_name` varchar(20) DEFAULT NULL,
  `parser_class` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of biz_parser_class
-- ----------------------------
INSERT INTO `biz_parser_class` VALUES ('1', 'MimiipParser', 'com.cv4j.proxy.site.mimiip.MimiipProxyListPageParser');
INSERT INTO `biz_parser_class` VALUES ('2', 'XicidailiParser', 'com.cv4j.proxy.site.xicidaili.XicidailiProxyListPageParser');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `permission_id` varchar(8) NOT NULL COMMENT '主键id',
  `parent_id` varchar(8) DEFAULT NULL COMMENT '上级权限',
  `permission_type` int(11) DEFAULT '0' COMMENT '权限类型：0菜单，1按钮',
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
INSERT INTO `sys_permission` VALUES ('1', '0', '0', '爬虫管理', null, '&#xe628;', '1', '0', '2018-04-05 13:42:46', '2018-04-05 12:45:35');
INSERT INTO `sys_permission` VALUES ('11', '1', '0', '监控引擎', 'spider/spiderlist', null, '1', '0', '2018-04-05 13:54:23', '2018-04-05 13:42:50');
INSERT INTO `sys_permission` VALUES ('2', '0', '0', '代理管理', null, '&#xe857;', '2', '0', '2018-04-04 18:10:53', '2018-04-05 12:45:42');
INSERT INTO `sys_permission` VALUES ('21', '2', '0', '管理Job资源', 'proxy/jobconfiglist', null, '1', '0', '2018-04-05 12:47:52', '2018-04-05 13:42:56');
INSERT INTO `sys_permission` VALUES ('22', '2', '0', '管理Job运行', 'proxy/joblist', null, '2', '0', '2018-04-08 21:38:24', '2018-04-08 21:38:26');
