package com.ctrip.framework.apollo.biz.entity;

import com.ctrip.framework.apollo.common.entity.BaseEntity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * @author Jason Song(song_s@ctrip.com)
 * 每次发布，都会有一个Release表的记录
 * 核心备注:
 * 1.Release表是真正的拉去配置的核心表，每一次发布的时候，该app的namespace下的配置都会记录在Release表的Configurations字段里
 * 2.灰度发布和正常发布的Release记录是根据ClusterName来区分的，因此灰度发布的Release记录，依靠先查找GrayReleaseRule的规则，匹配规则后，获取releaseId来查找对应的Release记录。
 * 例如:灰度发布的ClusterName=20181123183830-e4bdecabe0e49897,正常发布的ClusterName=default
 *
 */
@Entity
@Table(name = "Release")
@SQLDelete(sql = "Update Release set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Release extends BaseEntity {
  @Column(name = "ReleaseKey", nullable = false)
  private String releaseKey;

  @Column(name = "Name", nullable = false)
  private String name;

  @Column(name = "AppId", nullable = false)
  private String appId;

  @Column(name = "ClusterName", nullable = false)
  private String clusterName;

  @Column(name = "NamespaceName", nullable = false)
  private String namespaceName;

  @Column(name = "Configurations", nullable = false)
  @Lob
  private String configurations;

  @Column(name = "Comment", nullable = false)
  private String comment;

  @Column(name = "IsAbandoned", columnDefinition = "Bit default '0'")
  private boolean isAbandoned;

  public String getReleaseKey() {
    return releaseKey;
  }

  public String getAppId() {
    return appId;
  }

  public String getClusterName() {
    return clusterName;
  }

  public String getComment() {
    return comment;
  }

  public String getConfigurations() {
    return configurations;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public String getName() {
    return name;
  }

  public void setReleaseKey(String releaseKey) {
    this.releaseKey = releaseKey;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setConfigurations(String configurations) {
    this.configurations = configurations;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAbandoned() {
    return isAbandoned;
  }

  public void setAbandoned(boolean abandoned) {
    isAbandoned = abandoned;
  }

  public String toString() {
    return toStringHelper().add("name", name).add("appId", appId).add("clusterName", clusterName)
        .add("namespaceName", namespaceName).add("configurations", configurations)
        .add("comment", comment).add("isAbandoned", isAbandoned).toString();
  }
}
