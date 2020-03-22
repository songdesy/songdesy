package com.songdesy.jpa.datasource;

import com.songdesy.untils.AesUtils;
import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 少   年   辛   苦   终   身   事
 * 莫   向   光   阴   惰   寸   功
 * Today the best performance  as tomorrow newest starter!
 * Created by IntelliJ IDEA.
 *
 * @author : songsong.wu
 * github: https://github.com/songdesy
 * email: callwss@qq.com
 * <p>
 * Date: 18-4-30 下午9:30
 * Description:
 * Copyright(©) 2018 by songsong.wu.
 **/
public class MaobcHikariConfig extends HikariConfig {
    private static final Logger logger = LoggerFactory.getLogger(MaobcHikariConfig.class);

    public MaobcHikariConfig() {
    }

    public void setUsername(String username) {
        logger.debug("Set username: {}", username);
        super.setUsername(AesUtils.decrypt(username));
    }

    public void setPassword(String password) {
        logger.debug("Set password: {}", password);
        super.setPassword(AesUtils.decrypt(password));
    }
}
