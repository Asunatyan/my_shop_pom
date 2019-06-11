package com.tamakiakoo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.internal.bind.v2.model.core.ID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("shop_carts")
public class Cart implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int gid;
    private int uid;
    private int gnumber;
    private BigDecimal xiaoji;
    private Date createtime;
    private BigDecimal gprice;

    @TableField(exist = false)
    private Goods goods;

}
