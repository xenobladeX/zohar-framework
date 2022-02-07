package com.xenoblade.zohar.framework.core.db.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * HelloWorldEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FluentMybatis(
    table = "hello_world",
    schema = "fluent_mybatis"
)
public class HelloWorldEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  @TableId("id")
  private Long id;

  @TableField(
      value = "gmt_created",
      desc = "创建时间"
  )
  private Date gmtCreated;

  @TableField(
      value = "gmt_modified",
      desc = "更新时间"
  )
  private Date gmtModified;

  @TableField(
      value = "is_deleted",
      desc = "是否逻辑删除"
  )
  private Integer isDeleted;

  @TableField("say_hello")
  private String sayHello;

  @TableField("your_name")
  private String yourName;

  @Override
  public final Class entityClass() {
    return HelloWorldEntity.class;
  }
}
